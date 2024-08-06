package io.fabricatedatelier.mayor.access;

import net.minecraft.entity.Entity;
import net.minecraft.structure.StructureTemplate;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

public interface StructureTemplateAccess {

    List<StructureTemplate.PalettedBlockInfoList> getBlockInfoLists();

    List<Entity> getSpawnedEntities();
}
