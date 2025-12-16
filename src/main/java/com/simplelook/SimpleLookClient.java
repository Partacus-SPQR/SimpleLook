package com.simplelook;

import com.simplelook.config.SimpleLookConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class SimpleLookClient implements ClientModInitializer {
    public static final String MOD_ID = "simplelook";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static SimpleLookClient instance;
    private SimpleLookConfig config;
    
    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("Initializing SimpleLook - Free Look Mod");
        
        // Load config
        config = SimpleLookConfig.load();
        
        // Register keybindings
        SimpleLookKeybindings.register();
        
        LOGGER.info("SimpleLook initialized successfully!");
    }
    
    public static SimpleLookClient getInstance() {
        return instance;
    }
    
    public SimpleLookConfig getConfig() {
        return config;
    }
    
    public void reloadConfig() {
        config = SimpleLookConfig.load();
    }
}
