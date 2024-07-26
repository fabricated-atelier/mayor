package io.fabricatedatelier.mayor.mixin.access;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.structure.StructureTemplate;

@Mixin(StructureTemplate.class)
public interface StructureTemplateAccess {

    @Accessor("blockInfoLists")
    List<StructureTemplate.PalettedBlockInfoList> getBlockInfoLists();

}
