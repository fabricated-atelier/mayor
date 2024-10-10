package io.fabricatedatelier.mayor.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.fabricatedatelier.mayor.access.BuiltinModelItemRendererAccess;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.init.MayorClientEvents;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.mixin.access.BlockRenderManagerAccess;
import io.fabricatedatelier.mayor.screen.MayorScreen;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.StructureData;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL30C;

import java.util.HashMap;
import java.util.Map;

public class RenderUtil {

    public static void renderMayorHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();
        if (mayorManager.isInMajorView()) {
            if (mayorManager.getVillageData() != null && StructureHelper.findCrosshairTarget(client.player) instanceof BlockHitResult blockHitResult) {
                for (Map.Entry<BlockPos, StructureData> entry : mayorManager.getVillageData().getStructures().entrySet()) {
                    if (entry.getValue().getBlockBox().contains(blockHitResult.getBlockPos())) {
                        String structureName = StringUtil.getStructureName(entry.getValue().getIdentifier()) + " " + Text.translatable("mayor.screen.level", entry.getValue().getLevel()).getString();
                        context.drawText(client.textRenderer, structureName, 10, 10, 0xFFFFFF, false);
                        break;
                    }
                }
            }
        }
    }

    private static Map<BlockPos, BlockState> FLUID_MAP = new HashMap<>();


    public static void renderVillageStructure(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        if (mayorManager.isInMajorView()) {
            if (mayorManager.getVillageData() != null && !mayorManager.getVillageData().getConstructions().isEmpty()) {
                for (Map.Entry<BlockPos, ConstructionData> entry : mayorManager.getVillageData().getConstructions().entrySet()) {
                    if (client.worldRenderer.isRenderingReady(entry.getKey())) {
                        // Todo: Maybe change this
                        BlockBox box = entry.getValue().getStructureData().getBlockBox();
                        renderParticlePole(client, box.getMinX(), box.getMinY(), box.getMinZ(), 1);
                        renderParticlePole(client, box.getMinX(), box.getMinY(), box.getMaxZ(), 2);
                        renderParticlePole(client, box.getMaxX(), box.getMinY(), box.getMaxZ(), 3);
                        renderParticlePole(client, box.getMaxX(), box.getMinY(), box.getMinZ(), 4);
                    }
                }
            }
            if (mayorManager.getMayorStructure() != null) {
                BlockHitResult blockHitResult = StructureHelper.findCrosshairTarget(client.player);
                BlockPos origin = mayorManager.getStructureOriginBlockPos();
                if (origin != null || (blockHitResult != null && !client.world.getBlockState(blockHitResult.getBlockPos()).isAir())) {
                    Map<BlockPos, BlockState> blockMap = mayorManager.getMayorStructure().getBlockMap();


                    if (origin == null) {
                        origin = blockHitResult.getBlockPos();
                    }

                    context.matrixStack().push();
                    context.matrixStack().translate(-context.camera().getPos().getX(), -context.camera().getPos().getY(), -context.camera().getPos().getZ());

                    boolean canBuildStructure = StructureHelper.canPlaceStructure(mayorManager);

                    Framebuffer framebuffer = MayorClientEvents.ALPHA_FRAMEBUFFER.get();
                    framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
                    framebuffer.beginWrite(false);

                    GL30C.glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, client.getFramebuffer().fbo);
                    GL30C.glBlitFramebuffer(0, 0, framebuffer.textureWidth, framebuffer.textureHeight, 0, 0, client.getFramebuffer().textureWidth, client.getFramebuffer().textureHeight, GL30C.GL_DEPTH_BUFFER_BIT, GL30C.GL_NEAREST);
                    // render blocks
                    for (Map.Entry<BlockPos, BlockState> entry : blockMap.entrySet()) {
                        BlockPos pos = entry.getKey();
                        if (mayorManager.getStructureCentered()) {
                            pos = pos.add(-mayorManager.getMayorStructure().getSize().getX() / 2, 0, -mayorManager.getMayorStructure().getSize().getZ() / 2);
                        }

                        pos = pos.rotate(mayorManager.getStructureRotation());
                        BlockState state = entry.getValue().rotate(mayorManager.getStructureRotation());

                        renderBlock(client, context.matrixStack(), MayorClientEvents.IMMEDIATE, origin.add(pos), state, canBuildStructure);
                    }
                    MayorClientEvents.IMMEDIATE.draw();

                    if (!FLUID_MAP.isEmpty()) {
                        GlStateManager._depthMask(false); // Disable depth mask for rendering fluids
//                         render fluids
                        for (Map.Entry<BlockPos, BlockState> entry : FLUID_MAP.entrySet()) {
                            BlockPos pos = entry.getKey();
                            if (mayorManager.getStructureCentered()) {
                                pos = pos.add(-mayorManager.getMayorStructure().getSize().getX() / 2, 0, -mayorManager.getMayorStructure().getSize().getZ() / 2);
                            }

                            pos = pos.rotate(mayorManager.getStructureRotation());
                            BlockState state = entry.getValue().rotate(mayorManager.getStructureRotation());

                            renderFluid(client, context.matrixStack(), MayorClientEvents.IMMEDIATE, origin.add(pos), state);
                        }
                        GlStateManager._depthMask(true);
                        MayorClientEvents.IMMEDIATE.draw();
                    }

                    context.matrixStack().pop();

                    GlStateManager._depthMask(true);

                    client.getFramebuffer().beginWrite(false);

                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.backupProjectionMatrix();

                    framebuffer.draw(framebuffer.textureWidth, framebuffer.textureHeight, false);

                    RenderSystem.restoreProjectionMatrix();

                    FLUID_MAP.clear();
                }
            }
        }
    }

    public static void renderBlock(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, BlockPos blockPos, BlockState blockState, boolean canBuildStructure) {
        matrices.push();
        matrices.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        // May solve z fighting
        matrices.translate(.5, .5, .5);
        matrices.scale(1.0001f, 1.0001f, 1.0001f);
        matrices.translate(-.5, -.5, -.5);

        if (!blockState.getFluidState().isEmpty()) {
            FLUID_MAP.put(blockPos, blockState);
        } else {
            if (blockState.getRenderType().equals(BlockRenderType.ENTITYBLOCK_ANIMATED)) {
                ((BuiltinModelItemRendererAccess) ((BlockRenderManagerAccess) client.getBlockRenderManager()).getBuiltinModelItemRenderer()).setWorldAndBlockState(client.world, blockState);
            }
            client.getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vertexConsumerProvider, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, canBuildStructure ? OverlayTexture.DEFAULT_UV : 1);
        }
        matrices.pop();
    }

    public static void renderFluid(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, BlockPos blockPos, BlockState blockState) {
//        GlStateManager._depthMask(false); // Disable depth mask for rendering fluids

//                matrices.push();
//        matrices.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

//        var matrixStack = RenderSystem.getModelViewStack().pushMatrix();
//        matrixStack.mul(matrices.peek().getPositionMatrix());
//        RenderSystem.applyModelViewMatrix();

        client.getBlockRenderManager().renderFluid(blockPos, client.world, vertexConsumerProvider.getBuffer(RenderLayers.getFluidLayer(blockState.getFluidState())), blockState, blockState.getFluidState());

//        MayorClientEvents.IMMEDIATE.draw();
//        RenderSystem.getModelViewStack().popMatrix();
//        RenderSystem.applyModelViewMatrix();
//        matrices.pop();

        //        RenderSystem.getModelViewStack().popMatrix();
//        RenderSystem.applyModelViewMatrix();
//        GlStateManager._depthMask(true);
    }

    public static void renderCustomBackground(DrawContext context, int x, int y, int width, int height) {
        // top left
        context.drawTexture(MayorScreen.VILLAGE, x - 4, y, 25, 0, 7, 7, 128, 128);
        // top middle
        context.drawTexture(MayorScreen.VILLAGE, x - 4 + 7, y, width - 7, 7, 32, 0, 7, 7, 128, 128);
        // top right
        context.drawTexture(MayorScreen.VILLAGE, x - 4 + 7 + width - 7, y, 39, 0, 7, 7, 128, 128);
        // middle left
        context.drawTexture(MayorScreen.VILLAGE, x - 4, y + 7, 7, height - 7, 25, 7, 7, 7, 128, 128);
        // middle middle
        context.drawTexture(MayorScreen.VILLAGE, x - 4 + 7, y + 7, width - 7, height - 7, 32, 7, 7, 7, 128, 128);
        // middle right
        context.drawTexture(MayorScreen.VILLAGE, x - 4 + 7 + width - 7, y + 7, 7, height - 7, 39, 7, 7, 7, 128, 128);
        // bottom left
        context.drawTexture(MayorScreen.VILLAGE, x - 4, y + height, 25, 14, 7, 7, 128, 128);
        // bottom middle
        context.drawTexture(MayorScreen.VILLAGE, x - 4 + 7, y + height, width - 7, 7, 32, 14, 7, 7, 128, 128);
        // bottom right
        context.drawTexture(MayorScreen.VILLAGE, x - 4 + 7 + width - 7, y + height, 39, 14, 7, 7, 128, 128);
    }

    private static void renderParticlePole(MinecraftClient client, int x, int y, int z, int edge) {
        addParticles(client, x, y, z);

        if (edge == 1 || edge == 2) addParticles(client, x + 0.5f, y, z);
        if (edge == 3 || edge == 4) addParticles(client, x - 0.5f, y, z);
        if (edge == 2 || edge == 3) addParticles(client, x, y, z - 0.5f);
        if (edge == 1 || edge == 4) addParticles(client, x, y, z + 0.5f);

        if (edge == 1) addParticles(client, x + 0.5f, y, z + 0.5f);
        else if (edge == 2) addParticles(client, x + 0.5f, y, z - 0.5f);
        else if (edge == 3) addParticles(client, x - 0.5f, y, z - 0.5f);
        else if (edge == 4) addParticles(client, x - 0.5f, y, z + 0.5f);
    }

    private static void addParticles(MinecraftClient client, float x, float y, float z) {
        for (float i = 0; i <= 1; i += 0.5f) {
            client.particleManager.addParticle(ParticleTypes.END_ROD, x, y + i, z, 0, 0, 0);
        }
    }

}
