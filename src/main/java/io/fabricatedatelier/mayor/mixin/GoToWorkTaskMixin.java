package io.fabricatedatelier.mayor.mixin;

import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.GoToWorkTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(GoToWorkTask.class)
public class GoToWorkTaskMixin {

    @Inject(method = "method_46890",at = @At(value = "RETURN",ordinal = 2),locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void test(TaskTriggerer.TaskContext taskContext, MemoryQueryResult memoryQueryResult, MemoryQueryResult memoryQueryResult2, ServerWorld world, VillagerEntity entity, long time, CallbackInfoReturnable<Boolean> cir, GlobalPos globalPos, MinecraftServer minecraftServer){
        System.out.println(entity+ " : ");
//        GlobalPos globalPos = taskContext.getValue(memoryQueryResult);

//        MinecraftServer minecraftServer = world.getServer();
        if(minecraftServer.getWorld(globalPos.dimension()) != null){

//            System.out.println(   minecraftServer.getWorld(globalPos.dimension()).getPointOfInterestStorage().getType(globalPos.pos()));

//         System.out.println(   minecraftServer.getWorld(globalPos.dimension()).getPointOfInterestStorage().getType(globalPos.pos()).flatMap(poiType -> Registries.VILLAGER_PROFESSION.stream().filter(profession -> profession.heldWorkstation().test(poiType)).findFirst()));
        }

//        Optional.ofNullable(minecraftServer.getWorld(globalPos.dimension()))
//                .flatMap(jobSiteWorld -> jobSiteWorld.getPointOfInterestStorage().getType(globalPos.pos()))
//                .flatMap(poiType -> Registries.VILLAGER_PROFESSION.stream().filter(profession -> profession.heldWorkstation().test(poiType)).findFirst())
//                .ifPresent(profession -> {
//                    entity.setVillagerData(entity.getVillagerData().withProfession(profession));
//                    entity.reinitializeBrain(world);
//                });
    }
}
