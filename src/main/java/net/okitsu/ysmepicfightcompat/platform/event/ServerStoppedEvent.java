package net.okitsu.ysmepicfightcompat.platform.event;

import net.minecraft.server.MinecraftServer;

public final class ServerStoppedEvent {
    private final MinecraftServer server;

    public ServerStoppedEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }
}
