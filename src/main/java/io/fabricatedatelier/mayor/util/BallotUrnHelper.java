package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.access.BallotUrnAccess;
import io.fabricatedatelier.mayor.config.MayorConfig;
import io.fabricatedatelier.mayor.init.MayorComponents;
import io.fabricatedatelier.mayor.init.MayorItems;
import io.fabricatedatelier.mayor.state.VillageData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BallotUrnHelper {

    public static void clientTick(World world, BlockPos pos, BlockState state, DecoratedPotBlockEntity blockEntity) {
        if (blockEntity instanceof BallotUrnAccess ballotUrnAccess && !ballotUrnAccess.validated()) {
            return;
        }
        if (world.getRandom().nextFloat() <= 0.3f) {
            world.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + world.getRandom().nextFloat(), pos.getY() + world.getRandom().nextFloat(), pos.getZ() + world.getRandom().nextFloat(), 0, 0, 0);
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, DecoratedPotBlockEntity blockEntity) {
        if (world.getTime() % 20 == 0) {
            if (blockEntity instanceof BallotUrnAccess ballotUrnAccess) {
                if (!ballotUrnAccess.validated()) {
                    return;
                }
                if (world.getRandom().nextFloat() <= 0.1f) {
                    blockEntity.wobble(DecoratedPotBlockEntity.WobbleType.POSITIVE);
                }
                if ((int) (world.getTime() - ballotUrnAccess.getVoteStartTime()) > ballotUrnAccess.getVoteTicks()) {
                    endElection((ServerWorld) world, pos);
                }
            }
        }


    }

    // Todo: Add block info to general player mayor screen
    // Call it village screen

    public static void startElection(ServerWorld serverWorld, BlockPos blockPos, int voteTicks) {
        if (serverWorld.getBlockState(blockPos).isOf(Blocks.DECORATED_POT) && serverWorld.getBlockEntity(blockPos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity && decoratedPotBlockEntity.getSherds().stream().stream().allMatch(item -> item.equals(MayorItems.BALLOT_POTTERY_SHERD)) && decoratedPotBlockEntity instanceof BallotUrnAccess ballotUrnAccess) {
            VillageData villageData = StateHelper.getClosestVillage(serverWorld, blockPos);
            if (villageData != null && villageData.getBallotUrnPos() == null) {
                if (villageData.getMayorPlayerUuid() == null || (int) (serverWorld.getTime() - villageData.getMayorPlayerTime()) > MayorConfig.CONFIG.instance().minTickMayorTime) {
                    villageData.setBallotUrnPos(blockPos);

                    ballotUrnAccess.setValidated(true);
                    ballotUrnAccess.setVoteStartTime(serverWorld.getTime());
                    ballotUrnAccess.setVoteTicks(voteTicks);

                    decoratedPotBlockEntity.markDirty();

                    for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                        if (serverWorld.getPlayerByUuid(uuid) instanceof ServerPlayerEntity player) {
                            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                            player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.mayor_election_start")));
                        }
                    }
                    StateHelper.getMayorVillageState(serverWorld).markDirty();
                }
            }

        }
    }

    public static void endElection(ServerWorld serverWorld, BlockPos blockPos) {
        if (serverWorld.getBlockState(blockPos).isOf(Blocks.DECORATED_POT) && serverWorld.getBlockEntity(blockPos) instanceof DecoratedPotBlockEntity decoratedPotBlockEntity && decoratedPotBlockEntity.getSherds().stream().stream().allMatch(item -> item.equals(MayorItems.BALLOT_POTTERY_SHERD)) && decoratedPotBlockEntity instanceof BallotUrnAccess ballotUrnAccess && ballotUrnAccess.validated()) {
            VillageData villageData = StateHelper.getClosestVillage(serverWorld, blockPos);
            if (villageData != null) {
                if (villageData.getBallotUrnPos() != null && villageData.getBallotUrnPos().equals(blockPos)) {
                    if ((int) (serverWorld.getTime() - ballotUrnAccess.getVoteStartTime()) > ballotUrnAccess.getVoteTicks()) {

                        Map<UUID, Integer> voteMap = new HashMap<>();
                        int count = 0;
                        for (int i = 1; i < ballotUrnAccess.size(); i++) {
                            if (ballotUrnAccess.getStack(i).isOf(MayorItems.BALLOT_PAPER)) {
                                if (ballotUrnAccess.getStack(i).get(MayorComponents.VOTE_UUID) != null) {
                                    UUID uuid = ballotUrnAccess.getStack(i).get(MayorComponents.VOTE_UUID);
                                    if (voteMap.containsKey(uuid)) {
                                        voteMap.put(uuid, voteMap.get(uuid) + 1);
                                    } else {
                                        voteMap.put(uuid, 1);
                                    }
                                }
                                count++;
                            }
                        }

                        ballotUrnAccess.setValidated(false);
                        ballotUrnAccess.setVotedPlayerUuids(new ArrayList<>());
                        ballotUrnAccess.setVoteTicks(0);
                        ballotUrnAccess.setVoteStartTime(0);

                        serverWorld.updateListeners(blockPos, serverWorld.getBlockState(blockPos), serverWorld.getBlockState(blockPos), 0);
                        decoratedPotBlockEntity.markDirty();

                        Text text;

                        if (count >= MayorConfig.CONFIG.instance().minVoteCount) {
                            UUID electedMayorUuid = null;
                            int voteCount = 0;
                            for (Map.Entry<UUID, Integer> entry : voteMap.entrySet()) {
                                if (entry.getValue() > voteCount) {
                                    voteCount = entry.getValue();
                                    electedMayorUuid = entry.getKey();
                                }
                            }
                            if (electedMayorUuid != null) {
                                String playerName = "";
                                if (serverWorld.getPlayerByUuid(electedMayorUuid) != null) {
                                    serverWorld.getPlayerByUuid(electedMayorUuid).getName().getString();
                                } else {
                                    // New mayor is offline
                                    playerName = StringUtil.getOfflinePlayerUuidNames(serverWorld).getOrDefault(electedMayorUuid, "");
                                }
                                text = Text.translatable("mayor.village.mayor_election_end", playerName);
                                villageData.setMayorPlayerUuid(electedMayorUuid);
                                villageData.setMayorPlayerTime(serverWorld.getTime());
                                StateHelper.getMayorVillageState(serverWorld).markDirty();
                            } else {
                                text = Text.translatable("mayor.village.mayor_election_failed");
                            }
                        } else {
                            text = Text.translatable("mayor.village.mayor_election_failed");
                        }

                        for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                            if (serverWorld.getPlayerByUuid(uuid) instanceof ServerPlayerEntity player) {
                                player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                                player.networkHandler.sendPacket(new SubtitleS2CPacket(text));
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean voteMayor(ServerWorld serverWorld, BlockPos blockPos, PlayerEntity playerEntity, ItemStack itemStack) {
        if (itemStack.isOf(MayorItems.BALLOT_PAPER) && itemStack.get(MayorComponents.VOTE_UUID) != null) {
            VillageData villageData = StateHelper.getClosestVillage(serverWorld, blockPos);
            if (villageData != null) {
                if (villageData.getBallotUrnPos() != null && villageData.getBallotUrnPos().equals(blockPos)) {
                    if (serverWorld.getBlockEntity(blockPos) instanceof BallotUrnAccess ballotUrnAccess && ballotUrnAccess.validated() && !ballotUrnAccess.getVotedPlayerUuids().contains(playerEntity.getUuid())) {
                        ballotUrnAccess.addVotedPlayerUuid(playerEntity.getUuid());
                        ballotUrnAccess.addStack(itemStack.copy());
                        itemStack.decrement(1);

                        serverWorld.getBlockEntity(blockPos).markDirty();
                    }
                }
            }
        }
        return false;
    }

    public static void updateBallotUrn(ServerWorld serverWorld, BlockPos blockPos) {
        VillageData villageData = StateHelper.getClosestVillage(serverWorld, blockPos);
        if (villageData != null) {
            if (villageData.getBallotUrnPos() != null && villageData.getBallotUrnPos().equals(blockPos)) {
                villageData.setBallotUrnPos(null);
                for (UUID uuid : villageData.getCitizenData().getCitizens()) {
                    if (serverWorld.getPlayerByUuid(uuid) instanceof ServerPlayerEntity player) {
                        player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("mayor.village.news", villageData.getName())));
                        player.networkHandler.sendPacket(new SubtitleS2CPacket(Text.translatable("mayor.village.ballot_urn_destroy")));
                    }
                }
                StateHelper.getMayorVillageState(serverWorld).markDirty();
            }
        }
    }

}
