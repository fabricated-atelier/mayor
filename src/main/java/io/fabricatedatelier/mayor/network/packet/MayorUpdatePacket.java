package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.VillageHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.UUID;

public record MayorUpdatePacket(BlockPos centerPos, int villageLevel, UUID mayorUuid, boolean dismiss) implements CustomPayload {

    public static final CustomPayload.Id<MayorUpdatePacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("mayor_update_packet"));


    public static final PacketCodec<RegistryByteBuf, MayorUpdatePacket> PACKET_CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, MayorUpdatePacket::centerPos, PacketCodecs.INTEGER, MayorUpdatePacket::villageLevel, Uuids.PACKET_CODEC, MayorUpdatePacket::mayorUuid, PacketCodecs.BOOL, MayorUpdatePacket::dismiss, MayorUpdatePacket::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket() {
        ClientPlayNetworking.send(this);
    }

    public void handlePacket(ServerPlayNetworking.Context context) {
        MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(context.player().getServerWorld());
        if (mayorVillageState.hasVillage(this.centerPos())) {
            if (this.dismiss() && !this.mayorUuid().equals(context.player().getUuid())) {
                return;
            }
            mayorVillageState.getVillageData(this.centerPos()).setMayorPlayerUuid(null);
            mayorVillageState.getVillageData(this.centerPos()).setMayorPlayerTime(0);
            mayorVillageState.markDirty();

            String mayorName = context.player().getName().getString();
            for (ServerPlayerEntity serverPlayerEntity : context.player().getWorld().getEntitiesByClass(ServerPlayerEntity.class, new Box(this.centerPos()).expand(VillageHelper.VILLAGE_LEVEL_RADIUS.get(this.villageLevel())), EntityPredicates.EXCEPT_SPECTATOR)) {
                serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", mayorVillageState.getVillageData(this.centerPos()).getName())));
                if (serverPlayerEntity.getUuid().equals(this.mayorUuid())) {
                    serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.mayor_dismissed_2")));
                } else {
                    serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.mayor_dismissed", mayorName)));
                }
            }
        }
    }
}

