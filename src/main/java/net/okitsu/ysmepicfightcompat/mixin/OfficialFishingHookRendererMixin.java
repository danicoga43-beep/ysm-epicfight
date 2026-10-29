package net.okitsu.ysmepicfightcompat.mixin;

// Varian untuk YSM resmi 2.6.5 (nama kelas/method hasil obfuscation). Hanya dipasang bila kelas target ada (lihat YsmTargetPlugin).

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.projectile.FishingHook;
import net.okitsu.ysmepicfightcompat.render.SubEntityRenderPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Restores the complete vanilla hook-and-line renderer when YSM is disabled. */
@Mixin(targets = "com.elfmcys.yesstevemodel.oOoO0O0OOO0O0OOoOOOooooO",
        remap = false)
public abstract class OfficialFishingHookRendererMixin {
    @Inject(
            method = "OO0ooo0OooOoO0OoO0ooOO0o(Lnet/minecraft/class_1536;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)Z",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void ysmCompat$selectFishingHookRenderer(
            FishingHook hook, float entityYaw, float partialTick,
            PoseStack poseStack, MultiBufferSource buffers, int packedLight,
            CallbackInfoReturnable<Boolean> callback) {
        if (SubEntityRenderPolicy.suppressYsmFishingHook(hook)) {
            callback.setReturnValue(true);
        }
    }
}
