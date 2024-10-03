package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import io.fabricatedatelier.mayor.init.MayorVillagerUtilities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(VillagerResemblingModel.class)
public class VillagerResemblingModelMixin implements ModelWithArms {

    @Shadow
    @Mutable
    @Final
    private ModelPart root;
    @Shadow
    @Mutable
    @Final
    private ModelPart head;

    @Unique
    private ModelPart leftArm;
    @Unique
    private ModelPart rightArm;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initMixin(ModelPart root, CallbackInfo info) {
        this.leftArm = this.root.getChild("left_arm");
        this.rightArm = this.root.getChild("right_arm");
    }

    @Inject(method = "getModelData", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getModelDataMixin(CallbackInfoReturnable<ModelData> info, ModelData modelData, ModelPartData modelPartData) {
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(44, 22).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F))
                .uv(44, 26).cuboid(-2.0F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 2.0F, 0.0F));
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(44, 22).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F))
                .uv(44, 22).cuboid(-2.0F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 2.0F, 0.0F));
    }

    @Inject(method = "setAngles", at = @At("TAIL"))
    private void setAnglesMixin(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo info) {
        if (entity instanceof Builder builder && builder.getVillagerEntity().getVillagerData().getProfession().equals(MayorVillagerUtilities.BUILDER) && !builder.getVillagerEntity().isSleeping()) {
            this.head.xScale = 1.0005f;
            if (!builder.getCarryItemStack().isEmpty()) {
                if (builder.getTaskValue() == 1) {
                    this.head.pivotZ = 4f;
                    this.head.pivotY = 3.2f;
                    this.root.getChild(EntityModelPartNames.BODY).pitch = -0.5f;
                    this.root.getChild(EntityModelPartNames.BODY).pivotZ = 4.5f;
                    this.root.getChild(EntityModelPartNames.BODY).pivotY = 1.8f;
                    this.root.getChild(EntityModelPartNames.ARMS).pivotZ = 0.5f;
                    this.root.getChild(EntityModelPartNames.ARMS).pivotY = 5f;
                    this.root.getChild(EntityModelPartNames.ARMS).pitch = -0.85F;
                } else {
                    this.head.pivotZ = -5f;
                    this.head.pivotY = 3f;
                    this.root.getChild(EntityModelPartNames.BODY).pitch = 0.4f;
                    this.root.getChild(EntityModelPartNames.BODY).pivotZ = -4f;
                    this.root.getChild(EntityModelPartNames.BODY).pivotY = 1.8f;
                    this.root.getChild(EntityModelPartNames.ARMS).pitch = 0.8f;
                    this.root.getChild(EntityModelPartNames.ARMS).pivotZ = -2.5f;
                }
            } else if (builder.getTaskValue() == 2) {
                this.root.getChild(EntityModelPartNames.ARMS).visible = false;
            } else {
                this.root.getChild(EntityModelPartNames.ARMS).visible = true;
                this.rightArm.visible = false;
                this.leftArm.visible = false;
                renderNormalModel();
            }

//

            if (!this.root.getChild(EntityModelPartNames.ARMS).visible) {
                // hit animation
                this.rightArm.pitch = MathHelper.cos(animationProgress * 0.3332F + (float) Math.PI) * 0.85f - 1.9f;
                // walk animation
                // this.rightArm.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 2.0F * limbDistance * 0.5F;
                this.leftArm.pitch = MathHelper.cos(limbAngle * 0.6662F) * 2.0F * limbDistance * 0.5F;
            }
        } else {
            renderNormalModel();
            this.rightArm.visible = false;
            this.leftArm.visible = false;
        }
    }

    @Unique
    private void renderNormalModel() {
        this.head.pivotZ = 0f;
        this.head.pivotY = 0f;
        this.root.getChild(EntityModelPartNames.BODY).pitch = 0f;
        this.root.getChild(EntityModelPartNames.BODY).pivotZ = 0f;
        this.root.getChild(EntityModelPartNames.BODY).pivotY = 0f;
        this.root.getChild(EntityModelPartNames.ARMS).pitch = -0.75f;
        this.root.getChild(EntityModelPartNames.ARMS).pivotZ = -1f;
        this.root.getChild(EntityModelPartNames.ARMS).pivotY = 3f;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        if (arm.equals(Arm.LEFT)) {
            this.leftArm.rotate(matrices);
        } else {
            this.rightArm.rotate(matrices);
        }
    }
}
