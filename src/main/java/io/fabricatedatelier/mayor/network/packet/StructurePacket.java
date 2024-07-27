package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.util.MayorManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public record StructurePacket(Identifier structureId, Map<BlockPos, NbtCompound> posCompoundMap,
                              BlockRotation structureRotation) implements CustomPayload {

    public static final CustomPayload.Id<StructurePacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("structure_packet"));

    public static final PacketCodec<RegistryByteBuf, StructurePacket> PACKET_CODEC =
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC, StructurePacket::structureId,
                    PacketCodecs.map(Object2ObjectOpenHashMap::new, BlockPos.PACKET_CODEC, PacketCodecs.UNLIMITED_NBT_COMPOUND), StructurePacket::posCompoundMap,
                    PacketCodecs.BYTE.xmap(index -> BlockRotation.values()[index], blockRotation -> (byte) blockRotation.ordinal()), StructurePacket::structureRotation,
                    StructurePacket::new
            );

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

        Map<BlockPos, BlockState> blockMap = new HashMap<>();
        RegistryEntryLookup<Block> blockLookup = world.createCommandRegistryWrapper(RegistryKeys.BLOCK);

        for (var entry : this.posCompoundMap().entrySet()) {
            blockMap.put(entry.getKey(), NbtHelper.toBlockState(blockLookup, entry.getValue()));
        }

        MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
        mayorManager.setStructureBlockMap(blockMap);
    }
}
