package io.fabricatedatelier.mayor.mixin.access;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(BlockRenderManager.class)
public interface BlockRenderManagerAccess {

    @Accessor("builtinModelItemRenderer")
    BuiltinModelItemRenderer getBuiltinModelItemRenderer();
}
