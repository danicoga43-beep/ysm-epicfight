package net.okitsu.ysmepicfightcompat.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * Gives Epic Fight ownership of combat frames before official YSM's normal renderer runs.
 * Invoked from {@code PlayerRendererCombatMixin} at the head of {@code PlayerRenderer.render}.
 */
public final class CombatRenderInterceptor {
    private CombatRenderInterceptor() {
    }

    /** Returns true when Epic Fight rendered the player and the vanilla/YSM render must be cancelled. */
    public static boolean beforePlayer(Player player, float frameTime, PlayerRenderer renderer,
                                       MultiBufferSource buffers, PoseStack poseStack, int light) {
        LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(
                player, LivingEntityPatch.class);
        if (patch == null || !patch.overrideRender()
                || ClientEngine.getInstance().isVanillaModelDebuggingMode()) {
            return false;
        }

        RenderEngine engine = RenderEngine.getInstance();
        if ((frameTime == 0.0F || frameTime == 1.0F) && patch instanceof LocalPlayerPatch local) {
            renderGuiPlayer(engine, player, patch, local, renderer, buffers, poseStack, light, frameTime);
        } else {
            engine.renderEntityArmatureModel(player, patch, renderer, buffers, poseStack, light, frameTime);
        }
        return true;
    }

    private static void renderGuiPlayer(RenderEngine engine, Player player, LivingEntityPatch<?> patch,
                                        LocalPlayerPatch local, PlayerRenderer renderer,
                                        MultiBufferSource buffers, PoseStack poseStack, int light,
                                        float frameTime) {
        float savedYaw = local.getModelYRot();
        local.setModelYRotInGui(player.getYRot());
        poseStack.translate(0.0D, 0.1D, 0.0D);
        try {
            engine.renderEntityArmatureModel(player, patch, renderer, buffers, poseStack, light, frameTime);
        } finally {
            local.disableModelYRotInGui(savedYaw);
        }
    }
}
