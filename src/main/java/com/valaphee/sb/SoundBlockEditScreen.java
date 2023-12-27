package com.valaphee.sb;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import scala.Int;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SoundBlockEditScreen extends GuiScreen {
    private static final int GUI_TEXTURE_WIDTH = 512; // Dimensions of the GUI texture file
    private static final int GUI_TEXTURE_HEIGHT = 256;
    private static final int GUI_WIDTH = 307; // Dimensions of the GUI container to be drawn
    private static final int GUI_HEIGHT = 153;

    private static final int GAP_X = 2;
    private static final int GAP_Y = 2;
    private static final int LINE_HEIGHT = 16;
    private static final int BUTTON_WIDTH = 16;

    private static final int TEXTFIELD_WIDTH_SOUNDID = 238;
    private static final int TEXTFIELD_WIDTH_NUMBER = 32;
    private static final int TEXTFIELD_BORDER = 1;

    private static final int SOUNDENTRY_WIDTH = 277;
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
    private static final int SCROLLBAR_UV_X = 498;
    private static final int SCROLLBAR_UV_Y = 0;
    private static final int SCROLLBAR_BORDER = 1;

    private static final int SCROLLBAR_THUMB_WIDTH = 12;
    private static final int SCROLLBAR_THUMB_HEIGHT = 15;
    private static final int SCROLLBAR_THUMB_POS_X = SCROLLBAR_POS_X + 1;
    private static final int SCROLLBAR_THUMB_POS_Y = SCROLLBAR_POS_Y + 1;
    private static final int SCROLLBAR_THUMB_UV_X = 0;
    private static final int SCROLLBAR_THUMB_UV_Y = 241;

    private static final int ACTIVETAB_WIDTH = 64;
    private static final int ACTIVETAB_BUTTON_WIDTH = ACTIVETAB_WIDTH - 2;
    private static final int ACTIVETAB_TEXT_OFFSET_X = ACTIVETAB_BUTTON_WIDTH / 2 + 1;
    private static final int ACTIVETAB_HEIGHT = 20;
    private static final int ACTIVETAB_TEXT_OFFSET_Y = 6;
    private static final int ACTIVETAB_POS_X = 2;
    private static final int ACTIVETAB_BUTTON_POS_X = ACTIVETAB_POS_X + 1;
    private static final int ACTIVETAB_POS_Y = 0;
    private static final int ACTIVETAB_UV_X = 120;
    private static final int ACTIVETAB_UV_Y = 236;

    private static final int GENERAL_DESCRIPTION_WIDTH = 80;

    private static final ResourceLocation TEXTURE = new ResourceLocation("vsb", "textures/gui/sound_block.png");

    private SoundBlockData soundBlockData;

    private int lastComponentId = 0;
    private int guiOriginX = 0;
    private int guiOriginY = 0;

    private static int tab = 0;
    private int entryOffset = 0;

    private CheckBox alwaysOnCheckbox;
    private GuiTextField loopDelayTextField;
    private GuiTextField pitchTextField;
    private GuiTextField volumeTextField;
    private GuiTextField distanceTextField;

    private Entry sound1Entry;
    private Entry sound2Entry;
    private Entry sound3Entry;
    private Scrollbar scrollbar;

    private List<GuiTextField> tabbableFields;

    public SoundBlockEditScreen(SoundBlockData soundBlockData) {
        this.soundBlockData = soundBlockData;
    }

    @Override
    public void initGui() {
        lastComponentId = 0;
        guiOriginX = (this.width - GUI_WIDTH) / 2;
        guiOriginY = (this.height - GUI_HEIGHT) / 2;

        // General tab
        int startX = guiOriginX + SOUNDENTRY_POS_X;
        int startY = guiOriginY + SOUNDENTRY_POS_Y;

        alwaysOnCheckbox = new CheckBox(startX + GENERAL_DESCRIPTION_WIDTH, startY, CHECKBOX_UV_X, CHECKBOX_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);

        loopDelayTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + GENERAL_DESCRIPTION_WIDTH + TEXTFIELD_BORDER, startY + TEXTFIELD_BORDER + LINE_HEIGHT * 1 + GAP_Y * 1, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
        pitchTextField     = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + GENERAL_DESCRIPTION_WIDTH + TEXTFIELD_BORDER, startY + TEXTFIELD_BORDER + LINE_HEIGHT * 2 + GAP_Y * 2, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
        volumeTextField    = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + GENERAL_DESCRIPTION_WIDTH + TEXTFIELD_BORDER, startY + TEXTFIELD_BORDER + LINE_HEIGHT * 3 + GAP_Y * 3, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
        distanceTextField  = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + GENERAL_DESCRIPTION_WIDTH + TEXTFIELD_BORDER, startY + TEXTFIELD_BORDER + LINE_HEIGHT * 4 + GAP_Y * 4, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);

        loopDelayTextField.setMaxStringLength(8);
        pitchTextField.setMaxStringLength(8);
        volumeTextField.setMaxStringLength(8);
        distanceTextField.setMaxStringLength(8);

        loopDelayTextField.setValidator(isValidNumber);
        pitchTextField.setValidator(isValidNumber);
        volumeTextField.setValidator(isValidNumber);
        distanceTextField.setValidator(isValidNumber);

        // Sound tabs
        sound1Entry = new Entry(0, guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y);
        sound2Entry = new Entry(1, guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y + SOUNDENTRY_HEIGHT - GAP_Y);
        sound3Entry = new Entry(2, guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y + (SOUNDENTRY_HEIGHT - GAP_Y) * 2);

        scrollbar = new Scrollbar(guiOriginX + SCROLLBAR_THUMB_POS_X, guiOriginY + SCROLLBAR_THUMB_POS_Y, SCROLLBAR_THUMB_UV_X, SCROLLBAR_THUMB_UV_Y, SCROLLBAR_THUMB_WIDTH, SCROLLBAR_THUMB_HEIGHT, SCROLLBAR_HEIGHT - 2 * SCROLLBAR_BORDER, 1);

        // Cache the text fields for later use
        this.tabbableFields = Lists.newArrayList(
                this.loopDelayTextField,
                this.pitchTextField,
                this.volumeTextField,
                this.distanceTextField
        );

        openTab(tab);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // Background
        this.mc.getTextureManager().bindTexture(TEXTURE);
        drawModalRectWithCustomSizedTexture(guiOriginX, guiOriginY, 0, 0, GUI_WIDTH, GUI_HEIGHT, GUI_TEXTURE_WIDTH, GUI_TEXTURE_HEIGHT);

        // Tabs
        drawModalRectWithCustomSizedTexture(guiOriginX + ACTIVETAB_POS_X + ACTIVETAB_BUTTON_WIDTH * tab, guiOriginY + ACTIVETAB_POS_Y, ACTIVETAB_UV_X, ACTIVETAB_UV_Y, ACTIVETAB_WIDTH, ACTIVETAB_HEIGHT, GUI_TEXTURE_WIDTH, GUI_TEXTURE_HEIGHT);
        drawCenteredString(fontRenderer, "General", guiOriginX + ACTIVETAB_POS_X + ACTIVETAB_TEXT_OFFSET_X + ACTIVETAB_BUTTON_WIDTH * 0, guiOriginY + ACTIVETAB_POS_Y + ACTIVETAB_TEXT_OFFSET_Y, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Intro", guiOriginX + ACTIVETAB_POS_X + ACTIVETAB_TEXT_OFFSET_X + ACTIVETAB_BUTTON_WIDTH * 1, guiOriginY + ACTIVETAB_POS_Y + ACTIVETAB_TEXT_OFFSET_Y, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Loop", guiOriginX + ACTIVETAB_POS_X + ACTIVETAB_TEXT_OFFSET_X + ACTIVETAB_BUTTON_WIDTH * 2, guiOriginY + ACTIVETAB_POS_Y + ACTIVETAB_TEXT_OFFSET_Y, 0xFFFFFF);
        drawCenteredString(fontRenderer, "Outro", guiOriginX + ACTIVETAB_POS_X + ACTIVETAB_TEXT_OFFSET_X + ACTIVETAB_BUTTON_WIDTH * 3, guiOriginY + ACTIVETAB_POS_Y + ACTIVETAB_TEXT_OFFSET_Y, 0xFFFFFF);

        if (tab == 0) {
            // General tab
            alwaysOnCheckbox.draw(mouseX, mouseY, partialTicks, soundBlockData.isAlwaysPowered());

            loopDelayTextField.drawTextBox();
            pitchTextField.drawTextBox();
            volumeTextField.drawTextBox();
            distanceTextField.drawTextBox();

        } else {
            // Sound entries
            sound1Entry.draw(mouseX, mouseY, partialTicks);
            sound2Entry.draw(mouseX, mouseY, partialTicks);
            sound3Entry.draw(mouseX, mouseY, partialTicks);

            // Scrollbar
            drawModalRectWithCustomSizedTexture(guiOriginX + SCROLLBAR_POS_X, guiOriginY + SCROLLBAR_POS_Y, SCROLLBAR_UV_X, SCROLLBAR_UV_Y, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT, GUI_TEXTURE_WIDTH, GUI_TEXTURE_HEIGHT);
            scrollbar.draw(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int lastTab = tab;
        int newTab = tab;

        // Tabs
        int tabX = guiOriginX + ACTIVETAB_BUTTON_POS_X;
        int tabY = guiOriginY + ACTIVETAB_POS_Y;
        if (mouseX >= tabX && mouseY >= tabY && mouseX < tabX + ACTIVETAB_BUTTON_WIDTH && mouseY < tabY + ACTIVETAB_HEIGHT) {
            newTab = 0;
        }
        tabX += ACTIVETAB_BUTTON_WIDTH;
        if (mouseX >= tabX && mouseY >= tabY && mouseX < tabX + ACTIVETAB_BUTTON_WIDTH && mouseY < tabY + ACTIVETAB_HEIGHT) {
            newTab = 1;
        }
        tabX += ACTIVETAB_BUTTON_WIDTH;
        if (mouseX >= tabX && mouseY >= tabY && mouseX < tabX + ACTIVETAB_BUTTON_WIDTH && mouseY < tabY + ACTIVETAB_HEIGHT) {
            newTab = 2;
        }
        tabX += ACTIVETAB_BUTTON_WIDTH;
        if (mouseX >= tabX && mouseY >= tabY && mouseX < tabX + ACTIVETAB_BUTTON_WIDTH && mouseY < tabY + ACTIVETAB_HEIGHT) {
            newTab = 3;
        }

        if (lastTab != newTab) {
            openTab(newTab);
            return;
        }

        if (tab == 0) {
            // General tab
            if (alwaysOnCheckbox.mouseClicked(mouseX, mouseY, mouseButton)) {
                soundBlockData.setAlwaysPowered(!soundBlockData.isAlwaysPowered());
            }

            loopDelayTextField.mouseClicked(mouseX, mouseY, mouseButton);
            pitchTextField.mouseClicked(mouseX, mouseY, mouseButton);
            volumeTextField.mouseClicked(mouseX, mouseY, mouseButton);
            distanceTextField.mouseClicked(mouseX, mouseY, mouseButton);

        } else {
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

        if (tab == 0) {
            if (keyCode == Keyboard.KEY_TAB) {
                tabThroughTextFields(this.tabbableFields, GuiScreen.isShiftKeyDown());
                return;
            }

            if (loopDelayTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundBlockData.setLoopDelay(parseAsInt(loopDelayTextField.getText(), soundBlockData.getLoopDelay()));
                return;
            }
            if (pitchTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundBlockData.setPitch(parseAsFloat(pitchTextField.getText(), soundBlockData.getPitch()));
                return;
            }
            if (volumeTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundBlockData.setVolume(parseAsFloat(volumeTextField.getText(), soundBlockData.getVolume()));
                return;
            }
            if (distanceTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundBlockData.setDistance(parseAsFloat(distanceTextField.getText(), soundBlockData.getDistance()));
                return;
            }

        } else {
            // Sound entries
            sound1Entry.keyTyped(typedChar, keyCode);
            sound2Entry.keyTyped(typedChar, keyCode);
            sound3Entry.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        if (tab == 0) {
            return;
        }

        // Enable mouse wheel scrolling
        int dwheel = Mouse.getEventDWheel();

        if (dwheel != 0) {
            int scrollOffset = Integer.signum(dwheel);
            scrollbar.setPosition(scrollbar.position - scrollOffset);
        };

    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        soundBlockData.markDirty();
        Main.network.sendToServer(new SoundBlockMessage(soundBlockData));
    }

    private List<SoundBlockData.Sound> getActiveSoundList() {
        return tab == 1 ? soundBlockData.getIntro() : tab == 2 ? soundBlockData.getLoop() : soundBlockData.getOutro();
    }

    private void refreshAllStates() {
        List<SoundBlockData.Sound> soundList = getActiveSoundList();
        scrollbar.setMaxPosition(soundList.size() - 1);

        sound1Entry.refreshState();
        sound2Entry.refreshState();
        sound3Entry.refreshState();
    }

    private void openTab(int newTab) {
        tab = newTab;
        boolean isGeneralTab = (tab == 0);

        scrollbar.setVisible(!isGeneralTab);

        alwaysOnCheckbox.setVisible(isGeneralTab);

        loopDelayTextField.setVisible(isGeneralTab);
        pitchTextField.setVisible(isGeneralTab);
        volumeTextField.setVisible(isGeneralTab);
        distanceTextField.setVisible(isGeneralTab);

        if (isGeneralTab) {
            // General tab
            loopDelayTextField.setText(((Integer) soundBlockData.getLoopDelay()).toString());
            pitchTextField.setText(((Float) soundBlockData.getPitch()).toString());
            volumeTextField.setText(((Float) soundBlockData.getVolume()).toString());
            distanceTextField.setText(((Float) soundBlockData.getDistance()).toString());

            loopDelayTextField.setCursorPositionZero();
            pitchTextField.setCursorPositionZero();
            volumeTextField.setCursorPositionZero();
            distanceTextField.setCursorPositionZero();

        } else {
            // Sound tabs
            refreshAllStates();
        }
    }

    private static void tabThroughTextFields(List<GuiTextField> tabbableFields, boolean shiftPressed) {
        int size = tabbableFields.size();
        int textFieldPos = -1;

        // Determine the step direction (1 for forward, -1 for backward)
        int step = (shiftPressed ? -1 : 1);

        // Determine the currently focused text field, and increment by one
        for (int i = 0; i < size; i++) {
            if (tabbableFields.get(i).isFocused()) {
                textFieldPos = (i + step) % size;

                // Manually handle negative values (modulo isn't enough when decrementing)
                if (textFieldPos < 0) {
                    textFieldPos = size - 1;
                }

                break;
            }
        }

        // If no text field is currently focused, then no tabbing needs to be done
        if (textFieldPos < 0) {
            return;
        }

        // Focus the newly selected text field
        for (int i = 0; i < size; i++) {
            tabbableFields.get(i).setFocused(i == textFieldPos);
        }
    }

    private static int parseAsInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static float parseAsFloat(String text, float defaultValue) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static double parseAsDouble(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Custom text field validators
    protected static final Predicate<String> isValidNumber = (value) -> {
        try { // Evil number parsing hack, go bother Valaphee for details
            Double.parseDouble(value + (value.contains(".") ? "0" : ".0"));
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    };

    class Entry {
        private int localEntryOffset;
        private int originX;
        private int originY;

        private SoundBlockData.Sound soundData;

        private GuiTextField idTextField;
        private GuiTextField offsetXTextField;
        private GuiTextField offsetYTextField;
        private GuiTextField offsetZTextField;
        private GuiTextField pitchTextField;
        private GuiTextField volumeTextField;
        private GuiTextField distanceTextField;

        private Button buttonAdd;
        private Button buttonRemove;
        private Button buttonMoveUp;
        private Button buttonMoveDown;
        private CheckBox checkboxOnEnter;
        private CheckBox checkboxOnExit;

        private boolean checkboxStateOnEnter = false;
        private boolean checkboxStateOnExit = false;

        private final List<GuiTextField> tabbableFields;

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
            pitchTextField    = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 3 + GAP_X * 6, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            volumeTextField   = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 4 + GAP_X * 7, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            distanceTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 5 + GAP_X * 8, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);

            idTextField.setMaxStringLength(128);
            offsetXTextField.setMaxStringLength(8);
            offsetYTextField.setMaxStringLength(8);
            offsetZTextField.setMaxStringLength(8);
            pitchTextField.setMaxStringLength(8);
            volumeTextField.setMaxStringLength(8);
            volumeTextField.setMaxStringLength(8);

            offsetXTextField.setValidator(isValidNumber);
            offsetYTextField.setValidator(isValidNumber);
            offsetZTextField.setValidator(isValidNumber);
            pitchTextField.setValidator(isValidNumber);
            volumeTextField.setValidator(isValidNumber);
            distanceTextField.setValidator(isValidNumber);

            // Add/Remove
            buttonAdd = new Button(startX, startY, BUTTON_ADD_UV_X, BUTTON_ADD_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);
            buttonRemove = new Button(startX, startY + LINE_HEIGHT + GAP_Y, BUTTON_REMOVE_UV_X, BUTTON_REMOVE_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);

            // Arrows
            buttonMoveUp = new Button(startX + TEXTFIELD_WIDTH_SOUNDID + BUTTON_WIDTH + GAP_X * 2, startY, ARROW_UP_UV_X, ARROW_UP_UV_Y, ARROW_WIDTH, LINE_HEIGHT);
            buttonMoveDown = new Button(startX + TEXTFIELD_WIDTH_SOUNDID + BUTTON_WIDTH + GAP_X * 2, startY + LINE_HEIGHT + GAP_Y, ARROW_DOWN_UV_X, ARROW_DOWN_UV_Y, ARROW_WIDTH, LINE_HEIGHT);

            // Checkboxes
            checkboxOnEnter = new CheckBox(startX + BUTTON_WIDTH + TEXTFIELD_WIDTH_NUMBER * 3 + GAP_X * 4, startY + LINE_HEIGHT + GAP_Y, CHECKBOX_UV_X, CHECKBOX_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);
            checkboxOnExit = new CheckBox(startX + BUTTON_WIDTH * 2 + TEXTFIELD_WIDTH_NUMBER * 3 + GAP_X * 5, startY + LINE_HEIGHT + GAP_Y, CHECKBOX_UV_X, CHECKBOX_UV_Y, BUTTON_WIDTH, LINE_HEIGHT);

            // Cache the text fields for later use
            this.tabbableFields = Lists.newArrayList(
                    this.idTextField,
                    this.offsetXTextField,
                    this.offsetYTextField,
                    this.offsetZTextField,
                    this.pitchTextField,
                    this.volumeTextField,
                    this.distanceTextField
            );

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
                pitchTextField.setText(((Float) soundData.getPitch()).toString());
                volumeTextField.setText(((Float) soundData.getVolume()).toString());
                distanceTextField.setText(((Float) soundData.getDistance()).toString());

                offsetXTextField.setCursorPositionZero();
                offsetYTextField.setCursorPositionZero();
                offsetZTextField.setCursorPositionZero();
                pitchTextField.setCursorPositionZero();
                volumeTextField.setCursorPositionZero();
                distanceTextField.setCursorPositionZero();

                checkboxStateOnEnter = soundData.isStopOnEnter();
                checkboxStateOnExit = soundData.isStopOnExit();
            }

            idTextField.setVisible(isVisible);
            offsetXTextField.setVisible(isVisible);
            offsetYTextField.setVisible(isVisible);
            offsetZTextField.setVisible(isVisible);
            pitchTextField.setVisible(isVisible);
            volumeTextField.setVisible(isVisible);
            distanceTextField.setVisible(isVisible);

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
            drawModalRectWithCustomSizedTexture(originX, originY, SOUNDENTRY_UV_X, SOUNDENTRY_UV_Y, SOUNDENTRY_WIDTH, SOUNDENTRY_HEIGHT, GUI_TEXTURE_WIDTH, GUI_TEXTURE_HEIGHT);

            // Text fields
            idTextField.drawTextBox();
            offsetXTextField.drawTextBox();
            offsetYTextField.drawTextBox();
            offsetZTextField.drawTextBox();
            pitchTextField.drawTextBox();
            volumeTextField.drawTextBox();
            distanceTextField.drawTextBox();

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
            if (pitchTextField.getVisible()) {
                pitchTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (volumeTextField.getVisible()) {
                volumeTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }
            if (distanceTextField.getVisible()) {
                distanceTextField.mouseClicked(mouseX, mouseY, mouseButton);
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

            if (keyCode == Keyboard.KEY_TAB) {
                tabThroughTextFields(this.tabbableFields, GuiScreen.isShiftKeyDown());
                return;
            }

            if (idTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundData.setId(idTextField.getText());
                return;
            }
            if (offsetXTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundData.setOffsetX(parseAsDouble(offsetXTextField.getText(), soundData.getOffsetX()));
                return;
            }
            if (offsetYTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundData.setOffsetY(parseAsDouble(offsetYTextField.getText(), soundData.getOffsetY()));
                return;
            }
            if (offsetZTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundData.setOffsetZ(parseAsDouble(offsetZTextField.getText(), soundData.getOffsetZ()));
                return;
            }
            if (pitchTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundData.setPitch(parseAsFloat(pitchTextField.getText(), soundData.getPitch()));
                return;
            }
            if (volumeTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundData.setVolume(parseAsFloat(volumeTextField.getText(), soundData.getVolume()));
                return;
            }
            if (distanceTextField.textboxKeyTyped(typedChar, keyCode)) {
                soundData.setDistance(parseAsFloat(distanceTextField.getText(), soundData.getDistance()));
                return;
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

                drawModalRectWithCustomSizedTexture(buttonX, buttonY, buttonUV_x + coefHoveredX * buttonWidth, buttonUV_y + coefHoveredY * buttonHeight, buttonWidth, buttonHeight, GUI_TEXTURE_WIDTH, GUI_TEXTURE_HEIGHT);
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

                drawModalRectWithCustomSizedTexture(buttonX, buttonY, buttonUV_x + (coefHoveredX + coefCheckedX) * buttonWidth, buttonUV_y + (coefHoveredY + coefCheckedY) * buttonHeight, buttonWidth, buttonHeight, GUI_TEXTURE_WIDTH, GUI_TEXTURE_HEIGHT);
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

            position = 0;
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
                        float percent = (float) (mouseY - buttonY) / (float) (scrollableHeight + buttonHeight);

                        setPosition(Integer.min((int) (percent * (float) (maxPosition + 1)), maxPosition));
                    }

                    posY += (int) (((float) position / (float) maxPosition) * (float) scrollableHeight);
                }

                drawModalRectWithCustomSizedTexture(buttonX, posY, buttonUV_x + coefHoveredX * buttonWidth, buttonUV_y + coefHoveredY * buttonHeight, buttonWidth, buttonHeight, GUI_TEXTURE_WIDTH, GUI_TEXTURE_HEIGHT);
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
            int posY = this.buttonY + (int) (((float) position / (float) maxPosition) * (float) scrollableHeight);

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
