package com.simplelook.config;

import com.simplelook.SimpleLookClient;
import com.simplelook.SimpleLookKeybindings;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class ClothConfigScreen {
    
    public static Screen create(Screen parent) {
        SimpleLookConfig config = SimpleLookClient.getInstance().getConfig();
        
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.translatable("config.simplelook.title"))
            .setTransparentBackground(true)
            .setSavingRunnable(config::save);
        
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        
        // ==================== General Category ====================
        ConfigCategory general = builder.getOrCreateCategory(
            Text.translatable("config.simplelook.general")
        );
        
        // Bug notice for sliders
        general.addEntry(entryBuilder.startTextDescription(
            Text.literal("Note: ").formatted(Formatting.GOLD)
                .append(Text.literal("Due to a Cloth Config bug in 21.11.151, sliders may not drag properly. Use the text fields to type values directly, or disable Cloth Config to use the fallback config.").formatted(Formatting.GRAY)))
            .build());
        
        // Enable/Disable toggle
        general.addEntry(entryBuilder.startBooleanToggle(
            Text.translatable("config.simplelook.enabled"),
            config.enabled)
            .setDefaultValue(true)
            .setTooltip(Text.translatable("config.simplelook.enabled.tooltip"))
            .setSaveConsumer(newValue -> config.enabled = newValue)
            .build());
        
        // Max Yaw - Using IntField as workaround for slider bug
        general.addEntry(entryBuilder.startIntField(
            Text.translatable("config.simplelook.maxYaw"),
            (int) config.maxYaw)
            .setDefaultValue(135)
            .setMin(10)
            .setMax(180)
            .setTooltip(
                Text.translatable("config.simplelook.maxYaw.tooltip"),
                Text.literal("Range: 10° - 180°").formatted(Formatting.GRAY))
            .setSaveConsumer(newValue -> config.maxYaw = newValue.floatValue())
            .build());
        
        // Max Pitch - Using IntField as workaround for slider bug
        general.addEntry(entryBuilder.startIntField(
            Text.translatable("config.simplelook.maxPitch"),
            (int) config.maxPitch)
            .setDefaultValue(90)
            .setMin(10)
            .setMax(90)
            .setTooltip(
                Text.translatable("config.simplelook.maxPitch.tooltip"),
                Text.literal("Range: 10° - 90°").formatted(Formatting.GRAY))
            .setSaveConsumer(newValue -> config.maxPitch = newValue.floatValue())
            .build());
        
        // Return Speed - Using IntField as workaround for slider bug
        general.addEntry(entryBuilder.startIntField(
            Text.translatable("config.simplelook.returnSpeed"),
            config.returnSpeed)
            .setDefaultValue(25)
            .setMin(1)
            .setMax(100)
            .setTooltip(
                Text.translatable("config.simplelook.returnSpeed.tooltip"),
                Text.literal("Range: 1% - 100%").formatted(Formatting.GRAY))
            .setSaveConsumer(newValue -> config.returnSpeed = newValue)
            .build());
        
        // Smoothing - Using IntField as workaround for slider bug
        general.addEntry(entryBuilder.startIntField(
            Text.translatable("config.simplelook.smoothing"),
            config.smoothing)
            .setDefaultValue(30)
            .setMin(0)
            .setMax(100)
            .setTooltip(
                Text.translatable("config.simplelook.smoothing.tooltip"),
                Text.literal("Range: 0% - 100%").formatted(Formatting.GRAY))
            .setSaveConsumer(newValue -> config.smoothing = newValue)
            .build());
        
        // Toggle Mode
        general.addEntry(entryBuilder.startBooleanToggle(
            Text.translatable("config.simplelook.toggleMode"),
            config.toggleMode)
            .setDefaultValue(false)
            .setTooltip(Text.translatable("config.simplelook.toggleMode.tooltip"))
            .setSaveConsumer(newValue -> config.toggleMode = newValue)
            .build());
        
        // ==================== Keybinds Category ====================
        ConfigCategory keybinds = builder.getOrCreateCategory(
            Text.translatable("config.simplelook.keybinds")
        );
        
        keybinds.addEntry(entryBuilder.startTextDescription(
            Text.literal("Note: ").formatted(Formatting.GOLD)
                .append(Text.literal("Keybinds set here are also accessible in Options > Controls > Key Binds under the 'SimpleLook' category.").formatted(Formatting.WHITE)))
            .build());
        
        // Free Look keybind
        keybinds.addEntry(entryBuilder.fillKeybindingField(
            Text.translatable("key.simplelook.freelook"),
            SimpleLookKeybindings.getFreeLookKey())
            .build());
        
        // Config keybind
        keybinds.addEntry(entryBuilder.fillKeybindingField(
            Text.translatable("key.simplelook.config"),
            SimpleLookKeybindings.getConfigKey())
            .build());
        
        return builder.build();
    }
}
