package io.fabricatedatelier.mayor.util;

import java.util.Map;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.fabricatedatelier.mayor.access.BuiltinModelItemRendererAccess;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.mixin.access.BlockRenderManagerAccess;
import io.fabricatedatelier.mayor.state.ConstructionData;
import io.fabricatedatelier.mayor.state.StructureData;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

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

    public static void renderVillageStructure(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        MayorManager mayorManager = ((MayorManagerAccess) client.player).getMayorManager();

        if (mayorManager.isInMajorView()) {
            if (mayorManager.getVillageData() != null && !mayorManager.getVillageData().getConstructions().isEmpty()) {
                for (Map.Entry<BlockPos, ConstructionData> entry : mayorManager.getVillageData().getConstructions().entrySet()) {
                    if (client.worldRenderer.isRenderingReady(entry.getKey())) {
                        // Todo: Maybe change this
                        BlockBox box = entry.getValue().getStructureData().getBlockBox();
                        client.particleManager.addParticle(ParticleTypes.END_ROD, box.getMinX(), box.getMinY(), box.getMinZ(), 0,0,0);
                        client.particleManager.addParticle(ParticleTypes.END_ROD, box.getMinX()+1, box.getMinY(), box.getMinZ(), 0,0,0);
                        client.particleManager.addParticle(ParticleTypes.END_ROD, box.getMinX(), box.getMinY(), box.getMinZ()+1, 0,0,0);
                        client.particleManager.addParticle(ParticleTypes.END_ROD, box.getMaxX(), box.getMaxY(), box.getMaxZ(), 0,0,0);
                        client.particleManager.addParticle(ParticleTypes.END_ROD, box.getMaxX()-1, box.getMaxY(), box.getMaxZ(), 0,0,0);
                        client.particleManager.addParticle(ParticleTypes.END_ROD, box.getMaxX(), box.getMaxY(), box.getMaxZ()-1, 0,0,0);
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
                    for (Map.Entry<BlockPos, BlockState> entry : blockMap.entrySet()) {
                        BlockPos pos = entry.getKey();
                        if (mayorManager.getStructureCentered()) {
                            pos = pos.add(-mayorManager.getMayorStructure().getSize().getX() / 2, 0, -mayorManager.getMayorStructure().getSize().getZ() / 2);
                        }

                        pos = pos.rotate(mayorManager.getStructureRotation());
                        BlockState state = entry.getValue().rotate(mayorManager.getStructureRotation());

                        renderBlock(client, context.matrixStack(), context.consumers(), origin.add(pos), state, canBuildStructure);
                    }

                    context.matrixStack().pop();
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
            GlStateManager._depthMask(false); // Disable depth mask for rendering fluids
            var matrixStack = RenderSystem.getModelViewStack().pushMatrix();
            matrixStack.mul(matrices.peek().getPositionMatrix());
            RenderSystem.applyModelViewMatrix();
            client.getBlockRenderManager().renderFluid(blockPos, client.world, vertexConsumerProvider.getBuffer(RenderLayers.getFluidLayer(blockState.getFluidState())), blockState, blockState.getFluidState());
            RenderSystem.getModelViewStack().popMatrix();
            RenderSystem.applyModelViewMatrix();
            GlStateManager._depthMask(true);
        } else {
            if (blockState.getRenderType().equals(BlockRenderType.ENTITYBLOCK_ANIMATED)) {
                ((BuiltinModelItemRendererAccess) ((BlockRenderManagerAccess) client.getBlockRenderManager()).getBuiltinModelItemRenderer()).setWorldAndBlockState(client.world, blockState);
            }
            client.getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vertexConsumerProvider, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, canBuildStructure ? OverlayTexture.DEFAULT_UV : 1);
        }
        matrices.pop();
    }


}
