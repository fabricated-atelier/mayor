package io.fabricatedatelier.mayor.util;

import java.util.Iterator;
import java.util.Map;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRendering;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderingImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class RenderUtil {

    public static void renderMayorHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
        if (mayorManager.isInMajorView()) {
            // && mayorManager.getStructureBlockMap() != null
//            context.drawText(client.textRenderer, Text.translatable("gui.mayor.structures"), 10, 10, 0xFFFFFF, true);
//            context.drawText(client.textRenderer, Text.translatable("gui.mayor.houses"), 16, 22, 0xFFFFFF, false);
//            context.drawText(client.textRenderer, Text.translatable("gui.mayor.streets"), 16, 32, 0xFFFFFF, false);
        }
    }

    public static void renderVillageStructure(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        if (mayorManager.isInMajorView() && mayorManager.getMayorStructure() != null) {
            BlockHitResult blockHitResult = StructureHelper.findCrosshairTarget(client.player);

            BlockPos origin = mayorManager.getOriginBlockPos();
            if (origin != null || (blockHitResult != null && !client.world.getBlockState(blockHitResult.getBlockPos()).isAir())) {
                Map<BlockPos, BlockState> blockMap = mayorManager.getMayorStructure().getBlockMap();


                if (origin == null) {
                    origin = blockHitResult.getBlockPos();
                }

                context.matrixStack().push();
                context.matrixStack().translate(-context.camera().getPos().getX(), -context.camera().getPos().getY(), -context.camera().getPos().getZ());

//                GlStateManager._depthMask(false); // Disable depth mask for rendering fluids
//                var matrixStack = RenderSystem.getModelViewStack().pushMatrix();
//                matrixStack.mul( context.matrixStack().peek().getPositionMatrix());
//                RenderSystem.applyModelViewMatrix();

                Iterator<Map.Entry<BlockPos, BlockState>> iterator = blockMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<BlockPos, BlockState> entry = iterator.next();

                    BlockPos pos = entry.getKey();
                    if (mayorManager.getStructureCentered()) {
                        pos = pos.add(-mayorManager.getMayorStructure().getSize().getX() / 2, 0, -mayorManager.getMayorStructure().getSize().getZ() / 2);
                    }

                    pos = pos.rotate(mayorManager.getStructureRotation());
                    BlockState state = entry.getValue().rotate(mayorManager.getStructureRotation());

                    renderBlock(client, context.matrixStack(), context.consumers(), origin.add(pos), state);
                }

//                RenderSystem.getModelViewStack().popMatrix();
//                RenderSystem.applyModelViewMatrix();
//                GlStateManager._depthMask(true);

                context.matrixStack().pop();
            }
        }
    }

    private static final FluidRenderer fluidRenderer = new FluidRenderer();

    public static void renderBlock(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, BlockPos blockPos, BlockState blockState) {
        matrices.push();
        matrices.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        // Solves z fighting
        matrices.translate(.5, .5, .5);
        matrices.scale(1.0001f, 1.0001f, 1.0001f);
        matrices.translate(-.5, -.5, -.5);
        if (!blockState.getFluidState().isEmpty()) {

//            GlStateManager._depthMask(false); // Disable depth mask for rendering fluids
//            var matrixStack = RenderSystem.getModelViewStack().pushMatrix();
//            matrixStack.mul(matrices.peek().getPositionMatrix());
//            RenderSystem.applyModelViewMatrix();
//            client.getBlockRenderManager().renderFluid(blockPos, client.world, vertexConsumerProvider.getBuffer(RenderLayers.getFluidLayer(blockState.getFluidState())), blockState, blockState.getFluidState());
//            RenderSystem.getModelViewStack().popMatrix();
//            RenderSystem.applyModelViewMatrix();
//            GlStateManager._depthMask(true);

//
        } else {
            client.getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vertexConsumerProvider, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
        }
        matrices.pop();
    }


}
