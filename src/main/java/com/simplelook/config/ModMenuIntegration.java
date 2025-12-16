package com.simplelook.config;

import com.simplelook.SimpleLookClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    
    // Cache the result of Cloth Config compatibility check
    private static Boolean clothConfigCompatible = null;
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }
    
    /**
     * Creates the configuration screen, trying Cloth Config first with fallback.
     */
    private Screen createConfigScreen(Screen parent) {
        // Check if Cloth Config is compatible (only check once)
        if (clothConfigCompatible == null) {
            clothConfigCompatible = checkClothConfigCompatibility();
        }
        
        if (clothConfigCompatible) {
            try {
                SimpleLookClient.LOGGER.debug("Using Cloth Config for config screen");
                return ClothConfigScreen.create(parent);
            } catch (Throwable e) {
                // Cloth Config failed at runtime - mark as incompatible for future
                clothConfigCompatible = false;
                SimpleLookClient.LOGGER.warn("Cloth Config failed at runtime, switching to fallback: {}", e.getMessage());
            }
        }
        
        // Use fallback config screen
        SimpleLookClient.LOGGER.info("Using fallback config screen (Cloth Config unavailable or incompatible)");
        return new FallbackConfigScreen(parent);
    }
    
    /**
     * Checks if Cloth Config is compatible with the current Minecraft version.
     */
    private boolean checkClothConfigCompatibility() {
        try {
            // Try to load a Cloth Config class that would fail if incompatible
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
            SimpleLookClient.LOGGER.debug("Cloth Config found, assuming compatible");
            return true;
        } catch (ClassNotFoundException e) {
            SimpleLookClient.LOGGER.debug("Cloth Config not found: {}", e.getMessage());
            return false;
        } catch (Throwable e) {
            SimpleLookClient.LOGGER.warn("Error checking Cloth Config compatibility: {}", e.getMessage());
            return false;
        }
    }
}
