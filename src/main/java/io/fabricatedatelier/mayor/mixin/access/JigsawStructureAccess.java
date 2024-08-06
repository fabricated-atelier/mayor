package io.fabricatedatelier.mayor.mixin.access;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.world.gen.structure.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JigsawStructure.class)
public interface JigsawStructureAccess {

    @Accessor("startPool")
    public RegistryEntry<StructurePool> getStartPool();

}
