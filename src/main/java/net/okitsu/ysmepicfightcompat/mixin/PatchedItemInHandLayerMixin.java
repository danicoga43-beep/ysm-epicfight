package net.okitsu.ysmepicfightcompat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.okitsu.ysmepicfightcompat.render.RenderFrameContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/** Selects the displayed hand pose and suppresses items replaced by the active YSM model. */
@Mixin(value = PatchedItemInHandLayer.class, remap = false)
public abstract class PatchedItemInHandLayerMixin {
    @Redirect(
            method = "renderLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/client/renderer/patched/item/RenderItemBase;renderItemInHand",
                    remap = false
            ),
            require = 0,
            remap = false
    )
    private void ysmCompat$renderUnlessModelReplaces(
            RenderItemBase renderer, ItemStack stack, LivingEntityPatch<?> patch,
            InteractionHand hand, OpenMatrix4f[] poses, MultiBufferSource buffers,
            PoseStack matrices, int light, float partialTick) {
        LivingEntity entity = patch.getOriginal();
        if (RenderFrameContext.suppressesHeldItem(entity, hand)) {
            return;
        }
        try (RenderFrameContext.HeldItemDraw item = RenderFrameContext.openHeldItem(
                entity, hand, patch.getArmature(), poses)) {
            renderer.renderItemInHand(stack, patch, hand, item == null ? poses : item.poses(),
                    buffers, matrices, light, partialTick);
        }
    }
}
