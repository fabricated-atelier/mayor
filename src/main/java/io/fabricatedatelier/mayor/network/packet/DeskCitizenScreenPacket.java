package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.util.ScreenHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public record DeskCitizenScreenPacket(BlockPos deskPos, String villageName, int villageLevel, String mayorName, boolean citizen, int taxAmount, long taxTime, int registrationFee, int citizenCount,
                                      int villagerCount,
                                      int funds, boolean taxPayed, boolean registered) implements CustomPayload {

    public static final CustomPayload.Id<DeskCitizenScreenPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("desk_citizen_screen_packet"));

    public static final PacketCodec<RegistryByteBuf, DeskCitizenScreenPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.deskPos);
        buf.writeString(value.villageName);
        buf.writeInt(value.villageLevel);
        buf.writeString(value.mayorName);
        buf.writeBoolean(value.citizen);
        buf.writeInt(value.taxAmount);
        buf.writeLong(value.taxTime);
        buf.writeInt(value.registrationFee);
        buf.writeInt(value.citizenCount);
        buf.writeInt(value.villagerCount);
        buf.writeInt(value.funds);
        buf.writeBoolean(value.taxPayed);
        buf.writeBoolean(value.registered);
    }, buf -> new DeskCitizenScreenPacket(buf.readBlockPos(), buf.readString(), buf.readInt(), buf.readString(), buf.readBoolean(), buf.readInt(), buf.readLong(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ScreenHelper.openDeskCitizenScreen(context.client(), this.deskPos(), this.villageName(), this.villageLevel(), this.mayorName(), this.citizen(), this.taxAmount(), this.taxTime(), this.registrationFee(), this.citizenCount(), this.villagerCount(), this.funds(), this.taxPayed(), this.registered());
    }
}


