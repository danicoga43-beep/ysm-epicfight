package net.okitsu.ysmepicfightcompat.mixin;

import net.minecraft.world.entity.Entity;
import net.okitsu.ysmepicfightcompat.platform.CompatEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fabric has no mount event; this reproduces NeoForge's pre-mount notification. */
@Mixin(Entity.class)
public abstract class EntityMountMixin {
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At("HEAD"), require = 0)
    private void ysmCompat$mounting(Entity vehicle, boolean force,
                                    CallbackInfoReturnable<Boolean> callback) {
        CompatEvents.entityMounting((Entity) (Object) this, vehicle);
    }
}
