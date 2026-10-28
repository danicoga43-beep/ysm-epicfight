package net.okitsu.ysmepicfightcompat.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.okitsu.ysmepicfightcompat.event.ClientMaintenanceEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Replacement for NeoForge's RenderFrameEvent.Pre. */
@Mixin(GameRenderer.class)
public abstract class GameRendererFrameMixin {
    @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("HEAD"))
    private void ysmCompat$frameStart(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo callback) {
        ClientMaintenanceEvents.renderFrame();
    }
}
