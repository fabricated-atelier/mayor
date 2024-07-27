package io.fabricatedatelier.mayor.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureOriginPacket;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import io.fabricatedatelier.mayor.util.MayorManager;
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
                MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
                mayorManager.setStructureBlockMap(blockMap);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(StructureOriginPacket.PACKET_ID, (payload, context) -> {
            Optional<BlockPos> origin = payload.origin();
            context.client().execute(() -> {
                MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
                if (mayorManager.getOriginBlockPos() != null) {
                    mayorManager.setOriginBlockPos(null);
                } else {
                    mayorManager.setOriginBlockPos(origin.isPresent() ? origin.get() : null);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(MayorViewPacket.PACKET_ID, (payload, context) -> {
            boolean mayorView = payload.mayorView();
            context.client().execute(() -> {
                MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
                mayorManager.setMajorView(mayorView);
            });
        });

        // static initialisation
    }
}