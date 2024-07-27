package io.fabricatedatelier.mayor.util;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryEntryLookup;
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
    }

    public static Map<BlockPos, NbtCompound> getBlockMap(ServerWorld serverWorld, Identifier structureId, BlockRotation structureRotation) {
        Map<BlockPos, NbtCompound> blockMap = new HashMap<BlockPos, NbtCompound>();
        Optional<StructureTemplate> optional = StructureHelper.getStructureTemplate(serverWorld, structureId);
        if (optional.isPresent() && optional.get() instanceof StructureTemplateAccess structureTemplateAccess) {
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
                        blockState = BlockArgumentParser.block(serverWorld.createCommandRegistryWrapper(RegistryKeys.BLOCK), string, true).blockState();
                    } catch (CommandSyntaxException var15) {
                        Mayor.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo.pos());
                    }
                }
                BlockPos pos = StructureTemplate.transformAround(structureBlockInfo.pos().up(), BlockMirror.NONE, structureRotation, BlockPos.ORIGIN);

                blockMap.put(pos, NbtHelper.fromBlockState(blockState));
            }
        }
        return blockMap;
    }

    public static boolean updateMajorStructure(ServerPlayerEntity serverPlayerEntity, Identifier structureId, BlockRotation structureRotation) {
        Map<BlockPos, NbtCompound> blockMap = StructureHelper.getBlockMap(serverPlayerEntity.getServerWorld(), structureId, structureRotation);
        if (!blockMap.isEmpty()) {
            MayorManager mayorManager = ((MayorManagerAccess) serverPlayerEntity).getMayorManager();
            mayorManager.setStructureId(structureId);
            mayorManager.setStructureRotation(structureRotation);
            ServerPlayNetworking.send(serverPlayerEntity, new StructurePacket(structureId, blockMap, structureRotation));
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
        return switch (structureRotation) {
        case 0 -> BlockRotation.NONE;
        case 1 -> BlockRotation.CLOCKWISE_90;
        case 2 -> BlockRotation.CLOCKWISE_180;
        case 3 -> BlockRotation.COUNTERCLOCKWISE_90;
        default -> BlockRotation.NONE;
        };
    }

    public static BlockRotation getRotatedStructureRotation(BlockRotation structureRotation, boolean rotateLeft) {
        return switch (structureRotation) {
        case BlockRotation.NONE -> rotateLeft ? BlockRotation.COUNTERCLOCKWISE_90 : BlockRotation.CLOCKWISE_90;
        case BlockRotation.CLOCKWISE_90 -> rotateLeft ? BlockRotation.NONE : BlockRotation.CLOCKWISE_180;
        case BlockRotation.CLOCKWISE_180 -> rotateLeft ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90;
        case BlockRotation.COUNTERCLOCKWISE_90 -> rotateLeft ? BlockRotation.CLOCKWISE_180 : BlockRotation.NONE;
        default -> BlockRotation.NONE;
        };

    }

    public static List<ItemStack> getStructureItemRequirements(ServerWorld serverWorld, Identifier structureId) {
        List<ItemStack> requiredItemStacks = new ArrayList<ItemStack>();
        Map<BlockPos, NbtCompound> blockMap = StructureHelper.getBlockMap(serverWorld, structureId, BlockRotation.NONE);

        RegistryEntryLookup<Block> blockLookup = serverWorld.createCommandRegistryWrapper(RegistryKeys.BLOCK);

        Iterator<Map.Entry<BlockPos, NbtCompound>> iterator = blockMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, NbtCompound> entry = iterator.next();

            ItemStack itemStack = new ItemStack(NbtHelper.toBlockState(blockLookup, entry.getValue()).getBlock().asItem());
            if (requiredItemStacks.size() <= 0) {
                requiredItemStacks.add(itemStack);
            } else {
                for (int i = 0; i < requiredItemStacks.size(); i++) {
                    if (requiredItemStacks.get(i).isOf(itemStack.getItem())) {
                        if (requiredItemStacks.get(i).getCount() >= requiredItemStacks.get(i).getMaxCount()) {
                            continue;
                        }
                        itemStack.setCount(requiredItemStacks.get(i).getCount() + 1);
                        requiredItemStacks.remove(i);
                        requiredItemStacks.add(itemStack);
                        break;
                    }
                    if (i == requiredItemStacks.size() - 1) {
                        requiredItemStacks.add(itemStack);
                    }
                }
            }
        }

        return requiredItemStacks;
    }

}
