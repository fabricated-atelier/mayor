package io.fabricatedatelier.mayor.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.fabricatedatelier.mayor.state.MayorVillageState;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.MayorStateHelper;
import io.fabricatedatelier.mayor.util.StructureHelper;
import io.fabricatedatelier.mayor.util.VillageHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class Commands {

    private static final SuggestionProvider<ServerCommandSource> VILLAGE_BLOCKPOS_PROVIDER = (context, builder) -> CommandSource.suggestMatching(
            MayorStateHelper.getVillages(context.getSource().getWorld()).stream().map(VillageData::getCenterPos).map(blockPos -> blockPos.toShortString().replace(",", "")), builder);

    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("mayor").requires((serverCommandSource) -> {
                        return serverCommandSource.hasPermissionLevel(2);
                    })).then(CommandManager.literal("village").then(CommandManager.literal("create").executes((commandContext) -> {
                                        return executeVillageCommand(commandContext.getSource(), null, null, 0, null, 0);
                                    }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).suggests(VILLAGE_BLOCKPOS_PROVIDER).executes((commandContext) -> {
                                        return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, 0, null, 0);
                                    })))
                                    .then(CommandManager.literal("delete").executes((commandContext) -> {
                                        return executeVillageCommand(commandContext.getSource(), null, null, 0, null, 1);
                                    }).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).suggests(VILLAGE_BLOCKPOS_PROVIDER).executes((commandContext) -> {
                                        return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, 0, null, 1);
                                    })))
                                    .then(CommandManager.literal("info").executes((commandContext) -> {
                                        return executeVillageCommand(commandContext.getSource(), null, null, 0, null, 2);
                                    }))
                                    .then(CommandManager.literal("get").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).suggests(VILLAGE_BLOCKPOS_PROVIDER).executes((commandContext) -> {
                                                        return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, 0, null, 3);
                                                    })

                                                    .then(CommandManager.literal("mayor")

                                                            .then(CommandManager.literal("set").then(CommandManager.argument("player", EntityArgumentType.player()).executes((commandContext) -> {
                                                                return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), EntityArgumentType.getPlayer(commandContext, "player"), 0, null, 4);
                                                            })))

                                                            .then(CommandManager.literal("remove").executes((commandContext) -> {
                                                                return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, 0, null, 5);
                                                            }))

                                                            .then(CommandManager.literal("get").executes((commandContext) -> {
                                                                return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, 0, null, 6);
                                                            }))

                                                    )

                                                    .then(CommandManager.literal("level")
                                                            .then(CommandManager.literal("set").then(CommandManager.argument("level", IntegerArgumentType.integer()).executes((commandContext) -> {
                                                                return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, IntegerArgumentType.getInteger(commandContext, "level"), null, 7);
                                                            })))

                                                            .then(CommandManager.literal("get").executes((commandContext) -> {
                                                                return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, 0, null, 8);
                                                            }))
                                                    )

                                                    .then(CommandManager.literal("name")
                                                            .then(CommandManager.literal("set").then(CommandManager.argument("name", StringArgumentType.string()).executes((commandContext) -> {
                                                                return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, 0, StringArgumentType.getString(commandContext, "name"), 9);
                                                            })))

                                                            .then(CommandManager.literal("get").executes((commandContext) -> {
                                                                return executeVillageCommand(commandContext.getSource(), BlockPosArgumentType.getValidBlockPos(commandContext, "pos"), null, 0, null, 10);
                                                            }))
                                                    )

                                    ))
                    )
            );
        });
    }

    // code: 0 - create, 1 - delete, 2 - info, 3 - get, 4 - set mayor, 5 - remove mayor, 6 - get mayor, 7 - set level, 8 - get level, 9 - set name, 10 - get name
    private static int executeVillageCommand(ServerCommandSource source, @Nullable BlockPos blockPos, @Nullable ServerPlayerEntity serverPlayerEntity, int level, @Nullable String name, int code) {
        if (blockPos == null && source.getPlayer() == null) {
            source.sendFeedback(() -> Text.translatable("commands.mayor.something_went_wrong"), false);
        } else {
            MayorVillageState mayorVillageState = MayorStateHelper.getMayorVillageState(source.getWorld());
            if (code == 0) {
                // Create
                BlockPos pos = blockPos != null ? blockPos : source.getPlayer().getBlockPos();
                VillageData villageData = mayorVillageState.createVillageData(pos);
                if(villageData != null) {
                    villageData.setBiomeCategory(StructureHelper.getBiomeCategory(source.getWorld().getBiome(pos)));
                    source.sendFeedback(() -> Text.translatable("commands.mayor.created_village", pos.toShortString()), true);
                }else{
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_creation_failed", pos.toShortString()), false);
                }
            } else if (code == 1) {
                // Delete
                VillageData villageData = blockPos != null ? mayorVillageState.getVillageData(blockPos) : MayorStateHelper.getClosestVillage(source.getWorld(), source.getPlayer().getBlockPos());
                if (villageData != null) {
                    mayorVillageState.deleteVillageData(villageData.getCenterPos());
                    source.sendFeedback(() -> Text.translatable("commands.mayor.deleted_village", villageData.getName(), villageData.getCenterPos().toShortString()), true);
                } else {
                    Text text = blockPos != null ? Text.translatable("commands.mayor.village_not_found") : Text.translatable("commands.mayor.village_nearby_not_found");
                    source.sendFeedback(() -> text, false);
                }
            } else if (code == 2) {
                // Info
                if (source.getPlayer() == null) {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_nearby_not_found"), false);
                } else {
                    VillageData villageData = MayorStateHelper.getClosestVillage(source.getWorld(), source.getPlayer().getBlockPos());
                    if (villageData != null) {
                        source.sendFeedback(() -> Text.translatable("commands.mayor.village_pos_info", villageData.getCenterPos().toShortString()), true);
                    } else {
                        source.sendFeedback(() -> Text.translatable("commands.mayor.village_nearby_not_found"), false);
                    }
                }
            } else if (code == 3 && blockPos != null) {
                // Get
                VillageData villageData = mayorVillageState.getVillageData(blockPos);
                if (villageData != null) {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_info", villageData.getName(), villageData.getLevel(), villageData.getVillagers().size(), villageData.getIronGolems().size(), villageData.getStructures().size()), false);
                    if (villageData.getMayorPlayerUuid() != null) {
                        if (source.getServer().getPlayerManager().getPlayer(villageData.getMayorPlayerUuid()) != null) {
                            source.sendFeedback(() -> Text.translatable("commands.mayor.village_mayor_info", villageData.getName(), villageData.getMayorPlayerUuid(), source.getServer().getPlayerManager().getPlayer(villageData.getMayorPlayerUuid()).getName()), false);
                        }
                    } else {
                        source.sendFeedback(() -> Text.translatable("commands.mayor.village_no_mayor_info", villageData.getName()), false);
                    }
                } else {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_not_found", blockPos.toShortString()), false);
                }
            } else if (code == 4 && serverPlayerEntity != null) {
                // Set mayor
                VillageData villageData = mayorVillageState.getVillageData(blockPos);
                if (villageData != null) {
                    if (villageData.getMayorPlayerUuid() == null || !villageData.getMayorPlayerUuid().equals(serverPlayerEntity.getUuid())) {
                        villageData.setMayorPlayerTime(source.getWorld().getTime());
                    }
                    villageData.setMayorPlayerUuid(serverPlayerEntity.getUuid());
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_set_mayor", serverPlayerEntity.getName(), villageData.getName(), blockPos.toShortString()), true);
                } else {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_not_found", blockPos.toShortString()), false);
                }
            } else if (code == 5) {
                // Remove mayor
                VillageData villageData = mayorVillageState.getVillageData(blockPos);
                if (villageData != null) {
                    if (villageData.getMayorPlayerUuid() != null) {
                        villageData.setMayorPlayerTime(0);
                        villageData.setMayorPlayerUuid(null);
                        source.sendFeedback(() -> Text.translatable("commands.mayor.village_remove_mayor", villageData.getName(), blockPos.toShortString()), true);
                    } else {
                        source.sendFeedback(() -> Text.translatable("commands.mayor.village_no_mayor_info", villageData.getName()), false);
                    }

                } else {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_not_found", blockPos.toShortString()), false);
                }
            } else if (code == 6) {
                // Get mayor
                VillageData villageData = mayorVillageState.getVillageData(blockPos);
                if (villageData != null) {
                    if (villageData.getMayorPlayerUuid() != null) {
                        Text text;
                        if (source.getServer().getPlayerManager().getPlayer(villageData.getMayorPlayerUuid()) != null) {
                            text = source.getServer().getPlayerManager().getPlayer(villageData.getMayorPlayerUuid()).getName();
                        } else {
                            text = Text.of(villageData.getMayorPlayerUuid().toString());
                        }
                        source.sendFeedback(() -> Text.translatable("commands.mayor.village_mayor_info", villageData.getName(), text), false);
                    } else {
                        source.sendFeedback(() -> Text.translatable("commands.mayor.village_no_mayor_info", villageData.getName()), false);
                    }

                } else {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_not_found", blockPos.toShortString()), false);
                }
            } else if (code == 7) {
                // Set level
                VillageData villageData = mayorVillageState.getVillageData(blockPos);
                if (villageData != null && level > 0) {
                    villageData.setLevel(Math.min(level, VillageHelper.VILLAGE_MAX_LEVEL));
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_set_level", villageData.getLevel(), villageData.getName(), blockPos.toShortString()), true);
                } else {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_not_found", blockPos.toShortString()), false);
                }
            } else if (code == 8) {
                // Get level
                VillageData villageData = mayorVillageState.getVillageData(blockPos);
                if (villageData != null) {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_get_level", villageData.getName(), villageData.getLevel()), false);
                } else {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_not_found", blockPos.toShortString()), false);
                }
            } else if (code == 9 && name != null) {
                // Set name
                VillageData villageData = mayorVillageState.getVillageData(blockPos);
                if (villageData != null) {
                    villageData.setName(name);
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_set_name", name, blockPos.toShortString()), true);
                } else {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_not_found", blockPos.toShortString()), false);
                }
            } else if (code == 10) {
                // Get level
                VillageData villageData = mayorVillageState.getVillageData(blockPos);
                if (villageData != null) {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_get_name", blockPos.toShortString(), villageData.getName()), false);
                } else {
                    source.sendFeedback(() -> Text.translatable("commands.mayor.village_not_found", blockPos.toShortString()), false);
                }
            }
            mayorVillageState.markDirty();
        }
        return 0;
    }
}
