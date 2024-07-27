package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureOriginPacket;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class Events {

    public static void initialize() {

        UseItemCallback.EVENT.register((PlayerEntity player, World world, Hand hand) -> {
            if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                if (player.getStackInHand(hand).isOf(Items.STICK)) {
                    // TEST
                    Identifier identifier = Identifier.of("minecraft:village/plains/houses/plains_small_house_7");
                    // TEST END
                    StructureHelper.updateMajorStructure(serverPlayerEntity, identifier, BlockRotation.NONE);

                    BlockPos origin = StructureHelper.findCrosshairTarget(serverPlayerEntity) != null ? StructureHelper.findCrosshairTarget(serverPlayerEntity).getBlockPos() : null;
                    ServerPlayNetworking.send(serverPlayerEntity, new StructureOriginPacket(Optional.of(origin)));

                    ServerPlayNetworking.send(serverPlayerEntity, new MayorViewPacket(true));
                } else {
                    ServerPlayNetworking.send(serverPlayerEntity, new MayorViewPacket(false));
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }

}
