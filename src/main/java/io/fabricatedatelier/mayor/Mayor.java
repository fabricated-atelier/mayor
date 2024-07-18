package io.fabricatedatelier.mayor;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mayor implements ModInitializer {
	public static final String MODID = "mayor";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {

		LOGGER.info("No sir, we only take Emeralds as payment...");
	}
}