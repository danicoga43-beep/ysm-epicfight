package net.okitsu.ysmepicfightcompat.platform;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Minimal replacement for NeoForge's {@code PayloadContext}, created by the Fabric
 * receivers in {@code CompatNetwork} / {@code CompatClient}. Fabric already invokes
 * receivers on the game thread, so {@link #enqueueWork} only re-queues the task.
 */
public final class PayloadContext {
    private final PacketFlow flow;
    private final Player player;
    private final Connection connection;
    private final Executor executor;

    public PayloadContext(PacketFlow flow, Player player, Connection connection, Executor executor) {
        this.flow = Objects.requireNonNull(flow, "flow");
        this.player = player;
        this.connection = connection;
        this.executor = Objects.requireNonNull(executor, "executor");
    }

    /** Direction in which the payload travelled (CLIENTBOUND on a client, SERVERBOUND on a server). */
    public PacketFlow flow() {
        return flow;
    }

    public Player player() {
        return player;
    }

    public Connection connection() {
        return connection;
    }

    public CompletableFuture<Void> enqueueWork(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }
}
