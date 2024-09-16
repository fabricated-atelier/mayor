package io.fabricatedatelier.mayor;

import io.fabricatedatelier.mayor.datagen.BlockLootTableProvider;
import io.fabricatedatelier.mayor.datagen.ModelProvider;
import io.fabricatedatelier.mayor.datagen.TagProvider;
import io.fabricatedatelier.mayor.datagen.TranslationProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MayorDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(ModelProvider::new);
		pack.addProvider(TranslationProvider::new);
		pack.addProvider(BlockLootTableProvider::new);
		TagProvider.registerAll(pack);
	}
}
