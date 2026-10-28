package net.okitsu.ysmepicfightcompat.platform.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/** Value objects mirroring the NeoForge player events the server broadcasters consume. */
public final class PlayerEvent {
    private PlayerEvent() {
    }

    public abstract static class Base {
        private final Player player;

        Base(Player player) {
            this.player = player;
        }

        public Player getEntity() {
            return player;
        }
    }

    public static final class PlayerLoggedInEvent extends Base {
        public PlayerLoggedInEvent(Player player) {
            super(player);
        }
    }

    public static final class PlayerLoggedOutEvent extends Base {
        public PlayerLoggedOutEvent(Player player) {
            super(player);
        }
    }

    public static final class StartTracking extends Base {
        private final Entity target;

        public StartTracking(Player player, Entity target) {
            super(player);
            this.target = target;
        }

        public Entity getTarget() {
            return target;
        }
    }

    public static final class StopTracking extends Base {
        private final Entity target;

        public StopTracking(Player player, Entity target) {
            super(player);
            this.target = target;
        }

        public Entity getTarget() {
            return target;
        }
    }

    /** {@link #getEntity()} is the replacement player, matching NeoForge. */
    public static final class Clone extends Base {
        private final Player original;
        private final boolean wasDeath;

        public Clone(Player original, Player replacement, boolean wasDeath) {
            super(replacement);
            this.original = original;
            this.wasDeath = wasDeath;
        }

        public Player getOriginal() {
            return original;
        }

        public boolean isWasDeath() {
            return wasDeath;
        }
    }

    public static final class PlayerRespawnEvent extends Base {
        private final boolean endConquered;

        public PlayerRespawnEvent(Player player, boolean endConquered) {
            super(player);
            this.endConquered = endConquered;
        }

        public boolean isEndConquered() {
            return endConquered;
        }
    }

    public static final class PlayerChangedDimensionEvent extends Base {
        public PlayerChangedDimensionEvent(Player player) {
            super(player);
        }
    }
}
