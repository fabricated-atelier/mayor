package io.fabricatedatelier.mayor.init;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.access.MayorManagerAccess;
import io.fabricatedatelier.mayor.state.VillageData;
import io.fabricatedatelier.mayor.util.StateHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class MayorCompats {

    public static void initialize() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api")) {
            Placeholders.register(Mayor.identifierOf("mayor"), (ctx, arg) -> {
                if (ctx.hasPlayer()) {
                    BlockPos villagePos = ((MayorManagerAccess) ctx.player()).getMayorManager().getCitizenManager().getVillagePos();
                    if (villagePos != null) {
                        if (StateHelper.getVillage(ctx.world(), villagePos) instanceof VillageData villageData && ctx.player().getUuid().equals(villageData.getMayorPlayerUuid())) {
                            return PlaceholderResult.value(Text.translatable("mayor.type.mayor"));
                        } else {
                            return PlaceholderResult.value(Text.translatable("mayor.type.citizen"));
                        }
                    } else {
                        return PlaceholderResult.value("");
                    }
                } else {
                    return PlaceholderResult.invalid("No player!");
                }
            });
        }
    }

}
