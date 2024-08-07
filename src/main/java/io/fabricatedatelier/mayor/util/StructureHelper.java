package io.fabricatedatelier.mayor.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.access.StructureTemplateAccess;
import io.fabricatedatelier.mayor.init.Tags;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StructureHelper {

    public static Optional<StructureTemplate> getStructureTemplate(ServerWorld serverWorld, Identifier identifier) {
        StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();
        return structureTemplateManager.getTemplate(identifier);
    }

    public static Map<BlockPos, BlockState> getBlockPosBlockStateMap(ServerWorld serverWorld, Identifier structureId, BlockRotation structureRotation, boolean center) {
        Map<BlockPos, BlockState> blockMap = new HashMap<>();
        Optional<StructureTemplate> optional = StructureHelper.getStructureTemplate(serverWorld, structureId);
        if (optional.isPresent() && optional.get() instanceof StructureTemplateAccess structureTemplateAccess) {
            int centerX = 0;
            int centerZ = 0;
            if (center) {
                centerX = optional.get().getSize().getX() / 2;
                centerZ = optional.get().getSize().getZ() / 2;
            }
            for (int i = 0; i < structureTemplateAccess.getBlockInfoLists().getFirst().getAll().size(); i++) {
                StructureBlockInfo structureBlockInfo = structureTemplateAccess.getBlockInfoLists().getFirst().getAll().get(i);
                if (structureBlockInfo.state().isAir()) {
                    // maybe sync air too?
                    continue;
                }
                BlockState blockState = structureBlockInfo.state().rotate(structureRotation);
                if (structureBlockInfo.state().isOf(Blocks.JIGSAW) && structureBlockInfo.nbt() != null) {
                    String string = structureBlockInfo.nbt().getString("final_state");
                    try {
                        blockState = BlockArgumentParser.block(serverWorld.createCommandRegistryWrapper(RegistryKeys.BLOCK), string, true).blockState();
                    } catch (CommandSyntaxException var15) {
                        Mayor.LOGGER.error("Error while parsing blockstate {} @ {}", string, structureBlockInfo.pos());
                    }
                }
                BlockPos pos = StructureTemplate.transformAround(structureBlockInfo.pos().up().west(centerX).north(centerZ), BlockMirror.NONE, structureRotation, BlockPos.ORIGIN);

                blockMap.put(pos, blockState);
            }
        }
        return blockMap;
    }

    public static Map<BlockPos, NbtCompound> getBlockPosNbtMap(ServerWorld serverWorld, Identifier structureId, BlockRotation structureRotation, boolean center) {
        Map<BlockPos, NbtCompound> blockMap = new HashMap<>();
        Iterator<Map.Entry<BlockPos, BlockState>> iterator = getBlockPosBlockStateMap(serverWorld, structureId, structureRotation, center).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, BlockState> entry = iterator.next();
            blockMap.put(entry.getKey(), NbtHelper.fromBlockState(entry.getValue()));
        }
        return blockMap;
    }

    public static boolean updateMayorStructure(ServerPlayerEntity serverPlayerEntity, Identifier structureId, BlockRotation structureRotation, boolean center) {
        Map<BlockPos, NbtCompound> blockMap = StructureHelper.getBlockPosNbtMap(serverPlayerEntity.getServerWorld(), structureId, structureRotation, center);
        if (!blockMap.isEmpty()) {
            MayorManager mayorManager = ((MayorManagerAccess) serverPlayerEntity).getMayorManager();
            mayorManager.setStructureId(structureId);
            mayorManager.setStructureRotation(structureRotation);
            new StructurePacket(structureId, blockMap, structureRotation).sendPacket(serverPlayerEntity);
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
            case BlockRotation.CLOCKWISE_180 ->
                    rotateLeft ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90;
            case BlockRotation.COUNTERCLOCKWISE_90 -> rotateLeft ? BlockRotation.CLOCKWISE_180 : BlockRotation.NONE;
            default -> BlockRotation.NONE;
        };
    }

    public static BlockPos moveOrigin(BlockPos origin, int keyCode, Direction viewDirection) {
        return switch (viewDirection.getHorizontal()) {
            case 0 -> switch (keyCode) {
                case 0 -> origin.west();
                case 1 -> origin.east();
                case 2 -> origin.north();
                case 3 -> origin.south();
                default -> origin;
            };
            case 1 -> switch (keyCode) {
                case 0 -> origin.north();
                case 1 -> origin.south();
                case 2 -> origin.east();
                case 3 -> origin.west();
                default -> origin;
            };
            case 2 -> switch (keyCode) {
                case 0 -> origin.east();
                case 1 -> origin.west();
                case 2 -> origin.south();
                case 3 -> origin.north();
                default -> origin;
            };
            case 3 -> switch (keyCode) {
                case 0 -> origin.south();
                case 1 -> origin.north();
                case 2 -> origin.west();
                case 3 -> origin.east();
                default -> origin;
            };
            default -> origin;
        };
    }

    public static List<ItemStack> getStructureItemRequirements(ServerWorld serverWorld, Identifier structureId) {
        List<ItemStack> requiredItemStacks = new ArrayList<>();
        Map<BlockPos, NbtCompound> blockMap = StructureHelper.getBlockPosNbtMap(serverWorld, structureId, BlockRotation.NONE, false);

        RegistryEntryLookup<Block> blockLookup = serverWorld.createCommandRegistryWrapper(RegistryKeys.BLOCK);

        for (var entry : blockMap.entrySet()) {
            ItemStack itemStack = new ItemStack(NbtHelper.toBlockState(blockLookup, entry.getValue()).getBlock().asItem());
            if (itemStack.isIn(Tags.Items.MAYOR_STRUCTURE_EXCLUDED)) {
                continue;
            }
            if (requiredItemStacks.isEmpty()) requiredItemStacks.add(itemStack);
            else {
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

    public static BlockPos getBottomCenterPos(StructurePiece structurePiece) {
        return new BlockPos(structurePiece.getCenter().getX(), structurePiece.getBoundingBox().getMinY(), structurePiece.getCenter().getZ());
    }

    public static MayorCategory.BiomeCategory getBiomeCategory(RegistryEntry<Biome> biome){
        if (biome.isIn(BiomeTags.VILLAGE_DESERT_HAS_STRUCTURE)) {
            return MayorCategory.BiomeCategory.DESERT;
        } else if (biome.isIn(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE)) {
            return MayorCategory.BiomeCategory.PLAINS;
        } else if (biome.isIn(BiomeTags.VILLAGE_SAVANNA_HAS_STRUCTURE)) {
            return MayorCategory.BiomeCategory.SAVANNA;
        } else if (biome.isIn(BiomeTags.VILLAGE_SNOWY_HAS_STRUCTURE)) {
            return MayorCategory.BiomeCategory.SNOWY;
        } else if (biome.isIn(BiomeTags.VILLAGE_TAIGA_HAS_STRUCTURE)) {
            return MayorCategory.BiomeCategory.TAIGA;
        }
        return MayorCategory.BiomeCategory.PLAINS;
    }

    public static MayorCategory.BiomeCategory getBiomeCategory(Identifier structureIdentifier) {
        String string = structureIdentifier.getPath();
        if (string.contains("/desert/")) {
            return MayorCategory.BiomeCategory.DESERT;
        } else if (string.contains("/plains/")) {
            return MayorCategory.BiomeCategory.PLAINS;
        } else if (string.contains("/savanna/")) {
            return MayorCategory.BiomeCategory.SAVANNA;
        } else if (string.contains("/snowy/")) {
            return MayorCategory.BiomeCategory.SNOWY;
        } else if (string.contains("/taiga/")) {
            return MayorCategory.BiomeCategory.TAIGA;
        }
        return MayorCategory.BiomeCategory.PLAINS;
    }

    public static MayorCategory.BuildingCategory getBuildingCategory(Identifier structureIdentifier) {
        String string = structureIdentifier.getPath();
        if (string.contains("animal_pen") || string.contains("stable")) {
            return MayorCategory.BuildingCategory.BARN;
        } else if (string.contains("house")) {
            return MayorCategory.BuildingCategory.HOUSE;
        } else if (string.contains("fountain")) {
            return MayorCategory.BuildingCategory.FOUNTAIN;
        } else if (string.contains("armorer")) {
            return MayorCategory.BuildingCategory.ARMORER;
        } else if (string.contains("farm")) {
            return MayorCategory.BuildingCategory.FARMER;
        } else if (string.contains("butcher")) {
            return MayorCategory.BuildingCategory.BUTCHER;
        } else if (string.contains("cartographer")) {
            return MayorCategory.BuildingCategory.CARTOGRAPHER;
        } else if (string.contains("fisher")) {
            return MayorCategory.BuildingCategory.FISHER;
        } else if (string.contains("fletcher")) {
            return MayorCategory.BuildingCategory.FLETCHER;
        } else if (string.contains("library")) {
            return MayorCategory.BuildingCategory.LIBRARY;
        } else if (string.contains("tool_smith") || string.contains("weaponsmith")) {
            return MayorCategory.BuildingCategory.SMITH;
        } else if (string.contains("mason")) {
            return MayorCategory.BuildingCategory.MASON;
        } else if (string.contains("shepherd")) {
            return MayorCategory.BuildingCategory.SHEPHERD;
        } else if (string.contains("tannery")) {
            return MayorCategory.BuildingCategory.TANNERY;
        } else if (string.contains("temple")) {
            return MayorCategory.BuildingCategory.TEMPLE;
        }
        return MayorCategory.BuildingCategory.HOUSE;
    }

}
