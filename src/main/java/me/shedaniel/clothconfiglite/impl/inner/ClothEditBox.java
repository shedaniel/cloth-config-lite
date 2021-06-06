package me.shedaniel.clothconfiglite.impl.inner;

import me.shedaniel.clothconfiglite.impl.option.TextFieldOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public class ClothEditBox extends EditBox {
    public ClothEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
    }
    
    @Override
    public void setFocus(boolean bl) {
        for (GuiEventListener child : Minecraft.getInstance().screen.children()) {
            if (child instanceof TextFieldOption<?> option) {
                ClothEditBox box = option.widget;
                box.setFocused(box == this);
            }
        }
        super.setFocus(bl);
    }
}
