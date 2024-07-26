package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.mixin.access.StructureTemplateAccess;
import io.fabricatedatelier.mayor.network.packet.OriginBlockPosPacket;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.StructureTemplate.StructureBlockInfo;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;

public class Events {

    public static void initialize() {

        UseItemCallback.EVENT.register((PlayerEntity player, World world, Hand hand) -> {
            if (player instanceof ServerPlayerEntity serverPlayerEntity && player.getStackInHand(hand).isOf(Items.STICK)) {

                StructureTemplateManager structureTemplateManager = serverPlayerEntity.getServerWorld().getStructureTemplateManager();
                Optional<StructureTemplate> structure = structureTemplateManager.getTemplate(Identifier.of("minecraft:village/plains/houses/plains_small_house_7"));
                if (structure.get() instanceof StructureTemplateAccess structureTemplateAccess) {
                    Map<BlockPos, NbtCompound> blockMap = new HashMap<BlockPos, NbtCompound>();

               //     structure.get().getRotatedSize();
                    if (structureTemplateAccess.getBlockInfoLists().size() > 0) {

                        for (int i = 0; i < structureTemplateAccess.getBlockInfoLists().get(0).getAll().size(); i++) {
                            StructureBlockInfo structureBlockInfo = structureTemplateAccess.getBlockInfoLists().get(0).getAll().get(i);
                            if (structureBlockInfo.state().isAir()) {
                                // maybe sync air too?
                                continue;
                            }
                            BlockState blockState = structureBlockInfo.state();
                            if (structureBlockInfo.state().isOf(Blocks.JIGSAW)) {
                                String string = structureBlockInfo.nbt().getString("final_state");
                                try {
                                    blockState = BlockArgumentParser.block(world.createCommandRegistryWrapper(RegistryKeys.BLOCK), string, true).blockState();
                                } catch (CommandSyntaxException var15) {
                                    Mayor.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", string, structureBlockInfo.pos());
                                }
                            }

                            // BlockPos pos = structureBlockInfo.pos().offset(Direction.NORTH, 8); // BlockPos.ofFloored(structureBlockInfo.pos().getX() * -1, structureBlockInfo.pos().getY(),

                            // structureBlockInfo.pos().getZ() * -1);

                            // pos = pos.offset(Direction.WEST, 8);
                            // System.out.println(structureBlockInfo.pos());

                            //public static BlockPos transformAround(BlockPos pos, BlockMirror mirror, BlockRotation rotation, BlockPos pivot) {

                            blockMap.put(structureBlockInfo.pos().up(), NbtHelper.fromBlockState(blockState));
                        }
                        ServerPlayNetworking.send(serverPlayerEntity, new StructurePacket(blockMap));
                    }

                }
                BlockPos origin = Renderer.findCrosshairTarget(serverPlayerEntity) != null ? Renderer.findCrosshairTarget(serverPlayerEntity).getBlockPos() : null;
                ServerPlayNetworking.send(serverPlayerEntity, new OriginBlockPosPacket(Optional.of(origin)));

            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }

}
