package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.util.StructureHelper;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public record MayorStructuresPacket(MayorStructureDatas mayorStructures) implements CustomPayload {

    public static final CustomPayload.Id<MayorStructuresPacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("mayor_structure_packet"));


    public static final PacketCodec<RegistryByteBuf, MayorStructuresPacket> PACKET_CODEC = PacketCodec.of(MayorStructuresPacket::write, MayorStructuresPacket::new);

    private MayorStructuresPacket(RegistryByteBuf buf) {
        this(new MayorStructureDatas(buf));
    }

    private void write(RegistryByteBuf buf) {
        this.mayorStructures.write(buf);
    }

    public record MayorStructureDatas(int mayorStructureCount, List<MayorStructureData> mayorStructureDatas) {
        private MayorStructureDatas(RegistryByteBuf buf) {
            this(0, Stream.generate(() -> {
                return new MayorStructureData(buf);
            }).limit(buf.readInt()).toList());
        }

        public void write(RegistryByteBuf buf) {
            buf.writeInt(this.mayorStructureCount);
            for (int i = 0; i < this.mayorStructureDatas.size(); i++) {
                this.mayorStructureDatas.get(i).write(buf);
            }
        }
    }

    public record MayorStructureData(Identifier structureId, int level, String biomeCategory, String buildingCategory, List<ItemStack> requiredItemStacks,
                                     Map<BlockPos, NbtCompound> posCompoundMap, Vec3i size) {

        private MayorStructureData(RegistryByteBuf buf) {
            this(buf.readIdentifier(), buf.readInt(), buf.readString(), buf.readString(), ItemStack.LIST_PACKET_CODEC.decode(buf), buf.readMap(BlockPos.PACKET_CODEC::decode, (bufx) -> PacketByteBuf.readNbt(bufx)), readVec3i(buf));
        }

        public void write(RegistryByteBuf buf) {
            buf.writeIdentifier(this.structureId);
            buf.writeInt(this.level);
            buf.writeString(this.biomeCategory);
            buf.writeString(this.buildingCategory);
            ItemStack.LIST_PACKET_CODEC.encode(buf, this.requiredItemStacks);
            buf.writeMap(this.posCompoundMap, (buffer, pos) -> buffer.writeBlockPos(pos), (buffer, nbt) -> buffer.writeNbt(nbt));
            writeVec3i(buf, this.size);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }


    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        if (context == null || context.player() == null) return;
        World world = context.player().getWorld();

        MayorManager.mayorStructureMap.clear();

        for (int i = 0; i < this.mayorStructures().mayorStructureDatas().size(); i++) {
            MayorStructureData mayorStructureData = this.mayorStructures().mayorStructureDatas().get(i);

            Identifier identifier = mayorStructureData.structureId();
            int level = mayorStructureData.level();
            MayorCategory.BiomeCategory biomeCategory = MayorCategory.BiomeCategory.valueOf(mayorStructureData.biomeCategory());
            MayorCategory.BuildingCategory buildingCategory = MayorCategory.BuildingCategory.valueOf(mayorStructureData.buildingCategory());
            List<ItemStack> requiredItemStacks = mayorStructureData.requiredItemStacks();
            Map<BlockPos, BlockState> blockMap = StructureHelper.getBlockPosBlockStateMap(world, mayorStructureData.posCompoundMap());
            Vec3i size = mayorStructureData.size();

            MayorStructure mayorStructure = new MayorStructure(identifier, level, biomeCategory, buildingCategory, requiredItemStacks, blockMap, size);

            if (MayorManager.mayorStructureMap.containsKey(biomeCategory)) {
                MayorManager.mayorStructureMap.get(biomeCategory).add(mayorStructure);
            } else {
                List<MayorStructure> list = new ArrayList<>();
                list.add(mayorStructure);
                MayorManager.mayorStructureMap.put(biomeCategory, list);
            }

        }

    }

    public static Vec3i readVec3i(ByteBuf buf) {
        return new Vec3i(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void writeVec3i(ByteBuf buf, Vec3i vector) {
        buf.writeInt(vector.getX());
        buf.writeInt(vector.getY());
        buf.writeInt(vector.getZ());
    }


}

