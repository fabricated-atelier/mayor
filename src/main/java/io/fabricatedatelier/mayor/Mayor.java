package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.init.*;
import io.fabricatedatelier.mayor.network.CustomC2SNetworking;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mayor implements ModInitializer {
    public static final String MODID = "mayor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        Items.initialize();
        ItemGroups.initialize();

        Blocks.initialize();
        BlockEntities.initialize();

        NetworkPayloads.initialize();
        CustomC2SNetworking.initialize();

        LOGGER.info("No sir, we only take Emeralds as payment...");
    }

    public static Identifier identifierOf(String name) {
        return Identifier.of(MODID, name);
    }
}