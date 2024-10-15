package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.init.MayorBlocks;
import io.fabricatedatelier.mayor.init.MayorItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TranslationProvider extends FabricLanguageProvider {
    public TranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(TagProvider.ItemTags.LUMBER_STORAGE_STORABLE, cleanString(TagProvider.ItemTags.LUMBER_STORAGE_STORABLE.id()));
        translationBuilder.add(TagProvider.ItemTags.STONE_STORAGE_STORABLE, cleanString(TagProvider.ItemTags.STONE_STORAGE_STORABLE.id()));

        translationBuilder.add(MayorBlocks.LUMBER_STORAGE, cleanString(Registries.BLOCK.getId(MayorBlocks.LUMBER_STORAGE)));
        translationBuilder.add(MayorBlocks.STONE_STORAGE, cleanString(Registries.BLOCK.getId(MayorBlocks.STONE_STORAGE)));

        translationBuilder.add(MayorBlocks.CONSTRUCTION_TABLE, cleanString(Registries.BLOCK.getId(MayorBlocks.CONSTRUCTION_TABLE)));
        translationBuilder.add(MayorBlocks.DESK, cleanString(Registries.BLOCK.getId(MayorBlocks.DESK)));
        translationBuilder.add(MayorBlocks.POLE, cleanString(Registries.BLOCK.getId(MayorBlocks.POLE)));

        translationBuilder.add(MayorItems.DECONSTRUCTION_HAMMER, cleanString(Registries.ITEM.getId(MayorItems.DECONSTRUCTION_HAMMER)));

        translationBuilder.add(MayorItems.BALLOT_POTTERY_SHERD, cleanString(Registries.ITEM.getId(MayorItems.BALLOT_POTTERY_SHERD)));
        translationBuilder.add(MayorItems.BALLOT_PAPER, cleanString(Registries.ITEM.getId(MayorItems.BALLOT_PAPER)));

        // Load an existing language file.
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/%s/lang/en_us.existing.json".formatted(Mayor.MODID)).get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }

    /**
     * Provides String clean up for translation purposes.<br>
     * The process is as follows:
     * <ol>
     *     <li>get path of the given Identifier</li>
     *     <li>remove everything before the last <code>/</code></li>
     *     <li>replace <code>_</code> with space</li>
     *     <li>capitalizes first char and every char after a space</li>
     * </ol>
     *
     * @param identifier Identifier, which will be used to generate the translation
     * @return clean and human-readable translated String
     */
    public static String cleanString(Identifier identifier) {
        String[] words = List.of(identifier.getPath().split("/")).getLast().split("_");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            char capitalized = Character.toUpperCase(word.charAt(0));
            output.append(capitalized).append(word.substring(1));
            if (i < words.length - 1) {
                output.append(" ");
            }
        }
        return output.toString();
    }
}
