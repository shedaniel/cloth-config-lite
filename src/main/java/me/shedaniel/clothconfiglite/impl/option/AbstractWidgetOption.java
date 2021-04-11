package me.shedaniel.clothconfiglite.impl.option;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;

public abstract class AbstractWidgetOption<T, W extends AbstractWidget> extends BaseOption<T> {
    public static final int buttonWidth = 100;
    public static final int buttonHeight = 20;
    public W widget;
    
    @Override
    public void render(Minecraft minecraft, Font font, int x, int y, int width, int height, PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(minecraft, font, x, y, width, height, matrices, mouseX, mouseY, delta);
        int i = (widget instanceof EditBox ? 1 : 0);
        widget.x = x + width - 100 - resetButtonOffset + i;
        widget.y = y + i + 1;
        widget.render(matrices, mouseX, mouseY, delta);
    }
}
