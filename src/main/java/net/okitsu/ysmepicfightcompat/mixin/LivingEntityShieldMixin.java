package net.okitsu.ysmepicfightcompat.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.okitsu.ysmepicfightcompat.network.ServerShieldBlockObserver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Observes a successful shield block without modifying damage or shield use. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityShieldMixin {
    @Inject(method = "hurtCurrentlyUsingShield(F)V", at = @At("HEAD"), require = 0)
    private void ysmCompat$observeShieldBlock(float amount, CallbackInfo callback) {
        ServerShieldBlockObserver.afterShieldBlock((LivingEntity) (Object) this, amount);
    }
}
