package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.access.BuiltinModelItemRendererAccess;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin implements BuiltinModelItemRendererAccess {

    @Nullable
    @Unique
    private World world;
    @Nullable
    @Unique
    private BlockState blockState;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderEntity(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void renderMixin(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo info, Item item, Block block, BlockEntity blockEntity) {

        if (blockEntity != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (this.blockState != null && client.player != null && mode.equals(ModelTransformationMode.NONE) && ((MayorManagerAccess) client.player).getMayorManager().isInMajorView()) {
                blockEntity.setWorld(this.world);
                blockEntity.setCachedState(blockState);
            } else {
                blockEntity.setWorld(null);
            }
        }
    }

    @Override
    public void setWorldAndBlockState(World world, BlockState blockState) {
        this.world = world;
        this.blockState = blockState;
    }
}
