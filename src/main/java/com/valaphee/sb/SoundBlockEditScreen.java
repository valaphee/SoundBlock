package com.valaphee.sb;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SoundBlockEditScreen extends GuiScreen {
    private static final ResourceLocation TEXTURES = new ResourceLocation("vsb", "textures/gui/sound_block.png");
    private GuiListExtended soundsList;

    @Override
    public void initGui() {
        soundsList = new SoundListControl(mc, width, height, 24, height - 7, 42, Lists.newArrayList(new SoundBlockData.Sound(), new SoundBlockData.Sound(), new SoundBlockData.Sound(), new SoundBlockData.Sound(), new SoundBlockData.Sound()));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        soundsList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        soundsList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        soundsList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        soundsList.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    static class SoundListControl extends GuiListExtended {
        private final List<SoundListControl.Row> sounds = Lists.newArrayList();

        public SoundListControl(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, List<SoundBlockData.Sound> sounds) {
            super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);

            int componentId = 0;
            for (SoundBlockData.Sound sound : sounds) {
                GuiTextField idTextField = new GuiTextField(componentId++, mcIn.fontRenderer, 22, 4, 165, 16);
                idTextField.setText(sound.getId());
                GuiTextField offsetXTextField = new GuiTextField(componentId++, mcIn.fontRenderer, 22, 22, 22, 16);
                offsetXTextField.setText(((Double) sound.getOffsetX()).toString());
                GuiTextField offsetYTextField = new GuiTextField(componentId++, mcIn.fontRenderer, 48, 22, 22, 16);
                offsetYTextField.setText(((Double) sound.getOffsetY()).toString());
                GuiTextField offsetZTextField = new GuiTextField(componentId++, mcIn.fontRenderer, 74, 22, 22, 16);
                offsetZTextField.setText(((Double) sound.getOffsetZ()).toString());
                GuiTextField pitchTextField = new GuiTextField(componentId++, mcIn.fontRenderer, 137, 22, 22, 16);
                pitchTextField.setText(((Float) sound.getPitch()).toString());
                GuiTextField volumeTextField = new GuiTextField(componentId++, mcIn.fontRenderer, 163, 22, 22, 16);
                volumeTextField.setText(((Float) sound.getVolume()).toString());
                this.sounds.add(new Row(idTextField, offsetXTextField, offsetYTextField, offsetZTextField, pitchTextField, volumeTextField));
            }
        }

        @Override
        public IGuiListEntry getListEntry(int index) {
            return sounds.get(index);
        }

        @Override
        protected int getSize() {
            return sounds.size();
        }

        @AllArgsConstructor
        static class Row implements GuiListExtended.IGuiListEntry {
            private final GuiTextField idTextField;
            private final GuiTextField velocityXTextField;
            private final GuiTextField velocityYTextField;
            private final GuiTextField velocityZTextField;
            private final GuiTextField pitchTextField;
            private final GuiTextField volumeTextField;

            @Override
            public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
            }

            @Override
            public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
                idTextField.drawTextBox();
                velocityXTextField.drawTextBox();
                velocityYTextField.drawTextBox();
                velocityZTextField.drawTextBox();
                pitchTextField.drawTextBox();
                volumeTextField.drawTextBox();
            }

            @Override
            public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
                return false;
            }

            @Override
            public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            }
        }
    }
}
