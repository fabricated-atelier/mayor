package io.fabricatedatelier.mayor.init;

import java.util.Iterator;
import java.util.Map;

import io.fabricatedatelier.mayor.access.MinecraftClientAccess;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class Renderer {

    public static void initialize() {

        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (((MinecraftClientAccess) client).getStructureBlockMap() != null) {
                BlockHitResult blockHitResult = findCrosshairTarget(client.player);

                BlockPos origin = ((MinecraftClientAccess) client).getOriginBlockPos();
                if (origin != null || (blockHitResult != null && !client.world.getBlockState(blockHitResult.getBlockPos()).isAir())) {
                    Map<BlockPos, BlockState> blockMap = ((MinecraftClientAccess) client).getStructureBlockMap();
                    Iterator<Map.Entry<BlockPos, BlockState>> iterator = blockMap.entrySet().iterator();

                    if (origin == null) {
                        origin = blockHitResult.getBlockPos();
                    }
                    context.matrixStack().push();
                    context.matrixStack().translate(-context.camera().getPos().getX(), -context.camera().getPos().getY(), -context.camera().getPos().getZ());

                    while (iterator.hasNext()) {
                        Map.Entry<BlockPos, BlockState> entry = iterator.next();
                        renderBlock(client, context.matrixStack(), context.consumers(), origin.add(entry.getKey()), entry.getValue());// .multiply(-1)
                    }
                    context.matrixStack().pop();
                }
            }
        });

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

    @Nullable
    public static BlockHitResult findCrosshairTarget(Entity camera) {
        HitResult hitResult = camera.raycast(300D, 0.0f, false);
        return hitResult instanceof BlockHitResult blockHitResult ? blockHitResult : null;
    }

}
