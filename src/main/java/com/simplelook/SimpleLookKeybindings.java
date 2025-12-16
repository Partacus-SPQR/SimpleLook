package com.simplelook;

import com.simplelook.config.FallbackConfigScreen;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SimpleLookKeybindings {
    
    // Custom keybind category for SimpleLook
    private static final KeyBinding.Category SIMPLELOOK_CATEGORY = 
        new KeyBinding.Category(Identifier.of(SimpleLookClient.MOD_ID, "category"));
    
    private static KeyBinding freeLookKey;
    private static KeyBinding configKey;
    
    public static void register() {
        // Free Look key (default: UNBOUND - user must bind it)
        freeLookKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.simplelook.freelook",
            GLFW.GLFW_KEY_UNKNOWN,  // UNBOUND by default
            SIMPLELOOK_CATEGORY
        ));
        
        // Config key (default: UNBOUND - user must bind it)
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.simplelook.config",
            GLFW.GLFW_KEY_UNKNOWN,  // UNBOUND by default
            SIMPLELOOK_CATEGORY
        ));
        
        // Register tick handler for key state
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Handle config keybind - only when no screen is open
            while (configKey.wasPressed()) {
                if (client.currentScreen == null) {
                    openConfigScreen(client);
                }
            }
            
            if (client.player == null) return;
            
            var config = SimpleLookClient.getInstance().getConfig();
            if (!config.enabled) {
                // If disabled, make sure free look is off
                if (FreeLookHandler.isFreeLookActive()) {
                    FreeLookHandler.reset();
                }
                return;
            }
            
            // Check for key press (for toggle mode)
            if (freeLookKey.wasPressed()) {
                FreeLookHandler.onKeyPressed();
            }
            
            // Check for key held state (for hold mode)
            boolean isPressed = freeLookKey.isPressed();
            if (!config.toggleMode) {
                FreeLookHandler.updateKeyState(isPressed);
            }
            
            // Update the handler each tick
            FreeLookHandler.update(1.0f);
        });
    }
    
    // Cache the result of Cloth Config compatibility check
    private static Boolean clothConfigCompatible = null;
    
    /**
     * Opens the config screen, using Cloth Config if available, otherwise fallback
     */
    private static void openConfigScreen(MinecraftClient client) {
        // Check if Cloth Config is compatible (only check once)
        if (clothConfigCompatible == null) {
            clothConfigCompatible = checkClothConfigCompatibility();
        }
        
        if (clothConfigCompatible) {
            try {
                client.setScreen(com.simplelook.config.ClothConfigScreen.create(client.currentScreen));
                return;
            } catch (Throwable e) {
                // Cloth Config failed at runtime - mark as incompatible for future
                clothConfigCompatible = false;
                SimpleLookClient.LOGGER.warn("Cloth Config failed, using fallback: {}", e.getMessage());
            }
        }
        
        // Use fallback screen
        client.setScreen(new FallbackConfigScreen(client.currentScreen));
    }
    
    /**
     * Checks if Cloth Config is compatible with the current Minecraft version.
     */
    private static boolean checkClothConfigCompatibility() {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Throwable e) {
            SimpleLookClient.LOGGER.warn("Error checking Cloth Config compatibility: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if the free look key is currently being held down
     */
    public static boolean isFreeLookKeyPressed() {
        return freeLookKey != null && freeLookKey.isPressed();
    }
    
    /**
     * Get the free look keybinding for display purposes
     */
    public static KeyBinding getFreeLookKey() {
        return freeLookKey;
    }
    
    /**
     * Get the config keybinding for display purposes
     */
    public static KeyBinding getConfigKey() {
        return configKey;
    }
}
