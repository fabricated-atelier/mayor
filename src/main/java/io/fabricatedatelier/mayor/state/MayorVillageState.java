package io.fabricatedatelier.mayor.state;

import java.util.*;

import io.fabricatedatelier.mayor.util.StructureHelper;
import io.fabricatedatelier.mayor.util.VillageHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;

public class MayorVillageState extends PersistentState {

    // public static final DSL.TypeReference MAYOR_VILLAGES = TypeReferences.create("mayor_village");

    private final ServerWorld world;

    private Map<BlockPos, VillageData> villages = new HashMap<BlockPos, VillageData>();
    // village level
    // blockpos of chests? for structure requirements
    // structures though identifier with origin blockpos and level
    // calculate available structures
    // check which level and if more structures can be build


    public MayorVillageState(ServerWorld world) {
        this.world = world;
    }

    public static PersistentState.Type<MayorVillageState> getPersistentStateType(ServerWorld world) {
        return new PersistentState.Type<MayorVillageState>(() -> new MayorVillageState(world), (nbt, registryLookup) -> fromNbt(world, (NbtCompound) nbt), null);
    }

    public static MayorVillageState fromNbt(ServerWorld world, NbtCompound nbt) {
        MayorVillageState mayorVillageState = new MayorVillageState(world);

        NbtList nbtList = nbt.getList("VillageData", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); i++) {
            VillageData villageData = new VillageData(nbtList.getCompound(i));
            mayorVillageState.villages.put(villageData.getCenterPos(), villageData);
        }


        return mayorVillageState;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    @Nullable
    public VillageData createVillageData(BlockPos centerPos) {
        for (BlockPos pos : this.villages.keySet()) {
            if (pos.isWithinDistance(centerPos, VillageHelper.VILLAGE_LEVEL_RADIUS.get(VillageHelper.VILLAGE_MAX_LEVEL) * 1.5f)) {
                return null;
            }
        }

        VillageData villageData = new VillageData(centerPos);
        villageData.setBiomeCategory(StructureHelper.getBiomeCategory(world.getBiome(centerPos)));
        this.villages.put(centerPos, villageData);
        this.markDirty();
        return villageData;
    }

    public boolean deleteVillageData(BlockPos centerPos) {
        if (this.villages.containsKey(centerPos)) {
            this.villages.remove(centerPos);
            this.markDirty();
            return true;
        } else {
            return false;
        }
    }

    public VillageData getVillageData(BlockPos centerPos) {
        return this.villages.get(centerPos);
    }

    public List<BlockPos> getVillageCenterPoses() {
        return this.villages.keySet().stream().toList();
    }

    public boolean hasVillage(BlockPos centerPos) {
        return this.villages.containsKey(centerPos);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtList = new NbtList();
        for (VillageData villageData : this.villages.values()) {
            NbtCompound nbtCompound = new NbtCompound();
            villageData.writeDataToNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        nbt.put("VillageData", nbtList);
        return nbt;
    }


}
