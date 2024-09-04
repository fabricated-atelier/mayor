package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.Mayor;
import net.minecraft.block.Blocks;
import net.minecraft.block.SmithingTableBlock;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.TakeJobSiteTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Deprecated
@Mixin(TakeJobSiteTask.class)
public class TakeJobSiteTaskMixin {

   // @Inject(method = "method_47211",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/world/ServerWorld;getPointOfInterestStorage()Lnet/minecraft/world/poi/PointOfInterestStorage;"))
//   @Inject(method = "method_47211",at = @At(value = "RETURN",ordinal = 3),locals = LocalCapture.CAPTURE_FAILSOFT)
//    private static void canUseJobSite(TaskTriggerer.TaskContext taskContext, MemoryQueryResult memoryQueryResult, MemoryQueryResult memoryQueryResult2, MemoryQueryResult memoryQueryResult3, MemoryQueryResult memoryQueryResult4, float f, ServerWorld world, VillagerEntity entity, long time, CallbackInfoReturnable<Boolean> cir, BlockPos blockPos, Optional optional) {
//        System.out.println(entity+ " : "+optional+ " : "+blockPos);
////       memoryQueryResult.
////        Mayor.LOGGER.error(taskContext);
////       SmithingTableBlock
//    }

//    Blocks
    @Inject(method = "method_47211",at = @At(value = "HEAD"))
    private static void method_47211Mixin(TaskTriggerer.TaskContext taskContext, MemoryQueryResult memoryQueryResult, MemoryQueryResult memoryQueryResult2, MemoryQueryResult memoryQueryResult3, MemoryQueryResult memoryQueryResult4, float f, ServerWorld world, VillagerEntity entity, long time, CallbackInfoReturnable<Boolean> cir) {
//        System.out.println(entity+ " : "+memoryQueryResult+" : "+memoryQueryResult.getValue());

//       memoryQueryResult.
//        Mayor.LOGGER.error(taskContext);
//       SmithingTableBlock
//        System.out.println("TAKE JOB");
    }
}
