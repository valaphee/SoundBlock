package com.valaphee.sb;

import com.google.common.base.Predicate;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SoundBlockEditScreen extends GuiScreen {
    private static final int GUI_WIDTH = 234;
    private static final int GUI_HEIGHT = 153;

    private static final int GAP_X = 2;
    private static final int GAP_Y = 2;
    private static final int LINE_HEIGHT = 16;
    private static final int BUTTON_WIDTH = 16;

    private static final int TEXTFIELD_WIDTH_SOUNDID = 164;
    private static final int TEXTFIELD_WIDTH_NUMBER = 24;
    private static final int TEXTFIELD_BORDER = 1;

    private static final int SOUNDENTRY_WIDTH = 204;
    private static final int SOUNDENTRY_HEIGHT = 42;
    private static final int SOUNDENTRY_POS_X = 7;
    private static final int SOUNDENTRY_POS_Y = 24;
    private static final int SOUNDENTRY_UV_X = 0;
    private static final int SOUNDENTRY_UV_Y = 158;

    private static final int BUTTON_ADD_UV_X = 32;
    private static final int BUTTON_ADD_UV_Y = 224;
    private static final int BUTTON_REMOVE_UV_X = 64;
    private static final int BUTTON_REMOVE_UV_Y = 224;
    private static final int CHECKBOX_UV_X = 32;
    private static final int CHECKBOX_UV_Y = 240;

    private static final int ARROW_WIDTH = 12;
    private static final int ARROW_DOWN_UV_X = 0;
    private static final int ARROW_DOWN_UV_Y = 208;
    private static final int ARROW_UP_UV_X = 0;
    private static final int ARROW_UP_UV_Y = 224;

    private static final int SCROLLBAR_WIDTH = 14;
    private static final int SCROLLBAR_HEIGHT = 122;
    private static final int SCROLLBAR_POS_X = SOUNDENTRY_POS_X + SOUNDENTRY_WIDTH + GAP_X;
    private static final int SCROLLBAR_POS_Y = SOUNDENTRY_POS_Y;
    private static final int SCROLLBAR_UV_X = 242;
    private static final int SCROLLBAR_UV_Y = 24;
    private static final int SCROLLBAR_BORDER = 1;

    private static final int SCROLLBAR_THUMB_WIDTH = 12;
    private static final int SCROLLBAR_THUMB_HEIGHT = 15;
    private static final int SCROLLBAR_THUMB_POS_X = SCROLLBAR_POS_X + 1;
    private static final int SCROLLBAR_THUMB_POS_Y = SCROLLBAR_POS_Y + 1;
    private static final int SCROLLBAR_THUMB_UV_X = 0;
    private static final int SCROLLBAR_THUMB_UV_Y = 241;

    private static final int ACTIVETAB_WIDTH = 51;
    private static final int ACTIVETAB_BUTTON_WIDTH = ACTIVETAB_WIDTH - 2;
    private static final int ACTIVETAB_HEIGHT = 20;
    private static final int ACTIVETAB_POS_X = 2;
    private static final int ACTIVETAB_BUTTON_POS_X = ACTIVETAB_POS_X + 1;
    private static final int ACTIVETAB_POS_Y = 0;
    private static final int ACTIVETAB_UV_X = 120;
    private static final int ACTIVETAB_UV_Y = 236;

    private static final ResourceLocation TEXTURE = new ResourceLocation("vsb", "textures/gui/sound_block.png");

    private SoundBlockData soundBlockData;

    private int lastComponentId = 0;
    private int guiOriginX = 0;
    private int guiOriginY = 0;

    private int tab = 0;
    private int entryOffset = 0;

    private Entry sound1Entry;
    private Entry sound2Entry;
    private Entry sound3Entry;
    private Scrollbar scrollbar;

    public SoundBlockEditScreen(SoundBlockData soundBlockData) {
        this.soundBlockData = soundBlockData;
    }

    @Override
    public void initGui() {
        lastComponentId = 0;
        guiOriginX = (this.width - GUI_WIDTH) / 2;
        guiOriginY = (this.height - GUI_HEIGHT) / 2;

        sound1Entry = new Entry(0, guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y);
        sound2Entry = new Entry(1, guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y + SOUNDENTRY_HEIGHT - GAP_Y);
        sound3Entry = new Entry(2, guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y + (SOUNDENTRY_HEIGHT - GAP_Y) * 2);

        scrollbar = new Scrollbar(guiOriginX + SCROLLBAR_THUMB_POS_X, guiOriginY + SCROLLBAR_THUMB_POS_Y, SCROLLBAR_THUMB_UV_X, SCROLLBAR_THUMB_UV_Y, SCROLLBAR_THUMB_WIDTH, SCROLLBAR_THUMB_HEIGHT, SCROLLBAR_HEIGHT - 2 * SCROLLBAR_BORDER, 1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Background
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(guiOriginX, guiOriginY, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        // Tabs
        this.drawTexturedModalRect(guiOriginX + ACTIVETAB_POS_X + ACTIVETAB_BUTTON_WIDTH * tab, guiOriginY + ACTIVETAB_POS_Y, ACTIVETAB_UV_X, ACTIVETAB_UV_Y, ACTIVETAB_WIDTH, ACTIVETAB_HEIGHT);

        if (tab != 0) {

            // Sound entries
            sound1Entry.draw(mouseX, mouseY, partialTicks);
            sound2Entry.draw(mouseX, mouseY, partialTicks);
            sound3Entry.draw(mouseX, mouseY, partialTicks);

            // Scrollbar
            this.drawTexturedModalRect(guiOriginX + SCROLLBAR_POS_X, guiOriginY + SCROLLBAR_POS_Y, SCROLLBAR_UV_X, SCROLLBAR_UV_Y, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);
            scrollbar.draw(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int lastTab = tab;

        // Tabs
        int tabX = guiOriginX + ACTIVETAB_BUTTON_POS_X;
        int tabY = guiOriginY + ACTIVETAB_POS_Y;
        if (mouseX >= tabX && mouseY >= tabY && mouseX < tabX + ACTIVETAB_BUTTON_WIDTH && mouseY < tabY + ACTIVETAB_HEIGHT) {
            tab = 0;
        }
        tabX += ACTIVETAB_BUTTON_WIDTH;
        if (mouseX >= tabX && mouseY >= tabY && mouseX < tabX + ACTIVETAB_BUTTON_WIDTH && mouseY < tabY + ACTIVETAB_HEIGHT) {
            tab = 1;
        }
        tabX += ACTIVETAB_BUTTON_WIDTH;
        if (mouseX >= tabX && mouseY >= tabY && mouseX < tabX + ACTIVETAB_BUTTON_WIDTH && mouseY < tabY + ACTIVETAB_HEIGHT) {
            tab = 2;
        }
        tabX += ACTIVETAB_BUTTON_WIDTH;
        if (mouseX >= tabX && mouseY >= tabY && mouseX < tabX + ACTIVETAB_BUTTON_WIDTH && mouseY < tabY + ACTIVETAB_HEIGHT) {
            tab = 3;
        }

        scrollbar.setVisible(tab != 0);

        if (tab != 0) {
            if (tab != lastTab) {
                refreshAllStates();
            }

            // Sound entries
            sound1Entry.mouseClicked(mouseX, mouseY, mouseButton);
            sound2Entry.mouseClicked(mouseX, mouseY, mouseButton);
            sound3Entry.mouseClicked(mouseX, mouseY, mouseButton);

            // Scrollbar
            scrollbar.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int releasedMouseButton) {
        super.mouseReleased(mouseX, mouseY, releasedMouseButton);

        if (tab != 0) {
            scrollbar.mouseReleased(mouseX, mouseY, releasedMouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        // Sound entries
        if (tab != 0) {
            sound1Entry.keyTyped(typedChar, keyCode);
            sound2Entry.keyTyped(typedChar, keyCode);
            sound3Entry.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        soundBlockData.markDirty();
    }

    private List<SoundBlockData.Sound> getActiveSoundList() {
        return tab == 1 ? soundBlockData.getIntro() : tab == 2 ? soundBlockData.getLoop() : soundBlockData.getOutro();
    }

    private void refreshAllStates() {
        List<SoundBlockData.Sound> soundList = getActiveSoundList();
        scrollbar.setMaxPosition(soundList.size() - 2);

        sound1Entry.refreshState();
        sound2Entry.refreshState();
        sound3Entry.refreshState();
    }

    private static float parseAsFloat(String text, float defaultValue) {
        try {
            return Float.parseFloat(text);
        } catch(NumberFormatException e) {
            return defaultValue;
        }
    }

    private static double parseAsDouble(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch(NumberFormatException e) {
            return defaultValue;
        }
    }

    class Entry {
        private int localEntryOffset;
        private int originX;
        private int originY;

        private SoundBlockData.Sound soundData;

        private GuiTextField idTextField;
        private GuiTextField offsetXTextField;
        private GuiTextField offsetYTextField;
        private GuiTextField offsetZTextField;
        private GuiTextField volumeTextField;
        private GuiTextField pitchTextField;

        private Button buttonAdd;
        private Button buttonRemove;
        private Button buttonMoveUp;
        private Button buttonMoveDown;
        private CheckBox checkboxOnEnter;
        private CheckBox checkboxOnExit;

        private boolean checkboxStateOnEnter = false;
        private boolean checkboxStateOnExit = false;

        public Entry(int localEntryOffset, int originX, int originY) {
            this.localEntryOffset = localEntryOffset;
            this.originX = originX;
            this.originY = originY;
            this.checkboxStateOnEnter = false;
            this.checkboxStateOnExit = false;

            int startX = originX + GAP_X * 2;
            int startY = originY + GAP_Y * 2;
            idTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + GAP_X, startY + TEXTFIELD_BORDER, TEXTFIELD_WIDTH_SOUNDID - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetXTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + GAP_X, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetYTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + TEXTFIELD_WIDTH_NUMBER + GAP_X * 2, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetZTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + TEXTFIELD_WIDTH_NUMBER * 2 + GAP_X * 3, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            volumeTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 3 + GAP_X * 6, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            pitchTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 4 + GAP_X * 7, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);

            idTextField.setMaxStringLength(128);
            offsetXTextField.setMaxStringLength(8);
            offsetYTextField.setMaxStringLength(8);
            offsetZTextField.setMaxStringLength(8);
            volumeTextField.setMaxStringLength(8);
            pitchTextField.setMaxStringLength(8);

            // Custom text field validators
            Predicate<String> isValidDouble = (value) -> {
                if (value.isEmpty()) {
                    return true;
                }
                try {
                    Double.parseDouble(value);
                    return true;
                } catch (NumberFormatException ignored) {
                    return false;
                }
            };
            Predicate<String> isValidVolume = (value) -> {
                if (value.isEmpty()) {
                    return true;
                }
                try {
                    Double.parseDouble(value);
                    return true;
                } catch (NumberFormatException ignored) {
                    return false;
                }
            };
            Predicate<String> isValidPitch = (value) -> {
                if (value.isEmpty()) {
                    return true;
                }
                try {
                   Double.parseDouble(value);
                    return true;
                } catch (NumberFormatException ignored) {
                    return false;
                }
            };

            offsetXTextField.setValidator(isValidDouble);
            offsetYTextField.setValidator(isValidDouble);
            offsetZTextField.setValidator(isValidDouble);
            volumeTextField.setValidator(isValidVolume);
            pitchTextField.setValidator(isValidPitch);

            // Add/Remove
            buttonAdd = new Button(startX, startY, BUTTON_ADD_UV_X, BUTTON_ADD_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);
            buttonRemove = new Button(startX, startY + LINE_HEIGHT + GAP_Y, BUTTON_REMOVE_UV_X, BUTTON_REMOVE_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);

            // Arrows
            buttonMoveUp = new Button(startX + TEXTFIELD_WIDTH_SOUNDID + BUTTON_WIDTH + GAP_X * 2, startY, ARROW_UP_UV_X, ARROW_UP_UV_Y, ARROW_WIDTH, LINE_HEIGHT);
            buttonMoveDown = new Button(startX + TEXTFIELD_WIDTH_SOUNDID + BUTTON_WIDTH + GAP_X * 2, startY + LINE_HEIGHT + GAP_Y, ARROW_DOWN_UV_X, ARROW_DOWN_UV_Y, ARROW_WIDTH, LINE_HEIGHT);

            // Checkboxes
            checkboxOnEnter = new CheckBox(startX + BUTTON_WIDTH + TEXTFIELD_WIDTH_NUMBER * 3 + GAP_X * 4, startY + LINE_HEIGHT + GAP_Y, CHECKBOX_UV_X, CHECKBOX_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);
            checkboxOnExit = new CheckBox(startX + BUTTON_WIDTH * 2 + TEXTFIELD_WIDTH_NUMBER * 3 + GAP_X * 5, startY + LINE_HEIGHT + GAP_Y, CHECKBOX_UV_X, CHECKBOX_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);

            refreshState();
        }

        public void refreshState() {
            List<SoundBlockData.Sound> soundList = getActiveSoundList();
            int index = entryOffset + localEntryOffset;
            int size = soundList.size();
            boolean isVisible = index < size;
            soundData = isVisible ? soundList.get(entryOffset + localEntryOffset) : null;

            if (soundData != null) {
                // Fix cursor misplacement when switching from a long sound path to a short one
                idTextField.setCursorPositionZero();

                idTextField.setText(soundData.getId());
                offsetXTextField.setText(((Double) soundData.getOffsetX()).toString());
                offsetYTextField.setText(((Double) soundData.getOffsetY()).toString());
                offsetZTextField.setText(((Double) soundData.getOffsetZ()).toString());
                volumeTextField.setText(((Float) soundData.getVolume()).toString());
                pitchTextField.setText(((Float) soundData.getPitch()).toString());

                offsetXTextField.setCursorPositionZero();
                offsetYTextField.setCursorPositionZero();
                offsetZTextField.setCursorPositionZero();
                volumeTextField.setCursorPositionZero();
                pitchTextField.setCursorPositionZero();

                checkboxStateOnEnter = soundData.isStopOnEnter();
                checkboxStateOnExit = soundData.isStopOnExit();
            }

            idTextField.setVisible(isVisible);
            offsetXTextField.setVisible(isVisible);
            offsetYTextField.setVisible(isVisible);
            offsetZTextField.setVisible(isVisible);
            volumeTextField.setVisible(isVisible);
            pitchTextField.setVisible(isVisible);

            buttonAdd.setVisible(index <= size);
            buttonRemove.setVisible(isVisible);
            buttonMoveUp.setVisible(isVisible && index > 0);
            buttonMoveDown.setVisible(index < size - 1);

            checkboxOnEnter.setVisible(isVisible);
            checkboxOnExit.setVisible(isVisible);
        }

        public void draw(int mouseX, int mouseY, float partialTicks) {
            // Background
            mc.getTextureManager().bindTexture(TEXTURE);
            drawTexturedModalRect(originX, originY, SOUNDENTRY_UV_X, SOUNDENTRY_UV_Y, SOUNDENTRY_WIDTH, SOUNDENTRY_HEIGHT);

            // Text fields
            idTextField.drawTextBox();
            offsetXTextField.drawTextBox();
            offsetYTextField.drawTextBox();
            offsetZTextField.drawTextBox();
            volumeTextField.drawTextBox();
            pitchTextField.drawTextBox();

            buttonAdd.draw(mouseX, mouseY, partialTicks);
            buttonRemove.draw(mouseX, mouseY, partialTicks);
            buttonMoveUp.draw(mouseX, mouseY, partialTicks);
            buttonMoveDown.draw(mouseX, mouseY, partialTicks);

            checkboxOnEnter.draw(mouseX, mouseY, partialTicks, checkboxStateOnEnter);
            checkboxOnExit.draw(mouseX, mouseY, partialTicks, checkboxStateOnExit);
        }

        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            if (idTextField.getVisible()) {
                idTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (offsetXTextField.getVisible()) {
                offsetXTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (offsetYTextField.getVisible()) {
                offsetYTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (offsetZTextField.getVisible()) {
                offsetZTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (volumeTextField.getVisible()) {
                volumeTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (pitchTextField.getVisible()) {
                pitchTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }

            // Add / Remove
            if (buttonAdd.mouseClicked(mouseX, mouseY, mouseButton)) {
                List<SoundBlockData.Sound> soundList = getActiveSoundList();
                int soundListIndex = entryOffset + localEntryOffset;

                if (soundListIndex < soundList.size()) {
                    soundList.add(soundListIndex, new SoundBlockData.Sound());
                } else {
                    soundList.add(new SoundBlockData.Sound());
                }
                refreshAllStates();
                return;
            }
            if (buttonRemove.mouseClicked(mouseX, mouseY, mouseButton)) {
                List<SoundBlockData.Sound> soundList = getActiveSoundList();
                int soundListIndex = entryOffset + localEntryOffset;

                if (soundListIndex < soundList.size()) {
                    soundList.remove(soundListIndex);
                    refreshAllStates();
                    return;
                }
            }

            // Arrows
            if (buttonMoveUp.mouseClicked(mouseX, mouseY, mouseButton)) {
                List<SoundBlockData.Sound> soundList = getActiveSoundList();
                int soundListIndex = entryOffset + localEntryOffset;

                if (soundListIndex > 0) {
                    SoundBlockData.Sound sound = soundList.get(soundListIndex);
                    SoundBlockData.Sound sound2 = soundList.set(soundListIndex - 1, sound);
                    soundList.set(soundListIndex, sound2);
                    refreshAllStates();
                    return;
                }
            }
            if (buttonMoveDown.mouseClicked(mouseX, mouseY, mouseButton)) {
                List<SoundBlockData.Sound> soundList = getActiveSoundList();
                int soundListIndex = entryOffset + localEntryOffset;

                if (soundListIndex < soundList.size() - 1) {
                    SoundBlockData.Sound sound = soundList.get(soundListIndex);
                    SoundBlockData.Sound sound2 = soundList.set(soundListIndex + 1, sound);
                    soundList.set(soundListIndex, sound2);
                    refreshAllStates();
                    return;
                }
            }

            // Checkboxes
            if (checkboxOnEnter.mouseClicked(mouseX, mouseY, mouseButton)) {
                checkboxStateOnEnter = !checkboxStateOnEnter;
                soundData.setStopOnEnter(checkboxStateOnEnter);
            }
            if (checkboxOnExit.mouseClicked(mouseX, mouseY, mouseButton)) {
                checkboxStateOnExit = !checkboxStateOnExit;
                soundData.setStopOnExit(checkboxStateOnExit);
            }
        }

        public void keyTyped(char typedChar, int keyCode) {
            if (soundData == null) return;

            if (idTextField.getVisible()) {
                idTextField.textboxKeyTyped(typedChar, keyCode);
                soundData.setId(idTextField.getText());
            }
            if (offsetXTextField.getVisible()) {
                offsetXTextField.textboxKeyTyped(typedChar, keyCode);
                soundData.setOffsetX(parseAsDouble(offsetXTextField.getText(), soundData.getOffsetX()));
            }
            if (offsetYTextField.getVisible()) {
                offsetYTextField.textboxKeyTyped(typedChar, keyCode);
                soundData.setOffsetY(parseAsDouble(offsetYTextField.getText(), soundData.getOffsetY()));
            }
            if (offsetZTextField.getVisible()) {
                offsetZTextField.textboxKeyTyped(typedChar, keyCode);
                soundData.setOffsetZ(parseAsDouble(offsetZTextField.getText(), soundData.getOffsetZ()));
            }
            if (volumeTextField.getVisible()) {
                volumeTextField.textboxKeyTyped(typedChar, keyCode);
                soundData.setVolume(parseAsFloat(volumeTextField.getText(), soundData.getVolume()));
            }
            if (pitchTextField.getVisible()) {
                pitchTextField.textboxKeyTyped(typedChar, keyCode);
                soundData.setPitch(parseAsFloat(pitchTextField.getText(), soundData.getPitch()));
            }
        }
    }

    public class Button {

        protected static final int UV_OFFSET_X_HOVERED = 1; // Multiplier for the "hovered" state UV's X position (times width)
        protected static final int UV_OFFSET_Y_HOVERED = 0; // Multiplier for the "hovered" state UV's Y position (times height)

        protected int buttonX;
        protected int buttonY;
        protected int buttonUV_x;
        protected int buttonUV_y;
        protected int buttonWidth;
        protected int buttonHeight;

        @Getter
        @Setter
        private boolean visible = false;

        public Button(int originX, int originY, int UV_x, int UV_y, int width, int height) {
            buttonX = originX;
            buttonY = originY;
            buttonUV_x = UV_x;
            buttonUV_y = UV_y;
            buttonWidth = width;
            buttonHeight = height;
        }

        public void draw(int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(TEXTURE);

                boolean hovering = isPositionAboveButton(mouseX, mouseY);
                int coefHoveredX = hovering ? UV_OFFSET_X_HOVERED : 0;
                int coefHoveredY = hovering ? UV_OFFSET_Y_HOVERED : 0;

                drawTexturedModalRect(buttonX, buttonY, buttonUV_x + coefHoveredX * buttonWidth, buttonUV_y + coefHoveredY * buttonHeight, buttonWidth, buttonHeight);
            }
        }

        public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
            return this.visible && isPositionAboveButton(mouseX, mouseY);
        }

        protected boolean isPositionAboveButton(int mouseX, int mouseY) {
            return mouseX >= this.buttonX && mouseY >= this.buttonY && mouseX < this.buttonX + this.buttonWidth && mouseY < this.buttonY + this.buttonHeight;
        }
    }

    public class CheckBox extends Button {

        protected static final int UV_OFFSET_X_CHECKED = 2; // Multiplier for the "checked" state UV's X position (times width)
        protected static final int UV_OFFSET_Y_CHECKED = 0; // Multiplier for the "checked" state UV's Y position (times height)

        public CheckBox(int originX, int originY, int UV_x, int UV_y, int width, int height) {
            super(originX, originY, UV_x, UV_y, width, height);
        }

        public void draw(int mouseX, int mouseY, float partialTicks, boolean checked) {
            if (isVisible()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(TEXTURE);

                boolean hovering = isPositionAboveButton(mouseX, mouseY);
                int coefHoveredX = hovering ? UV_OFFSET_X_HOVERED : 0;
                int coefHoveredY = hovering ? UV_OFFSET_Y_HOVERED : 0;
                int coefCheckedX = checked ? UV_OFFSET_X_CHECKED : 0;
                int coefCheckedY = checked ? UV_OFFSET_Y_CHECKED : 0;

                drawTexturedModalRect(buttonX, buttonY, buttonUV_x + (coefHoveredX + coefCheckedX) * buttonWidth, buttonUV_y + (coefHoveredY + coefCheckedY) * buttonHeight, buttonWidth, buttonHeight);
            }
        }
    }

    public class Scrollbar extends Button {

        private int scrollableHeight;
        private int position;

        @Getter
        private int maxPosition;

        private boolean inUse;

        public Scrollbar(int originX, int originY, int UV_x, int UV_y, int width, int height, int argScrollableHeight, int argPositions) {
            super(originX, originY, UV_x, UV_y, width, height);

            scrollableHeight = Integer.max(argScrollableHeight - height, 0);
            maxPosition = Integer.max(argPositions, 1);

            position = 1;
            inUse = false;
        }

        @Override
        public void draw(int mouseX, int mouseY, float partialTicks) {
            if (isVisible()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(TEXTURE);

                boolean hovering = inUse || isPositionAboveButton(mouseX, mouseY);
                int coefHoveredX = hovering ? UV_OFFSET_X_HOVERED : 0;
                int coefHoveredY = hovering ? UV_OFFSET_Y_HOVERED : 0;
                int posY = buttonY;

                if (maxPosition > 0) {
                    if (inUse) {
                        float percent = (float)(mouseY - buttonY) / (float)(scrollableHeight + buttonHeight);

                        setPosition(Integer.min((int)(percent * (float)(maxPosition + 1)), maxPosition));
                    }

                    posY += (int)(((float)position / (float)maxPosition) * (float)scrollableHeight);
                }

                drawTexturedModalRect(buttonX, posY, buttonUV_x + coefHoveredX * buttonWidth, buttonUV_y + coefHoveredY * buttonHeight, buttonWidth, buttonHeight);
            }
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
            boolean result = isVisible() && mouseButton == 0 && isPositionAboveScrollbar(mouseX, mouseY);

            if (result) {
                inUse = true;
            }

            return result;
        }

        public void mouseReleased(int mouseX, int mouseY, int releasedMouseButton) {
            if (releasedMouseButton == 0) {
                inUse = false;
            }
        }

        @Override
        protected boolean isPositionAboveButton(int mouseX, int mouseY) {
            int posY = this.buttonY + (int)(((float)position / (float)maxPosition) * (float)scrollableHeight);

            return mouseX >= this.buttonX && mouseY >= posY && mouseX < this.buttonX + this.buttonWidth && mouseY < posY + this.buttonHeight;
        }

        protected boolean isPositionAboveScrollbar(int mouseX, int mouseY) {
            return mouseX >= this.buttonX && mouseY >= this.buttonY && mouseX < this.buttonX + this.buttonWidth && mouseY < this.buttonY + this.buttonHeight + this.scrollableHeight;
        }

        public void setPosition(int value) {
            if (value == position) {
                return;
            }
            position = Integer.max(Integer.min(value, maxPosition), 0);
            entryOffset = position;
            refreshAllStates();
        }

        public void setMaxPosition(int value) {
            if (value == maxPosition) {
                return;
            }
            maxPosition = Integer.max(value, 0);
            setPosition(Integer.min(position, maxPosition));
        }
    }
}
