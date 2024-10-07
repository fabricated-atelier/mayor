package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.access.SinglePoolElementAccess;
import io.fabricatedatelier.mayor.access.StructureTemplateAccess;
import io.fabricatedatelier.mayor.config.MayorConfig;
import io.fabricatedatelier.mayor.data.StructureDataLoader;
import io.fabricatedatelier.mayor.init.MayorTags;
import io.fabricatedatelier.mayor.mixin.access.JigsawStructureAccess;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StringUtil;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(StructureStart.class)
public class StructureStartMixin {

    @Shadow
    @Mutable
    @Final
    private Structure structure;
    @Unique
    private BlockPos centerPos = BlockPos.ORIGIN;

    @Inject(method = "place", at = @At("HEAD"))
    private void placeMixin(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator,
                            Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo info) {
        if (!structure.getType().equals(StructureType.JIGSAW)) return;
        if (!(structure instanceof JigsawStructureAccess jigsawStructureAccess)) return;
        if (!jigsawStructureAccess.getStartPool().isIn(MayorTags.StructurePools.VILLAGES)) return;

        MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(world.toServerWorld());
        BlockPos centerPos = ((StructureStart) (Object) this).getBoundingBox().getCenter();
        if (mayorVillageState.hasVillage(centerPos)) return;
        VillageData villageData = mayorVillageState.createVillageData(centerPos);
        if (villageData != null) {
            this.centerPos = centerPos;
        }
    }

    @SuppressWarnings("rawtypes")
    @Inject(method = "place", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/structure/StructurePiece;generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)V",
            shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void placeMixin(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator,
                            Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo info, List list,
                            BlockBox blockBox, BlockPos blockPos, BlockPos blockPos2, Iterator var11,
                            StructurePiece structurePiece) {
        if (this.centerPos.equals(BlockPos.ORIGIN)) return;
        MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(world.toServerWorld());
        if (!mayorVillageState.hasVillage(this.centerPos)) return;
        if (!(structurePiece instanceof PoolStructurePiece poolStructurePiece)) {
            mayorVillageState.markDirty();
            return;
        }
        if (!(poolStructurePiece.getPoolElement() instanceof SinglePoolElementAccess singlePoolElementAccess)) {
            mayorVillageState.markDirty();
            return;
        }

        singlePoolElementAccess.getLocation().left().ifPresent(locationIdentifier -> {
            VillageData villageData = mayorVillageState.getVillageData(this.centerPos);
            if (singlePoolElementAccess instanceof StructureTemplateAccess structureTemplate) {
                addEntitiesToVillageData(structureTemplate, villageData);
            }
            addStructureToVillageData(locationIdentifier, world, structurePiece, villageData);
        });
        mayorVillageState.markDirty();
    }


    @Unique
    private static void addEntitiesToVillageData(StructureTemplateAccess structureTemplate, VillageData villageData) {
        for (int i = 0; i < structureTemplate.getSpawnedEntities().size(); i++) {
            if (structureTemplate.getSpawnedEntities().get(i) instanceof VillagerEntity villagerEntity) {
                villageData.addVillager(villagerEntity.getUuid());
            } else if (structureTemplate.getSpawnedEntities().get(i) instanceof IronGolemEntity ironGolemEntity) {
                villageData.addIronGolem(ironGolemEntity.getUuid());
            }
        }
        structureTemplate.clearSpawnedEntities();
    }

    @Unique
    private static void addStructureToVillageData(Identifier locationIdentifier, StructureWorldAccess world,
                                                  StructurePiece structurePiece, VillageData villageData) {
        if (!StringUtil.shouldStoreStructureIdentifier(locationIdentifier)) return;
        Identifier structureIdentifier = StringUtil.getMayorStructureIdentifier(locationIdentifier);
        int experience = 0;
        int price = 8;

        if (!MayorConfig.CONFIG.instance().generatedStructureXp) {
            if (StructureDataLoader.structureDataMap.containsKey(StringUtil.getMayorStructureString(structureIdentifier))) {
                experience = StructureDataLoader.structureDataMap.get(StringUtil.getMayorStructureString(structureIdentifier)).getFirst();
                // price = StructureDataLoader.structureDataMap.get(StringUtil.getMayorStructureString(structureIdentifier)).get(1);
            } else {
                List<ItemStack> requiredItemStacks = StructureHelper.getStructureItemRequirements(world.toServerWorld(), structureIdentifier);
                experience = StructureHelper.getStructureExperience(requiredItemStacks);
                StructureDataLoader.structureDataMap.put(StringUtil.getMayorStructureString(structureIdentifier), List.of(experience, price));
            }
        }

        StructureData structureData = new StructureData(
                StructureHelper.getBottomCenterPos(structurePiece),
                structurePiece.getBoundingBox(),
                StructureHelper.getStructureRotation(structurePiece.getRotation()),
                structureIdentifier, StringUtil.getStructureLevelByIdentifier(structureIdentifier), experience);

        villageData.addStructure(structureData);
    }
}
