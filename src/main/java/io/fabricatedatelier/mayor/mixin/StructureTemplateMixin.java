package io.fabricatedatelier.mayor.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.fabricatedatelier.mayor.access.StructureTemplateAccess;
import net.minecraft.entity.Entity;
import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin implements StructureTemplateAccess {

    @Unique
    private List<Entity> spawnedEntities = new ArrayList<>();

    @Shadow
    @Mutable
    @Final
    private List<StructureTemplate.PalettedBlockInfoList> blockInfoLists;

    @ModifyExpressionValue(method = "spawnEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructureTemplate;getEntity(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/nbt/NbtCompound;)Ljava/util/Optional;"))
    private Optional<Entity> spawnEntitiesMixin(Optional<Entity> original) {
        if (original.isPresent()) {
            this.spawnedEntities.add(original.get());
        }
        return original;
    }

    @Override
    public List<StructureTemplate.PalettedBlockInfoList> getBlockInfoLists() {
        return this.blockInfoLists;
    }

    @Override
    public List<Entity> getSpawnedEntities() {
        return spawnedEntities;
    }
}
