package io.fabricatedatelier.mayor.entity.custom.client;

import io.fabricatedatelier.mayor.entity.custom.CameraPullEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CameraPullEntityRenderer extends EntityRenderer<CameraPullEntity> {
    public CameraPullEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(CameraPullEntity entity) {
        return null;
    }

    @Override
    public void render(CameraPullEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
