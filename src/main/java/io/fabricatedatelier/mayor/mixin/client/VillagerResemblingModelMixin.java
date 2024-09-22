package io.fabricatedatelier.mayor.mixin.client;

import io.fabricatedatelier.mayor.entity.villager.access.Builder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(VillagerResemblingModel.class)
public class VillagerResemblingModelMixin {

    @Shadow
    @Mutable
    @Final
    private ModelPart root;
    @Shadow
    @Mutable
    @Final
    private ModelPart head;

    @Inject(method = "setAngles", at = @At("TAIL"))
    private void setAnglesMixin(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo info) {
        if (entity instanceof Builder builder && !builder.getCarryItemStack().isEmpty() && !builder.getVillagerEntity().isSleeping()) {
            this.head.pivotZ = -5f;
            this.head.pivotY = 3f;
            this.root.getChild(EntityModelPartNames.BODY).pitch = 0.4f;
            this.root.getChild(EntityModelPartNames.BODY).pivotZ = -4f;
            this.root.getChild(EntityModelPartNames.BODY).pivotY = 1.8f;
            this.root.getChild(EntityModelPartNames.ARMS).pitch = 0.8f;
            this.root.getChild(EntityModelPartNames.ARMS).pivotZ = -2.5f;
        } else {
            this.head.pivotZ = 0f;
            this.head.pivotY = 0f;
            this.root.getChild(EntityModelPartNames.BODY).pitch = 0f;
            this.root.getChild(EntityModelPartNames.BODY).pivotZ = 0f;
            this.root.getChild(EntityModelPartNames.BODY).pivotY = 0f;
            this.root.getChild(EntityModelPartNames.ARMS).pitch = -0.75F;
            this.root.getChild(EntityModelPartNames.ARMS).pivotZ = -1f;
        }
    }
}
