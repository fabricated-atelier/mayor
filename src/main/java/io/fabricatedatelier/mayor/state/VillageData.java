package io.fabricatedatelier.mayor.state;

import io.fabricatedatelier.mayor.manager.MayorCategory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VillageData {

    private final BlockPos centerPos;
    private  MayorCategory.BiomeCategory biomeCategory;
    private int level = 1;
    // private Text name = Text.translatable("mayor.village");
    private String name = "Village";
    private long age = 0;
    @Nullable
    private UUID mayorPlayerUuid = null;
    private long mayorPlayerTime = 0;
    private List<BlockPos> storageOriginBlockPosList = new ArrayList<BlockPos>();
    private List<UUID> villagers = new ArrayList<UUID>();
    private List<UUID> ironGolems = new ArrayList<UUID>();
    private Map<BlockPos, StructureData> structures = new HashMap<>();

    public VillageData(BlockPos centerPos) {
        this.centerPos = centerPos;
    }

    public VillageData(BlockPos centerPos, MayorCategory.BiomeCategory biomeCategory, int level, String name, long age, @Nullable UUID mayorPlayerUuid, long mayorPlayerTime, List<BlockPos> storageOriginBlockPosList, List<UUID> villagers,
                       List<UUID> ironGolems, Map<BlockPos, StructureData> structures) {

        this.centerPos = centerPos;
        this.biomeCategory = biomeCategory;
        this.level = level;
        this.name = name;
        this.age = age;
        this.mayorPlayerUuid = mayorPlayerUuid;
        this.mayorPlayerTime = mayorPlayerTime;
        this.storageOriginBlockPosList = storageOriginBlockPosList;
        this.villagers = villagers;
        this.ironGolems = ironGolems;
        this.structures = structures;
    }

    public VillageData(NbtCompound nbt) {
        this.centerPos = NbtHelper.toBlockPos(nbt, "Center").get();
        this.biomeCategory = MayorCategory.BiomeCategory.valueOf(nbt.getString("Biome"));
        this.level = nbt.getInt("Level");
        this.name = nbt.getString("Name");
        this.age = nbt.getLong("Age");
        if (nbt.getBoolean("HasMayor")) {
            this.mayorPlayerUuid = nbt.getUuid("MayorUuid");
            this.mayorPlayerTime = nbt.getLong("MayorPlayTime");
        }
        this.storageOriginBlockPosList.clear();
        for (int i = 0; i < nbt.getInt("Origins"); i++) {
            this.storageOriginBlockPosList.add(NbtHelper.toBlockPos(nbt, "Origin" + i).get());
        }
        this.villagers.clear();
        for (int i = 0; i < nbt.getInt("VillagerUuids"); i++) {
            this.villagers.add(nbt.getUuid("VillagerUuid" + i));
        }
        this.ironGolems.clear();
        for (int i = 0; i < nbt.getInt("IronGolemUuids"); i++) {
            this.ironGolems.add(nbt.getUuid("IronGolemUuid" + i));
        }
        this.structures.clear();
        NbtList nbtList = nbt.getList("Structures", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); i++) {
            StructureData structureData = new StructureData(nbtList.getCompound(i));
            structures.put(structureData.getBottomCenterPos(), structureData);
        }
    }

    public void writeDataToNbt(NbtCompound nbt) {
        nbt.put("Center", NbtHelper.fromBlockPos(this.centerPos));
        nbt.putString("Biome", this.biomeCategory.name());
        nbt.putInt("Level", this.level);
        nbt.putString("Name", this.name);
        nbt.putLong("Age", this.age);
        nbt.putBoolean("HasMayjor", this.mayorPlayerUuid != null);
        if (this.mayorPlayerUuid != null) {
            nbt.putUuid("MayorUuid", this.mayorPlayerUuid);
            nbt.putLong("MayorPlayTime", this.mayorPlayerTime);
        }
        nbt.putInt("Origins", this.storageOriginBlockPosList.size());
        for (int i = 0; i < this.storageOriginBlockPosList.size(); i++) {
            nbt.put("Origin" + i, NbtHelper.fromBlockPos(this.storageOriginBlockPosList.get(i)));
        }
        nbt.putInt("VillagerUuids", this.villagers.size());
        for (int i = 0; i < this.villagers.size(); i++) {
            nbt.putUuid("VillagerUuid" + i, this.villagers.get(i));
        }
        nbt.putInt("IronGolemUuids", this.ironGolems.size());
        for (int i = 0; i < this.ironGolems.size(); i++) {
            nbt.putUuid("IronGolemUuid" + i, this.ironGolems.get(i));
        }
        NbtList nbtList = new NbtList();
        for (StructureData structureData : this.structures.values()) {
            NbtCompound nbtCompound = new NbtCompound();
            structureData.writeDataToNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        nbt.put("Structures", nbtList);

        // this.structureData.values().
        // var test = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos);
        // NbtElement nbtCompound = test.getOrThrow();
    }

    // Center
    public BlockPos getCenterPos() {
        return centerPos;
    }

    // Biome
    public MayorCategory.BiomeCategory getBiomeCategory() {
        return this.biomeCategory;
    }

    public void setBiomeCategory(MayorCategory.BiomeCategory biomeCategory){
        this.biomeCategory = biomeCategory;
    }

    // Level
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    // Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Age
    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    // Mayor
    @Nullable
    public UUID getMayorPlayerUuid() {
        return mayorPlayerUuid;
    }

    public void setMayorPlayerUuid(@Nullable UUID mayorPlayerUuid) {
        this.mayorPlayerUuid = mayorPlayerUuid;
    }

    // Mayor Time
    public long getMayorPlayerTime() {
        return mayorPlayerTime;
    }

    public void setMayorPlayerTime(long mayorPlayerTime) {
        this.mayorPlayerTime = mayorPlayerTime;
    }

    // StorageBlocks
    public List<BlockPos> getStorageOriginBlockPosList() {
        return storageOriginBlockPosList;
    }

    public void setStorageOriginBlockPosList(List<BlockPos> storageOriginBlockPosList) {
        this.storageOriginBlockPosList = storageOriginBlockPosList;
    }

    public void addStorageOriginBlockPos(BlockPos blockPos) {
        this.storageOriginBlockPosList.add(blockPos);
    }

    public void removeStorageOriginBlockPos(BlockPos blockPos) {
        this.storageOriginBlockPosList.remove(blockPos);
    }

    // Villagers
    public List<UUID> getVillagers() {
        return villagers;
    }

    public void setVillagers(List<UUID> villagers) {
        this.villagers = villagers;
    }

    public void addVillager(UUID villager) {
        this.villagers.add(villager);
    }

    public void removeVillager(UUID villager) {
        this.villagers.remove(villager);
    }

    // Iron Golems
    public List<UUID> getIronGolems() {
        return ironGolems;
    }

    public void setIronGolems(List<UUID> ironGolems) {
        this.ironGolems = ironGolems;
    }

    public void addIronGolem(UUID ironGolem) {
        this.ironGolems.add(ironGolem);
    }

    public void removeIronGolem(UUID ironGolem) {
        this.ironGolems.remove(ironGolem);
    }

    // Structures
    public Map<BlockPos, StructureData> getStructures() {
        return structures;
    }

    public void setStructures(Map<BlockPos, StructureData> structures) {
        this.structures = structures;
    }

    public void addStructure(StructureData structureData) {
        this.structures.put(structureData.getBottomCenterPos(), structureData);
    }

    public void removeStructure(StructureData structureData) {
        this.structures.remove(structureData.getBottomCenterPos());
    }

}
