package io.fabricatedatelier.mayor.screen;

import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.network.packet.StructurePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Colors;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Environment(EnvType.CLIENT)
public class MayorScreen extends Screen {
    //    public static final double SHIFT_SCROLL_AMOUNT = 7.0;
//    private static final Text USAGE_TEXT = Text.translatable("chat_screen.usage");
//    private static final int MAX_INDICATOR_TOOLTIP_WIDTH = 210;
//    private String chatLastMessage = "";
//    private int messageHistoryIndex = -1;
//    protected TextFieldWidget chatField;
//    private String originalChatText;
//    ChatInputSuggestor chatInputSuggestor;
    private final MayorManager mayorManager;

    private Map<MayorCategory.BuildingCategory, List<MayorStructure>> structureMap = new HashMap<>();

    public MayorScreen(MayorManager mayorManager) {
        super(Text.translatable("mayor_screen.title"));
        this.mayorManager = mayorManager;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int x = 10;
        int y = this.height / 2 - 10 * MayorCategory.BuildingCategory.values().length / 2;
        for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
            Text text = Text.translatable(MayorCategory.BuildingCategory.values()[i].name());
            if (isMouseWithinBounds(x, y, this.textRenderer.getWidth(text), 8, mouseX, mouseY)) {
                context.drawText(this.textRenderer, text, x, y, Colors.WHITE, false);
            } else {
                context.drawText(this.textRenderer, text, x, y, Colors.LIGHT_GRAY, false);
            }
            y += 10;
        }

        if (mayorManager.getVillageData() != null) {

        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = 10;
        int y = this.height / 2 - 10 * MayorCategory.BuildingCategory.values().length / 2;
        for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
            if (isMouseWithinBounds(x, y, this.textRenderer.getWidth(Text.translatable(MayorCategory.BuildingCategory.values()[i].name())), 8, mouseX, mouseY)) {
//                System.out.println("WITHIN " + i + " : "+structureMap.get(MayorCategory.BuildingCategory.values()[i]));
                if (structureMap.containsKey(MayorCategory.BuildingCategory.values()[i]) && structureMap.get(MayorCategory.BuildingCategory.values()[i]).size() > 0) {
                    mayorManager.setMayorStructure(structureMap.get(MayorCategory.BuildingCategory.values()[i]).get(0));
                    //  new StructurePacket(structureMap.get(MayorCategory.BuildingCategory.values()[i]).get(0), BlockRotation.NONE, false).sendClientPacket();
//                    System.out.println("SET STRUCTURE");
                    return true;
                }
            }
            y += 10;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void init() {
        this.structureMap.clear();

        if (MayorManager.mayorStructureMap.containsKey(mayorManager.getBiomeCategory())) {
            for (int i = 0; i < MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).size(); i++) {
                MayorCategory.BuildingCategory buildingCategory = MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i).getBuildingCategory();
                if (structureMap.containsKey(buildingCategory)) {
                    structureMap.get(buildingCategory).add(MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i));
                } else {
                    List<MayorStructure> list = new ArrayList<>();
                    list.add(MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i));
                    structureMap.put(buildingCategory, list);


                }
            }
        }
//        System.out.println(MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).size());
//        System.out.println(MayorManager.mayorStructureMap);
//        System.out.println(this.structureMap);
//        this.messageHistoryIndex = this.client.inGameHud.getChatHud().getMessageHistory().size();
//        this.chatField = new TextFieldWidget(this.client.advanceValidatingTextRenderer, 4, this.height - 12, this.width - 4, 12, Text.translatable("chat.editBox")) {
//            @Override
//            protected MutableText getNarrationMessage() {
//                return super.getNarrationMessage().append(net.minecraft.client.gui.screen.MayorScreen.this.chatInputSuggestor.getNarration());
//            }
//        };
//        this.chatField.setMaxLength(256);
//        this.chatField.setDrawsBackground(false);
//        this.chatField.setText(this.originalChatText);
//        this.chatField.setChangedListener(this::onChatFieldUpdate);
//        this.chatField.setFocusUnlocked(false);
//        this.addSelectableChild(this.chatField);
//        this.chatInputSuggestor = new ChatInputSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
//        this.chatInputSuggestor.setCanLeave(false);
//        this.chatInputSuggestor.refresh();
    }

//    @Override
//    protected void setInitialFocus() {
//        this.setInitialFocus(this.chatField);
//    }

//    @Override
//    public void resize(MinecraftClient client, int width, int height) {
//        String string = this.chatField.getText();
//        this.init(client, width, height);
//        this.setText(string);
//        this.chatInputSuggestor.refresh();
//    }

//    @Override
//    public void removed() {
//        this.client.inGameHud.getChatHud().resetScroll();
//    }

//    private void onChatFieldUpdate(String chatText) {
//        String string = this.chatField.getText();
//        this.chatInputSuggestor.setWindowActive(!string.equals(this.originalChatText));
//        this.chatInputSuggestor.refresh();
//    }
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
//            return true;
//        } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
//            this.client.setScreen(null);
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
//            this.sendMessage(this.chatField.getText(), true);
//            this.client.setScreen(null);
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_UP) {
//            this.setChatFromHistory(-1);
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
//            this.setChatFromHistory(1);
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
//            this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
//            return true;
//        } else if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
//            this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
//            return true;
//        } else {
//            return false;
//        }
//    }

//    @Override
//    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
//        verticalAmount = MathHelper.clamp(verticalAmount, -1.0, 1.0);
//        if (this.chatInputSuggestor.mouseScrolled(verticalAmount)) {
//            return true;
//        } else {
//            if (!hasShiftDown()) {
//                verticalAmount *= 7.0;
//            }
//
//            this.client.inGameHud.getChatHud().scroll((int)verticalAmount);
//            return true;
//        }
//    }

//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        if (this.chatInputSuggestor.mouseClicked((double)((int)mouseX), (double)((int)mouseY), button)) {
//            return true;
//        } else {
//            if (button == 0) {
//                ChatHud chatHud = this.client.inGameHud.getChatHud();
//                if (chatHud.mouseClicked(mouseX, mouseY)) {
//                    return true;
//                }
//
//                Style style = this.getTextStyleAt(mouseX, mouseY);
//                if (style != null && this.handleTextClick(style)) {
//                    this.originalChatText = this.chatField.getText();
//                    return true;
//                }
//            }
//
//            return this.chatField.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
//        }
//    }

//    @Override
//    protected void insertText(String text, boolean override) {
//        if (override) {
//            this.chatField.setText(text);
//        } else {
//            this.chatField.write(text);
//        }
//    }
//
//    public void setChatFromHistory(int offset) {
//        int i = this.messageHistoryIndex + offset;
//        int j = this.client.inGameHud.getChatHud().getMessageHistory().size();
//        i = MathHelper.clamp(i, 0, j);
//        if (i != this.messageHistoryIndex) {
//            if (i == j) {
//                this.messageHistoryIndex = j;
//                this.chatField.setText(this.chatLastMessage);
//            } else {
//                if (this.messageHistoryIndex == j) {
//                    this.chatLastMessage = this.chatField.getText();
//                }
//
//                this.chatField.setText(this.client.inGameHud.getChatHud().getMessageHistory().get(i));
//                this.chatInputSuggestor.setWindowActive(false);
//                this.messageHistoryIndex = i;
//            }
//        }
//    }

//    @Override
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        this.client.inGameHud.getChatHud().render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);
//        context.fill(2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));
//        this.chatField.render(context, mouseX, mouseY, delta);
//        super.render(context, mouseX, mouseY, delta);
//        context.getMatrices().push();
//        context.getMatrices().translate(0.0F, 0.0F, 200.0F);
//        this.chatInputSuggestor.render(context, mouseX, mouseY);
//        context.getMatrices().pop();
//        MessageIndicator messageIndicator = this.client.inGameHud.getChatHud().getIndicatorAt((double)mouseX, (double)mouseY);
//        if (messageIndicator != null && messageIndicator.text() != null) {
//            context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(messageIndicator.text(), 210), mouseX, mouseY);
//        } else {
//            Style style = this.getTextStyleAt((double)mouseX, (double)mouseY);
//            if (style != null && style.getHoverEvent() != null) {
//                context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
//            }
//        }
//    }
//
//    @Override
//    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
//    }
//
//    @Override
//    public boolean shouldPause() {
//        return false;
//    }
//
//    private void setText(String text) {
//        this.chatField.setText(text);
//    }
//
//    @Override
//    protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
//        messageBuilder.put(NarrationPart.TITLE, this.getTitle());
//        messageBuilder.put(NarrationPart.USAGE, USAGE_TEXT);
//        String string = this.chatField.getText();
//        if (!string.isEmpty()) {
//            messageBuilder.nextMessage().put(NarrationPart.TITLE, Text.translatable("chat_screen.message", string));
//        }
//    }
//
//    @Nullable
//    private Style getTextStyleAt(double x, double y) {
//        return this.client.inGameHud.getChatHud().getTextStyleAt(x, y);
//    }
//
//    public void sendMessage(String chatText, boolean addToHistory) {
//        chatText = this.normalize(chatText);
//        if (!chatText.isEmpty()) {
//            if (addToHistory) {
//                this.client.inGameHud.getChatHud().addToMessageHistory(chatText);
//            }
//
//            if (chatText.startsWith("/")) {
//                this.client.player.networkHandler.sendChatCommand(chatText.substring(1));
//            } else {
//                this.client.player.networkHandler.sendChatMessage(chatText);
//            }
//        }
//    }
//
//    /**
//     * {@return the {@code message} normalized by trimming it and then normalizing spaces}
//     */
//    public String normalize(String chatText) {
//        return StringHelper.truncateChat(StringUtils.normalizeSpace(chatText.trim()));
//    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyBindings.mayorViewSelectionBind.matchesKey(keyCode, scanCode) || KeyBindings.mayorViewBind.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public boolean shouldPause() {
        return false;
    }

    private boolean isMouseWithinBounds(int x, int y, int width, int height, double mouseX, double mouseY) {
        int i = 0;//this.width / 2;
        int j = 0;//this.height / 2;
        return (mouseX -= (double) i) >= (double) (x - 1) && mouseX < (double) (x + width + 1) && (mouseY -= (double) j) >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }
}

