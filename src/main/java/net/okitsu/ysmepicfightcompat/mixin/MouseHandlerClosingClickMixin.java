package net.okitsu.ysmepicfightcompat.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.okitsu.ysmepicfightcompat.fabric.shim.bus.EventBus;
import net.okitsu.ysmepicfightcompat.fabric.shim.event.ScreenEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric replacement for NeoForge's {@code ScreenEvent.MouseButtonPressed.Post}: a click that closes a GUI
 * must not also start a gameplay action (attack, use) in the same dispatch. Vanilla has already queued
 * that action by the time {@code onPress} returns, so the queued click is withdrawn here.
 */
@Mixin(MouseHandler.class)
public abstract class MouseHandlerClosingClickMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private boolean ysmCompat$screenWasOpenOnPress;

    @Inject(method = "onPress(JIII)V", at = @At("HEAD"), require = 0)
    private void ysmCompat$rememberScreen(long window, int button, int action, int modifiers,
                                          CallbackInfo info) {
        ysmCompat$screenWasOpenOnPress = action == 1 && this.minecraft.screen != null;
    }

    @Inject(method = "onPress(JIII)V", at = @At("RETURN"), require = 0)
    private void ysmCompat$withdrawClosingClick(long window, int button, int action, int modifiers,
                                                CallbackInfo info) {
        if (!ysmCompat$screenWasOpenOnPress) {
            return;
        }
        ysmCompat$screenWasOpenOnPress = false;
        ScreenEvent.MouseButtonPressed.Post event = EventBus.GAME.post(
                new ScreenEvent.MouseButtonPressed.Post(false));
        if (event.getResult() != ScreenEvent.MouseButtonPressed.Post.Result.FORCE_HANDLED) {
            return;
        }
        KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(button), false);
        for (KeyMapping mapping : this.minecraft.options.keyMappings) {
            if (mapping.matchesMouse(button)) {
                while (mapping.consumeClick()) {
                    // Drain the click queued by this press.
                }
            }
        }
    }
}
