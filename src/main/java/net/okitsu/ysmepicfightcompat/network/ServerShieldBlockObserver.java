package net.okitsu.ysmepicfightcompat.network;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.okitsu.ysmepicfightcompat.network.message.ShieldBlockMessage;

/** Notifies trackers when the server-side shield-damage hook runs; never changes damage or shield use. */
public final class ServerShieldBlockObserver {
    private ServerShieldBlockObserver() {
    }

    /** Called from the vanilla {@code hurtCurrentlyUsingShield} hook, which only runs for a successful block. */
    public static void afterShieldBlock(LivingEntity entity, float blockedDamage) {
        if (entity == null || entity.level().isClientSide() || entity.isRemoved()
                || entity.getId() < 0 || !wasSuccessful(false, true, blockedDamage)) {
            return;
        }
        ShieldBlockMessage message = new ShieldBlockMessage(entity.getId(), entity.getUUID());
        if (entity instanceof Player player) {
            CompatNetwork.toTrackersAndSelf(player, message);
        } else {
            CompatNetwork.toTrackers(entity, message);
        }
    }

    static boolean wasSuccessful(boolean canceled, boolean blocked, float blockedDamage) {
        return !canceled && blocked && Float.isFinite(blockedDamage) && blockedDamage > 0.0F;
    }
}
