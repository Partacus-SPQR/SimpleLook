package com.simplelook;

import com.simplelook.config.FallbackConfigScreen;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//?} else {
/*import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;*/
//?}
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;*/
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.simplelook.compat.ScreenCompat;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class SimpleLookKeybindings {
    
    // Custom keybind category for SimpleLook
    //? if >=1.21.11 {
    private static final KeyMapping.Category SIMPLELOOK_CATEGORY = 
        new KeyMapping.Category(Identifier.fromNamespaceAndPath(SimpleLookClient.MOD_ID, "category"));
    //?} else {
    /*private static final KeyMapping.Category SIMPLELOOK_CATEGORY = 
        new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(SimpleLookClient.MOD_ID, "category"));*/
    //?}
    
    private static KeyMapping freeLookKey;
    private static KeyMapping configKey;
    
    public static void register() {
        // Free Look key (default: UNBOUND - user must bind it)
        //? if >=26.1 {
        freeLookKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        //?} else {
        /*freeLookKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(*/
        //?}
            "key.simplelook.freelook",
            GLFW.GLFW_KEY_UNKNOWN,  // UNBOUND by default
            SIMPLELOOK_CATEGORY
        ));
        
        // Config key (default: UNBOUND - user must bind it)
        //? if >=26.1 {
        configKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
        //?} else {
        /*configKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(*/
        //?}
            "key.simplelook.config",
            GLFW.GLFW_KEY_UNKNOWN,  // UNBOUND by default
            SIMPLELOOK_CATEGORY
        ));
        
        // Register tick handler for key state
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Handle config keybind - only when no screen is open
            while (configKey.consumeClick()) {
                if (ScreenCompat.current(client) == null) {
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
            if (freeLookKey.consumeClick()) {
                FreeLookHandler.onKeyPressed();
            }
            
            // Check for key held state (for hold mode)
            boolean isPressed = freeLookKey.isDown();
            if (!config.toggleMode) {
                FreeLookHandler.updateKeyState(isPressed);
            }
            
            // Update the handler each tick
            FreeLookHandler.update(1.0f);
        });
    }
    
    /**
     * Opens the config screen, using Cloth Config if available, otherwise fallback
     */
    private static void openConfigScreen(Minecraft client) {
        //? if <26.1 {
        /*// Check if Cloth Config is compatible (only check once)
        if (clothConfigCompatible == null) {
            clothConfigCompatible = checkClothConfigCompatibility();
        }
        
        if (clothConfigCompatible) {
            try {
                ScreenCompat.open(client, com.simplelook.config.ClothConfigScreen.create(ScreenCompat.current(client)));
                return;
            } catch (Throwable e) {
                // Cloth Config failed at runtime - mark as incompatible for future
                clothConfigCompatible = false;
                SimpleLookClient.LOGGER.warn("Cloth Config failed, using fallback: {}", e.getMessage());
            }
        }*/
        //?}
        
        // Use fallback screen
        ScreenCompat.open(client, new FallbackConfigScreen(ScreenCompat.current(client)));
    }
    
    //? if <26.1 {
    /*private static Boolean clothConfigCompatible = null;
    
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
    }*/
    //?}
    
    /**
     * Check if the free look key is currently being held down
     */
    public static boolean isFreeLookKeyPressed() {
        return freeLookKey != null && freeLookKey.isDown();
    }
    
    /**
     * Get the free look keybinding for display purposes
     */
    public static KeyMapping getFreeLookKey() {
        return freeLookKey;
    }
    
    /**
     * Get the config keybinding for display purposes
     */
    public static KeyMapping getConfigKey() {
        return configKey;
    }
}
