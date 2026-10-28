package net.okitsu.ysmepicfightcompat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.okitsu.ysmepicfightcompat.config.ServerPreferences;
import net.okitsu.ysmepicfightcompat.network.CompatNetwork;
import net.okitsu.ysmepicfightcompat.platform.CompatEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Fabric common entry point for the official YSM to Epic Fight bridge. */
public final class CompatMod implements ModInitializer {
    public static final String MOD_ID = "ysm_epicfight_compat";
    public static final Logger LOG = LogManager.getLogger("YSM-EF Compat");

    private static volatile MinecraftServer currentServer;

    @Override
    public void onInitialize() {
        ServerPreferences.register();
        CompatNetwork.registerMessages();
        ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServer = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (currentServer == server) {
                currentServer = null;
            }
        });
        CompatEvents.register();
        LOG.info("YSM-EF Compat: initialized (Fabric)");
    }

    /** Replacement for NeoForge's {@code ServerLifecycleHooks.getCurrentServer()}; null on a pure client. */
    public static MinecraftServer currentServer() {
        return currentServer;
    }
}
