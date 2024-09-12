package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.Mayor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class TranslationProvider extends FabricLanguageProvider {
    public TranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(TagProvider.ItemTags.LUMBER_STORAGE_STORABLE, "Lumber Storage");
        translationBuilder.add(TagProvider.ItemTags.STONE_STORAGE_STORABLE, "Stone Storage");

        // Load an existing language file.
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/%s/lang/en_us.existing.json".formatted(Mayor.MODID)).get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
