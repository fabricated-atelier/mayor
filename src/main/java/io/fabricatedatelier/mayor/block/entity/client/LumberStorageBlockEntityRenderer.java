package io.fabricatedatelier.mayor.block.entity.client;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.MayorProperties;
import io.fabricatedatelier.mayor.block.entity.LumberStorageBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class LumberStorageBlockEntityRenderer<T extends LumberStorageBlockEntity> implements BlockEntityRenderer<T> {
    private int tick = 0, rotTick = 0;

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        tick++;
        if (tick % 100 == 0) rotTick++;
        if (rotTick > 10) rotTick = 0;

        BlockState state = blockEntity.getCachedState();
        if (state.contains(AbstractVillageContainerBlock.POSITION) && state.get(AbstractVillageContainerBlock.POSITION).equals(MayorProperties.Position.SINGLE)) {
            int blockPerLog = 2;

            matrices.push();
            matrices.translate(0.2, 0.125, 0);
            renderLog(blockPerLog, client, matrices, light, overlay, vertexConsumers, blockEntity);
            matrices.pop();
        }
    }

    private void renderLog(int blockCount, MinecraftClient client, MatrixStack matrices,
                           int light, int overlay, VertexConsumerProvider vertexConsumers, T entity) {
        float logScale = 1f / blockCount;
        for (int i = 0; i < blockCount; i++) {
            matrices.push();
            matrices.scale(logScale, logScale, logScale);
            matrices.translate(0.5, 0.5, 0.5);
            matrices.translate(0, 0, i);
            matrices.scale(2f, 2f, 2f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            client.getItemRenderer().renderItem(new ItemStack(Blocks.SPRUCE_LOG),
                    ModelTransformationMode.FIXED, light, overlay,
                    matrices, vertexConsumers,
                    client.world, (int) entity.getPos().asLong());

            matrices.pop();
        }
    }
}
