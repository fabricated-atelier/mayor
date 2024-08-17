package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.access.MayorVillageStateAccess;
import io.fabricatedatelier.mayor.access.StructureTemplateAccess;
import io.fabricatedatelier.mayor.init.Tags;
import io.fabricatedatelier.mayor.mixin.access.JigsawStructureAccess;
import io.fabricatedatelier.mayor.access.SinglePoolElementAccess;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StringUtil;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.structure.*;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import org.jetbrains.annotations.Nullable;
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
                && jigsawStructureAccess.getStartPool().isIn(Tags.StructurePools.VILLAGES)) {
            MayorVillageState mayorVillageState = ((MayorVillageStateAccess) world.toServerWorld()).getMayorVillageState();
            BlockPos centerPos = ((StructureStart) (Object) this).getBoundingBox().getCenter();
            if (!mayorVillageState.hasVillage(centerPos)) {
                VillageData villageData = ((MayorVillageStateAccess) world.toServerWorld()).getMayorVillageState().createVillageData(centerPos);
                villageData.setBiomeCategory(StructureHelper.getBiomeCategory(world.toServerWorld().getBiome(centerPos)));
                this.centerPos = centerPos;
            }
        }

    }

    @SuppressWarnings("rawtypes")
    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructurePiece;generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void placeMixinX(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, CallbackInfo info,
                             List list, BlockBox blockBox, BlockPos blockPos, BlockPos blockPos2, Iterator var11, StructurePiece structurePiece) {
        if (!this.centerPos.equals(BlockPos.ORIGIN)) {
            MayorVillageState mayorVillageState = ((MayorVillageStateAccess) world.toServerWorld()).getMayorVillageState();
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
                    }
                    if (StringUtil.shouldStoreStructureIdentifier(singlePoolElementAccess.getLocation().left().get())) {
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
