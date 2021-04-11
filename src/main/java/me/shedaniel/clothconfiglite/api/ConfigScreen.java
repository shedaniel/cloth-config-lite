package me.shedaniel.clothconfiglite.api;

import me.shedaniel.clothconfiglite.impl.ConfigScreenImpl;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ConfigScreen {
    static ConfigScreen create(Component title, Screen parent) {
        return new ConfigScreenImpl(title, parent);
    }
    
    default Screen get() {
        return (Screen) this;
    }
    
    <T> void add(Component text, T value, @Nullable Supplier<T> defaultValue, Consumer<T> savingConsumer);
    
    void save();
}
