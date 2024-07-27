package io.fabricatedatelier.mayor.state;

import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

public class MayorVillageState extends PersistentState {

    private final ServerWorld world;

    public MayorVillageState(ServerWorld world) {
        this.world = world;
    }

    public static PersistentState.Type<MayorVillageState> getPersistentStateType(ServerWorld world) {
        return new PersistentState.Type<MayorVillageState>(() -> new MayorVillageState(world), (nbt, registryLookup) -> fromNbt(world, (NbtCompound) nbt), null);
        // DataFixTypes.SAVED_DATA_RAIDS
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        return nbt;
    }

    public static MayorVillageState fromNbt(ServerWorld world, NbtCompound nbt) {
        MayorVillageState mayorVillageState = new MayorVillageState(world);
        return mayorVillageState;
    }
}
