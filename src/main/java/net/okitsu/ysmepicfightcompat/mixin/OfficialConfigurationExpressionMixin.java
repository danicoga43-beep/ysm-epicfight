package net.okitsu.ysmepicfightcompat.mixin;

// Varian untuk YSM resmi 2.6.5 (nama kelas/method hasil obfuscation). Hanya dipasang bila kelas target ada (lihat YsmTargetPlugin).

import net.minecraft.client.Minecraft;
import net.okitsu.ysmepicfightcompat.animation.OfficialConfigurationVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/** Mirrors configuration expressions that official YSM queues while combat rendering owns pose. */
@Mixin(targets = "com.elfmcys.yesstevemodel.OOooO0Oo00O0OOoo0o0oO00o",
        remap = false)
public abstract class OfficialConfigurationExpressionMixin {
    @Inject(
            method = "OO0ooo0OooOoO0OoO0ooOO0o(Ljava/lang/String;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"),
            remap = false
    )
    private void ysmCompat$mirrorConfigurationExpression(
            String expression, Consumer<String> callback, CallbackInfo info) {
        OfficialConfigurationVariables.apply(Minecraft.getInstance().player, expression);
    }
}
