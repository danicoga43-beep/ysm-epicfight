package net.okitsu.ysmepicfightcompat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.okitsu.ysmepicfightcompat.fabric.shim.bus.EventBus;
import net.okitsu.ysmepicfightcompat.fabric.shim.event.RenderPlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric replacement for NeoForge's {@code RenderPlayerEvent.Pre}: lets the combat renderer take over a
 * player frame before Fox Model Loader's own HEAD injection in {@code PlayerRenderer#render} runs.
 *
 * <p>Mixin applies lower priority values first and inserts same-point HEAD injections in application
 * order, so priority 100 runs before Fox Model Loader's default (1000).
 */
@Mixin(value = PlayerRenderer.class, priority = 100)
public abstract class PlayerRendererCombatMixin {
    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void ysmCompat$combatRender(AbstractClientPlayer player, float entityYaw, float partialTick,
                                        PoseStack poseStack, MultiBufferSource buffers, int packedLight,
                                        CallbackInfo info) {
        RenderPlayerEvent.Pre event = EventBus.GAME.post(new RenderPlayerEvent.Pre(
                player, (PlayerRenderer) (Object) this, partialTick, poseStack, buffers, packedLight));
        if (event.isCanceled()) {
            info.cancel();
        }
    }
}
