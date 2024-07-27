package io.fabricatedatelier.mayor.network.packet;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

import io.fabricatedatelier.mayor.util.StructureHelper;

// public record StructurePacket(Identifier structureId, Map<BlockPos, NbtCompound> posCompoundMap, Integer structureRotation) implements CustomPayload {

//     public static final CustomPayload.Id<StructurePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("mayor", "structure_packet"));

//     public static final PacketCodec<RegistryByteBuf, StructurePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
//         buf.writeIdentifier(value.structureId);
//         buf.writeMap(value.posCompoundMap, (buffer, pos) -> buffer.writeBlockPos(pos), (buffer, nbt) -> buffer.writeNbt(nbt));
//         buf.writeInt(value.structureRotation);
//     }, buf -> new StructurePacket(buf.readIdentifier(), buf.readMap(BlockPos.PACKET_CODEC::decode, (bufx) -> PacketByteBuf.readNbt(bufx)), buf.readInt()));

//     @Override
//     public Id<? extends CustomPayload> getId() {
//         return PACKET_ID;
//     };
// }

public record StructurePacket(Identifier structureId, Map<BlockPos, NbtCompound> posCompoundMap, BlockRotation structureRotation) implements CustomPayload {

    public static final CustomPayload.Id<StructurePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("mayor", "structure_packet"));

    public static final PacketCodec<RegistryByteBuf, StructurePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeIdentifier(value.structureId);
        buf.writeMap(value.posCompoundMap, (buffer, pos) -> buffer.writeBlockPos(pos), (buffer, nbt) -> buffer.writeNbt(nbt));
        buf.writeInt(StructureHelper.getStructureRotation(value.structureRotation));
    }, buf -> new StructurePacket(buf.readIdentifier(), buf.readMap(BlockPos.PACKET_CODEC::decode, (bufx) -> PacketByteBuf.readNbt(bufx)), StructureHelper.getStructureRotation(buf.readInt())));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    };
}
