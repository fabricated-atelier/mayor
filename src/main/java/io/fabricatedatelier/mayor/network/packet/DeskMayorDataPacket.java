package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.ScreenHelper;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * code: 0 = villageName, 1 = tax, 2 = tax interval, 3 = registration fee
 */
public record DeskMayorDataPacket(BlockPos deskPos, int code, int value, String villageName) implements CustomPayload {

    public static final CustomPayload.Id<DeskMayorDataPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("desk_mayor_data_packet"));

    public static final PacketCodec<RegistryByteBuf, DeskMayorDataPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.deskPos);
        buf.writeInt(value.code);
        buf.writeInt(value.value);
        buf.writeString(value.villageName);
    }, buf -> new DeskMayorDataPacket(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readString()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ScreenHelper.updateDeskMayorScreen(context.client(), this.code(), this.value(), this.villageName());
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        VillageData villageData = StateHelper.getClosestVillage(context.player().getServerWorld(), context.player().getBlockPos());
        if (villageData != null && villageData.getMayorPlayerUuid() != null && villageData.getMayorPlayerUuid().equals(context.player().getUuid())) {
            if (villageData.getCitizenData().getDeskPos() == null || !villageData.getCitizenData().getDeskPos().equals(this.deskPos())) {
                return;
            }
            if (this.code() == 0) {
                if (!this.villageName().isEmpty()) {
                    villageData.setName(this.villageName());
                }
            } else if (this.code() == 1) {
                villageData.getCitizenData().setTaxAmount(this.value());
            } else if (this.code() == 2) {
                int interval = this.value() * 20 * 60 * 60;
                villageData.getCitizenData().setTaxInterval(interval);
                if (this.value() == 0) {
                    villageData.getCitizenData().setTaxTime(0);
                } else {
                    villageData.getCitizenData().setTaxTime(context.player().getServerWorld().getTime() + interval);
                }
            } else if (this.code() == 3) {
                villageData.getCitizenData().setRegistrationFee(this.value());
            }
            StateHelper.getMayorVillageState(context.player().getServerWorld()).markDirty();
            new DeskMayorDataPacket(this.deskPos(), this.code(), this.value(), villageData.getName()).sendPacket(context.player());
        }
    }
}

