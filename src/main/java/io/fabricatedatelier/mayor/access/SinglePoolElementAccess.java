package io.fabricatedatelier.mayor.access;

import com.mojang.datafixers.util.Either;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

public interface SinglePoolElementAccess {

    //    @Accessor("location")
    public Either<Identifier, StructureTemplate> getLocation();

    public StructureTemplate getStructureTemplate();
//
//    @Invoker("getStructure")
//    public StructureTemplate invokeGetStructure(StructureTemplateManager structureTemplateManager);
}
