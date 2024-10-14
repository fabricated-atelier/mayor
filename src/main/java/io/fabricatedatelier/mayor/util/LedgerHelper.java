package io.fabricatedatelier.mayor.util;

import io.fabricatedatelier.mayor.block.entity.DeskBlockEntity;
import io.fabricatedatelier.mayor.init.MayorBlocks;
import io.fabricatedatelier.mayor.network.packet.LedgerPacket;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class LedgerHelper {

    /**
     code: 1 = donation, 2 = fee, 3 = work, 4 tax
     */
    public static void updateLedger(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos, int code, int amount) {
        if (serverPlayerEntity.getServerWorld().getBlockState(blockPos).isOf(MayorBlocks.DESK) && serverPlayerEntity.getServerWorld().getBlockEntity(blockPos) instanceof DeskBlockEntity deskBlockEntity && deskBlockEntity.isValidated()) {
            if (!deskBlockEntity.getBook().isEmpty()) {

                ItemStack itemStack = getLedger(serverPlayerEntity.getServerWorld(), blockPos);
                if (!itemStack.isEmpty()) {
                    String currentPage = "";
                    if (itemStack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT) != null) {
                        if (!itemStack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT).pages().isEmpty()) {
                            currentPage = itemStack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT).pages().getLast().raw();
                        }
                    }
                    new LedgerPacket(blockPos, "", currentPage, code, amount).sendPacket(serverPlayerEntity);
                }
            }
        }
    }

    /**
     code: -1 = normal, -2 new page
     */
    public static void updateLedger(ItemStack itemStack, String textUpdate, int code) {
        WritableBookContentComponent writableBookContentComponent = itemStack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (writableBookContentComponent != null) {
            List<RawFilteredPair<String>> list = writableBookContentComponent.pages();

            if (code == -2) {
                list.add(RawFilteredPair.of(textUpdate));
            } else {
                if (!list.isEmpty()) {
                    textUpdate = list.getLast().raw() + "\n" + textUpdate;
                }

                if (list.size() > 1) {
                    List<RawFilteredPair<String>> newList = new ArrayList<>();
                    for (int i = 0; i < list.size() - 1; i++) {
                        newList.add(list.get(i));
                    }
                    newList.add(RawFilteredPair.of(textUpdate));
                    list = newList;
                } else {
                    list = new ArrayList<>();
                    list.add(RawFilteredPair.of(textUpdate));
                }
            }
            writableBookContentComponent = new WritableBookContentComponent(list);
        } else {
            List<RawFilteredPair<String>> list = new ArrayList<>();
            list.add(RawFilteredPair.of(textUpdate));
            writableBookContentComponent = new WritableBookContentComponent(list);
        }
        itemStack.set(DataComponentTypes.WRITABLE_BOOK_CONTENT, writableBookContentComponent);
    }

    public static ItemStack getLedger(ServerWorld world, BlockPos blockPos) {
        if (world.getBlockState(blockPos).isOf(MayorBlocks.DESK) && world.getBlockEntity(blockPos) instanceof DeskBlockEntity deskBlockEntity && deskBlockEntity.isValidated()) {
            if (!deskBlockEntity.getBook().isEmpty()) {
                return deskBlockEntity.getBook();
            }
        }
        return ItemStack.EMPTY;
    }

    public static int countNewlines(String input) {
        return input.split("\n", -1).length;
    }

    public static String formatStringWithSpaces(String prefix, int number, TextRenderer textRenderer) {
        return formatStringWithSpaces(prefix, String.valueOf(number), textRenderer);
    }

    public static String formatStringWithSpaces(String prefix, String endfix, TextRenderer textRenderer) {
        int targetLength = 112;

        int remainingWidth = targetLength - (textRenderer.getWidth(prefix) + textRenderer.getWidth(endfix));
        if (remainingWidth % 2 == 0) {
            prefix = prefix + " ";
        } else {
            prefix = prefix + "\u200A";
        }
        endfix = " " + endfix;

        remainingWidth = targetLength - (textRenderer.getWidth(prefix) + textRenderer.getWidth(endfix));

        int dotCount = remainingWidth / textRenderer.getWidth(".");

        StringBuilder result = new StringBuilder(prefix);

        for (int i = 0; i < dotCount; i++) {
            result.append(".");
        }
        result.append(endfix);

        return result.toString();
    }

    public static boolean exceedsSerializedLengthLimit(Text text, RegistryWrapper.WrapperLookup lookup) {
        return Text.Serialization.toJsonString(text, lookup).length() > 32767;
    }

}
