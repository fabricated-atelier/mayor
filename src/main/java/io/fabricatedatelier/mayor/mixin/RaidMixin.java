package io.fabricatedatelier.mayor.mixin;

import io.fabricatedatelier.mayor.state.VillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import io.fabricatedatelier.mayor.util.VillageHelper;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.raid.Raid;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.UUID;

@Mixin(Raid.class)
public class RaidMixin {

    @Shadow
    @Mutable
    @Final
    private Set<UUID> heroesOfTheVillage;

    @Nullable
    @Unique
    private BlockPos centerPos = null;

    @Shadow
    @Mutable
    @Final
    private ServerWorld world;

    @Inject(method = "<init>(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V", at = @At("TAIL"))
    private void initMixin(int id, ServerWorld world, BlockPos pos, CallbackInfo info) {
        VillageData villageData = StateHelper.getClosestVillage(world, pos);
        if (villageData != null) {
            this.centerPos = villageData.getCenterPos();
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void initMixin(ServerWorld world, NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("CenterPos")) {
            this.centerPos = NbtHelper.toBlockPos(nbt, "CenterPos").get();
        }
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info) {
        if (this.centerPos != null) {
            nbt.put("CenterPos", NbtHelper.fromBlockPos(this.centerPos));
        }
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/village/raid/Raid$Status;VICTORY:Lnet/minecraft/village/raid/Raid$Status;"))
    private void tickMixin(CallbackInfo info) {
        if (this.centerPos == null) return;
        VillageState villageState = StateHelper.getMayorVillageState(this.world);
        if (villageState == null || !villageState.hasVillage(this.centerPos)) return;
        VillageData villageData = villageState.getVillageData(this.centerPos);
        if (villageData.getMayorPlayerUuid() != null) return;

        int maxReputation = 0;
        UUID mayorUuid = null;
        String mayorName = "";

        for (UUID uUID : this.heroesOfTheVillage) {
            if (!(this.world.getEntity(uUID) instanceof ServerPlayerEntity serverPlayerEntity)) continue;
            int reputation = 0;
            for (UUID villager : villageData.getVillagers()) {
                if (this.world.getEntity(villager) instanceof VillagerEntity villagerEntity) {
                    reputation += villagerEntity.getReputation(serverPlayerEntity);
                }
            }
            if (reputation > maxReputation) {
                maxReputation = reputation;
                mayorUuid = uUID;
                mayorName = serverPlayerEntity.getName().getString();
            }
        }

        if (mayorUuid != null) {
            villageData.setMayorPlayerUuid(mayorUuid);
            villageData.setMayorPlayerTime(this.world.getTime());
            villageState.markDirty();

            broadcastVillageNews(this.world, villageData, villageState, this.centerPos, mayorUuid, mayorName);
        }
    }

    @Unique
    private static void broadcastVillageNews(ServerWorld world, VillageData villageData, VillageState villageState,
                                             BlockPos centerPos, UUID mayorUuid, String mayorName) {
        Box villageBoundingBox = new Box(centerPos).expand(VillageHelper.VILLAGE_LEVEL_RADIUS.get(villageData.getLevel()));
        for (ServerPlayerEntity serverPlayerEntity : world.getEntitiesByClass(ServerPlayerEntity.class, villageBoundingBox, EntityPredicates.EXCEPT_SPECTATOR)) {
            serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageState.getVillageData(centerPos).getName())));
            if (serverPlayerEntity.getUuid().equals(mayorUuid)) {
                serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.mayor.elected.2")));
            } else {
                serverPlayerEntity.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.mayor.elected", mayorName)));
            }
        }
    }
}
