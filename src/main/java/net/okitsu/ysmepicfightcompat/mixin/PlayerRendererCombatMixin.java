package net.okitsu.ysmepicfightcompat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.okitsu.ysmepicfightcompat.event.CombatRenderInterceptor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Runs before official YSM's own player render hook (low priority number = applied first), giving
 * Epic Fight ownership of combat frames exactly like the former RenderPlayerEvent.Pre handler.
 */
@Mixin(value = PlayerRenderer.class, priority = 50)
public abstract class PlayerRendererCombatMixin {
    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void ysmCompat$combatRender(AbstractClientPlayer player, float yaw, float partialTick,
                                        PoseStack poseStack, MultiBufferSource buffers, int light,
                                        CallbackInfo callback) {
        if (CombatRenderInterceptor.beforePlayer(player, partialTick,
                (PlayerRenderer) (Object) this, buffers, poseStack, light)) {
            callback.cancel();
        }
    }
}
