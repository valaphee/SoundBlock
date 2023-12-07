package com.valaphee.sb;

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
    private static final int TICKBOX_EMPTY_UV_X = 32;
    private static final int TICKBOX_EMPTY_UV_Y = 240;
    private static final int TICKBOX_TICKED_UV_X = 64;
    private static final int TICKBOX_TICKED_UV_Y = 240;

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
            // Scrollbar
            this.drawTexturedModalRect(guiOriginX + SCROLLBAR_POS_X, guiOriginY + SCROLLBAR_POS_Y, SCROLLBAR_UV_X, SCROLLBAR_UV_Y, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);

            // Scrollbar handle
            this.drawTexturedModalRect(guiOriginX + SCROLLBAR_THUMB_POS_X, guiOriginY + SCROLLBAR_THUMB_POS_Y, SCROLLBAR_THUMB_UV_X, SCROLLBAR_THUMB_UV_Y, SCROLLBAR_THUMB_WIDTH, SCROLLBAR_THUMB_HEIGHT);

            // Sound entries
            sound1Entry.draw();
            sound2Entry.draw();
            sound3Entry.draw();
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

        // Sound entries
        if (tab != 0) {
            if (tab != lastTab) {
                sound1Entry.load();
                sound2Entry.load();
                sound3Entry.load();
            }

            sound1Entry.mouseClicked(mouseX, mouseY, mouseButton);
            sound2Entry.mouseClicked(mouseX, mouseY, mouseButton);
            sound3Entry.mouseClicked(mouseX, mouseY, mouseButton);
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

    class Entry {
        private int localEntryOffset;
        private int originX;
        private int originY;

        private GuiTextField idTextField;
        private GuiTextField offsetXTextField;
        private GuiTextField offsetYTextField;
        private GuiTextField offsetZTextField;
        private GuiTextField volumeTextField;
        private GuiTextField pitchTextField;

        public Entry(int localEntryOffset, int originX, int originY) {
            this.localEntryOffset = localEntryOffset;
            this.originX = originX;
            this.originY = originY;

            int startX = originX + GAP_X * 2;
            int startY = originY + GAP_Y * 2;
            idTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + GAP_X, startY + TEXTFIELD_BORDER, TEXTFIELD_WIDTH_SOUNDID - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetXTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + GAP_X, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetYTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + TEXTFIELD_WIDTH_NUMBER + GAP_X * 2, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetZTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + TEXTFIELD_WIDTH_NUMBER * 2 + GAP_X * 3, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            volumeTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 3 + GAP_X * 6, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            pitchTextField = new GuiTextField(lastComponentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 4 + GAP_X * 7, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);

            idTextField.setMaxStringLength(256);
            offsetXTextField.setMaxStringLength(8);
            offsetYTextField.setMaxStringLength(8);
            offsetZTextField.setMaxStringLength(8);
            volumeTextField.setMaxStringLength(8);
            pitchTextField.setMaxStringLength(8);

            load();
        }

        public void load() {
            List<SoundBlockData.Sound> soundList = tab == 1 ? soundBlockData.getIntro() : tab == 2 ? soundBlockData.getLoop() : soundBlockData.getOutro();
            if (entryOffset + localEntryOffset < soundList.size()) {
                SoundBlockData.Sound sound = soundList.get(entryOffset + localEntryOffset);
                idTextField.setText(sound.getId());
                offsetXTextField.setText(((Double) sound.getOffsetX()).toString());
                offsetYTextField.setText(((Double) sound.getOffsetY()).toString());
                offsetZTextField.setText(((Double) sound.getOffsetZ()).toString());
                volumeTextField.setText(((Float) sound.getVolume()).toString());
                pitchTextField.setText(((Float) sound.getPitch()).toString());
            } else {
                idTextField.setVisible(false);
                offsetXTextField.setVisible(false);
                offsetYTextField.setVisible(false);
                offsetZTextField.setVisible(false);
                volumeTextField.setVisible(false);
                pitchTextField.setVisible(false);
            }
        }

        public void draw() {
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
        }

        public void keyTyped(char typedChar, int keyCode) {
            if (idTextField.getVisible()) {
                idTextField.textboxKeyTyped(typedChar, keyCode);
            }
            if (offsetXTextField.getVisible()) {
                offsetXTextField.textboxKeyTyped(typedChar, keyCode);
            }
            if (offsetYTextField.getVisible()) {
                offsetYTextField.textboxKeyTyped(typedChar, keyCode);
            }
            if (offsetZTextField.getVisible()) {
                offsetZTextField.textboxKeyTyped(typedChar, keyCode);
            }
            if (volumeTextField.getVisible()) {
                volumeTextField.textboxKeyTyped(typedChar, keyCode);
            }
            if (pitchTextField.getVisible()) {
                pitchTextField.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }
}
