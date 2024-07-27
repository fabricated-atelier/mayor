package io.fabricatedatelier.mayor.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.mixin.access.StructureTemplateAccess;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class StructureHelper {

    public static Optional<StructureTemplate> getStructureTemplate(ServerWorld serverWorld, Identifier identifier) {
        StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();
        Optional<StructureTemplate> structure = structureTemplateManager.getTemplate(identifier);

        return structure;
    };

    public static boolean updateMajorStructure(ServerPlayerEntity serverPlayerEntity, Identifier structureId, BlockRotation structureRotation) {
        Optional<StructureTemplate> optional = StructureHelper.getStructureTemplate(serverPlayerEntity.getServerWorld(), structureId);
        if (optional.isPresent() && optional.get() instanceof StructureTemplateAccess structureTemplateAccess) {

            Map<BlockPos, NbtCompound> blockMap = new HashMap<BlockPos, NbtCompound>();
            if (structureTemplateAccess.getBlockInfoLists().size() > 0) {

                for (int i = 0; i < structureTemplateAccess.getBlockInfoLists().get(0).getAll().size(); i++) {
                    StructureBlockInfo structureBlockInfo = structureTemplateAccess.getBlockInfoLists().get(0).getAll().get(i);
                    if (structureBlockInfo.state().isAir()) {
                        // maybe sync air too?
                        continue;
                    }
                    BlockState blockState = structureBlockInfo.state().rotate(structureRotation);
                    if (structureBlockInfo.state().isOf(Blocks.JIGSAW)) {
                        String string = structureBlockInfo.nbt().getString("final_state");
                        try {
                            blockState = BlockArgumentParser.block(serverPlayerEntity.getServerWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), string, true).blockState();
                        } catch (CommandSyntaxException var15) {
                            Mayor.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo.pos());
                        }
                    }
                    BlockPos pos = StructureTemplate.transformAround(structureBlockInfo.pos().up(), BlockMirror.NONE, structureRotation, BlockPos.ORIGIN);

                    blockMap.put(pos, NbtHelper.fromBlockState(blockState));
                }
                MayorManager mayorManager = ((MayorManagerAccess) serverPlayerEntity).getMayorManager();
                mayorManager.setStructureId(structureId);
                mayorManager.setStructureRotation(structureRotation);
                ServerPlayNetworking.send(serverPlayerEntity, new StructurePacket(structureId, blockMap, structureRotation));
            }

            return true;
        }
        return false;
    }

    @Nullable
    public static BlockHitResult findCrosshairTarget(Entity camera) {
        HitResult hitResult = camera.raycast(300D, 0.0f, false);
        return hitResult instanceof BlockHitResult blockHitResult ? blockHitResult : null;
    }

    public static int getStructureRotation(BlockRotation structureRotation) {
        return structureRotation.ordinal();
    }

    public static BlockRotation getStructureRotation(int structureRotation) {
        switch (structureRotation) {
        case 0:
            return BlockRotation.NONE;
        case 1:
            return BlockRotation.CLOCKWISE_90;
        case 2:
            return BlockRotation.CLOCKWISE_180;
        case 3:
            return BlockRotation.COUNTERCLOCKWISE_90;
        default:
            return BlockRotation.NONE;
        }
    }

    public static BlockRotation getRotatedStructureRotation(BlockRotation structureRotation, boolean rotateLeft) {
        switch (structureRotation) {
        case BlockRotation.NONE:
            return rotateLeft ? BlockRotation.COUNTERCLOCKWISE_90 : BlockRotation.CLOCKWISE_90;
        case BlockRotation.CLOCKWISE_90:
            return rotateLeft ? BlockRotation.NONE : BlockRotation.CLOCKWISE_180;
        case BlockRotation.CLOCKWISE_180:
            return rotateLeft ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90;
        case BlockRotation.COUNTERCLOCKWISE_90:
            return rotateLeft ? BlockRotation.CLOCKWISE_180 : BlockRotation.NONE;
        default:
            return BlockRotation.NONE;
        }

    }

}
