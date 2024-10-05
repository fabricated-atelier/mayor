package io.fabricatedatelier.mayor.entity.villager.task;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.ScheduleActivityTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;

public class BuilderTaskListProvider {

    // 0 is more important than higher integers
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> createBuildingTasks() {
        return ImmutableList.of(
                Pair.of(4, new BuilderBuildTask()),
                Pair.of(4, new BuilderBreakTask()),
                Pair.of(5, new BuilderCollectTask()),
                Pair.of(6, new BuilderDumpTask()),
                Pair.of(7, GoToJobSiteTask.create(MemoryModuleType.JOB_SITE, 0.7F, 16)),
                Pair.of(99, ScheduleActivityTask.create()
                ));
    }
}
