package io.fabricatedatelier.mayor.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.access.StructureTemplateAccess;
import io.fabricatedatelier.mayor.entity.access.Builder;
import io.fabricatedatelier.mayor.init.Tags;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import io.fabricatedatelier.mayor.network.packet.VillageDataPacket;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.StructureData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
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
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
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
                if (structureBlockInfo.state().isAir() || structureBlockInfo.state().isIn(Tags.Blocks.MAYOR_STRUCTURE_EXCLUDED)) {
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

    @Deprecated
    public static Map<BlockPos, NbtCompound> getBlockPosNbtMap(ServerWorld serverWorld, Identifier structureId, BlockRotation structureRotation, boolean center) {
        Map<BlockPos, NbtCompound> blockMap = new HashMap<>();
        for (Map.Entry<BlockPos, BlockState> entry : getBlockPosBlockStateMap(serverWorld, structureId, structureRotation, center).entrySet()) {
            blockMap.put(entry.getKey(), NbtHelper.fromBlockState(entry.getValue()));
        }
        return blockMap;
    }

    public static Map<BlockPos, BlockState> getBlockPosBlockStateMap(World world, Map<BlockPos, NbtCompound> blockPosNbtCompoundMap) {
        Map<BlockPos, BlockState> blockMap = new HashMap<>();
        RegistryEntryLookup<Block> blockLookup = world.createCommandRegistryWrapper(RegistryKeys.BLOCK);

        for (var entry : blockPosNbtCompoundMap.entrySet()) {
            blockMap.put(entry.getKey(), NbtHelper.toBlockState(blockLookup, entry.getValue()));
        }
        return blockMap;
    }

    public static Map<BlockPos, NbtCompound> getBlockPosNbtMap(Map<BlockPos, BlockState> blockPosNbtCompoundMap) {
        Map<BlockPos, NbtCompound> blockMap = new HashMap<>();

        for (var entry : blockPosNbtCompoundMap.entrySet()) {
            blockMap.put(entry.getKey(), NbtHelper.fromBlockState(entry.getValue()));
        }
        return blockMap;
    }

    public static Vec3i getStructureSize(ServerWorld serverWorld, Identifier structureId) {
        Optional<StructureTemplate> optional = StructureHelper.getStructureTemplate(serverWorld, structureId);
        if (optional.isPresent()) {
            return optional.get().getSize();
        }
        return Vec3i.ZERO;
    }

    // Unused
    @Deprecated
    public static boolean updateMayorStructure(ServerPlayerEntity serverPlayerEntity, Identifier structureId, BlockRotation structureRotation, boolean center) {
        MayorManager mayorManager = ((MayorManagerAccess) serverPlayerEntity).getMayorManager();
        List<MayorStructure> list = MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIdentifier().equals(structureId)) {
                mayorManager.setMayorStructure(list.get(i));

                mayorManager.setStructureRotation(structureRotation);

                new StructurePacket(structureId, structureRotation, center).sendPacket(serverPlayerEntity);
                return true;
            }
            if (i == list.size() - 1) {
                mayorManager.setMayorStructure(null);
            }
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
            case BlockRotation.CLOCKWISE_180 -> rotateLeft ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90;
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
        Map<BlockPos, BlockState> blockMap = StructureHelper.getBlockPosBlockStateMap(serverWorld, structureId, BlockRotation.NONE, false);

        for (var entry : blockMap.entrySet()) {
            if (entry.getValue().contains(Properties.DOUBLE_BLOCK_HALF) && entry.getValue().get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                continue;
            }
            if (entry.getValue().contains(Properties.BED_PART) && entry.getValue().get(Properties.BED_PART) == BedPart.FOOT) {
                continue;
            }
            ItemStack itemStack = new ItemStack(entry.getValue().getBlock().asItem());
            if (itemStack.isIn(Tags.Items.MAYOR_STRUCTURE_EXCLUDED)) {
                continue;
            }
            if (requiredItemStacks.isEmpty()) {
                requiredItemStacks.add(itemStack);
            } else {
                for (int i = 0; i < requiredItemStacks.size(); i++) {
                    if (requiredItemStacks.get(i).isOf(itemStack.getItem())) {
                        if (requiredItemStacks.get(i).getCount() >= requiredItemStacks.get(i).getMaxCount()) {
                            continue;
                        }
                        requiredItemStacks.get(i).setCount(requiredItemStacks.get(i).getCount() + 1);
                        break;
                    }
                    if (i == requiredItemStacks.size() - 1) {
                        requiredItemStacks.add(itemStack);
                        break;
                    }
                }
            }
        }

        return requiredItemStacks;
    }

    public static List<ItemStack> getStructureItems(World world, BlockBox blockBox) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < blockBox.getBlockCountX(); i++) {
            for (int u = 0; u < blockBox.getBlockCountZ(); u++) {
                for (int o = 0; o < blockBox.getBlockCountY(); o++) {
                    BlockPos pos = new BlockPos(blockBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ());

                    BlockState blockState = world.getBlockState(pos.add(i, u, o));

                    if (blockState.contains(Properties.DOUBLE_BLOCK_HALF) && blockState.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
                        continue;
                    }
                    if (blockState.contains(Properties.BED_PART) && blockState.get(Properties.BED_PART) == BedPart.FOOT) {
                        continue;
                    }
                    ItemStack itemStack = new ItemStack(blockState.getBlock().asItem());
                    if (itemStack.isIn(Tags.Items.MAYOR_STRUCTURE_EXCLUDED)) {
                        continue;
                    }
                    if (itemStacks.isEmpty()) {
                        itemStacks.add(itemStack);
                    } else {
                        for (int k = 0; k < itemStacks.size(); k++) {
                            if (itemStacks.get(k).isOf(itemStack.getItem())) {
                                if (itemStacks.get(k).getCount() >= itemStacks.get(k).getMaxCount()) {
                                    continue;
                                }
                                itemStacks.get(k).setCount(itemStacks.get(k).getCount() + 1);
                                break;
                            }
                            if (k == itemStacks.size() - 1) {
                                itemStacks.add(itemStack);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return itemStacks;
    }

    public static int getStructureExperience(List<ItemStack> requiredStacks) {
        int experience = 0;
        for (ItemStack requiredStack : requiredStacks) {
            if (!requiredStack.isEmpty()) {
                experience += requiredStack.getCount();
            }
        }
        return experience;
    }

    public static BlockPos getBottomCenterPos(StructurePiece structurePiece) {
        return new BlockPos(structurePiece.getCenter().getX(), structurePiece.getBoundingBox().getMinY(), structurePiece.getCenter().getZ());
    }

    public static BlockPos getBottomCenterPos(BlockPos originBlockPos, Vec3i size, BlockRotation structureRotation, boolean center) {
        BlockPos bottomCenterPos = originBlockPos.mutableCopy().up();
        if (!center) {
            BlockPos rotated = new BlockPos(new Vec3i(size.getX() / 2, 0, size.getZ() / 2)).rotate(structureRotation);
            bottomCenterPos = bottomCenterPos.add(rotated);
        }
        return bottomCenterPos;
    }


    @Nullable
    public static MayorStructure getUpgradeStructure(Identifier currentStructureId, MayorCategory.BiomeCategory biomeCategory) {
        int currentStructureLevel = StringUtil.getStructureLevelByIdentifier(currentStructureId);
        String currentStructureString = StringUtil.getStructureString(currentStructureId);

        for (int i = 0; i < MayorManager.mayorStructureMap.get(biomeCategory).size(); i++) {
            MayorStructure mayorStructure = MayorManager.mayorStructureMap.get(biomeCategory).get(i);
            if (!currentStructureString.equals(StringUtil.getStructureString(mayorStructure.getIdentifier()))) {
                continue;
            }
            if (currentStructureLevel + 1 == StringUtil.getStructureLevelByIdentifier(mayorStructure.getIdentifier())) {
                return mayorStructure;
            }
        }
        return null;
    }

    public static MayorCategory.BiomeCategory getBiomeCategory(RegistryEntry<Biome> biome) {
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
        } else if (string.contains("house")) {
            return MayorCategory.BuildingCategory.HOUSE;
        }
        return MayorCategory.BuildingCategory.HOUSE;
    }

    public static BlockBox getStructureBlockBox(BlockPos originBlockPos, Vec3i size, BlockRotation structureRotation, boolean center) {
        BlockPos bottomCorner = originBlockPos.mutableCopy().up();
        if (center) {
            BlockPos rotated = new BlockPos(new Vec3i(-size.getX() / 2, 0, -size.getZ() / 2)).rotate(structureRotation);
            bottomCorner = bottomCorner.add(rotated);
        }
        BlockPos rotated = new BlockPos(new Vec3i(size.getX() - 1, size.getY(), size.getZ() - 1)).rotate(structureRotation);
        BlockPos topCorner = bottomCorner.mutableCopy().add(rotated);

        int minX = Math.min(bottomCorner.getX(), topCorner.getX());
        int minY = Math.min(bottomCorner.getY(), topCorner.getY());
        int minZ = Math.min(bottomCorner.getZ(), topCorner.getZ());
        int maxX = Math.max(bottomCorner.getX(), topCorner.getX());
        int maxY = Math.max(bottomCorner.getY(), topCorner.getY());
        int maxZ = Math.max(bottomCorner.getZ(), topCorner.getZ());
        return new BlockBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static boolean tryBuildStructure(ServerPlayerEntity serverPlayerEntity, MayorStructure mayorStructure, BlockPos originBlockPos, BlockRotation structureRotation, boolean center) {
        Map<BlockPos, BlockState> blockPosBlockStateMap = mayorStructure.getBlockMap();

        if (!blockPosBlockStateMap.isEmpty()) {
            MayorManager mayorManager = ((MayorManagerAccess) serverPlayerEntity).getMayorManager();

            mayorManager.setMayorStructure(mayorStructure);
            mayorManager.setStructureOriginBlockPos(originBlockPos);
            mayorManager.setStructureRotation(structureRotation);
            mayorManager.setStructureCentered(center);

            if (canPlaceStructure(mayorManager)) {

//                boolean buildStructure = false;

                if (serverPlayerEntity.isCreativeLevelTwoOp() || (mayorManager.getVillageData().getMayorPlayerUuid() != null && mayorManager.getVillageData().getMayorPlayerUuid().equals(serverPlayerEntity.getUuid()) && InventoryUtil.getMissingItems(InventoryUtil.getAvailableItems(mayorManager.getVillageData(), serverPlayerEntity.getServerWorld()), mayorStructure.getRequiredItemStacks()).isEmpty() && VillageHelper.hasTasklessBuildingVillager(mayorManager.getVillageData(), serverPlayerEntity.getServerWorld()))) {
                    BlockPos bottomCenterPos = getBottomCenterPos(originBlockPos, mayorManager.getMayorStructure().getSize(), structureRotation, center);
                    BlockBox blockBox = getStructureBlockBox(originBlockPos, mayorManager.getMayorStructure().getSize(), structureRotation, center);
                    StructureData structureData = new StructureData(bottomCenterPos, blockBox,
                            mayorStructure.getIdentifier(), mayorStructure.getLevel(), mayorStructure.getExperience());

                    if (serverPlayerEntity.isCreativeLevelTwoOp()) {
                        placeBlocks(serverPlayerEntity.getServerWorld(), blockPosBlockStateMap, originBlockPos, mayorManager.getMayorStructure().getSize(), structureRotation, center);
                        mayorManager.getVillageData().addStructure(structureData);
                        VillageHelper.tryLevelUpVillage(mayorManager.getVillageData(), serverPlayerEntity.getServerWorld());
                        // Sync village data to show structure name on hud
                        new VillageDataPacket(mayorManager.getVillageData().getCenterPos(), mayorManager.getVillageData().getBiomeCategory().name(), mayorManager.getVillageData().getLevel(), mayorManager.getVillageData().getName(), mayorManager.getVillageData().getAge(), Optional.ofNullable(mayorManager.getVillageData().getMayorPlayerUuid()), mayorManager.getVillageData().getMayorPlayerTime(), mayorManager.getVillageData().getStorageOriginBlockPosList(), mayorManager.getVillageData().getVillagers(), mayorManager.getVillageData().getIronGolems(), mayorManager.getVillageData().getStructures()).sendPacket(serverPlayerEntity);
                    } else {
                        Builder builder = VillageHelper.getTasklessBuildingVillagerBuilder(mayorManager.getVillageData(), serverPlayerEntity.getServerWorld());
                        ConstructionData constructionData = new ConstructionData(structureData.getBottomCenterPos(), structureData, getBlockPosBlockStateMap(blockPosBlockStateMap, originBlockPos, mayorManager.getMayorStructure().getSize(), structureRotation, center));
                        //    mayorManager.getVillageData().getConstructions().g
                        // Todo: Set multiple target positions to use a random one at the edges of the building

                        builder.setVillageCenterPosition(mayorManager.getVillageData().getCenterPos());
                        builder.setTargetPosition(structureData.getBottomCenterPos());
                        mayorManager.getVillageData().addConstruction(constructionData);
//                        }

                        System.out.println("ASSIGNED "+builder);
                        //                        // Todo: Assign villager to build this mayorStructure here
//                        // add info to villageData that this building is in construction
//                        // remove using items of available items, put in extra inventory for villager to use

                    }
                    MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(serverPlayerEntity.getServerWorld());
                    mayorVillageState.markDirty();

                    mayorManager.setMayorStructure(null);
                    mayorManager.setStructureOriginBlockPos(null);
                    return true;
                }
            }
        }
        return false;
    }

    public static Map<BlockPos, BlockState> getBlockPosBlockStateMap(Map<BlockPos, BlockState> blockPosBlockStateMap, BlockPos origin, Vec3i size, BlockRotation rotation, boolean center) {
        Map<BlockPos, BlockState> blockMap = new HashMap<>();

        for (Map.Entry<BlockPos, BlockState> entry : blockPosBlockStateMap.entrySet()) {
            BlockPos pos = entry.getKey();
            if (center) {
                pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);
            }
            pos = pos.rotate(rotation);
            BlockState state = entry.getValue().rotate(rotation);

            blockMap.put(origin.add(pos), state);
        }

        return blockMap;
    }

    public static void placeBlocks(ServerWorld serverWorld, Map<BlockPos, BlockState> blockPosBlockStateMap, BlockPos origin, Vec3i size, BlockRotation rotation, boolean center) {
        for (Map.Entry<BlockPos, BlockState> entry : blockPosBlockStateMap.entrySet()) {
            BlockPos pos = entry.getKey();
            if (center) {
                pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);
            }
            pos = pos.rotate(rotation);
            BlockState state = entry.getValue().rotate(rotation);

            serverWorld.setBlockState(origin.add(pos), state, 3, 0);
        }
    }

    public static boolean canPlaceStructure(MayorManager mayorManager) {
        if (mayorManager.getVillageData() != null && mayorManager.getMayorStructure() != null) {
            BlockPos origin = mayorManager.getStructureOriginBlockPos();
            if (origin == null) {
                if (StructureHelper.findCrosshairTarget(mayorManager.playerEntity()) instanceof BlockHitResult blockHitResult) {
                    origin = blockHitResult.getBlockPos();
                } else {
                    return false;
                }
            }
            if (!mayorManager.playerEntity().isCreativeLevelTwoOp() && mayorManager.getMayorStructure().getLevel() <= mayorManager.getVillageData().getLevel()) {
                return false;
            }
            BlockPos villageCenterPos = mayorManager.getVillageData().getCenterPos();
            int radius = VillageHelper.VILLAGE_LEVEL_RADIUS.get(mayorManager.getVillageData().getLevel());
            if (!origin.isWithinDistance(villageCenterPos, radius)) {
                return false;
            }
            BlockBox blockBox = getStructureBlockBox(origin, mayorManager.getMayorStructure().getSize(), mayorManager.getStructureRotation(), mayorManager.getStructureCentered());
            if (!villageCenterPos.isWithinDistance(new Vec3i(blockBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ()), radius)) {
                return false;
            }
            if (!villageCenterPos.isWithinDistance(new Vec3i(blockBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ()), radius)) {
                return false;
            }
            if (!mayorManager.getVillageData().getStructures().isEmpty()) {
                for (StructureData structureData : mayorManager.getVillageData().getStructures().values()) {
                    if (structureData.getBlockBox().contains(origin) || structureData.getBlockBox().intersects(blockBox)) {
                        return false;
                    }
                }
            }
            if (!mayorManager.getVillageData().getConstructions().isEmpty()) {
                for (ConstructionData constructionData : mayorManager.getVillageData().getConstructions().values()) {
                    if (constructionData.getStructureData().getBlockBox().contains(origin) || constructionData.getStructureData().getBlockBox().intersects(blockBox)) {
                        return false;
                    }
                }
            }
            // Todo: Give villager the possibility to remove curtain blocks before building a structure
            for (int i = blockBox.getMinX(); i <= blockBox.getMaxX(); i++) {
                for (int u = blockBox.getMinY(); u <= blockBox.getMaxY(); u++) {
                    for (int o = blockBox.getMinZ(); o <= blockBox.getMaxZ(); o++) {
                        BlockState state = mayorManager.playerEntity().getWorld().getBlockState(BlockPos.ofFloored(i, u, o));
                        if (!state.isAir() && !state.isIn(Tags.Blocks.MAYOR_STRUCTURE_REPLACEABLE)) {
                            return false;
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public static Map<BlockPos, BlockState> getMissingConstructionBlockMap(ServerWorld serverWorld, ConstructionData constructionData) {
        Map<BlockPos, BlockState> blockMap = new HashMap<>();
        for (Map.Entry<BlockPos, BlockState> entry : constructionData.getBlockMap().entrySet()) {
            if (!serverWorld.getBlockState(entry.getKey()).equals(entry.getValue())) {
                blockMap.put(entry.getKey(), entry.getValue());
            }
        }
        return blockMap;
    }

    // Todo: void method to place blocks 

}
