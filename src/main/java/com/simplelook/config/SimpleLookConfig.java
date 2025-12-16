package com.simplelook.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.simplelook.SimpleLookClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class SimpleLookConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir().resolve("simplelook.json");
    
    // Config fields with defaults
    
    /** Whether free look is enabled */
    public boolean enabled = true;
    
    /** Maximum horizontal look angle (degrees) */
    public float maxYaw = 135.0f;
    
    /** Maximum vertical look angle (degrees) */
    public float maxPitch = 90.0f;
    
    /** How fast the camera returns to center (1-100) */
    public int returnSpeed = 25;
    
    /** Camera smoothing factor (0-100) */
    public int smoothing = 20;
    
    /** Use toggle mode instead of hold mode */
    public boolean toggleMode = false;
    
    // Transient fields (not saved)
    private transient boolean dirty = false;
    
    public static SimpleLookConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                SimpleLookConfig config = GSON.fromJson(reader, SimpleLookConfig.class);
                if (config != null) {
                    config.validate();
                    return config;
                }
            } catch (Exception e) {
                SimpleLookClient.LOGGER.error("Failed to load config", e);
            }
        }
        
        SimpleLookConfig config = new SimpleLookConfig();
        config.save();
        return config;
    }
    
    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
            dirty = false;
        } catch (Exception e) {
            SimpleLookClient.LOGGER.error("Failed to save config", e);
        }
    }
    
    public void validate() {
        // Clamp values to valid ranges
        maxYaw = Math.max(10.0f, Math.min(maxYaw, 180.0f));
        maxPitch = Math.max(10.0f, Math.min(maxPitch, 90.0f));
        returnSpeed = Math.max(1, Math.min(returnSpeed, 100));
        smoothing = Math.max(0, Math.min(smoothing, 100));
    }
    
    public void markDirty() {
        dirty = true;
    }
    
    public boolean isDirty() {
        return dirty;
    }
}
