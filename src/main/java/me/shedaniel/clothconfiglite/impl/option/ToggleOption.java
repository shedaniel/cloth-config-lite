package me.shedaniel.clothconfiglite.impl.option;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.List;
import java.util.function.Function;

public class ToggleOption<T> extends AbstractWidgetOption<T, Button> {
    private final List<T> options;
    private final Function<T, Component> toComponent;
    
    public ToggleOption(List<T> options, Function<T, Component> toComponent) {
        this.options = options;
        this.toComponent = toComponent;
        this.widget = addChild(new Button(0, 0, buttonWidth, buttonHeight, TextComponent.EMPTY, this::switchNext));
    }
    
    @Override
    public void onAdd() {
        widget.setMessage(toComponent.apply(value));
    }
    
    private void switchNext(Button button) {
        value = options.get((options.indexOf(value) + 1) % options.size());
        onAdd();
    }
}
