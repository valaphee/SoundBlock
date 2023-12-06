package com.valaphee.sb;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

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
    private static final int ACTIVETAB_HEIGHT = 20;
    private static final int ACTIVETAB_POS_X = 2;
    private static final int ACTIVETAB_POS_Y = 0;
    private static final int ACTIVETAB_UV_X = 120;
    private static final int ACTIVETAB_UV_Y = 236;

    private static final ResourceLocation TEXTURE = new ResourceLocation("vsb", "textures/gui/sound_block.png");

    private int componentId = 0;

    private int guiOriginX = 0;
    private int guiOriginY = 0;

    private SoundEntry soundEntry;
    private SoundEntry soundEntry2;
    private SoundEntry soundEntry3;

    @Override
    public void initGui() {
        guiOriginX = (this.width - GUI_WIDTH) / 2;
        guiOriginY = (this.height - GUI_HEIGHT) / 2;

        componentId = 0;
        soundEntry = new SoundEntry(new SoundBlockData.Sound(), guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y);
        soundEntry2 = new SoundEntry(new SoundBlockData.Sound(), guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y + SOUNDENTRY_HEIGHT - GAP_Y);
        soundEntry3 = new SoundEntry(new SoundBlockData.Sound(), guiOriginX + SOUNDENTRY_POS_X, guiOriginY + SOUNDENTRY_POS_Y + (SOUNDENTRY_HEIGHT - GAP_Y) * 2);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        soundEntry.mouseClicked(mouseX, mouseY, mouseButton);
        soundEntry2.mouseClicked(mouseX, mouseY, mouseButton);
        soundEntry3.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(guiOriginX, guiOriginY, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        // Active tab
        this.drawTexturedModalRect(guiOriginX + ACTIVETAB_POS_X, guiOriginY + ACTIVETAB_POS_Y, ACTIVETAB_UV_X, ACTIVETAB_UV_Y, ACTIVETAB_WIDTH, ACTIVETAB_HEIGHT);

        // Scrollbar
        this.drawTexturedModalRect(guiOriginX + SCROLLBAR_POS_X, guiOriginY + SCROLLBAR_POS_Y, SCROLLBAR_UV_X, SCROLLBAR_UV_Y, SCROLLBAR_WIDTH, SCROLLBAR_HEIGHT);

        // Scrollbar thumb
        this.drawTexturedModalRect(guiOriginX + SCROLLBAR_THUMB_POS_X, guiOriginY + SCROLLBAR_THUMB_POS_Y, SCROLLBAR_THUMB_UV_X, SCROLLBAR_THUMB_UV_Y, SCROLLBAR_THUMB_WIDTH, SCROLLBAR_THUMB_HEIGHT);

        soundEntry.draw();
        soundEntry2.draw();
        soundEntry3.draw();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        soundEntry.keyTyped(typedChar, keyCode);
        soundEntry2.keyTyped(typedChar, keyCode);
        soundEntry3.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    class SoundEntry {

        private GuiTextField idTextField;
        private GuiTextField offsetXTextField;
        private GuiTextField offsetYTextField;
        private GuiTextField offsetZTextField;
        private GuiTextField volumeTextField;
        private GuiTextField pitchTextField;

        private int soundEntryOriginX = 0;
        private int soundEntryOriginY = 0;

        public SoundEntry(SoundBlockData.Sound sound, int posX, int posY) {
            soundEntryOriginX = posX;
            soundEntryOriginY = posY;

            int startX = posX + GAP_X * 2;
            int startY = posY + GAP_Y * 2;

            idTextField      = new GuiTextField(componentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + GAP_X, startY + TEXTFIELD_BORDER, TEXTFIELD_WIDTH_SOUNDID - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetXTextField = new GuiTextField(componentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + GAP_X, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetYTextField = new GuiTextField(componentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + TEXTFIELD_WIDTH_NUMBER + GAP_X * 2, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            offsetZTextField = new GuiTextField(componentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH + TEXTFIELD_WIDTH_NUMBER * 2 + GAP_X * 3, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            volumeTextField  = new GuiTextField(componentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 3 + GAP_X * 6, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);
            pitchTextField   = new GuiTextField(componentId++, mc.fontRenderer, startX + TEXTFIELD_BORDER + BUTTON_WIDTH * 3 + TEXTFIELD_WIDTH_NUMBER * 4 + GAP_X * 7, startY + TEXTFIELD_BORDER + LINE_HEIGHT + GAP_Y, TEXTFIELD_WIDTH_NUMBER - 2 * TEXTFIELD_BORDER, LINE_HEIGHT - 2 * TEXTFIELD_BORDER);

            idTextField.setText(sound.getId());
            offsetXTextField.setText(((Double) sound.getOffsetX()).toString());
            offsetYTextField.setText(((Double) sound.getOffsetY()).toString());
            offsetZTextField.setText(((Double) sound.getOffsetZ()).toString());
            volumeTextField.setText(((Float) sound.getVolume()).toString());
            pitchTextField.setText(((Float) sound.getPitch()).toString());

            idTextField.setMaxStringLength(256);
            offsetXTextField.setMaxStringLength(8);
            offsetYTextField.setMaxStringLength(8);
            offsetZTextField.setMaxStringLength(8);
            volumeTextField.setMaxStringLength(8);
            pitchTextField.setMaxStringLength(8);
        }

        public void draw() {

            // Background
            mc.getTextureManager().bindTexture(TEXTURE);
            drawTexturedModalRect(soundEntryOriginX, soundEntryOriginY, SOUNDENTRY_UV_X, SOUNDENTRY_UV_Y, SOUNDENTRY_WIDTH, SOUNDENTRY_HEIGHT);

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
