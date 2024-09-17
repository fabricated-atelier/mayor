package io.fabricatedatelier.mayor.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.apache.commons.lang3.mutable.MutableLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Deprecated
@Mixin(FindPointOfInterestTask.class)
public class FindPointOfInterestTaskMixin {

    @Inject(method = "method_46885",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/ai/brain/task/FindPointOfInterestTask;findPathToPoi(Lnet/minecraft/entity/mob/MobEntity;Ljava/util/Set;)Lnet/minecraft/entity/ai/pathing/Path;"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void method_46885Mixin(boolean bl, MutableLong mutableLong, Long2ObjectMap long2ObjectMap, Predicate predicate, MemoryQueryResult memoryQueryResult, Optional optional, ServerWorld world, PathAwareEntity entity, long time, CallbackInfoReturnable<Boolean> cir, PointOfInterestStorage pointOfInterestStorage, Predicate predicate2, Set set){
//        Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> test = (Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>>)pointOfInterestStorage.getSortedTypesAndPositions(
//                        entry -> entry.matchesKey(MayorEntities.BUILDER_POI_KEY), predicate2, entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE
//                )
//                .limit(5L)
//                .collect(Collectors.toSet());
//        System.out.println(test);
//        FindPointOfInterestTask.create(profession.acquirableWorkstation(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())
//        System.out.println(Registries.POINT_OF_INTEREST_TYPE.getEntry(MayorEntities.BUILDER_POI).isIn(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE)+ " : "+Registries.POINT_OF_INTEREST_TYPE.getEntry(MayorEntities.BUILDER_POI).getIdAsString()+ " : "+Registries.POINT_OF_INTEREST_TYPE.getId(MayorEntities.BUILDER_POI));

//        System.out.println(entity+ " : "+pointOfInterestStorage.count(PointOfInterestType.NONE, entity.getBlockPos(), 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE)+ " : "+set+ " : "+optional);

//   System.out.println("FIND POINT");
    }
}
