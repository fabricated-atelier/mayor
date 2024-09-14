package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.fabricatedatelier.mayor.init.Entities;
import io.fabricatedatelier.mayor.init.VillagerUtilities;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

public class BuilderTaskListProvider {

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createBuildingTasks(VillagerProfession profession, float speed) {
        VillagerWorkTask villagerWorkTask;
        if (profession == VillagerUtilities.BUILDER) {
            villagerWorkTask = new BuilderWorkTask();
        } else {
            return ImmutableList.of();
        }

        return ImmutableList.of(Pair.of(5, new RandomTask<>(
                                ImmutableList.of(
//                                        Pair.of(villagerWorkTask, 7),
//                                        Pair.of(GoToIfNearbyTask.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
                                        Pair.of(GoToIfNearbyTask.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 7),
                                        Pair.of(GoToNearbyPositionTask.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
//                                        Pair.of(GoToSecondaryPositionTask.create(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5),
                                        Pair.of(new BuilderBuildTask(), 2),
                                        Pair.of(new BuilderCollectTask(), 2)
//                                        Pair.of(new BoneMealTask(), profession == VillagerProfession.FARMER ? 4 : 7)
                                ))),
                // Todo: check here?
                Pair.of(2, VillagerWalkTowardsTask.create(MemoryModuleType.JOB_SITE, speed, 9, 100, 1200)),
                Pair.of(99, ScheduleActivityTask.create())
        );
    }
}
