package net.okitsu.ysmepicfightcompat.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.fabricmc.api.EnvType;
import net.okitsu.ysmepicfightcompat.animation.ClientAttackSoundRouter;
import net.okitsu.ysmepicfightcompat.animation.ClientShieldBlockState;
import net.okitsu.ysmepicfightcompat.animation.OfficialConfigurationVariables;
import net.okitsu.ysmepicfightcompat.animation.OfficialRoamingVariables;
import net.okitsu.ysmepicfightcompat.animation.OfficialGroundSpeedQuery;
import net.okitsu.ysmepicfightcompat.animation.OfficialClientModelSelection;
import net.okitsu.ysmepicfightcompat.integration.tlm.TouhouMaidRenderBridge;
import net.okitsu.ysmepicfightcompat.integration.tlm.TouhouMaidSelectionAccess;
import net.okitsu.ysmepicfightcompat.mesh.CombatMeshCache;
import net.okitsu.ysmepicfightcompat.network.ClientHeldItemModelPreferences;
import net.okitsu.ysmepicfightcompat.network.ClientMovementAnimationPreferences;
import net.okitsu.ysmepicfightcompat.network.ClientMaidPreferenceSync;
import net.okitsu.ysmepicfightcompat.network.ClientSubEntityModelPreferences;
import net.okitsu.ysmepicfightcompat.network.RemoteMaidPreferences;
import net.okitsu.ysmepicfightcompat.network.RemoteSelectionState;
import net.okitsu.ysmepicfightcompat.network.RemoteSubEntityModelPreferences;
import net.okitsu.ysmepicfightcompat.network.geometry.ClientModelTransfers;
import net.okitsu.ysmepicfightcompat.render.PlayerSelectionResolver;

/** Clears session state and schedules conversion refreshes after official YSM reloads. */
public final class ClientMaintenanceEvents {
    private static final int RELOAD_DELAY = 40;
    private static final int FAILURE_RECHECK_INTERVAL = 100;
    private static int reloadCountdown = -1;
    private static int failureCountdown;

    private ClientMaintenanceEvents() {
    }

    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> disconnect());
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) -> playerLeftLevel(entity));
        ClientSendMessageEvents.COMMAND.register(ClientMaintenanceEvents::commandFinished);
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client) {
                tagsUpdated();
            }
        });
        ClientTickEvents.START_CLIENT_TICK.register(client -> clientTickStart());
        ClientTickEvents.END_CLIENT_TICK.register(client -> clientTick());
    }

    public static void disconnect() {
        PlayerSelectionResolver.clear();
        RemoteSelectionState.beginConnection();
        ClientModelTransfers.clear();
        OfficialConfigurationVariables.clear();
        OfficialRoamingVariables.clear();
        OfficialGroundSpeedQuery.clear();
        OfficialClientModelSelection.clear();
        ClientAttackSoundRouter.clear();
        ClientShieldBlockState.clear();
        ClientHeldItemModelPreferences.beginConnection();
        ClientMovementAnimationPreferences.beginConnection();
        ClientMaidPreferenceSync.beginConnection();
        ClientSubEntityModelPreferences.beginConnection();
        TouhouMaidRenderBridge.clear();
        reloadCountdown = -1;
        failureCountdown = 0;
        Minecraft.getInstance().execute(CombatMeshCache::clear);
    }

    public static void playerLeftLevel(Entity removed) {
        ClientModelTransfers.removeSource(removed.getId(), removed.getUUID());
        RemoteSubEntityModelPreferences.remove(removed.getUUID());
        if (removed instanceof LivingEntity entity) {
            ClientShieldBlockState.remove(entity);
            CombatMeshCache.releaseEntity(entity);
            if (TouhouMaidSelectionAccess.isSupportedMaid(entity)) {
                RemoteMaidPreferences.remove(entity.getUUID());
            }
            if (entity instanceof Player player) {
                OfficialConfigurationVariables.reset(player);
                OfficialGroundSpeedQuery.remove(player);
                OfficialClientModelSelection.remove(player);
            }
        }
    }

    public static void commandFinished(String input) {
        if (isYsmReload(input)) {
            reloadCountdown = RELOAD_DELAY;
        }
    }

    public static void tagsUpdated() {
        ClientMaidPreferenceSync.itemTagsUpdated();
        ClientSubEntityModelPreferences.entityTypesUpdated();
    }

    /** Called from {@code GameRendererFrameMixin} at the start of every rendered frame. */
    public static void renderFrame() {
        OfficialRoamingVariables.refreshNativeWriteOwnership();
        CombatMeshCache.uploadReadyTextures();
    }

    public static void clientTickStart() {
        OfficialRoamingVariables.refreshNativeWriteOwnership();
    }

    public static void clientTick() {
        TouhouMaidRenderBridge.endClientTick();
        CombatMeshCache.advanceAnimationOutputs();
        ClientAttackSoundRouter.tick();
        OfficialConfigurationVariables.tickSync();
        ClientModelTransfers.tick();
        ClientHeldItemModelPreferences.tickSync();
        ClientMovementAnimationPreferences.tickSync();
        ClientMaidPreferenceSync.tickSync();
        ClientSubEntityModelPreferences.tickSync();
        CombatMeshCache.maintainModelCache();
        CombatMeshCache.releaseExpiredTextures();
        if (++failureCountdown >= FAILURE_RECHECK_INTERVAL) {
            failureCountdown = 0;
            CombatMeshCache.retryChangedFailures();
        }
        if (reloadCountdown > 0) {
            reloadCountdown--;
        } else if (reloadCountdown == 0) {
            reloadCountdown = -1;
            Minecraft.getInstance().execute(() -> {
                ClientSubEntityModelPreferences.modelDefinitionsUpdated();
                PlayerSelectionResolver.clear();
                ClientModelTransfers.clear();
                TouhouMaidRenderBridge.clear();
                CombatMeshCache.clear();
            });
        }
    }

    private static boolean isYsmReload(String command) {
        if (command == null) {
            return false;
        }
        String normalized = command.trim().replaceAll("\\s+", " ");
        return normalized.equals("ysm reload") || normalized.startsWith("ysm reload ")
                || normalized.equals("ysm model reload")
                || normalized.startsWith("ysm model reload ");
    }
}
