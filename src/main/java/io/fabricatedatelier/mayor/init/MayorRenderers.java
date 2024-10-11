package io.fabricatedatelier.mayor.init;

import io.fabricatedatelier.mayor.block.entity.client.DeskBlockEntityRenderer;
import io.fabricatedatelier.mayor.block.entity.client.LumberStorageBlockEntityRenderer;
import io.fabricatedatelier.mayor.screen.block.BallotUrnBlockScreen;
import io.fabricatedatelier.mayor.screen.block.DeskBlockScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

@Environment(EnvType.CLIENT)
public class MayorRenderers {

    public static void initialize() {
        BlockEntityRendererFactories.register(MayorBlockEntities.VILLAGE_STORAGE, context -> new LumberStorageBlockEntityRenderer<>());
        BlockEntityRendererFactories.register(MayorBlockEntities.DESK, DeskBlockEntityRenderer::new);

        HandledScreens.register(MayorBlockEntities.BALLOT_URN_SCREEN_HANDLER, BallotUrnBlockScreen::new);
        HandledScreens.register(MayorBlockEntities.DESK_SCREEN_HANDLER, DeskBlockScreen::new);
    }
}
