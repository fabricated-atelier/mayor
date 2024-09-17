package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.access.SinglePoolElementAccess;
import io.fabricatedatelier.mayor.access.StructureTemplateAccess;
import io.fabricatedatelier.mayor.data.StructureXpLoader;
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
    private void placeMixin(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo info) {
        if (structure.getType().equals(StructureType.JIGSAW) && structure instanceof JigsawStructureAccess jigsawStructureAccess
                && jigsawStructureAccess.getStartPool().isIn(MayorTags.StructurePools.VILLAGES)) {
            MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(world.toServerWorld());
            BlockPos centerPos = ((StructureStart) (Object) this).getBoundingBox().getCenter();
            if (!mayorVillageState.hasVillage(centerPos)) {
                VillageData villageData = mayorVillageState.createVillageData(centerPos);
                if (villageData != null) {
                    villageData.setBiomeCategory(StructureHelper.getBiomeCategory(world.toServerWorld().getBiome(centerPos)));
                    this.centerPos = centerPos;
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructurePiece;generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void placeMixin(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo info,
                            List list, BlockBox blockBox, BlockPos blockPos, BlockPos blockPos2, Iterator var11, StructurePiece structurePiece) {
        if (!this.centerPos.equals(BlockPos.ORIGIN)) {
            MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(world.toServerWorld());
            if (mayorVillageState.hasVillage(this.centerPos)) {
                if (structurePiece instanceof PoolStructurePiece poolStructurePiece && poolStructurePiece.getPoolElement() instanceof SinglePoolElementAccess singlePoolElementAccess
                        && singlePoolElementAccess.getLocation().left().isPresent()) {
                    VillageData villageData = mayorVillageState.getVillageData(this.centerPos);
                    if (singlePoolElementAccess.getStructureTemplate() instanceof StructureTemplateAccess structureTemplateAccess && !structureTemplateAccess.getSpawnedEntities().isEmpty()) {
                        for (int i = 0; i < structureTemplateAccess.getSpawnedEntities().size(); i++) {
                            if (structureTemplateAccess.getSpawnedEntities().get(i) instanceof VillagerEntity villagerEntity) {
                                villageData.addVillager(villagerEntity.getUuid());
                            } else if (structureTemplateAccess.getSpawnedEntities().get(i) instanceof IronGolemEntity ironGolemEntity) {
                                villageData.addIronGolem(ironGolemEntity.getUuid());
                            }
                        }
                        structureTemplateAccess.clearSpawnedEntities();
                    }
                    if (StringUtil.shouldStoreStructureIdentifier(singlePoolElementAccess.getLocation().left().get())) {

                        // Todo: Config option for adding xp at structure generation
                        if (false) {
                            Identifier test = StringUtil.getMayorStructureIdentifier(singlePoolElementAccess.getLocation().left().get());
                            int level = StringUtil.getStructureLevelByIdentifier(test);
                            List<ItemStack> requiredItemStacks = StructureHelper.getStructureItemRequirements(world.toServerWorld(), test);

                            int experience = 0;
                            if (StructureXpLoader.structureExperienceMap.containsKey(StringUtil.getMayorStructureString(test))) {
                                experience = StructureXpLoader.structureExperienceMap.get(StringUtil.getMayorStructureString(test));
                            } else {
                                experience = StructureHelper.getStructureExperience(requiredItemStacks);
                                StructureXpLoader.structureExperienceMap.put(StringUtil.getMayorStructureString(test), experience);
                            }

//                            System.out.println(StructureXpLoader.structureExperienceMap.containsKey(StringUtil.getMayorStructureString(test))+ " : "+experience+ " : "+level);
                        }
                        StructureData structureData = new StructureData(StructureHelper.getBottomCenterPos(structurePiece), structurePiece.getBoundingBox(),
                                StringUtil.getMayorStructureIdentifier(singlePoolElementAccess.getLocation().left().get()), 1, 0);
                        villageData.addStructure(structureData);
                    }
                }
                mayorVillageState.markDirty();
            }
        }

    }
}
