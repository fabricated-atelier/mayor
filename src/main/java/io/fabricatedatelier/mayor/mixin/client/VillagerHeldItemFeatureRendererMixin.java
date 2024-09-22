package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.init.MayorTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(VillagerHeldItemFeatureRenderer.class)
public class VillagerHeldItemFeatureRendererMixin {

    @Shadow
    @Mutable
    @Final
    private HeldItemRenderer heldItemRenderer;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void renderMixin(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info) {
        if (livingEntity instanceof Builder builder && !builder.getCarryItemStack().isEmpty() && !builder.getVillagerEntity().isSleeping()) {
//            if (this.builderItem != null) {
            matrixStack.push();
            matrixStack.translate(0.0F, 0.62F, 0.3F);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            matrixStack.scale(2.8f, 2.8f, 2.8f);
            this.heldItemRenderer.renderItem(livingEntity, builder.getCarryItemStack(), ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
            matrixStack.pop();
            info.cancel();
//            }
//            else {
//                for (ItemStack stack : builder.getBuilderInventory().getHeldStacks()) {
//                    if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
//                        this.builderItem = stack;
//                        break;
//                    }
//                }
//            }
        }
//        else {
//            this.builderItem = null;
//        }
    }
}
