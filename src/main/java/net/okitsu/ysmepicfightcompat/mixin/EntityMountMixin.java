package net.okitsu.ysmepicfightcompat.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.okitsu.ysmepicfightcompat.fabric.shim.bus.EventBus;
import net.okitsu.ysmepicfightcompat.fabric.shim.event.EntityMountEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Fabric replacement for NeoForge's {@code EntityMountEvent} (mount side, server only). */
@Mixin(Entity.class)
public abstract class EntityMountMixin {
    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At("RETURN"), require = 0)
    private void ysmCompat$mounted(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue()) {
            return;
        }
        Entity rider = (Entity) (Object) this;
        Level level = rider.level();
        if (!level.isClientSide()) {
            EventBus.GAME.post(new EntityMountEvent(rider, vehicle, level, true));
        }
    }
}
