package net.okitsu.ysmepicfightcompat.mixin;

// Varian untuk YSM resmi 2.6.5 (nama kelas/method hasil obfuscation). Hanya dipasang bila kelas target ada (lihat YsmTargetPlugin).

import net.minecraft.world.entity.player.Player;
import net.okitsu.ysmepicfightcompat.animation.OfficialRoamingVariables;
import net.okitsu.ysmepicfightcompat.animation.OfficialGroundSpeedQuery;
import net.okitsu.ysmepicfightcompat.animation.OfficialClientModelSelection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Associates each player with official YSM's live roaming-state holder. */
@Mixin(targets = "com.elfmcys.yesstevemodel.oo0ooOoOO00ooO0OoOOoOoOo",
        remap = false)
public abstract class OfficialPlayerStateCapabilityMixin {
    @Inject(
            method = "<init>(Lnet/minecraft/class_1657;)V",
            at = @At("RETURN"),
            remap = false
    )
    private void ysmCompat$capturePlayer(Player player, CallbackInfo callback) {
        OfficialRoamingVariables.register(player, this);
        OfficialGroundSpeedQuery.register(player, this);
        OfficialClientModelSelection.register(player, this);
    }
}
