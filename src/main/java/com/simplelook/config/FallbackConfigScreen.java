package com.simplelook.config;

import com.simplelook.SimpleLookClient;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.MouseButtonEvent;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.options.KeyBindsScreen;*/
//?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class FallbackConfigScreen extends Screen {
    private final Screen parent;
    private final SimpleLookConfig config;
    
    // Layout constants
    private static final int HEADER_HEIGHT = 35;
    private static final int FOOTER_HEIGHT = 35;
    private static final int ROW_HEIGHT = 24;
    private static final int WIDGET_WIDTH = 180;
    private static final int RESET_BTN_WIDTH = 40;
    private static final int SPACING = 4;
    private static final int SCROLL_SPEED = 10;
    private static final int SCROLLBAR_WIDTH = 6;
    
    // Scroll state
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int contentHeight = 0;
    private boolean isDraggingScrollbar = false;
    private int scrollbarDragOffset = 0;
    
    // Tooltip system
    private record TooltipEntry(int x, int y, int width, int height, String tooltip) {}
    private final List<TooltipEntry> tooltips = new ArrayList<>();
    
    // Widget tracking
    private record WidgetEntry(AbstractWidget widget, int originalY) {}
    private final List<WidgetEntry> scrollableWidgets = new ArrayList<>();
    private final List<AbstractWidget> footerButtons = new ArrayList<>();
    
    // Sliders for reset functionality
    private IntSlider maxYawSlider;
    private IntSlider maxPitchSlider;
    private IntSlider returnSpeedSlider;
    private IntSlider smoothingSlider;
    private Button enabledButton;
    private Button toggleModeButton;
    
    // Track original values for cancel
    private boolean originalEnabled;
    private float originalMaxYaw;
    private float originalMaxPitch;
    private int originalReturnSpeed;
    private int originalSmoothing;
    private boolean originalToggleMode;
    
    public FallbackConfigScreen(Screen parent) {
        super(Component.translatable("config.simplelook.title"));
        this.parent = parent;
        this.config = SimpleLookClient.getInstance().getConfig();
        
        // Store original values for cancel
        this.originalEnabled = config.enabled;
        this.originalMaxYaw = config.maxYaw;
        this.originalMaxPitch = config.maxPitch;
        this.originalReturnSpeed = config.returnSpeed;
        this.originalSmoothing = config.smoothing;
        this.originalToggleMode = config.toggleMode;
    }
    
    @Override
    protected void init() {
        scrollableWidgets.clear();
        footerButtons.clear();
        tooltips.clear();
        
        int centerX = this.width / 2;
        int widgetX = centerX - (WIDGET_WIDTH + SPACING + RESET_BTN_WIDTH) / 2;
        int resetX = widgetX + WIDGET_WIDTH + SPACING;
        int y = HEADER_HEIGHT + 10;
        
        // === ENABLED TOGGLE ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Enable or disable the free look feature. Default: ON");
        enabledButton = Button.builder(
            Component.literal("Enabled: " + (config.enabled ? "ON" : "OFF")),
            button -> {
                config.enabled = !config.enabled;
                button.setMessage(Component.literal("Enabled: " + (config.enabled ? "ON" : "OFF")));
            }
        ).bounds(widgetX, y, WIDGET_WIDTH, 20).build();
        addScrollableWidget(enabledButton, y);
        
        // Reset button for enabled
        Button enabledReset = Button.builder(Component.literal("↺"), button -> {
            config.enabled = true;
            enabledButton.setMessage(Component.literal("Enabled: ON"));
        }).bounds(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(enabledReset, y);
        y += ROW_HEIGHT;
        
        // === MAX YAW SLIDER ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Maximum horizontal look angle (10-180°). Default: 135°");
        maxYawSlider = new IntSlider(widgetX, y, WIDGET_WIDTH, 20,
            Component.literal("Max Yaw: " + (int)config.maxYaw + "°"),
            (int)config.maxYaw, 10, 180) {
            @Override
            protected void applyValue() {
                config.maxYaw = this.getIntValue();
            }
        };
        addScrollableWidget(maxYawSlider, y);
        
        // Reset button for maxYaw
        Button maxYawReset = Button.builder(Component.literal("↺"), button -> {
            maxYawSlider.setValue(135, 10, 180);
            config.maxYaw = 135;
        }).bounds(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(maxYawReset, y);
        y += ROW_HEIGHT;
        
        // === MAX PITCH SLIDER ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Maximum vertical look angle (10-90°). Default: 90°");
        maxPitchSlider = new IntSlider(widgetX, y, WIDGET_WIDTH, 20,
            Component.literal("Max Pitch: " + (int)config.maxPitch + "°"),
            (int)config.maxPitch, 10, 90) {
            @Override
            protected void applyValue() {
                config.maxPitch = this.getIntValue();
            }
        };
        addScrollableWidget(maxPitchSlider, y);
        
        // Reset button for maxPitch
        Button maxPitchReset = Button.builder(Component.literal("↺"), button -> {
            maxPitchSlider.setValue(90, 10, 90);
            config.maxPitch = 90;
        }).bounds(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(maxPitchReset, y);
        y += ROW_HEIGHT;
        
        // === RETURN SPEED SLIDER ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "How fast camera returns to center (1-100%). Default: 25%");
        returnSpeedSlider = new IntSlider(widgetX, y, WIDGET_WIDTH, 20,
            Component.literal("Return Speed: " + config.returnSpeed + "%"),
            config.returnSpeed, 1, 100) {
            @Override
            protected void applyValue() {
                config.returnSpeed = this.getIntValue();
            }
        };
        addScrollableWidget(returnSpeedSlider, y);
        
        // Reset button for returnSpeed
        Button returnSpeedReset = Button.builder(Component.literal("↺"), button -> {
            returnSpeedSlider.setValue(25, 1, 100);
            config.returnSpeed = 25;
        }).bounds(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(returnSpeedReset, y);
        y += ROW_HEIGHT;
        
        // === SMOOTHING SLIDER ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Camera smoothing amount (0-100%). Default: 30%");
        smoothingSlider = new IntSlider(widgetX, y, WIDGET_WIDTH, 20,
            Component.literal("Smoothing: " + config.smoothing + "%"),
            config.smoothing, 0, 100) {
            @Override
            protected void applyValue() {
                config.smoothing = this.getIntValue();
            }
        };
        addScrollableWidget(smoothingSlider, y);
        
        // Reset button for smoothing
        Button smoothingReset = Button.builder(Component.literal("↺"), button -> {
            smoothingSlider.setValue(30, 0, 100);
            config.smoothing = 30;
        }).bounds(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(smoothingReset, y);
        y += ROW_HEIGHT;
        
        // === TOGGLE MODE ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Toggle: press to activate/deactivate. Hold: hold key to look. Default: OFF (Hold)");
        toggleModeButton = Button.builder(
            Component.literal("Toggle Mode: " + (config.toggleMode ? "ON" : "OFF")),
            button -> {
                config.toggleMode = !config.toggleMode;
                button.setMessage(Component.literal("Toggle Mode: " + (config.toggleMode ? "ON" : "OFF")));
            }
        ).bounds(widgetX, y, WIDGET_WIDTH, 20).build();
        addScrollableWidget(toggleModeButton, y);
        
        // Reset button for toggleMode
        Button toggleModeReset = Button.builder(Component.literal("↺"), button -> {
            config.toggleMode = false;
            toggleModeButton.setMessage(Component.literal("Toggle Mode: OFF"));
        }).bounds(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(toggleModeReset, y);
        y += ROW_HEIGHT;
        
        // Calculate content height and max scroll
        contentHeight = y - HEADER_HEIGHT + 10;
        int visibleHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
        maxScrollOffset = Math.max(0, contentHeight - visibleHeight);
        
        // === FOOTER BUTTONS ===
        int footerY = this.height - FOOTER_HEIGHT + 7;
        int buttonWidth = 100;
        int buttonSpacing = 10;
        int totalButtonsWidth = buttonWidth * 3 + buttonSpacing * 2;
        int footerStartX = centerX - totalButtonsWidth / 2;
        
        // Save & Close button
        Button saveButton = Button.builder(Component.literal("Save & Close"), button -> {
            config.save();
            this.minecraft.setScreen(parent);
        }).bounds(footerStartX, footerY, buttonWidth, 20).build();
        footerButtons.add(saveButton);
        addRenderableWidget(saveButton);
        
        // Key Binds button
        Button keyBindsButton = Button.builder(Component.literal("Key Binds"), button -> {
            this.minecraft.setScreen(new KeyBindsScreen(this, this.minecraft.options));
        }).bounds(footerStartX + buttonWidth + buttonSpacing, footerY, buttonWidth, 20).build();
        footerButtons.add(keyBindsButton);
        addRenderableWidget(keyBindsButton);
        
        // Cancel button
        Button cancelButton = Button.builder(Component.literal("Cancel"), button -> {
            // Restore original values
            config.enabled = originalEnabled;
            config.maxYaw = originalMaxYaw;
            config.maxPitch = originalMaxPitch;
            config.returnSpeed = originalReturnSpeed;
            config.smoothing = originalSmoothing;
            config.toggleMode = originalToggleMode;
            this.minecraft.setScreen(parent);
        }).bounds(footerStartX + (buttonWidth + buttonSpacing) * 2, footerY, buttonWidth, 20).build();
        footerButtons.add(cancelButton);
        addRenderableWidget(cancelButton);
    }
    
    private void addScrollableWidget(AbstractWidget widget, int originalY) {
        scrollableWidgets.add(new WidgetEntry(widget, originalY));
        addRenderableWidget(widget);
    }
    
    private void addTooltip(int x, int y, int width, int height, String tooltip) {
        tooltips.add(new TooltipEntry(x, y, width, height, tooltip));
    }
    
    //? if >=26.1 {
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        
        for (WidgetEntry entry : scrollableWidgets) {
            entry.widget.setY(entry.originalY - scrollOffset);
        }
        
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        
        int scrollableTop = HEADER_HEIGHT;
        int scrollableBottom = this.height - FOOTER_HEIGHT;
        guiGraphics.enableScissor(0, scrollableTop, this.width, scrollableBottom);
        
        for (WidgetEntry entry : scrollableWidgets) {
            entry.widget.extractRenderState(guiGraphics, mouseX, mouseY, delta);
        }
        
        guiGraphics.disableScissor();
        
        if (maxScrollOffset > 0) {
            renderScrollbar(guiGraphics);
        }
        
        for (AbstractWidget button : footerButtons) {
            button.extractRenderState(guiGraphics, mouseX, mouseY, delta);
        }
        
        for (TooltipEntry entry : tooltips) {
            int adjustedY = entry.y - scrollOffset;
            if (adjustedY >= HEADER_HEIGHT && adjustedY + entry.height <= this.height - FOOTER_HEIGHT) {
                if (mouseX >= entry.x && mouseX <= entry.x + entry.width &&
                    mouseY >= adjustedY && mouseY <= adjustedY + entry.height) {
                    guiGraphics.setTooltipForNextFrame(this.font, Component.literal(entry.tooltip), mouseX, mouseY);
                }
            }
        }
    }
    
    private void renderScrollbar(GuiGraphicsExtractor guiGraphics) {
    //?} else {
    /*@Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        
        for (WidgetEntry entry : scrollableWidgets) {
            entry.widget.setY(entry.originalY - scrollOffset);
        }
        
        guiGraphics.drawCenteredString(this.font, this.title, 
            this.width / 2, 15, 0xFFFFFF);
        
        int scrollableTop = HEADER_HEIGHT;
        int scrollableBottom = this.height - FOOTER_HEIGHT;
        guiGraphics.enableScissor(0, scrollableTop, this.width, scrollableBottom);
        
        for (WidgetEntry entry : scrollableWidgets) {
            entry.widget.render(guiGraphics, mouseX, mouseY, delta);
        }
        
        guiGraphics.disableScissor();
        
        if (maxScrollOffset > 0) {
            renderScrollbar(guiGraphics);
        }
        
        for (AbstractWidget button : footerButtons) {
            button.render(guiGraphics, mouseX, mouseY, delta);
        }
        
        for (TooltipEntry entry : tooltips) {
            int adjustedY = entry.y - scrollOffset;
            if (adjustedY >= HEADER_HEIGHT && adjustedY + entry.height <= this.height - FOOTER_HEIGHT) {
                if (mouseX >= entry.x && mouseX <= entry.x + entry.width &&
                    mouseY >= adjustedY && mouseY <= adjustedY + entry.height) {
                    guiGraphics.renderTooltip(this.font, Component.literal(entry.tooltip), mouseX, mouseY);
                }
            }
        }
    }
    
    private void renderScrollbar(GuiGraphics guiGraphics) {*/
    //?}
        int scrollableHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
        int scrollbarX = this.width - SCROLLBAR_WIDTH - 4;
        int scrollbarY = HEADER_HEIGHT;
        
        // Background track
        guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollableHeight, 0x40FFFFFF);
        
        // Calculate thumb size and position
        float visibleRatio = (float) scrollableHeight / contentHeight;
        int thumbHeight = Math.max(20, (int) (scrollableHeight * visibleRatio));
        float scrollRatio = (float) scrollOffset / maxScrollOffset;
        int thumbY = scrollbarY + (int) ((scrollableHeight - thumbHeight) * scrollRatio);
        
        // Thumb
        int thumbColor = isDraggingScrollbar ? 0xFFCCCCCC : 0xFFAAAAAA;
        guiGraphics.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= (int) (verticalAmount * SCROLL_SPEED);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
        return true;
    }
    
    //? if >=26.1 {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (maxScrollOffset > 0 && button == 0) {
            int scrollbarX = this.width - SCROLLBAR_WIDTH - 4;
            int scrollableHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
            
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
                mouseY >= HEADER_HEIGHT && mouseY <= this.height - FOOTER_HEIGHT) {
                
                float visibleRatio = (float) scrollableHeight / contentHeight;
                int thumbHeight = Math.max(20, (int) (scrollableHeight * visibleRatio));
                float scrollRatio = (float) scrollOffset / maxScrollOffset;
                int thumbY = HEADER_HEIGHT + (int) ((scrollableHeight - thumbHeight) * scrollRatio);
                
                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                    isDraggingScrollbar = true;
                    scrollbarDragOffset = (int) mouseY - thumbY;
                } else {
                    float clickRatio = (float) (mouseY - HEADER_HEIGHT - thumbHeight / 2) / (scrollableHeight - thumbHeight);
                    scrollOffset = (int) (clickRatio * maxScrollOffset);
                    scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
                }
                return true;
            }
        }
        
        return super.mouseClicked(event, bl);
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && isDraggingScrollbar) {
            isDraggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(event);
    }
    
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            double mouseY = event.y();
            int scrollableHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
            float visibleRatio = (float) scrollableHeight / contentHeight;
            int thumbHeight = Math.max(20, (int) (scrollableHeight * visibleRatio));
            
            float dragRatio = (float) (mouseY - HEADER_HEIGHT - scrollbarDragOffset) / (scrollableHeight - thumbHeight);
            scrollOffset = (int) (dragRatio * maxScrollOffset);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (maxScrollOffset > 0 && button == 0) {
            int scrollbarX = this.width - SCROLLBAR_WIDTH - 4;
            int scrollableHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
            
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
                mouseY >= HEADER_HEIGHT && mouseY <= this.height - FOOTER_HEIGHT) {
                
                float visibleRatio = (float) scrollableHeight / contentHeight;
                int thumbHeight = Math.max(20, (int) (scrollableHeight * visibleRatio));
                float scrollRatio = (float) scrollOffset / maxScrollOffset;
                int thumbY = HEADER_HEIGHT + (int) ((scrollableHeight - thumbHeight) * scrollRatio);
                
                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                    isDraggingScrollbar = true;
                    scrollbarDragOffset = (int) mouseY - thumbY;
                } else {
                    float clickRatio = (float) (mouseY - HEADER_HEIGHT - thumbHeight / 2) / (scrollableHeight - thumbHeight);
                    scrollOffset = (int) (clickRatio * maxScrollOffset);
                    scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
                }
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isDraggingScrollbar) {
            isDraggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            int scrollableHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
            float visibleRatio = (float) scrollableHeight / contentHeight;
            int thumbHeight = Math.max(20, (int) (scrollableHeight * visibleRatio));
            
            float dragRatio = (float) (mouseY - HEADER_HEIGHT - scrollbarDragOffset) / (scrollableHeight - thumbHeight);
            scrollOffset = (int) (dragRatio * maxScrollOffset);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }*/
    //?}
    
    @Override
    public void onClose() {
        // Save on close (same as Save & Close)
        config.save();
        this.minecraft.setScreen(parent);
    }
    
    /**
     * Custom integer slider widget for vanilla Minecraft GUIs.
     * Handles conversion between slider's 0.0-1.0 normalized value and integer range.
     */
    private abstract static class IntSlider extends AbstractSliderButton {
        private final int min;
        private final int max;
        private final String labelPrefix;
        private final String labelSuffix;
        
        public IntSlider(int x, int y, int width, int height, Component message, int value, int min, int max) {
            super(x, y, width, height, message, normalize(value, min, max));
            this.min = min;
            this.max = max;
            
            // Extract label format from message
            String msg = message.getString();
            int colonIndex = msg.indexOf(':');
            if (colonIndex != -1) {
                this.labelPrefix = msg.substring(0, colonIndex + 1) + " ";
                // Check for suffix (° or %)
                if (msg.endsWith("°")) {
                    this.labelSuffix = "°";
                } else if (msg.endsWith("%")) {
                    this.labelSuffix = "%";
                } else {
                    this.labelSuffix = "";
                }
            } else {
                this.labelPrefix = "";
                this.labelSuffix = "";
            }
        }
        
        private static double normalize(int value, int min, int max) {
            return (double) (value - min) / (max - min);
        }
        
        public int getIntValue() {
            return (int) Math.round(this.value * (max - min) + min);
        }
        
        public void setValue(int value, int min, int max) {
            this.value = normalize(value, min, max);
            updateMessage();
            applyValue();
        }
        
        @Override
        protected void updateMessage() {
            setMessage(Component.literal(labelPrefix + getIntValue() + labelSuffix));
        }
        
        @Override
        protected abstract void applyValue();
    }
}
