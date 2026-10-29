package net.okitsu.ysmepicfightcompat.mixin;

// Varian untuk YSM resmi 2.6.5 (nama kelas/method hasil obfuscation). Hanya dipasang bila kelas target ada (lihat YsmTargetPlugin).

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.okitsu.ysmepicfightcompat.render.CombatOverlayPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stops the optional YSM paper-doll pass while Epic Fight owns combat rendering. */
@Mixin(targets = "com.elfmcys.yesstevemodel.Oo0oOOOoOO00ooooOo0OOOOo", remap = false)
public abstract class OfficialCombatOverlayMixin {
    @Inject(
            method = "OO0ooo0OooOoO0OoO0ooOO0o(Lnet/minecraft/class_332;Lnet/minecraft/class_746;FFFFIF)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void ysmCompat$suppressBattleOverlay(
            GuiGraphics graphics, LocalPlayer player, float x, float y,
            float scale, float yawOffset, int depth, float partialTick,
            CallbackInfo callback) {
        if (CombatOverlayPolicy.shouldSuppress(player)) {
            callback.cancel();
        }
    }
}
