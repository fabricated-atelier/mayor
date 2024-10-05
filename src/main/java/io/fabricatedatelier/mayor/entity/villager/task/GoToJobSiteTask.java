package io.fabricatedatelier.mayor.entity.villager.task;

import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableLong;

import java.util.Optional;

public class GoToJobSiteTask {

    public static SingleTickTask<VillagerEntity> create(MemoryModuleType<GlobalPos> posModule, float walkSpeed, int maxDistance) {
        MutableLong mutableLong = new MutableLong(0L);
        return TaskTriggerer.task(
                context -> context.group(context.queryMemoryAbsent(MayorVillagerUtilities.BUSY), context.queryMemoryOptional(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(posModule))
                        .apply(context, (busy, walkTarget, pos) -> (world, entity, time) -> {
                            if (entity instanceof Builder builder && builder.hasTargetPosition()) {
                                return false;
                            }
                            GlobalPos globalPos = context.getValue(pos);
                            if (world.getRegistryKey() != globalPos.dimension() || !globalPos.pos().isWithinDistance(entity.getPos(), maxDistance)) {
                                return false;
                            } else if (time <= mutableLong.getValue()) {
                                return true;
                            } else {
                                Optional<Vec3d> optional = Optional.ofNullable(FuzzyTargeting.find(entity, maxDistance, 8));
                                walkTarget.remember(optional.map(targetPos -> new WalkTarget(targetPos, walkSpeed, 1)));
                                mutableLong.setValue(time + 180L);
                                return true;
                            }
                        })
        );
    }
}

