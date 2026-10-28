package net.okitsu.ysmepicfightcompat.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.okitsu.ysmepicfightcompat.platform.PayloadContext;
import net.okitsu.ysmepicfightcompat.network.message.AttackSwingSoundMessage;
import net.okitsu.ysmepicfightcompat.network.message.ModelChunkMessage;
import net.okitsu.ysmepicfightcompat.network.message.ModelRequestMessage;
import net.okitsu.ysmepicfightcompat.network.message.ConfigurationVariableSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.ConfigurationVariableUpdateMessage;
import net.okitsu.ysmepicfightcompat.network.message.ConfigurationVariableScopeRequestMessage;
import net.okitsu.ysmepicfightcompat.network.message.ConfigurationVariableScopeReplyMessage;
import net.okitsu.ysmepicfightcompat.network.message.HeldItemPreferenceSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.HeldItemPreferenceUpdateMessage;
import net.okitsu.ysmepicfightcompat.network.message.MovementAnimationPreferenceSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.MovementAnimationPreferenceUpdateMessage;
import net.okitsu.ysmepicfightcompat.network.message.MaidMovementPreferenceQueryMessage;
import net.okitsu.ysmepicfightcompat.network.message.MaidMovementPreferenceUpdateMessage;
import net.okitsu.ysmepicfightcompat.network.message.MaidPreferenceQueryMessage;
import net.okitsu.ysmepicfightcompat.network.message.MaidPreferenceSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.MaidPreferenceUpdateMessage;
import net.okitsu.ysmepicfightcompat.network.message.OwnerPreferenceEpochMessage;
import net.okitsu.ysmepicfightcompat.network.message.SelectionUpdateMessage;
import net.okitsu.ysmepicfightcompat.network.message.ScriptSyncRequestMessage;
import net.okitsu.ysmepicfightcompat.network.message.ScriptSyncSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.ShieldBlockMessage;
import net.okitsu.ysmepicfightcompat.network.message.SubEntityPreferenceQueryMessage;
import net.okitsu.ysmepicfightcompat.network.message.SubEntityPreferenceSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.SubEntityPreferenceUpdateMessage;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Fabric payloads for compatibility-owned state; official YSM's payloads remain untouched. */
public final class CompatNetwork {
    public static final String PROTOCOL = "1";

    private CompatNetwork() {
    }

    private static volatile Consumer<CustomPacketPayload> clientSender = message -> {
    };

    /** Installed by the client entry point; keeps client-only networking classes off dedicated servers. */
    public static void setClientSender(Consumer<CustomPacketPayload> sender) {
        clientSender = sender == null ? message -> {
        } : sender;
    }

    private static <T extends CustomPacketPayload> void clientboundType(
            CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void serverbound(
            CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> codec,
            BiConsumer<T, PayloadContext> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(
                payload, new PayloadContext(PacketFlow.SERVERBOUND, context.player(),
                        context.player().connection.getConnection(), context.server())));
    }

    /** Registers every payload type on both sides plus the server-bound receivers. */
    public static void registerMessages() {
        clientboundType(SelectionUpdateMessage.TYPE, SelectionUpdateMessage.STREAM_CODEC);
        serverbound(ModelRequestMessage.TYPE,
                ModelRequestMessage.STREAM_CODEC, ModelRequestMessage::receive);
        clientboundType(ModelChunkMessage.TYPE, ModelChunkMessage.STREAM_CODEC);
        serverbound(ConfigurationVariableUpdateMessage.TYPE,
                ConfigurationVariableUpdateMessage.STREAM_CODEC, ConfigurationVariableUpdateMessage::receive);
        clientboundType(ConfigurationVariableSnapshotMessage.TYPE,
                ConfigurationVariableSnapshotMessage.STREAM_CODEC);
        clientboundType(AttackSwingSoundMessage.TYPE, AttackSwingSoundMessage.STREAM_CODEC);
        serverbound(HeldItemPreferenceUpdateMessage.TYPE,
                HeldItemPreferenceUpdateMessage.STREAM_CODEC, HeldItemPreferenceUpdateMessage::receive);
        clientboundType(HeldItemPreferenceSnapshotMessage.TYPE,
                HeldItemPreferenceSnapshotMessage.STREAM_CODEC);
        serverbound(MovementAnimationPreferenceUpdateMessage.TYPE,
                MovementAnimationPreferenceUpdateMessage.STREAM_CODEC,
                MovementAnimationPreferenceUpdateMessage::receive);
        clientboundType(MovementAnimationPreferenceSnapshotMessage.TYPE,
                MovementAnimationPreferenceSnapshotMessage.STREAM_CODEC);
        serverbound(OwnerPreferenceEpochMessage.TYPE,
                OwnerPreferenceEpochMessage.STREAM_CODEC, OwnerPreferenceEpochMessage::receive);
        clientboundType(MaidPreferenceQueryMessage.TYPE, MaidPreferenceQueryMessage.STREAM_CODEC);
        serverbound(MaidPreferenceUpdateMessage.TYPE,
                MaidPreferenceUpdateMessage.STREAM_CODEC, MaidPreferenceUpdateMessage::receive);
        clientboundType(MaidMovementPreferenceQueryMessage.TYPE,
                MaidMovementPreferenceQueryMessage.STREAM_CODEC);
        serverbound(MaidMovementPreferenceUpdateMessage.TYPE,
                MaidMovementPreferenceUpdateMessage.STREAM_CODEC, MaidMovementPreferenceUpdateMessage::receive);
        clientboundType(MaidPreferenceSnapshotMessage.TYPE, MaidPreferenceSnapshotMessage.STREAM_CODEC);
        clientboundType(SubEntityPreferenceQueryMessage.TYPE, SubEntityPreferenceQueryMessage.STREAM_CODEC);
        serverbound(SubEntityPreferenceUpdateMessage.TYPE,
                SubEntityPreferenceUpdateMessage.STREAM_CODEC, SubEntityPreferenceUpdateMessage::receive);
        clientboundType(SubEntityPreferenceSnapshotMessage.TYPE,
                SubEntityPreferenceSnapshotMessage.STREAM_CODEC);
        serverbound(ScriptSyncRequestMessage.TYPE,
                ScriptSyncRequestMessage.STREAM_CODEC, ScriptSyncRequestMessage::receive);
        clientboundType(ScriptSyncSnapshotMessage.TYPE, ScriptSyncSnapshotMessage.STREAM_CODEC);
        clientboundType(ShieldBlockMessage.TYPE, ShieldBlockMessage.STREAM_CODEC);
        serverbound(ConfigurationVariableScopeRequestMessage.TYPE,
                ConfigurationVariableScopeRequestMessage.STREAM_CODEC,
                ConfigurationVariableScopeRequestMessage::receive);
        clientboundType(ConfigurationVariableScopeReplyMessage.TYPE,
                ConfigurationVariableScopeReplyMessage.STREAM_CODEC);
    }

    public static boolean isConnected(ServerPlayer player) {
        return player != null && player.connection != null
                && player.connection.getConnection() != null
                && player.connection.getConnection().isConnected();
    }

    /** The payload owns its bytes; encoding occurs on the connection event loop. */
    public static final class PreparedModelPacket {
        private final ModelChunkMessage payload;

        private PreparedModelPacket(ModelChunkMessage payload) {
            this.payload = payload;
        }

        public ModelChunkMessage payload() {
            return payload;
        }
    }

    /** Called on model workers; never reads game state or copies data on the server thread. */
    public static PreparedModelPacket prepareModelChunk(ModelChunkMessage message) {
        return new PreparedModelPacket(message);
    }

    public static void sendPreparedModelChunk(ServerPlayer player, PreparedModelPacket packet) {
        toPlayer(player, packet.payload);
    }

    /** Sends only to connected clients that registered this compatibility channel. */
    public static void toPlayer(ServerPlayer player, CustomPacketPayload message) {
        if (isConnected(player) && ServerPlayNetworking.canSend(player, message.type())) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public static void toTrackersAndSelf(Player player, CustomPacketPayload message) {
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        for (ServerPlayer tracker : PlayerLookup.tracking(player)) {
            toPlayer(tracker, message);
        }
        if (player instanceof ServerPlayer self) {
            toPlayer(self, message);
        }
    }

    public static void toTrackers(Entity entity, CustomPacketPayload message) {
        if (!(entity.level() instanceof net.minecraft.server.level.ServerLevel)) {
            return;
        }
        for (ServerPlayer tracker : PlayerLookup.tracking(entity)) {
            toPlayer(tracker, message);
        }
    }

    public static void toServer(CustomPacketPayload message) {
        clientSender.accept(message);
    }

    public static void sendConfigurationUpdate(ConfigurationVariableUpdateMessage message) {
        toServer(message);
    }

    public static void requestConfigurationScope(ConfigurationVariableScopeRequestMessage message) {
        toServer(message);
    }

    public static void sendHeldItemPreferences(HeldItemModelDisplayState state) {
        toServer(new HeldItemPreferenceUpdateMessage(state));
    }

    public static void sendMovementAnimationPreferences(
            MovementAnimationDisplayState state) {
        toServer(new MovementAnimationPreferenceUpdateMessage(state));
    }

    public static void sendMaidPreferences(MaidPreferenceUpdateMessage message) {
        toServer(message);
    }

    public static void sendMaidMovementPreferences(
            MaidMovementPreferenceUpdateMessage message) {
        toServer(message);
    }

    public static void sendSubEntityPreferences(
            SubEntityPreferenceUpdateMessage message) {
        toServer(message);
    }

    public static void sendOwnerPreferenceEpoch(
            UUID heldItemEpoch, UUID movementEpoch) {
        toServer(new OwnerPreferenceEpochMessage(
                heldItemEpoch, movementEpoch));
    }
}
