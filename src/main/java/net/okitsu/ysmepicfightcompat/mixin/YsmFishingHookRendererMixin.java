package net.okitsu.ysmepicfightcompat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.projectile.FishingHook;
import net.okitsu.ysmepicfightcompat.render.SubEntityRenderPolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Restores the complete vanilla hook-and-line renderer when YSM is disabled. */
@Mixin(targets = "com.elfmcys.yesstevemodel.client.renderer.CustomFishingHookRenderer",
        remap = false)
public abstract class YsmFishingHookRendererMixin {
    @Inject(
            method = "tryRenderCustomHook(Lnet/minecraft/class_1536;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)Z",
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
