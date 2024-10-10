package io.fabricatedatelier.mayor.manager;

import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MayorManager {

    public static Map<MayorCategory.BiomeCategory, List<MayorStructure>> mayorStructureMap = new HashMap<>();

    private boolean isInMajorView = false;
    @Nullable
    private MayorCategory.BiomeCategory biomeCategory = null;
    @Nullable
    private VillageData villageData = null;
    @Nullable
    private MayorStructure mayorStructure;

    @Nullable
    private BlockPos originBlockPos = null;
    private BlockRotation structureRotation = BlockRotation.NONE;
    private boolean center = false;

    private int availableBuilder = 0;
    @Nullable
    private Perspective oldPerspective = null;

    private final PlayerEntity playerEntity;

    private final CitizenManager citizenManager;

    public MayorManager(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
        this.citizenManager = new CitizenManager();
    }

    public PlayerEntity playerEntity() {
        return this.playerEntity;
    }

    public CitizenManager getCitizenManager() {
        return citizenManager;
    }

    // Mayor View
    public void setMajorView(boolean majorView) {
        this.isInMajorView = majorView;
    }

    public boolean isInMajorView() {
        return this.isInMajorView;
    }

    // Biome Category
    @Nullable
    public MayorCategory.BiomeCategory getBiomeCategory() {
        return biomeCategory;
    }

    public void setBiomeCategory(@Nullable MayorCategory.BiomeCategory biomeCategory) {
        this.biomeCategory = biomeCategory;
    }

    // VillageData
    @Nullable
    public VillageData getVillageData() {
        return villageData;
    }

    public void setVillageData(@Nullable VillageData villageData) {
        this.villageData = villageData;
    }

    // Structure
    @Nullable
    public MayorStructure getMayorStructure() {
        return mayorStructure;
    }

    public void setMayorStructure(@Nullable MayorStructure mayorStructure) {
        this.mayorStructure = mayorStructure;
    }

    public void setStructureOriginBlockPos(@Nullable BlockPos origin) {
        this.originBlockPos = origin;
    }

    @Nullable
    public BlockPos getStructureOriginBlockPos() {
        return this.originBlockPos;
    }

    public void setStructureRotation(BlockRotation structureRotation) {
        this.structureRotation = structureRotation;
    }

    public BlockRotation getStructureRotation() {
        return this.structureRotation;
    }

    public void setStructureCentered(boolean center) {
        this.center = center;
    }

    public boolean getStructureCentered() {
        return this.center;
    }

    // Other
    public void setAvailableBuilder(int availableBuilder) {
        this.availableBuilder = availableBuilder;
    }

    public int getAvailableBuilder() {
        return this.availableBuilder;
    }

    @Nullable
    public Perspective getOldPerspective() {
        return oldPerspective;
    }

    public void setOldPerspective(@Nullable Perspective oldPerspective) {
        this.oldPerspective = oldPerspective;
    }

}
