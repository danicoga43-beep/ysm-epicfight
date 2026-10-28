package net.okitsu.ysmepicfightcompat.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.okitsu.ysmepicfightcompat.network.message.AttackSwingSoundMessage;
import net.okitsu.ysmepicfightcompat.network.message.ConfigurationVariableScopeReplyMessage;
import net.okitsu.ysmepicfightcompat.network.message.ConfigurationVariableSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.HeldItemPreferenceSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.MaidMovementPreferenceQueryMessage;
import net.okitsu.ysmepicfightcompat.network.message.MaidPreferenceQueryMessage;
import net.okitsu.ysmepicfightcompat.network.message.MaidPreferenceSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.ModelChunkMessage;
import net.okitsu.ysmepicfightcompat.network.message.MovementAnimationPreferenceSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.ScriptSyncSnapshotMessage;
import net.okitsu.ysmepicfightcompat.network.message.SelectionUpdateMessage;
import net.okitsu.ysmepicfightcompat.network.message.ShieldBlockMessage;
import net.okitsu.ysmepicfightcompat.network.message.SubEntityPreferenceQueryMessage;
import net.okitsu.ysmepicfightcompat.network.message.SubEntityPreferenceSnapshotMessage;
import net.okitsu.ysmepicfightcompat.platform.PayloadContext;

import java.util.function.BiConsumer;

/** Client-bound payload receivers; kept separate so dedicated servers never load client networking. */
@Environment(EnvType.CLIENT)
final class CompatNetworkClient {
    private CompatNetworkClient() {
    }

    private static <T extends CustomPacketPayload> void clientbound(
            CustomPacketPayload.Type<T> type, BiConsumer<T, PayloadContext> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            Minecraft client = context.client();
            handler.accept(payload, new PayloadContext(PacketFlow.CLIENTBOUND, context.player(),
                    client.getConnection() == null ? null : client.getConnection().getConnection(), client));
        });
    }

    static void register() {
        clientbound(SelectionUpdateMessage.TYPE, SelectionUpdateMessage::receive);
        clientbound(ModelChunkMessage.TYPE, ModelChunkMessage::receive);
        clientbound(ConfigurationVariableSnapshotMessage.TYPE, ConfigurationVariableSnapshotMessage::receive);
        clientbound(AttackSwingSoundMessage.TYPE, AttackSwingSoundMessage::receive);
        clientbound(HeldItemPreferenceSnapshotMessage.TYPE, HeldItemPreferenceSnapshotMessage::receive);
        clientbound(MovementAnimationPreferenceSnapshotMessage.TYPE,
                MovementAnimationPreferenceSnapshotMessage::receive);
        clientbound(MaidPreferenceQueryMessage.TYPE, MaidPreferenceQueryMessage::receive);
        clientbound(MaidMovementPreferenceQueryMessage.TYPE, MaidMovementPreferenceQueryMessage::receive);
        clientbound(MaidPreferenceSnapshotMessage.TYPE, MaidPreferenceSnapshotMessage::receive);
        clientbound(SubEntityPreferenceQueryMessage.TYPE, SubEntityPreferenceQueryMessage::receive);
        clientbound(SubEntityPreferenceSnapshotMessage.TYPE, SubEntityPreferenceSnapshotMessage::receive);
        clientbound(ScriptSyncSnapshotMessage.TYPE, ScriptSyncSnapshotMessage::receive);
        clientbound(ShieldBlockMessage.TYPE, ShieldBlockMessage::receive);
        clientbound(ConfigurationVariableScopeReplyMessage.TYPE,
                ConfigurationVariableScopeReplyMessage::receive);
    }
}
