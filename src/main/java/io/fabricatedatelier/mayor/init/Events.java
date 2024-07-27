package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.network.packet.MayorViewPacket;
import io.fabricatedatelier.mayor.network.packet.StructureOriginPacket;
import io.fabricatedatelier.mayor.util.StructureHelper;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class Events {

    public static void initialize() {

        UseItemCallback.EVENT.register((PlayerEntity player, World world, Hand hand) -> {
            if (player instanceof ServerPlayerEntity serverPlayerEntity) {

                MayorViewPacket viewPacket;
                if (player.getStackInHand(hand).isOf(Items.STICK)) {
                    // TEST
                    Identifier identifier = Identifier.ofVanilla("village/plains/houses/plains_small_house_7");
                    // TEST END
                    StructureHelper.updateMayorStructure(serverPlayerEntity, identifier, BlockRotation.NONE, false);

                    Optional<BlockHitResult> hitResult = Optional.ofNullable(StructureHelper.findCrosshairTarget(serverPlayerEntity));
                    Optional<BlockPos> origin = hitResult.map(BlockHitResult::getBlockPos);

                    new StructureOriginPacket(origin).sendPacket(serverPlayerEntity);
                    viewPacket = new MayorViewPacket(true);
                } else {
                    viewPacket = new MayorViewPacket(false);
                }
                viewPacket.sendPacket(serverPlayerEntity);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }

}
