package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.state.ConstructionData;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TaskHelper {


    public static boolean canReachSite(PathAwareEntity entity, BlockPos pos) {
        Path path = entity.getNavigation().findPathTo(pos, 256);
        return path != null && path.reachesTarget();
    }

    @Nullable
    public static BlockPos getAirPos(ServerWorld serverWorld, int x, int z, int minY, int maxY) {
        for (int i = minY; i < maxY; i++) {
            BlockPos pos = new BlockPos(x, i, z);
            if (serverWorld.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return null;
    }

    public static List<BlockPos> getPossibleMiddleTargetBlockPoses(ServerWorld serverWorld, BlockBox blockBox) {
        List<BlockPos> blockPosList = new ArrayList<>();
        int middleX = blockBox.getMinX() + (blockBox.getMaxX() - blockBox.getMinX()) / 2;
        int middleZ = blockBox.getMinZ() + (blockBox.getMaxZ() - blockBox.getMinZ()) / 2;

        BlockPos pos;

        pos = getAirPos(serverWorld, middleX, blockBox.getMinZ(), blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, middleX, blockBox.getMaxZ(), blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, blockBox.getMinX(), middleZ, blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }
        pos = getAirPos(serverWorld, blockBox.getMaxX(), middleZ, blockBox.getMinY(), blockBox.getMaxY());
        if (pos != null) {
            blockPosList.add(pos);
        }

        return blockPosList;
    }

    @Nullable
    public static BlockPos findClosestTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, ConstructionData constructionData) {
        if (constructionData != null) {
            BlockBox blockBox = constructionData.getStructureData().getBlockBox();

            BlockPos targetPos = null;
            for (BlockPos pos : getPossibleMiddleTargetBlockPoses(serverWorld, blockBox)) {
                if (VillageHelper.canReachSite(villagerEntity, pos)) {
                    if (targetPos != null) {
                        if (pos.getSquaredDistance(villagerEntity.getPos()) < targetPos.getSquaredDistance(villagerEntity.getPos())) {
                            targetPos = pos;
                        }
                    } else {
                        targetPos = pos;
                    }
                }
            }
            return targetPos;
        }
        return null;
    }

}
