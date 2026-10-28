package net.okitsu.ysmepicfightcompat.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.okitsu.ysmepicfightcompat.config.ClientPreferences;
import net.okitsu.ysmepicfightcompat.event.ClientLifecycleEvents;
import net.okitsu.ysmepicfightcompat.event.ClientMaintenanceEvents;
import net.okitsu.ysmepicfightcompat.network.CompatNetwork;

/** Fabric client entry point: client config, client-bound receivers and client lifecycle hooks. */
@Environment(EnvType.CLIENT)
public final class CompatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPreferences.register();
        CompatNetwork.setClientSender(message -> {
            if (ClientPlayNetworking.canSend(message.type())) {
                ClientPlayNetworking.send(message);
            }
        });
        CompatNetworkClient.register();
        ClientLifecycleEvents.register();
        ClientMaintenanceEvents.register();
    }
}
