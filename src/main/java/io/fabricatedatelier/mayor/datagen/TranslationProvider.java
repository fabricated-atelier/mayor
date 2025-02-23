package io.fabricatedatelier.mayor.datagen;

import io.fabricatedatelier.mayor.Mayor;
import io.fabricatedatelier.mayor.init.MayorBlocks;
import io.fabricatedatelier.mayor.init.MayorItems;
import io.fabricatedatelier.mayor.init.MayorKeyBind;
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
        translationBuilder.add(TagProvider.ItemTags.LUMBER_STORAGE_STORABLE, cleanStringFromIdentifier(TagProvider.ItemTags.LUMBER_STORAGE_STORABLE.id()));
        translationBuilder.add(TagProvider.ItemTags.STONE_STORAGE_STORABLE, cleanStringFromIdentifier(TagProvider.ItemTags.STONE_STORAGE_STORABLE.id()));

        translationBuilder.add(MayorBlocks.LUMBER_STORAGE, cleanStringFromIdentifier(Registries.BLOCK.getId(MayorBlocks.LUMBER_STORAGE)));
        translationBuilder.add(MayorBlocks.STONE_STORAGE, cleanStringFromIdentifier(Registries.BLOCK.getId(MayorBlocks.STONE_STORAGE)));

        translationBuilder.add(MayorItems.DECONSTRUCTION_HAMMER, cleanStringFromIdentifier(Registries.ITEM.getId(MayorItems.DECONSTRUCTION_HAMMER)));

        translationBuilder.add(MayorBlocks.CONSTRUCTION_TABLE, cleanStringFromIdentifier(Registries.BLOCK.getId(MayorBlocks.CONSTRUCTION_TABLE)));
        translationBuilder.add(MayorBlocks.DESK, cleanStringFromIdentifier(Registries.BLOCK.getId(MayorBlocks.DESK)));
        translationBuilder.add(MayorBlocks.POLE, cleanStringFromIdentifier(Registries.BLOCK.getId(MayorBlocks.POLE)));
        translationBuilder.add(MayorBlocks.WOODCUTTER, cleanStringFromIdentifier(Registries.BLOCK.getId(MayorBlocks.WOODCUTTER)));
        translationBuilder.add(MayorBlocks.MINER_TABLE, cleanStringFromIdentifier(Registries.BLOCK.getId(MayorBlocks.MINER_TABLE)));

        translationBuilder.add(MayorItems.BALLOT_POTTERY_SHERD, cleanStringFromIdentifier(Registries.ITEM.getId(MayorItems.BALLOT_POTTERY_SHERD)));
        translationBuilder.add(MayorItems.BALLOT_PAPER, cleanStringFromIdentifier(Registries.ITEM.getId(MayorItems.BALLOT_PAPER)));

        for (MayorKeyBind.MayorKeyBindCategory entry : MayorKeyBind.MayorKeyBindCategory.values()) {
            translationBuilder.add(entry.getTranslation(), cleanStringFromTranslation(entry.getTranslation()));
        }

        // not available on server side
        /*for (MayorKeyBind entry : MayorKeyBind.values()) {
            translationBuilder.add(entry.getTranslation(), cleanStringFromTranslation(entry.getTranslation()));
        }*/

        // Load an existing language file.
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/%s/lang/en_us.existing.json".formatted(Mayor.MODID)).orElseThrow();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }


    public static String cleanStringFromIdentifier(Identifier identifier) {
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

    public static String cleanStringFromTranslation(String translation) {
        String[] words = List.of(translation.split("\\.")).getLast().split("_");
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
