package net.okitsu.ysmepicfightcompat.mixin;

import net.minecraft.sounds.SoundEvent;
import net.okitsu.ysmepicfightcompat.network.ServerAttackSoundRouter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/** Routes only Epic Fight's attack-phase swing sound; hit and impact sounds remain untouched. */
@Mixin(value = AttackAnimation.class, remap = false)
public abstract class AttackAnimationSoundMixin {
    @Redirect(
            method = "attackTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;playSound",
                    remap = false
            ),
            require = 0,
            remap = false
    )
    private void ysmCompat$routePlayerSwingSound(
            LivingEntityPatch<?> patch, SoundEvent sound,
            float minimumPitch, float maximumPitch) {
        ServerAttackSoundRouter.route((AttackAnimation) (Object) this,
                patch, sound, minimumPitch, maximumPitch);
    }
}
