package me.shedaniel.clothconfiglite.impl.inner;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.clothconfiglite.impl.ConfigScreenImpl;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ClothConfigScreenButtons extends AbstractButton {
    ConfigScreenImpl screen;
    boolean cancel;
    
    public ClothConfigScreenButtons(ConfigScreenImpl screen, int i, int j, int k, int l, Component component, boolean cancel) {
        super(i, j, k, l, component);
        this.screen = screen;
        this.cancel = cancel;
    }
    
    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        if (cancel) {
            setMessage(new TranslatableComponent(screen.isEdited() ? "t.ccl.cancel_discard" : "gui.cancel"));
        } else {
            boolean hasErrors = screen.hasErrors();
            active = screen.isEdited() && !hasErrors;
            setMessage(new TranslatableComponent(hasErrors ? "t.ccl.error" : "t.ccl.save"));
        }
        super.render(poseStack, i, j, f);
    }
    
    @Override
    public void onPress() {
        if (cancel) {
            screen.onClose();
        } else {
            screen.save();
        }
    }
    
    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.USAGE, getMessage());
    }
}
