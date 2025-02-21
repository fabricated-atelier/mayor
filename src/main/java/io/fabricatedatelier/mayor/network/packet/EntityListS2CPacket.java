package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.entity.villager.access.Worker;
import io.fabricatedatelier.mayor.screen.MayorVillageScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public record EntityListS2CPacket(List<Integer> entityList, List<BlockPos> targetPositions) implements CustomPayload {

    public static final CustomPayload.Id<EntityListS2CPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("entity_list_s2c_packet"));

    public static final PacketCodec<RegistryByteBuf, EntityListS2CPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeCollection(value.entityList, PacketByteBuf::writeInt);
        buf.writeCollection(value.targetPositions, (bufx, pos) -> bufx.writeBlockPos(pos));
    }, buf -> new EntityListS2CPacket(buf.readList(PacketByteBuf::readInt), buf.readList(bufx -> bufx.readBlockPos())));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        if (context.client().currentScreen instanceof MayorVillageScreen mayorVillageScreen) {
            List<Object> objects = new ArrayList<>();
            List<Text> texts = new ArrayList<>();
            for (int i = 0; i < this.entityList().size(); i++) {
                if (context.player().getWorld().getEntityById(this.entityList().get(i)) instanceof VillagerEntity villagerEntity && villagerEntity instanceof Worker worker) {
                    objects.add(villagerEntity);
                    texts.add(villagerEntity.getName());
                    worker.setTargetPosition(this.targetPositions().get(i));
                }
            }

            mayorVillageScreen.getVillagerScrollableWidget().setObjects(objects, texts);
        }
    }
}


