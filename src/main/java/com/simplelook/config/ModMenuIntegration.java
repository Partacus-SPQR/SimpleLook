package com.simplelook.config;

import com.simplelook.SimpleLookClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    
    //? if <26.1 {
    /*private static Boolean clothConfigCompatible = null;*/
    //?}
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }
    
    private Screen createConfigScreen(Screen parent) {
        //? if <26.1 {
        /*if (clothConfigCompatible == null) {
            clothConfigCompatible = checkClothConfigCompatibility();
        }
        
        if (clothConfigCompatible) {
            try {
                SimpleLookClient.LOGGER.debug("Using Cloth Config for config screen");
                return ClothConfigScreen.create(parent);
            } catch (Throwable e) {
                clothConfigCompatible = false;
                SimpleLookClient.LOGGER.warn("Cloth Config failed at runtime, switching to fallback: {}", e.getMessage());
            }
        }
        
        SimpleLookClient.LOGGER.info("Using fallback config screen (Cloth Config unavailable or incompatible)");*/
        //?}
        return new FallbackConfigScreen(parent);
    }
    
    //? if <26.1 {
    /*private boolean checkClothConfigCompatibility() {
        try {
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
    }*/
    //?}
}
