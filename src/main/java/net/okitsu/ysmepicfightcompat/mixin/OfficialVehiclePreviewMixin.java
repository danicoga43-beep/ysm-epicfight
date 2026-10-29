package net.okitsu.ysmepicfightcompat.mixin;

// Varian untuk YSM resmi 2.6.5 (nama kelas/method hasil obfuscation). Hanya dipasang bila kelas target ada (lihat YsmTargetPlugin).

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Entity;
import net.okitsu.ysmepicfightcompat.render.SubEntityRenderPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Prevents a disabled YSM vehicle locator from moving its Epic Fight passenger. */
@Mixin(targets = "com.elfmcys.yesstevemodel.Oo0oOOOoOO00ooooOo0OOOOo",
        remap = false)
public abstract class OfficialVehiclePreviewMixin {
    @Inject(
            method = "OO0ooo0OooOoO0OoO0ooOO0o(Lnet/minecraft/class_1297;Lnet/minecraft/class_4587;F)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void ysmCompat$selectVehiclePassengerLocator(
            Entity rider, PoseStack poseStack, float partialTick,
            CallbackInfo callback) {
        if (SubEntityRenderPolicy.suppressYsmVehicleLocator(rider)) {
            callback.cancel();
        }
    }
}
