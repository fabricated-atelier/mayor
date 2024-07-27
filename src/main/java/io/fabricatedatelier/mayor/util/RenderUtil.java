package io.fabricatedatelier.mayor.util;

import java.util.Iterator;
import java.util.Map;

import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class RenderUtil {

    public static void renderMayorHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
        if (mayorManager.isInMajorView()) {
            // && mayorManager.getStructureBlockMap() != null
            context.drawText(client.textRenderer, Text.translatable("gui.major.structures"), 10, 10, 0xFFFFFF, true);
            context.drawText(client.textRenderer, Text.translatable("gui.major.houses"), 16, 22, 0xFFFFFF, false);
            context.drawText(client.textRenderer, Text.translatable("gui.major.streets"), 16, 32, 0xFFFFFF, false);
        }
    }

    public static void renderVillageStructure(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
        if (mayorManager.isInMajorView() && mayorManager.getStructureBlockMap() != null) {
            BlockHitResult blockHitResult = StructureHelper.findCrosshairTarget(client.player);

            BlockPos origin = mayorManager.getOriginBlockPos();
            if (origin != null || (blockHitResult != null && !client.world.getBlockState(blockHitResult.getBlockPos()).isAir())) {
                Map<BlockPos, BlockState> blockMap = mayorManager.getStructureBlockMap();
                Iterator<Map.Entry<BlockPos, BlockState>> iterator = blockMap.entrySet().iterator();

                if (origin == null) {
                    origin = blockHitResult.getBlockPos();
                }
                context.matrixStack().push();
                context.matrixStack().translate(-context.camera().getPos().getX(), -context.camera().getPos().getY(), -context.camera().getPos().getZ());

                while (iterator.hasNext()) {
                    Map.Entry<BlockPos, BlockState> entry = iterator.next();
                    renderBlock(client, context.matrixStack(), context.consumers(), origin.add(entry.getKey()), entry.getValue());
                }
                context.matrixStack().pop();
            }
        }
    }

    public static void renderBlock(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, BlockPos blockPos, BlockState blockState) {
        matrices.push();
        matrices.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        // matrices.translate(.5, .5, .5);
        // matrices.scale(1.0001f, 1.0001f, 1.0001f);
        // matrices.translate(-.5, -.5, -.5);

        client.getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vertexConsumerProvider, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
