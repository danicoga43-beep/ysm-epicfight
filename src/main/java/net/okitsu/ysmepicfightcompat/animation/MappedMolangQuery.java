package net.okitsu.ysmepicfightcompat.animation;

import net.okitsu.ysmmapping.api.YsmMethodSymbol;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.OptionalDouble;

/** Invokes Fox Model Loader's physical ground-speed query against its live player tracker. */
final class MappedMolangQuery {
    private final MethodHandle query;
    private final MethodHandle trackerGetter;
    private final Class<?> entityType;
    private final Class<?> contextType;
    private final Class<?> trackerType;

    private MappedMolangQuery(MethodHandle query, MethodHandle trackerGetter,
                              Class<?> entityType, Class<?> contextType,
                              Class<?> trackerType) {
        this.query = query;
        this.trackerGetter = trackerGetter;
        this.entityType = entityType;
        this.contextType = contextType;
        this.trackerType = trackerType;
    }

    static Optional<MappedMolangQuery> resolve(YsmMethodSymbol querySymbol,
            YsmMethodSymbol trackerGetterSymbol, ClassLoader loader) {
        if (querySymbol == null || trackerGetterSymbol == null) return Optional.empty();
        try {
            Class<?> queryOwner = Class.forName(querySymbol.binaryOwner(), false, loader);
            MethodType queryType = MethodType.fromMethodDescriptorString(
                    querySymbol.descriptor(), loader);
            if (queryType.parameterCount() != 2 || queryType.returnType() != float.class) {
                return Optional.empty();
            }
            Method queryMethod = queryOwner.getDeclaredMethod(
                    querySymbol.name(), queryType.parameterArray());
            if (!Modifier.isStatic(queryMethod.getModifiers())
                    || Modifier.isAbstract(queryMethod.getModifiers())
                    || Modifier.isNative(queryMethod.getModifiers())
                    || queryMethod.getReturnType() != float.class) {
                return Optional.empty();
            }

            Class<?> contextType = Class.forName(
                    trackerGetterSymbol.binaryOwner(), false, loader);
            MethodType getterType = MethodType.fromMethodDescriptorString(
                    trackerGetterSymbol.descriptor(), loader);
            if (getterType.parameterCount() != 0 || getterType.returnType().isPrimitive()) {
                return Optional.empty();
            }
            Method trackerGetter = contextType.getDeclaredMethod(
                    trackerGetterSymbol.name(), getterType.parameterArray());
            if (Modifier.isStatic(trackerGetter.getModifiers())
                    || trackerGetter.getReturnType() != getterType.returnType()
                    || !queryType.parameterType(1).isAssignableFrom(getterType.returnType())
                    || !trackerGetter.trySetAccessible()
                    || !queryMethod.trySetAccessible()) {
                return Optional.empty();
            }
            MethodHandle queryHandle = MethodHandles.lookup().unreflect(queryMethod);
            MethodHandle getterHandle = MethodHandles.lookup().unreflect(trackerGetter);
            if (!queryHandle.type().equals(queryType)) return Optional.empty();
            return Optional.of(new MappedMolangQuery(queryHandle, getterHandle,
                    queryType.parameterType(0), contextType, getterType.returnType()));
        } catch (ReflectiveOperationException | RuntimeException | LinkageError unavailable) {
            return Optional.empty();
        }
    }

    /** Samples only an existing capability/tracker; it does not create or mutate animation state. */
    OptionalDouble sample(Object entity, Object context) {
        if (entity == null || context == null || !entityType.isInstance(entity)
                || !contextType.isInstance(context)) {
            return OptionalDouble.empty();
        }
        try {
            Object tracker = trackerGetter.invoke(context);
            if (tracker == null || !trackerType.isInstance(tracker)) {
                return OptionalDouble.empty();
            }
            float speed = (float) query.invoke(entity, tracker);
            return Float.isFinite(speed)
                    ? OptionalDouble.of(speed) : OptionalDouble.empty();
        } catch (VirtualMachineError | ThreadDeath fatal) {
            throw fatal;
        } catch (Throwable unavailable) {
            return OptionalDouble.empty();
        }
    }
}
