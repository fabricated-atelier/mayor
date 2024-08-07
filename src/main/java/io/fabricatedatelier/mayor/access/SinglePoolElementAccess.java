package io.fabricatedatelier.mayor.access;

import com.mojang.datafixers.util.Either;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;

public interface SinglePoolElementAccess {

    public Either<Identifier, StructureTemplate> getLocation();

    public StructureTemplate getStructureTemplate();
}
