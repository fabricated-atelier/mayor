//package io.fabricatedatelier.mayor.network.packet;
//
//import io.fabricatedatelier.mayor.Mayor;
//import io.fabricatedatelier.mayor.access.MayorManagerAccess;
//import io.fabricatedatelier.mayor.manager.MayorManager;
//import io.fabricatedatelier.mayor.util.StructureHelper;
//import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtHelper;
//import net.minecraft.network.RegistryByteBuf;
//import net.minecraft.network.codec.PacketCodec;
//import net.minecraft.network.codec.PacketCodecs;
//import net.minecraft.network.packet.CustomPayload;
//import net.minecraft.registry.RegistryEntryLookup;
//import net.minecraft.registry.RegistryKeys;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.util.BlockRotation;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public record StructurePacket(Identifier structureId, Map<BlockPos, NbtCompound> posCompoundMap,
//                              BlockRotation structureRotation) implements CustomPayload {
//
//    public static final CustomPayload.Id<StructurePacket> PACKET_ID =
//            new CustomPayload.Id<>(Mayor.identifierOf("structure_packet"));
//
//    public static final PacketCodec<RegistryByteBuf, StructurePacket> PACKET_CODEC =
//            PacketCodec.tuple(
//                    Identifier.PACKET_CODEC, StructurePacket::structureId,
//                    PacketCodecs.map(HashMap::new, BlockPos.PACKET_CODEC, PacketCodecs.UNLIMITED_NBT_COMPOUND), StructurePacket::posCompoundMap,
//                    PacketCodecs.BYTE.xmap(index -> BlockRotation.values()[index], blockRotation -> (byte) blockRotation.ordinal()), StructurePacket::structureRotation,
//                    StructurePacket::new
//            );
//
//    @Override
//    public Id<? extends CustomPayload> getId() {
//        return PACKET_ID;
//    }
//
//
//    public void sendPacket(ServerPlayerEntity targetClient) {
//        ServerPlayNetworking.send(targetClient, this);
//    }
//
//    public void handlePacket(ClientPlayNetworking.Context context) {
//        if (context == null || context.player() == null) return;
//        World world = context.player().getWorld();
//
//        Map<BlockPos, BlockState> blockMap =   StructureHelper.getBlockPosBlockStateMap(world,this.posCompoundMap );
//
//        MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
//        mayorManager.setStructureBlockMap(blockMap);
//    }
//}

package io.fabricatedatelier.mayor.network.packet;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.util.StructureHelper;
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
import java.util.List;
import java.util.Map;

public record StructurePacket(Identifier structureId, BlockRotation structureRotation, boolean center) implements CustomPayload {

    public static final CustomPayload.Id<StructurePacket> PACKET_ID =
            new CustomPayload.Id<>(Mayor.identifierOf("structure_packet"));

    public static final PacketCodec<RegistryByteBuf, StructurePacket> PACKET_CODEC =
            PacketCodec.tuple(
                    Identifier.PACKET_CODEC, StructurePacket::structureId,
                    PacketCodecs.BYTE.xmap(index -> BlockRotation.values()[index], blockRotation -> (byte) blockRotation.ordinal()), StructurePacket::structureRotation, PacketCodecs.BOOL, StructurePacket::center,
                    StructurePacket::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }


    public void sendPacket(ServerPlayerEntity targetClient) {
        ServerPlayNetworking.send(targetClient, this);
    }

//    public void sendClientPacket() {
//        ClientPlayNetworking.send(this);
//    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        if (context == null || context.player() == null) return;
        MayorManager mayorManager = ((MayorManagerAccess) context.player()).getMayorManager();
        List<MayorStructure> list = MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdentifier().equals(this.structureId())) {
                mayorManager.setMayorStructure(list.get(i));
                mayorManager.setStructureRotation(this.structureRotation());
                mayorManager.setStructureCentered(this.center());
            }
            if (i == list.size() - 1) {
                mayorManager.setMayorStructure(null);
            }
        }
    }

//    public void handleClientPacket(ServerPlayNetworking.Context context) {
//
//    }
}

