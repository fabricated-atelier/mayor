package io.fabricatedatelier.mayor.network.packet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public record StructurePacket(Map<BlockPos, NbtCompound> posCompoundMap) implements CustomPayload {

    public static final CustomPayload.Id<StructurePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("mayor", "structure_packet"));
    // PacketCodecs.entryOf(Block.STATE_IDS)
    // public static final PacketCodec<RegistryByteBuf, StructurePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
    // buf.writeMap(value.blockMap, PacketCodecs.entryOf(Block.STATE_IDS), (buffer, pos) -> buffer.writeBlockPos(pos));
    // }, buf -> new StructurePacket(buf.readMap(PacketCodecs.registryCodec(Block.STATE_IDS), BlockPos.PACKET_CODEC::decode)));

    // public static final PacketCodec<RegistryByteBuf, BlockUpdateS2CPacket> CODEC = PacketCodec.tuple(
    // BlockPos.PACKET_CODEC, BlockUpdateS2CPacket::getPos, PacketCodecs.entryOf(Block.STATE_IDS), BlockUpdateS2CPacket::getState, BlockUpdateS2CPacket::new
    // );

    public static final PacketCodec<RegistryByteBuf, StructurePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeMap(value.posCompoundMap, (buffer, pos) -> buffer.writeBlockPos(pos), (buffer, nbt) -> buffer.writeNbt(nbt));
    }, buf -> new StructurePacket(buf.readMap(BlockPos.PACKET_CODEC::decode, (bufx) -> PacketByteBuf.readNbt(bufx))));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    };
    // NbtHelper.fromBlockState();
}

// public record StructurePacket(Map<BlockState, BlockPos> blockMap) implements CustomPayload {

// public static final CustomPayload.Id<StructurePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("mayor", "structure_packet"));

// public static final PacketCodec<RegistryByteBuf, StructurePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
// buf.writeMap(value.blockMap, PacketByteBuf::writeBlockState, (buffer, pos) -> buffer.writeBlockPos(pos));
// }, buf -> new StructurePacket(buf.readMap(PacketByteBuf::readBlock, BlockPos.PACKET_CODEC::decode)));

// @Override
// public Id<? extends CustomPayload> getId() {
// return PACKET_ID;
// }

// }
