package net.okitsu.ysmepicfightcompat.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.okitsu.ysmepicfightcompat.network.ServerShieldBlockObserver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Player overrides the shield damage hook, so it needs its own observer. */
@Mixin(Player.class)
public abstract class PlayerShieldMixin {
    @Inject(method = "hurtCurrentlyUsingShield(F)V", at = @At("HEAD"), require = 0)
    private void ysmCompat$observeShieldBlock(float amount, CallbackInfo callback) {
        ServerShieldBlockObserver.afterShieldBlock((LivingEntity) (Object) this, amount);
    }
}
