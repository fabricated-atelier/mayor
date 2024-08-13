package io.fabricatedatelier.mayor.screen;

import io.fabricatedatelier.mayor.block.AbstractVillageContainerBlock;
import io.fabricatedatelier.mayor.block.entity.AbstractVillageContainerBlockEntity;
import io.fabricatedatelier.mayor.init.KeyBindings;
import io.fabricatedatelier.mayor.manager.MayorCategory;
import io.fabricatedatelier.mayor.manager.MayorManager;
import io.fabricatedatelier.mayor.manager.MayorStructure;
import io.fabricatedatelier.mayor.state.StructureData;
import io.fabricatedatelier.mayor.util.StringUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;


@Environment(EnvType.CLIENT)
public class MayorScreen extends Screen {

    private final MayorManager mayorManager;

    private Map<MayorCategory.BuildingCategory, List<MayorStructure>> availableStructureMap = new HashMap<>();
    private Map<Integer, List<MayorStructure>> villageStructureMap = new HashMap<>();
    private List<ItemStack> availableStacks = new ArrayList<>();

    @Nullable
    private MayorCategory.BuildingCategory selectedCategory = null;

    public MayorScreen(MayorManager mayorManager) {
        super(Text.translatable("mayor.screen.title"));
        this.mayorManager = mayorManager;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int xTest = 0;
        for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
            Text text = Text.translatable(MayorCategory.BuildingCategory.values()[i].name());
            if (this.textRenderer.getWidth(text) > xTest) {
                xTest = this.textRenderer.getWidth(text);
            }
        }

        int x = 10;
        int y = this.height / 2 - 10 * MayorCategory.BuildingCategory.values().length / 2;
        for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
            Text text = Text.translatable(MayorCategory.BuildingCategory.values()[i].name());
            if (MayorCategory.BuildingCategory.values()[i].equals(this.selectedCategory)) {
                context.drawText(this.textRenderer, text, x, y, Colors.WHITE, false);

                if (this.selectedCategory != null) {
                    int buildingY = 0;
                    for (int u = 0; u < this.availableStructureMap.get(this.selectedCategory).size(); u++) {
                        Text building = Text.translatable("building_" + this.availableStructureMap.get(this.selectedCategory).get(u).getIdentifier().getPath());

                        if (mayorManager.getMayorStructure() != null && mayorManager.getMayorStructure().equals(this.availableStructureMap.get(this.selectedCategory).get(u))) {
                            context.drawText(this.textRenderer, building, x + 5 + xTest, y + buildingY, Colors.WHITE, false);

//                            if(isMouseWithinBounds(x + 5 + xTest, y + buildingY,this.textRenderer.getWidth(building),8,mouseX,mouseY)){
////                                context.it
//                            }

                        } else {
                            context.drawText(this.textRenderer, building, x + 5 + xTest, y + buildingY, Colors.LIGHT_GRAY, false);
                        }
                        buildingY += 10;
                    }
                }
            } else if (isMouseWithinBounds(x, y, this.textRenderer.getWidth(text), 8, mouseX, mouseY)) {
                context.drawText(this.textRenderer, text, x, y, Colors.WHITE, false);
            } else {
                context.drawText(this.textRenderer, text, x, y, Colors.LIGHT_GRAY, false);
            }
            y += 10;
        }

        // VillageData
        if (mayorManager.getVillageData() != null) {
            int villageX = this.width - 10;
            int villageY = 10;
            String name = mayorManager.getVillageData().getName();
            Text level = Text.translatable("mayor.screen.level", mayorManager.getVillageData().getLevel());

            context.drawText(this.textRenderer, name, villageX - this.textRenderer.getWidth(name) - 5 - this.textRenderer.getWidth(level), villageY, Colors.WHITE, false);
            context.drawText(this.textRenderer, level, villageX - this.textRenderer.getWidth(level), villageY, Colors.WHITE, false);

            Text villagers = Text.translatable("mayor.screen.villagers", mayorManager.getVillageData().getVillagers().size());
            context.drawText(this.textRenderer, villagers, villageX - this.textRenderer.getWidth(villagers), villageY + 10, Colors.LIGHT_GRAY, false);
            Text ironGolems = Text.translatable("mayor.screen.iron_golems", mayorManager.getVillageData().getIronGolems().size());
            context.drawText(this.textRenderer, ironGolems, villageX - this.textRenderer.getWidth(ironGolems), villageY + 20, Colors.LIGHT_GRAY, false);

            Text structures = Text.translatable("mayor.screen.structures", mayorManager.getVillageData().getStructures().size());
            context.drawText(this.textRenderer, structures, villageX - this.textRenderer.getWidth(structures), villageY + 30, Colors.LIGHT_GRAY, false);

            if (isMouseWithinBounds(villageX - this.textRenderer.getWidth(structures), villageY + 30, this.textRenderer.getWidth(structures), 8, mouseX, mouseY)) {
                List<Text> structuresTooltip = new ArrayList<>();

//                Map<String, AbstractMap.SimpleEntry<Integer, Integer>> structure = new HashMap<>();
                Map<String, List<Integer>> structureTooltip = new HashMap<>();
                for (Map.Entry<BlockPos, StructureData> entry : mayorManager.getVillageData().getStructures().entrySet()) {
                    StructureData structureData = entry.getValue();
//                    String structure = "building_" + structureData.getIdentifier().getPath();

                    String structureName = StringUtil.getStructureName(structureData.getIdentifier());
//                    Text structure = Text.translatable("building_" + structureData.getIdentifier().getPath());
//                    if (structure.containsKey(structureName)) {
//                        int structureLevel = StringUtil.getStructureLevelByIdentifier(structureData.getIdentifier());
//
//                    } else {
//
//                    }
//                    StringUtil.getStructureName( structureData.getIdentifier());
//                    structuresTooltip.add(StringUtil.getStructureName(structureData.getIdentifier()));
//                    context.drawText(this.textRenderer, structure, villageX - this.textRenderer.getWidth(structure), villageY + 40, Colors.LIGHT_GRAY, false);
//                    villageY += 10;
                }
                if (!structuresTooltip.isEmpty()) {
                    context.drawTooltip(this.textRenderer, structuresTooltip, mouseX, mouseY);
                }

            }

            // CHeck what needs to a higher level village

        }
        // Structure requirements
        if (this.mayorManager.getMayorStructure() != null) {
            Text requiredItems = Text.translatable("mayor.screen.required_items");
            context.drawText(this.textRenderer, requiredItems, this.width / 2 - this.textRenderer.getWidth(requiredItems) / 2, this.height - 70, Colors.WHITE, false);

            int xItem = 0;
            int stackCount = this.mayorManager.getMayorStructure().getRequiredItemStacks().size();
            for (int i = 0; i < this.mayorManager.getMayorStructure().getRequiredItemStacks().size(); i++) {
                context.drawItemWithoutEntity(this.mayorManager.getMayorStructure().getRequiredItemStacks().get(i), this.width / 2 - stackCount * 18 / 2 + xItem, this.height - 50);
                context.drawItemInSlot(this.textRenderer, this.mayorManager.getMayorStructure().getRequiredItemStacks().get(i), this.width / 2 - stackCount * 18 / 2 + xItem, this.height - 50);
                xItem += 18;
            }
        }
        // Available items
        if (!this.availableStacks.isEmpty()) {

        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int xTest = 0;
        for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
            Text text = Text.translatable(MayorCategory.BuildingCategory.values()[i].name());
            if (this.textRenderer.getWidth(text) > xTest) {
                xTest = this.textRenderer.getWidth(text);
            }
        }
        int x = 10;
        int y = this.height / 2 - 10 * MayorCategory.BuildingCategory.values().length / 2;
        for (int i = 0; i < MayorCategory.BuildingCategory.values().length; i++) {
            if (isMouseWithinBounds(x, y, this.textRenderer.getWidth(Text.translatable(MayorCategory.BuildingCategory.values()[i].name())), 8, mouseX, mouseY)) {
//                System.out.println("WITHIN " + i + " : "+availableStructureMap.get(MayorCategory.BuildingCategory.values()[i]));
                if (availableStructureMap.containsKey(MayorCategory.BuildingCategory.values()[i]) && availableStructureMap.get(MayorCategory.BuildingCategory.values()[i]).size() > 0) {
                    this.selectedCategory = MayorCategory.BuildingCategory.values()[i];
                    // TEST
//                    mayorManager.setMayorStructure(availableStructureMap.get(MayorCategory.BuildingCategory.values()[i]).get(0));
                    //  new StructurePacket(availableStructureMap.get(MayorCategory.BuildingCategory.values()[i]).get(0), BlockRotation.NONE, false).sendClientPacket();
//                    System.out.println("SET STRUCTURE");
                    return true;
                }


            }
            if (this.selectedCategory != null) {
                int buildingY = 0;
                for (int u = 0; u < this.availableStructureMap.get(this.selectedCategory).size(); u++) {
                    Text building = Text.translatable("building_" + this.availableStructureMap.get(this.selectedCategory).get(u).getIdentifier().getPath());
                    if (isMouseWithinBounds(x + xTest, y + buildingY, this.textRenderer.getWidth(building), 8, mouseX, mouseY)) {
                        mayorManager.setMayorStructure(availableStructureMap.get(this.selectedCategory).get(u));
                        return true;
                    }
//                        if (mayorManager.getMayorStructure() != null && mayorManager.getMayorStructure().equals(this.availableStructureMap.get(this.selectedCategory).get(u))) {
//                            context.drawText(this.textRenderer, building, x + 5 + xTest, y + buildingY, Colors.WHITE, false);
//                        } else {
//                            context.drawText(this.textRenderer, building, x + 5 + xTest, y + buildingY, Colors.LIGHT_GRAY, false);
//                        }
                    buildingY += 10;
                }
            }
            y += 10;
        }
        // TEST
        this.selectedCategory = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void init() {
        this.availableStructureMap.clear();

        if (MayorManager.mayorStructureMap.containsKey(mayorManager.getBiomeCategory())) {
            for (int i = 0; i < MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).size(); i++) {
                MayorCategory.BuildingCategory buildingCategory = MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i).getBuildingCategory();
                if (availableStructureMap.containsKey(buildingCategory)) {
                    availableStructureMap.get(buildingCategory).add(MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i));
                } else {
                    List<MayorStructure> list = new ArrayList<>();
                    list.add(MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).get(i));
                    availableStructureMap.put(buildingCategory, list);
                }
            }
        }
        this.availableStacks.clear();
        if (this.mayorManager.getVillageData() != null && this.client != null && this.client.world != null) {
            for (int i = 0; i < this.mayorManager.getVillageData().getStorageOriginBlockPosList().size(); i++) {
                if (this.client.world.getBlockState(this.mayorManager.getVillageData().getStorageOriginBlockPosList().get(i)).getBlock() instanceof AbstractVillageContainerBlock && this.client.world.getBlockEntity(this.mayorManager.getVillageData().getStorageOriginBlockPosList().get(i)) instanceof AbstractVillageContainerBlockEntity abstractVillageContainerBlockEntity) {

//                    for (int u = 0; u < this.availableStacks.size(); u++) {
//                        if (this.availableStacks.get(u).isOf() && this.availableStacks.get(u).getCount() < this.availableStacks.get(u).getMaxCount()) {
//
//                        }
//                    }
//                    this.availableStacks.add();
                }
            }
        }

//        System.out.println(MayorManager.mayorStructureMap.get(mayorManager.getBiomeCategory()).size());
//        System.out.println(MayorManager.mayorStructureMap);
//        System.out.println(this.availableStructureMap);
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

    private void updateVillageStructures(){
//        this.villageStructureMap
        if(this.mayorManager.getVillageData() != null){
            for (Map.Entry<BlockPos, StructureData> entry : mayorManager.getVillageData().getStructures().entrySet()) {

            }
        }
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
        return mouseX >= (double) (x - 1) && mouseX < (double) (x + width + 1) && mouseY >= (double) (y - 1) && mouseY < (double) (y + height + 1);
    }
}

