package io.fabricatedatelier.mayor.mixin;

import com.mojang.datafixers.util.Either;
import io.fabricatedatelier.mayor.access.SinglePoolElementAccess;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SinglePoolElement.class)
public class SinglePoolElementMixin implements SinglePoolElementAccess {

    @Nullable
    @Unique
    private StructureTemplate structureTemplate;

    @Shadow
    @Mutable
    @Final
    protected Either<Identifier, StructureTemplate> location;

    @Override
    public Either<Identifier, StructureTemplate> getLocation() {
        return this.location;
    }

    @Nullable
    @Override
    public StructureTemplate getStructureTemplate() {
        return this.structureTemplate;
    }

    @Inject(method = "generate", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/structure/pool/SinglePoolElement;getStructure(Lnet/minecraft/structure/StructureTemplateManager;)Lnet/minecraft/structure/StructureTemplate;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void generateMixin(StructureTemplateManager structureTemplateManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos pivot, BlockRotation rotation, BlockBox box, Random random, StructureLiquidSettings liquidSettings, boolean keepJigsaws, CallbackInfoReturnable<Boolean> info, StructureTemplate structureTemplate) {
        this.structureTemplate = structureTemplate;
    }
}
