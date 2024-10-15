package io.fabricatedatelier.mayor.block.entity;

import io.fabricatedatelier.mayor.init.MayorBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PoleBlockEntity extends BlockEntity {

    private List<UUID> assignedWorkers = new ArrayList<>();

    public PoleBlockEntity(BlockPos pos, BlockState state) {
        super(MayorBlockEntities.POLE, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.assignedWorkers.clear();
        for (int i = 0; i < nbt.getInt("WorkerCount"); i++) {
            this.assignedWorkers.add(nbt.getUuid("Worker" + i));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("WorkerCount", this.assignedWorkers.size());
        for (int i = 0; i < this.assignedWorkers.size(); i++) {
            nbt.putUuid("Worker" + i, this.assignedWorkers.get(i));
        }
    }

    public List<UUID> getAssignedWorkers() {
        return assignedWorkers;
    }

    public void setAssignedWorkers(List<UUID> assignedWorkers) {
        this.assignedWorkers = assignedWorkers;
    }

    public void addAssignedWorker(UUID assignedWorker) {
        if (!this.assignedWorkers.contains(assignedWorker)) {
            this.assignedWorkers.add(assignedWorker);
        }
    }

    public void removeAssignedWorker(UUID assignedWorker) {
        this.assignedWorkers.remove(assignedWorker);
    }
}
