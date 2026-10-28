package net.okitsu.ysmepicfightcompat.platform;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.okitsu.ysmepicfightcompat.network.HeldItemPreferenceBroadcaster;
import net.okitsu.ysmepicfightcompat.network.MaidPreferenceBroadcaster;
import net.okitsu.ysmepicfightcompat.network.MovementAnimationPreferenceBroadcaster;
import net.okitsu.ysmepicfightcompat.network.SelectionBroadcaster;
import net.okitsu.ysmepicfightcompat.network.SubEntityPreferenceBroadcaster;
import net.okitsu.ysmepicfightcompat.platform.event.EntityJoinLevelEvent;
import net.okitsu.ysmepicfightcompat.platform.event.EntityLeaveLevelEvent;
import net.okitsu.ysmepicfightcompat.platform.event.EntityMountEvent;
import net.okitsu.ysmepicfightcompat.platform.event.PlayerEvent;
import net.okitsu.ysmepicfightcompat.platform.event.ServerStoppedEvent;
import net.okitsu.ysmepicfightcompat.platform.event.ServerTickEvent;

/**
 * Fabric wiring for the server-side handlers that used to be NeoForge {@code @SubscribeEvent}
 * methods. Handler order inside each event matches the previous NeoForge registration.
 */
public final class CompatEvents {
    private CompatEvents() {
    }

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerEvent.PlayerLoggedInEvent event = new PlayerEvent.PlayerLoggedInEvent(handler.getPlayer());
            SelectionBroadcaster.playerLoggedIn(event);
            HeldItemPreferenceBroadcaster.playerLoggedIn(event);
            MovementAnimationPreferenceBroadcaster.playerLoggedIn(event);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerEvent.PlayerLoggedOutEvent event = new PlayerEvent.PlayerLoggedOutEvent(handler.getPlayer());
            MaidPreferenceBroadcaster.playerLoggedOut(event);
            SelectionBroadcaster.playerLoggedOut(event);
            HeldItemPreferenceBroadcaster.playerLoggedOut(event);
            MovementAnimationPreferenceBroadcaster.playerLoggedOut(event);
            SubEntityPreferenceBroadcaster.playerLoggedOut(event);
        });
        EntityTrackingEvents.START_TRACKING.register((tracked, player) -> {
            PlayerEvent.StartTracking event = new PlayerEvent.StartTracking(player, tracked);
            MaidPreferenceBroadcaster.startedTracking(event);
            SelectionBroadcaster.startedTracking(event);
            HeldItemPreferenceBroadcaster.startedTracking(event);
            MovementAnimationPreferenceBroadcaster.startedTracking(event);
            SubEntityPreferenceBroadcaster.startedTracking(event);
        });
        EntityTrackingEvents.STOP_TRACKING.register((tracked, player) -> {
            PlayerEvent.StopTracking event = new PlayerEvent.StopTracking(player, tracked);
            MaidPreferenceBroadcaster.stoppedTracking(event);
            SelectionBroadcaster.stoppedTracking(event);
            SubEntityPreferenceBroadcaster.stoppedTracking(event);
        });
        // Fabric reports alive == true when the copy keeps all data (returning from the End).
        ServerPlayerEvents.COPY_FROM.register((original, replacement, alive) ->
                SelectionBroadcaster.playerCloned(new PlayerEvent.Clone(original, replacement, !alive)));
        ServerPlayerEvents.AFTER_RESPAWN.register((original, replacement, alive) ->
                SelectionBroadcaster.playerRespawned(new PlayerEvent.PlayerRespawnEvent(replacement, alive)));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
                SelectionBroadcaster.playerChangedDimension(new PlayerEvent.PlayerChangedDimensionEvent(player)));
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerTickEvent.Post event = new ServerTickEvent.Post(server);
            MaidPreferenceBroadcaster.serverTick(event);
            SelectionBroadcaster.serverTick(event);
            SubEntityPreferenceBroadcaster.serverTick(event);
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            ServerStoppedEvent event = new ServerStoppedEvent(server);
            MaidPreferenceBroadcaster.serverStopped(event);
            SelectionBroadcaster.serverStopped(event);
            HeldItemPreferenceBroadcaster.serverStopped(event);
            MovementAnimationPreferenceBroadcaster.serverStopped(event);
            SubEntityPreferenceBroadcaster.serverStopped(event);
        });
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) ->
                SubEntityPreferenceBroadcaster.entityJoined(new EntityJoinLevelEvent(entity, level)));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> {
            EntityLeaveLevelEvent event = new EntityLeaveLevelEvent(entity, level);
            MaidPreferenceBroadcaster.entityLeftLevel(event);
            SubEntityPreferenceBroadcaster.entityLeftLevel(event);
        });
    }

    /** Called from {@code EntityMountMixin}; Fabric has no mount event. */
    public static void entityMounting(Entity mounting, Entity vehicle) {
        if (mounting == null || vehicle == null || mounting.level() == null) {
            return;
        }
        Level level = mounting.level();
        if (!level.isClientSide()) {
            SubEntityPreferenceBroadcaster.entityMounted(new EntityMountEvent(mounting, vehicle, true));
        }
    }
}
