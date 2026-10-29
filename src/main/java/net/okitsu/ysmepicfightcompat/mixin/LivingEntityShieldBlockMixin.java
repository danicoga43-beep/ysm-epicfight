package net.okitsu.ysmepicfightcompat.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.okitsu.ysmepicfightcompat.fabric.shim.event.LivingShieldBlockEvent;
import net.okitsu.ysmepicfightcompat.network.ServerShieldBlockObserver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric replacement for NeoForge's {@code LivingShieldBlockEvent}: vanilla calls
 * {@code hurtCurrentlyUsedShield} exactly when a shield block succeeds with damage above zero.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityShieldBlockMixin {
    @Inject(
            method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V"),
            require = 0)
    private void ysmCompat$observeShieldBlock(DamageSource source, float amount,
                                              CallbackInfoReturnable<Boolean> info) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide()) {
            ServerShieldBlockObserver.afterShieldBlock(
                    self, new LivingShieldBlockEvent(self, true, amount));
        }
    }
}
