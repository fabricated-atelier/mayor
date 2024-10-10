package io.fabricatedatelier.mayor.manager;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CitizenManager {

    @Nullable
    private BlockPos villagePos = null;

    public CitizenManager() {
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("VillagePos")) {
            this.villagePos = BlockPos.ofFloored(nbt.getInt("VillageX"), nbt.getInt("VillageY"), nbt.getInt("VillageZ"));
        }
    }

    public void writeNbt(NbtCompound nbt) {
        if (this.villagePos != null) {
            nbt.putInt("VillageX", this.villagePos.getX());
            nbt.putInt("VillageY", this.villagePos.getY());
            nbt.putInt("VillageZ", this.villagePos.getZ());
        }
    }

    @Nullable
    public BlockPos getVillagePos() {
        return villagePos;
    }

    public void setVillagePos(@Nullable BlockPos villagePos) {
        this.villagePos = villagePos;
    }
}
