package com.simplelook.config;

import com.simplelook.SimpleLookClient;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

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
    private record WidgetEntry(ClickableWidget widget, int originalY) {}
    private final List<WidgetEntry> scrollableWidgets = new ArrayList<>();
    private final List<ClickableWidget> footerButtons = new ArrayList<>();
    
    // Sliders for reset functionality
    private IntSlider maxYawSlider;
    private IntSlider maxPitchSlider;
    private IntSlider returnSpeedSlider;
    private IntSlider smoothingSlider;
    private ButtonWidget enabledButton;
    private ButtonWidget toggleModeButton;
    
    // Track original values for cancel
    private boolean originalEnabled;
    private float originalMaxYaw;
    private float originalMaxPitch;
    private int originalReturnSpeed;
    private int originalSmoothing;
    private boolean originalToggleMode;
    
    public FallbackConfigScreen(Screen parent) {
        super(Text.translatable("config.simplelook.title"));
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
        enabledButton = ButtonWidget.builder(
            Text.literal("Enabled: " + (config.enabled ? "ON" : "OFF")),
            button -> {
                config.enabled = !config.enabled;
                button.setMessage(Text.literal("Enabled: " + (config.enabled ? "ON" : "OFF")));
            }
        ).dimensions(widgetX, y, WIDGET_WIDTH, 20).build();
        addScrollableWidget(enabledButton, y);
        
        // Reset button for enabled
        ButtonWidget enabledReset = ButtonWidget.builder(Text.literal("↺"), button -> {
            config.enabled = true;
            enabledButton.setMessage(Text.literal("Enabled: ON"));
        }).dimensions(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(enabledReset, y);
        y += ROW_HEIGHT;
        
        // === MAX YAW SLIDER ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Maximum horizontal look angle (10-180°). Default: 135°");
        maxYawSlider = new IntSlider(widgetX, y, WIDGET_WIDTH, 20,
            Text.literal("Max Yaw: " + (int)config.maxYaw + "°"),
            (int)config.maxYaw, 10, 180) {
            @Override
            protected void applyValue() {
                config.maxYaw = this.getIntValue();
            }
        };
        addScrollableWidget(maxYawSlider, y);
        
        // Reset button for maxYaw
        ButtonWidget maxYawReset = ButtonWidget.builder(Text.literal("↺"), button -> {
            maxYawSlider.setValue(135, 10, 180);
            config.maxYaw = 135;
        }).dimensions(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(maxYawReset, y);
        y += ROW_HEIGHT;
        
        // === MAX PITCH SLIDER ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Maximum vertical look angle (10-90°). Default: 90°");
        maxPitchSlider = new IntSlider(widgetX, y, WIDGET_WIDTH, 20,
            Text.literal("Max Pitch: " + (int)config.maxPitch + "°"),
            (int)config.maxPitch, 10, 90) {
            @Override
            protected void applyValue() {
                config.maxPitch = this.getIntValue();
            }
        };
        addScrollableWidget(maxPitchSlider, y);
        
        // Reset button for maxPitch
        ButtonWidget maxPitchReset = ButtonWidget.builder(Text.literal("↺"), button -> {
            maxPitchSlider.setValue(90, 10, 90);
            config.maxPitch = 90;
        }).dimensions(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(maxPitchReset, y);
        y += ROW_HEIGHT;
        
        // === RETURN SPEED SLIDER ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "How fast camera returns to center (1-100%). Default: 25%");
        returnSpeedSlider = new IntSlider(widgetX, y, WIDGET_WIDTH, 20,
            Text.literal("Return Speed: " + config.returnSpeed + "%"),
            config.returnSpeed, 1, 100) {
            @Override
            protected void applyValue() {
                config.returnSpeed = this.getIntValue();
            }
        };
        addScrollableWidget(returnSpeedSlider, y);
        
        // Reset button for returnSpeed
        ButtonWidget returnSpeedReset = ButtonWidget.builder(Text.literal("↺"), button -> {
            returnSpeedSlider.setValue(25, 1, 100);
            config.returnSpeed = 25;
        }).dimensions(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(returnSpeedReset, y);
        y += ROW_HEIGHT;
        
        // === SMOOTHING SLIDER ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Camera smoothing amount (0-100%). Default: 30%");
        smoothingSlider = new IntSlider(widgetX, y, WIDGET_WIDTH, 20,
            Text.literal("Smoothing: " + config.smoothing + "%"),
            config.smoothing, 0, 100) {
            @Override
            protected void applyValue() {
                config.smoothing = this.getIntValue();
            }
        };
        addScrollableWidget(smoothingSlider, y);
        
        // Reset button for smoothing
        ButtonWidget smoothingReset = ButtonWidget.builder(Text.literal("↺"), button -> {
            smoothingSlider.setValue(30, 0, 100);
            config.smoothing = 30;
        }).dimensions(resetX, y, RESET_BTN_WIDTH, 20).build();
        addScrollableWidget(smoothingReset, y);
        y += ROW_HEIGHT;
        
        // === TOGGLE MODE ===
        addTooltip(widgetX, y, WIDGET_WIDTH, 20, "Toggle: press to activate/deactivate. Hold: hold key to look. Default: OFF (Hold)");
        toggleModeButton = ButtonWidget.builder(
            Text.literal("Toggle Mode: " + (config.toggleMode ? "ON" : "OFF")),
            button -> {
                config.toggleMode = !config.toggleMode;
                button.setMessage(Text.literal("Toggle Mode: " + (config.toggleMode ? "ON" : "OFF")));
            }
        ).dimensions(widgetX, y, WIDGET_WIDTH, 20).build();
        addScrollableWidget(toggleModeButton, y);
        
        // Reset button for toggleMode
        ButtonWidget toggleModeReset = ButtonWidget.builder(Text.literal("↺"), button -> {
            config.toggleMode = false;
            toggleModeButton.setMessage(Text.literal("Toggle Mode: OFF"));
        }).dimensions(resetX, y, RESET_BTN_WIDTH, 20).build();
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
        ButtonWidget saveButton = ButtonWidget.builder(Text.literal("Save & Close"), button -> {
            config.save();
            this.client.setScreen(parent);
        }).dimensions(footerStartX, footerY, buttonWidth, 20).build();
        footerButtons.add(saveButton);
        addDrawableChild(saveButton);
        
        // Key Binds button
        ButtonWidget keyBindsButton = ButtonWidget.builder(Text.literal("Key Binds"), button -> {
            this.client.setScreen(new KeybindsScreen(this, this.client.options));
        }).dimensions(footerStartX + buttonWidth + buttonSpacing, footerY, buttonWidth, 20).build();
        footerButtons.add(keyBindsButton);
        addDrawableChild(keyBindsButton);
        
        // Cancel button
        ButtonWidget cancelButton = ButtonWidget.builder(Text.literal("Cancel"), button -> {
            // Restore original values
            config.enabled = originalEnabled;
            config.maxYaw = originalMaxYaw;
            config.maxPitch = originalMaxPitch;
            config.returnSpeed = originalReturnSpeed;
            config.smoothing = originalSmoothing;
            config.toggleMode = originalToggleMode;
            this.client.setScreen(parent);
        }).dimensions(footerStartX + (buttonWidth + buttonSpacing) * 2, footerY, buttonWidth, 20).build();
        footerButtons.add(cancelButton);
        addDrawableChild(cancelButton);
    }
    
    private void addScrollableWidget(ClickableWidget widget, int originalY) {
        scrollableWidgets.add(new WidgetEntry(widget, originalY));
        addDrawableChild(widget);
    }
    
    private void addTooltip(int x, int y, int width, int height, String tooltip) {
        tooltips.add(new TooltipEntry(x, y, width, height, tooltip));
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render dark background - use fillGradient for compatibility
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);
        
        // Update widget positions based on scroll
        for (WidgetEntry entry : scrollableWidgets) {
            entry.widget.setY(entry.originalY - scrollOffset);
        }
        
        // Render title (fixed header)
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, 
            this.width / 2, 15, 0xFFFFFF);
        
        // Enable scissoring for scrollable content area
        int scrollableTop = HEADER_HEIGHT;
        int scrollableBottom = this.height - FOOTER_HEIGHT;
        context.enableScissor(0, scrollableTop, this.width, scrollableBottom);
        
        // Render scrollable widgets
        for (WidgetEntry entry : scrollableWidgets) {
            entry.widget.render(context, mouseX, mouseY, delta);
        }
        
        context.disableScissor();
        
        // Render scrollbar if needed
        if (maxScrollOffset > 0) {
            renderScrollbar(context);
        }
        
        // Render footer buttons (outside scissor region)
        for (ClickableWidget button : footerButtons) {
            button.render(context, mouseX, mouseY, delta);
        }
        
        // Render tooltips (after everything else, outside scissor)
        for (TooltipEntry entry : tooltips) {
            int adjustedY = entry.y - scrollOffset;
            if (adjustedY >= HEADER_HEIGHT && adjustedY + entry.height <= this.height - FOOTER_HEIGHT) {
                if (mouseX >= entry.x && mouseX <= entry.x + entry.width &&
                    mouseY >= adjustedY && mouseY <= adjustedY + entry.height) {
                    context.drawTooltip(this.textRenderer, Text.literal(entry.tooltip), mouseX, mouseY);
                }
            }
        }
    }
    
    private void renderScrollbar(DrawContext context) {
        int scrollableHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
        int scrollbarX = this.width - SCROLLBAR_WIDTH - 4;
        int scrollbarY = HEADER_HEIGHT;
        
        // Background track
        context.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + scrollableHeight, 0x40FFFFFF);
        
        // Calculate thumb size and position
        float visibleRatio = (float) scrollableHeight / contentHeight;
        int thumbHeight = Math.max(20, (int) (scrollableHeight * visibleRatio));
        float scrollRatio = (float) scrollOffset / maxScrollOffset;
        int thumbY = scrollbarY + (int) ((scrollableHeight - thumbHeight) * scrollRatio);
        
        // Thumb
        int thumbColor = isDraggingScrollbar ? 0xFFCCCCCC : 0xFFAAAAAA;
        context.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= (int) (verticalAmount * SCROLL_SPEED);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
        return true;
    }
    
    @Override
    public boolean mouseClicked(Click click, boolean fromElement) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        
        // Check scrollbar click
        if (maxScrollOffset > 0 && button == 0) {
            int scrollbarX = this.width - SCROLLBAR_WIDTH - 4;
            int scrollableHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
            
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + SCROLLBAR_WIDTH &&
                mouseY >= HEADER_HEIGHT && mouseY <= this.height - FOOTER_HEIGHT) {
                
                // Calculate thumb position
                float visibleRatio = (float) scrollableHeight / contentHeight;
                int thumbHeight = Math.max(20, (int) (scrollableHeight * visibleRatio));
                float scrollRatio = (float) scrollOffset / maxScrollOffset;
                int thumbY = HEADER_HEIGHT + (int) ((scrollableHeight - thumbHeight) * scrollRatio);
                
                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                    // Clicked on thumb - start dragging
                    isDraggingScrollbar = true;
                    scrollbarDragOffset = (int) mouseY - thumbY;
                } else {
                    // Clicked on track - jump to position
                    float clickRatio = (float) (mouseY - HEADER_HEIGHT - thumbHeight / 2) / (scrollableHeight - thumbHeight);
                    scrollOffset = (int) (clickRatio * maxScrollOffset);
                    scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
                }
                return true;
            }
        }
        
        return super.mouseClicked(click, fromElement);
    }
    
    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0 && isDraggingScrollbar) {
            isDraggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(click);
    }
    
    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (isDraggingScrollbar) {
            double mouseY = click.y();
            int scrollableHeight = this.height - HEADER_HEIGHT - FOOTER_HEIGHT;
            float visibleRatio = (float) scrollableHeight / contentHeight;
            int thumbHeight = Math.max(20, (int) (scrollableHeight * visibleRatio));
            
            float dragRatio = (float) (mouseY - HEADER_HEIGHT - scrollbarDragOffset) / (scrollableHeight - thumbHeight);
            scrollOffset = (int) (dragRatio * maxScrollOffset);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }
    
    @Override
    public void close() {
        // Save on close (same as Save & Close)
        config.save();
        this.client.setScreen(parent);
    }
    
    /**
     * Custom integer slider widget for vanilla Minecraft GUIs.
     * Handles conversion between slider's 0.0-1.0 normalized value and integer range.
     */
    private abstract static class IntSlider extends SliderWidget {
        private final int min;
        private final int max;
        private final String labelPrefix;
        private final String labelSuffix;
        
        public IntSlider(int x, int y, int width, int height, Text message, int value, int min, int max) {
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
            setMessage(Text.literal(labelPrefix + getIntValue() + labelSuffix));
        }
        
        @Override
        protected abstract void applyValue();
    }
}
