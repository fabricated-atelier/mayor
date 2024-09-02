package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.state.VillageData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public record VillageDataPacket(BlockPos centerPos, String biomeCategory, int level, String name, long age, Optional<UUID> mayorPlayerUuid, long mayorPlayerTime,
                                List<BlockPos> storageOriginBlockPosList, List<UUID> villagers, List<UUID> ironGolems, Map<BlockPos, StructureData> structures) implements CustomPayload {

    public static final CustomPayload.Id<VillageDataPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("village_data_packet"));

    public static final PacketCodec<RegistryByteBuf, VillageDataPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeBlockPos(value.centerPos);
        buf.writeString(value.biomeCategory);
        buf.writeInt(value.level);
        buf.writeString(value.name);
        buf.writeLong(value.age);
        buf.writeOptional(value.mayorPlayerUuid, (bufx, uuid) -> bufx.writeUuid(uuid));
        buf.writeLong(value.mayorPlayerTime);
        buf.writeCollection(value.storageOriginBlockPosList, (bufx, pos) -> bufx.writeBlockPos(pos));
        buf.writeCollection(value.villagers, (bufx, uuid) -> bufx.writeUuid(uuid));
        buf.writeCollection(value.ironGolems, (bufx, uuid) -> bufx.writeUuid(uuid));
        buf.writeMap(value.structures, (buffer, pos) -> buffer.writeBlockPos(pos), (buffer, data) -> buffer.writeNbt(data.writeDataToNbt()));
    }, buf -> new VillageDataPacket(buf.readBlockPos(), buf.readString(), buf.readInt(), buf.readString(), buf.readLong(), buf.readOptional(bufx -> bufx.readUuid()), buf.readLong(), buf.readList((bufx) -> bufx.readBlockPos()), buf.readList(bufx -> bufx.readUuid()),
            buf.readList(bufx -> bufx.readUuid()), buf.readMap(BlockPos.PACKET_CODEC::decode, (bufx) -> new StructureData(PacketByteBuf.readNbt(bufx)))));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }


    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();

        BlockPos centerPos = this.centerPos();
        MayorCategory.BiomeCategory biomeCategory = MayorCategory.BiomeCategory.valueOf(this.biomeCategory());
        int level = this.level();
        String name = this.name();
        long age = this.age();
        UUID mayorPlayerUuid = this.mayorPlayerUuid().orElse(null);
        long mayorPlayerTime = this.mayorPlayerTime();
        List<BlockPos> storageOriginBlockPosList = this.storageOriginBlockPosList();
        List<UUID> villagers = this.villagers();
        List<UUID> ironGolems = this.ironGolems();
        Map<BlockPos, StructureData> structures = this.structures();
        Map<BlockPos, ConstructionData> constructions = new HashMap<>();

        VillageData villageData = new VillageData(centerPos, biomeCategory, level, name, age, mayorPlayerUuid, mayorPlayerTime, storageOriginBlockPosList, villagers, ironGolems, structures, constructions);
        mayorManager.setVillageData(villageData);
        mayorManager.setBiomeCategory(biomeCategory);
    }
}
