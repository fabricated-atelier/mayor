package io.fabricatedatelier.mayor.item;

import io.fabricatedatelier.mayor.init.MayorComponents;
import io.fabricatedatelier.mayor.network.packet.BallotPaperScreenPacket;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BallotPaperItem extends Item {

    public BallotPaperItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            Optional<UUID> votedUuid = Optional.empty();
            Optional<String> votedName = Optional.empty();
            Map<UUID, String> availablePlayers = new HashMap<>();

            availablePlayers.putAll(StringUtil.getOnlinePlayerUuidNames(serverWorld));
            availablePlayers.putAll(StringUtil.getOfflinePlayerUuidNames(serverWorld));

            if (user.getStackInHand(hand).get(MayorComponents.VOTE_UUID) != null) {
                votedUuid = Optional.ofNullable(user.getStackInHand(hand).get(MayorComponents.VOTE_UUID));
                if (votedUuid.isPresent()) {
                    votedName = Optional.ofNullable(availablePlayers.get(votedUuid.get()));
                }
            }
            new BallotPaperScreenPacket(votedUuid, votedName, availablePlayers).sendPacket((ServerPlayerEntity) user);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }


}
