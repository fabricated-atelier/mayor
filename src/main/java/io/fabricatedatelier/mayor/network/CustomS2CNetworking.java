package io.fabricatedatelier.mayor.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import io.fabricatedatelier.mayor.access.MinecraftClientAccess;
import io.fabricatedatelier.mayor.network.packet.OriginBlockPosPacket;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;

public class CustomS2CNetworking {
    static {
        // ClientPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
        // ClientPlayNetworking.registerGlobalReceiver(CustomPacket.IDENTIFIER, CustomPacket::handlePacket);
    }

    public static void initialize() {

        ClientPlayNetworking.registerGlobalReceiver(StructurePacket.PACKET_ID, (payload, context) -> {
            Iterator<Map.Entry<BlockPos, NbtCompound>> iterator = payload.posCompoundMap().entrySet().iterator();
            Map<BlockPos, BlockState> blockMap = new HashMap<BlockPos, BlockState>();
            @SuppressWarnings("resource")
            RegistryEntryLookup<Block> blockLookup = context.client().world.createCommandRegistryWrapper(RegistryKeys.BLOCK);
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, NbtCompound> entry = iterator.next();
                blockMap.put(entry.getKey(), NbtHelper.toBlockState(blockLookup, entry.getValue()));
            }

            context.client().execute(() -> {
                ((MinecraftClientAccess) context.client()).setStructureBlockMap(blockMap);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(OriginBlockPosPacket.PACKET_ID, (payload, context) -> {
            Optional<BlockPos> origin = payload.origin();
            context.client().execute(() -> {
                if (((MinecraftClientAccess) context.client()).getOriginBlockPos() != null) {
                    ((MinecraftClientAccess) context.client()).setOriginBlockPos(null);
                } else {
                    ((MinecraftClientAccess) context.client()).setOriginBlockPos(origin.isPresent() ? origin.get() : null);
                }
            });
        });

        // static initialisation
    }
}