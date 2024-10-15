package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.entity.villager.access.Worker;
import io.fabricatedatelier.mayor.init.MayorItems;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(VillagerHeldItemFeatureRenderer.class)
public abstract class VillagerHeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {

    @Shadow
    @Mutable
    @Final
    private HeldItemRenderer heldItemRenderer;

    @Unique
    private static final ItemStack HAMMER = new ItemStack(MayorItems.DECONSTRUCTION_HAMMER);
    @Unique
    private static final ItemStack AXE = new ItemStack(Items.IRON_AXE);
    @Unique
    private static final ItemStack PICKAXE = new ItemStack(Items.IRON_PICKAXE);

    public VillagerHeldItemFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void renderMixin(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo info) {
        if (livingEntity instanceof Worker worker && worker.getVillagerEntity().getVillagerData().getProfession().equals(MayorVillagerUtilities.BUILDER) && !worker.getVillagerEntity().isSleeping()) {
            matrixStack.push();
            if (!worker.getCarryItemStack().isEmpty()) {
                if (worker.getTaskValue() == 1) {
                    matrixStack.translate(0.0F, 0.7F, -0.5F);
                } else {
                    matrixStack.translate(0.0F, 0.62F, 0.3F);
                }
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
                matrixStack.scale(2.8f, 2.8f, 2.8f);
                this.heldItemRenderer.renderItem(livingEntity, worker.getCarryItemStack(), ModelTransformationMode.GROUND, false, matrixStack, vertexConsumerProvider, i);
            } else if (worker.getTaskValue() == 2) {
                if (this.getContextModel() instanceof ModelWithArms modelWithArms) {
                    modelWithArms.setArmAngle(Arm.RIGHT, matrixStack);
                }
                matrixStack.translate(0.0F, 0.6F, 0.0F);
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                matrixStack.scale(1.3f, 1.3f, 1.3f);
                this.heldItemRenderer.renderItem(livingEntity, HAMMER, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, false, matrixStack, vertexConsumerProvider, i);
            }
            matrixStack.pop();
            info.cancel();
        }
    }
}
