package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.util.ScreenHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record DeskMayorScreenPacket(BlockPos deskPos, String villageName, int villageLevel, boolean mayor, int taxAmount, int taxInterval, long taxTime, int registrationFee,
                                    int villagerCount, int funds, int foundingCost, Map<UUID, String> registeredCitizens, Map<UUID, String> requestingCitizens,
                                    List<UUID> taxPaidCitizens, List<UUID> taxUnpaidCitizens) implements CustomPayload {

    public static final CustomPayload.Id<DeskMayorScreenPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("desk_mayor_screen_packet"));

    public static final PacketCodec<RegistryByteBuf, DeskMayorScreenPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.deskPos);
        buf.writeString(value.villageName);
        buf.writeInt(value.villageLevel);
        buf.writeBoolean(value.mayor);
        buf.writeInt(value.taxAmount);
        buf.writeInt(value.taxInterval);
        buf.writeLong(value.taxTime);
        buf.writeInt(value.registrationFee);
        buf.writeInt(value.villagerCount);
        buf.writeInt(value.funds);
        buf.writeInt(value.foundingCost);
        buf.writeMap(value.registeredCitizens, (bufx, uuid) -> bufx.writeUuid(uuid), PacketByteBuf::writeString);
        buf.writeMap(value.requestingCitizens, (bufx, uuid) -> bufx.writeUuid(uuid), PacketByteBuf::writeString);
        buf.writeCollection(value.taxPaidCitizens, (bufx, uuid) -> bufx.writeUuid(uuid));
        buf.writeCollection(value.taxUnpaidCitizens, (bufx, uuid) -> bufx.writeUuid(uuid));
    }, buf -> new DeskMayorScreenPacket(buf.readBlockPos(), buf.readString(), buf.readInt(), buf.readBoolean(), buf.readInt(), buf.readInt(), buf.readLong(),
            buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readMap((bufx) -> bufx.readUuid(),
            PacketByteBuf::readString), buf.readMap((bufx) -> bufx.readUuid(), PacketByteBuf::readString), buf.readList(bufx -> bufx.readUuid()), buf.readList(bufx -> bufx.readUuid())));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ScreenHelper.openDeskMayorScreen(context.client(), this.deskPos(), this.villageName(), this.villageLevel(), this.mayor(), this.taxAmount(), this.taxInterval(), this.taxTime(), this.registrationFee(), this.villagerCount(), this.funds(), this.foundingCost(), this.registeredCitizens(), this.requestingCitizens(), this.taxPaidCitizens(), this.taxUnpaidCitizens());
    }
}


