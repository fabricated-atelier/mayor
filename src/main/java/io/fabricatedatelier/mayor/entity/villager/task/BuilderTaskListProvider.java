package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;

public class BuilderTaskListProvider {

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createBuildingTasks() {
//        VillagerWorkTask villagerWorkTask;
//        if (profession == VillagerUtilities.BUILDER) {
//            villagerWorkTask = new BuilderWorkTask();
//        } else {
//            return ImmutableList.of();
//        }

        System.out.println("CREATE BUILDING TASKS LOL");

//        return ImmutableList.of(Pair.of(5, new RandomTask<>(ImmutableList.of(
////                                        Pair.of(villagerWorkTask, 7),
////                                        Pair.of(GoToIfNearbyTask.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 2),
//                                        Pair.of(GoToIfNearbyTask.create(MemoryModuleType.JOB_SITE, 0.4F, 4), 7),
//                                        Pair.of(GoToNearbyPositionTask.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10), 5),
////                                        Pair.of(GoToSecondaryPositionTask.create(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6, MemoryModuleType.JOB_SITE), 5),
//                                        Pair.of(new BuilderBuildTask(), 2),
//                                        Pair.of(new BuilderCollectTask(), 2)
////                                        Pair.of(new BoneMealTask(), profession == VillagerProfession.FARMER ? 4 : 7)
//                                ))),
//                // Todo: check here?
//                Pair.of(2, VillagerWalkTowardsTask.create(MemoryModuleType.JOB_SITE, 0.5f, 9, 100, 1200)),
//                Pair.of(99, ScheduleActivityTask.create())
//        );
                return ImmutableList.of(
//                        Pair.of(6,GoToIfNearbyTask.create(MemoryModuleType.JOB_SITE, 0.4F, 4)),
//                        Pair.of(7,GoToNearbyPositionTask.create(MemoryModuleType.JOB_SITE, 0.4F, 1, 10)),
                        Pair.of(4,new BuilderBuildTask()),
                        Pair.of(5,new BuilderCollectTask()),
                       // Pair.of(8, VillagerWalkTowardsTask.create(MemoryModuleType.JOB_SITE, 0.5f, 1, 100, 1200)),

//                        Pair.of(8,GoToPointOfInterestTask.create(1, 1)),
//                        Pair.of(10, VillagerWalkTowardsTask.create(MemoryModuleType.JOB_SITE, 0.5f, 1, 100, 1200)),
                        Pair.of(99, ScheduleActivityTask.create()
        ));

                // 0 is more important than higher integers

//                WalkHomeTask
    }
}
