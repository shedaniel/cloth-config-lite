package me.shedaniel.clothconfiglite.test;

import me.shedaniel.clothconfiglite.api.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

public class TestModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigScreen screen = ConfigScreen.create(new TextComponent("Epic"), parent);
            screen.add(new TextComponent("Epic Game mode"), GameType.SURVIVAL, () -> GameType.SURVIVAL, System.out::println);
            screen.add(new TextComponent("Epic Int"), 10, () -> 10, System.out::println);
            screen.add(new TextComponent("Epic Float"), 0.1F, () -> 0.1F, System.out::println);
            screen.add(new TextComponent("Epic Double"), 0.1, () -> 0.1, System.out::println);
            screen.add(new TextComponent("Epic Double No Default"), 0.1, null, System.out::println);
            screen.add(new TextComponent("Epic String"), "Epic", () -> "Epic", System.out::println);
            screen.add(new TextComponent("Epic ID"), new ResourceLocation("epic:yes"), () -> new ResourceLocation("epic:yes"), System.out::println);
            for (int i = 0; i < 100; i++) {
                screen.add(new TextComponent("Epic Bool"), true, () -> true, System.out::println);
            }
            return screen.get();
        };
    }
}
