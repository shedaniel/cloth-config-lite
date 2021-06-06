package me.shedaniel.clothconfiglite.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import me.shedaniel.clothconfiglite.api.ConfigScreen;
import me.shedaniel.clothconfiglite.impl.inner.ClothConfigScreenButtons;
import me.shedaniel.clothconfiglite.impl.option.Option;
import me.shedaniel.clothconfiglite.impl.option.TextFieldOption;
import me.shedaniel.clothconfiglite.impl.option.ToggleOption;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigScreenImpl extends Screen implements ConfigScreen {
    private static final int TOP = 26;
    private static final int BOTTOM = 24;
    public static final float SCROLLBAR_BOTTOM_COLOR = .5f;
    public static final float SCROLLBAR_TOP_COLOR = .67f;
    private final Screen parent;
    private final List<Option<?>> options = new ArrayList<>();
    public double scrollerAmount;
    private boolean dragging;
    
    public ConfigScreenImpl(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }
    
    private static Component toText(Enum val) {
        return new TranslatableComponent(val.toString());
    }
    
    private static Component toText(Boolean bool) {
        return new TranslatableComponent("t.ccl.boolean.value." + bool.toString());
    }
    
    @Override
    public <T> void add(Component text, T value, @Nullable Supplier<T> defaultValue, Consumer<T> savingConsumer) {
        Option<T> option = (Option<T>) createOption(value);
        option.text = text;
        option.defaultValue = defaultValue;
        option.savingConsumer = savingConsumer;
        option.originalValue = value;
        option.value = value;
        options.add(option);
        option.onAdd();
    }
    
    private <T> Option<?> createOption(T value) {
        if (value instanceof Enum) {
            Object[] objects = value.getClass().getEnumConstants();
            return new ToggleOption<Enum>((List) Arrays.asList(objects), ConfigScreenImpl::toText);
        }
        if (value instanceof Boolean) {
            return new ToggleOption<>(Arrays.asList(Boolean.TRUE, Boolean.FALSE), ConfigScreenImpl::toText);
        }
        if (value instanceof String) {
            return new TextFieldOption<>(Function.identity(), Function.identity());
        }
        if (value instanceof Integer) {
            return new TextFieldOption<>(Objects::toString, Integer::valueOf);
        }
        if (value instanceof Long) {
            return new TextFieldOption<>(Objects::toString, Long::valueOf);
        }
        if (value instanceof Double) {
            return new TextFieldOption<>(Objects::toString, Double::valueOf);
        }
        if (value instanceof Float) {
            return new TextFieldOption<>(Objects::toString, Float::valueOf);
        }
        if (value instanceof BigInteger) {
            return new TextFieldOption<>(Objects::toString, BigInteger::new);
        }
        if (value instanceof BigDecimal) {
            return new TextFieldOption<>(Objects::toString, BigDecimal::new);
        }
        if (value instanceof ResourceLocation) {
            return new TextFieldOption<>(Objects::toString, ResourceLocation::new);
        }
        throw new IllegalArgumentException(String.valueOf(value));
    }
    
    @Override
    protected void init() {
        super.init();
        ((List) children()).addAll(options);
        
        int buttonWidths = Math.min(200, (width - 50 - 12) / 3);
        addRenderableWidget(new ClothConfigScreenButtons(this, width / 2 - buttonWidths - 3, height - 22, buttonWidths, 20, TextComponent.EMPTY, true));
        addRenderableWidget(new ClothConfigScreenButtons(this, width / 2 + 3, height - 22, buttonWidths, 20, TextComponent.EMPTY, false));
    }
    
    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        overlayBackground(matrices, TOP, height - BOTTOM, 32);
        int y = (int) (TOP + 4 - Math.round(scrollerAmount));
        for (Option<?> option : options) {
            int height1 = option.height();
            option.render(minecraft, font, 40, y, width - 80, height1, matrices, mouseX, mouseY, delta);
            y += height1;
        }
        
        renderScrollBar();
        
        matrices.pushPose();
        matrices.translate(0, 0, 500.0);
        overlayBackground(matrices, 0, TOP, 64);
        overlayBackground(matrices, height - BOTTOM, height, 64);
        renderShadow(matrices);
        drawCenteredString(matrices, font, getTitle(), width / 2, 9, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        matrices.popPose();
    }
    
    private void renderScrollBar() {
        int listHeight = height - BOTTOM - TOP;
        int totalHeight = totalHeight();
        if (totalHeight > listHeight) {
            int maxScroll = Math.max(0, totalHeight - listHeight);
            int height = listHeight * listHeight / totalHeight;
            height = Mth.clamp(height, 32, listHeight);
            height = Math.max(10, height);
            int minY = Math.min(Math.max((int) scrollerAmount * (listHeight - height) / maxScroll + TOP, TOP), this.height - BOTTOM - height);
            
            int scrollbarPositionMaxX = width;
            int scrollbarPositionMinX = scrollbarPositionMaxX - 6;
            
            int maxY = this.height - BOTTOM;
            RenderSystem.disableTexture();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            
            buffer.vertex(scrollbarPositionMinX, maxY, 0.0D).color(0, 0, 0, 255).endVertex();
            buffer.vertex(scrollbarPositionMaxX, maxY, 0.0D).color(0, 0, 0, 255).endVertex();
            buffer.vertex(scrollbarPositionMaxX, TOP, 0.0D).color(0, 0, 0, 255).endVertex();
            buffer.vertex(scrollbarPositionMinX, TOP, 0.0D).color(0, 0, 0, 255).endVertex();
            
            buffer.vertex(scrollbarPositionMinX, minY + height, 0.0D).color(SCROLLBAR_BOTTOM_COLOR, SCROLLBAR_BOTTOM_COLOR, SCROLLBAR_BOTTOM_COLOR, 1).endVertex();
            buffer.vertex(scrollbarPositionMaxX, minY + height, 0.0D).color(SCROLLBAR_BOTTOM_COLOR, SCROLLBAR_BOTTOM_COLOR, SCROLLBAR_BOTTOM_COLOR, 1).endVertex();
            buffer.vertex(scrollbarPositionMaxX, minY, 0.0D).color(SCROLLBAR_BOTTOM_COLOR, SCROLLBAR_BOTTOM_COLOR, SCROLLBAR_BOTTOM_COLOR, 1).endVertex();
            buffer.vertex(scrollbarPositionMinX, minY, 0.0D).color(SCROLLBAR_BOTTOM_COLOR, SCROLLBAR_BOTTOM_COLOR, SCROLLBAR_BOTTOM_COLOR, 1).endVertex();
            buffer.vertex(scrollbarPositionMinX, (minY + height - 1), 0.0D).color(SCROLLBAR_TOP_COLOR, SCROLLBAR_TOP_COLOR, SCROLLBAR_TOP_COLOR, 1).endVertex();
            buffer.vertex((scrollbarPositionMaxX - 1), (minY + height - 1), 0.0D).color(SCROLLBAR_TOP_COLOR, SCROLLBAR_TOP_COLOR, SCROLLBAR_TOP_COLOR, 1).endVertex();
            buffer.vertex((scrollbarPositionMaxX - 1), minY, 0.0D).color(SCROLLBAR_TOP_COLOR, SCROLLBAR_TOP_COLOR, SCROLLBAR_TOP_COLOR, 1).endVertex();
            buffer.vertex(scrollbarPositionMinX, minY, 0.0D).color(SCROLLBAR_TOP_COLOR, SCROLLBAR_TOP_COLOR, SCROLLBAR_TOP_COLOR, 1).endVertex();
            tesselator.end();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
        }
    }
    
    private void renderShadow(PoseStack matrices) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 0, 1);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Matrix4f matrix = matrices.last().pose();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(matrix, 0, TOP + 4, 0.0F).uv(0, 1).color(0, 0, 0, 0).endVertex();
        buffer.vertex(matrix, width, TOP + 4, 0.0F).uv(1, 1).color(0, 0, 0, 0).endVertex();
        buffer.vertex(matrix, width, TOP, 0.0F).uv(1, 0).color(0, 0, 0, 185).endVertex();
        buffer.vertex(matrix, 0, TOP, 0.0F).uv(0, 0).color(0, 0, 0, 185).endVertex();
        buffer.vertex(matrix, 0, height - BOTTOM, 0.0F).uv(0, 1).color(0, 0, 0, 185).endVertex();
        buffer.vertex(matrix, width, height - BOTTOM, 0.0F).uv(1, 1).color(0, 0, 0, 185).endVertex();
        buffer.vertex(matrix, width, height - BOTTOM - 4, 0.0F).uv(1, 0).color(0, 0, 0, 0).endVertex();
        buffer.vertex(matrix, 0, height - BOTTOM - 4, 0.0F).uv(0, 0).color(0, 0, 0, 0).endVertex();
        tesselator.end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    
    protected void overlayBackground(PoseStack matrices, int h1, int h2, int color) {
        overlayBackground(matrices.last().pose(), 0, h1, width, h2, color, color, color, 255, 255);
    }
    
    protected void overlayBackground(Matrix4f matrix, int minX, int minY, int maxX, int maxY, int red, int green, int blue, int startAlpha, int endAlpha) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(matrix, minX, maxY, 0.0F).uv(minX / 32.0F, maxY / 32.0F).color(red, green, blue, endAlpha).endVertex();
        buffer.vertex(matrix, maxX, maxY, 0.0F).uv(maxX / 32.0F, maxY / 32.0F).color(red, green, blue, endAlpha).endVertex();
        buffer.vertex(matrix, maxX, minY, 0.0F).uv(maxX / 32.0F, minY / 32.0F).color(red, green, blue, startAlpha).endVertex();
        buffer.vertex(matrix, minX, minY, 0.0F).uv(minX / 32.0F, minY / 32.0F).color(red, green, blue, startAlpha).endVertex();
        tesselator.end();
    }
    
    public int scrollHeight() {
        int totalHeight = totalHeight();
        int listHeight = height - BOTTOM - TOP;
        if (totalHeight <= listHeight) {
            return 0;
        }
        return totalHeight - listHeight;
    }
    
    public int totalHeight() {
        int i = 8;
        for (Option<?> option : options) {
            i += option.height();
        }
        return i;
    }
    
    public boolean hasErrors() {
        for (Option<?> option : options) {
            if (option.hasErrors) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEdited() {
        for (Option<?> option : options) {
            if (option.isEdited()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void save() {
        for (Option option : options) {
            option.save();
            option.originalValue = option.value;
        }
    }
    
    @Override
    public void onClose() {
        if (isEdited()) {
            minecraft.setScreen(new ConfirmScreen(this::acceptConfirm, new TranslatableComponent("t.ccl.quit_config"),
                    new TranslatableComponent("t.ccl.quit_config_sure"),
                    new TranslatableComponent("t.ccl.quit_discard"),
                    new TranslatableComponent("gui.cancel")));
        } else {
            minecraft.setScreen(parent);
        }
    }
    
    @Override
    public boolean mouseScrolled(double d, double e, double f) {
        if (e >= TOP && e <= height - BOTTOM) {
            scrollerAmount = Mth.clamp(scrollerAmount - f * 16.0D, 0, scrollHeight());
            return true;
        }
        return super.mouseScrolled(d, e, f);
    }
    
    @Override
    public boolean mouseClicked(double d, double e, int i) {
        this.dragging = i == 0 && d >= width - 6 && d < width;
        return super.mouseClicked(d, e, i) || dragging;
    }
    
    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (super.mouseDragged(d, e, i, f, g)) {
            return true;
        }
        if (i != 0 || !this.dragging) {
            return false;
        }
        if (e < TOP) {
            scrollerAmount = 0;
        } else if (e > height - BOTTOM) {
            scrollerAmount = scrollHeight();
        } else {
            double h = Math.max(1, this.scrollHeight());
            int j = height - BOTTOM - TOP;
            int k = Mth.clamp((int) ((float) (j * j) / (float) this.scrollHeight()), 32, j - 8);
            double l = Math.max(1.0, h / (double) (j - k));
            scrollerAmount = Mth.clamp(scrollerAmount + g * l, 0, scrollHeight());
        }
        return true;
    }
    
    private void acceptConfirm(boolean t) {
        if (!t) {
            minecraft.setScreen(this);
        } else {
            minecraft.setScreen(parent);
        }
    }
}
