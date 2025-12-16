package com.simplelook;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

/**
 * Core handler for the free look functionality.
 * Manages the camera offset state and interpolation.
 */
@Environment(EnvType.CLIENT)
public class FreeLookHandler {
    
    private static boolean freeLookActive = false;
    private static boolean toggleState = false;  // For toggle mode
    
    // Camera offset from player's actual look direction
    private static float yawOffset = 0.0f;
    private static float pitchOffset = 0.0f;
    
    // Smoothed offsets for display
    private static float smoothYawOffset = 0.0f;
    private static float smoothPitchOffset = 0.0f;
    
    // Stored player rotation when free look started
    private static float storedYaw = 0.0f;
    private static float storedPitch = 0.0f;
    
    // Previous frame values for interpolation
    private static float prevYawOffset = 0.0f;
    private static float prevPitchOffset = 0.0f;
    
    /**
     * Called when the free look key is pressed (for toggle mode)
     */
    public static void onKeyPressed() {
        var config = SimpleLookClient.getInstance().getConfig();
        
        if (config.toggleMode) {
            // Toggle mode: flip the state
            toggleState = !toggleState;
            if (toggleState) {
                startFreeLook();
            } else {
                stopFreeLook();
            }
        }
    }
    
    /**
     * Called when the free look key state changes (for hold mode)
     */
    public static void updateKeyState(boolean keyDown) {
        var config = SimpleLookClient.getInstance().getConfig();
        
        if (config.toggleMode) {
            // Toggle mode is handled by onKeyPressed
            return;
        }
        
        // Hold mode
        if (keyDown && !freeLookActive) {
            startFreeLook();
        } else if (!keyDown && freeLookActive) {
            stopFreeLook();
        }
    }
    
    private static void startFreeLook() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        freeLookActive = true;
        
        // Store the current player rotation
        storedYaw = client.player.getYaw();
        storedPitch = client.player.getPitch();
        
        // Reset offsets
        yawOffset = 0.0f;
        pitchOffset = 0.0f;
        smoothYawOffset = 0.0f;
        smoothPitchOffset = 0.0f;
        prevYawOffset = 0.0f;
        prevPitchOffset = 0.0f;
    }
    
    private static void stopFreeLook() {
        freeLookActive = false;
        // Offsets will smoothly return to 0 via update()
    }
    
    /**
     * Check if free look is currently active
     */
    public static boolean isFreeLookActive() {
        return freeLookActive;
    }
    
    /**
     * Called each frame to update the smooth interpolation
     */
    public static void update(float tickDelta) {
        var config = SimpleLookClient.getInstance().getConfig();
        
        prevYawOffset = smoothYawOffset;
        prevPitchOffset = smoothPitchOffset;
        
        if (freeLookActive) {
            // Smooth the offset towards the target
            float smoothFactor = 1.0f - (config.smoothing / 100.0f) * 0.9f;
            smoothYawOffset = MathHelper.lerp(smoothFactor, smoothYawOffset, yawOffset);
            smoothPitchOffset = MathHelper.lerp(smoothFactor, smoothPitchOffset, pitchOffset);
        } else {
            // Return to center smoothly
            float returnFactor = config.returnSpeed / 100.0f;
            returnFactor = Math.max(0.05f, returnFactor);  // Minimum return speed
            
            smoothYawOffset = MathHelper.lerp(returnFactor, smoothYawOffset, 0.0f);
            smoothPitchOffset = MathHelper.lerp(returnFactor, smoothPitchOffset, 0.0f);
            
            yawOffset = smoothYawOffset;
            pitchOffset = smoothPitchOffset;
            
            // Snap to zero if very close
            if (Math.abs(smoothYawOffset) < 0.01f && Math.abs(smoothPitchOffset) < 0.01f) {
                smoothYawOffset = 0.0f;
                smoothPitchOffset = 0.0f;
                yawOffset = 0.0f;
                pitchOffset = 0.0f;
            }
        }
    }
    
    /**
     * Apply mouse movement to the free look offset
     */
    public static void applyMouseDelta(double deltaX, double deltaY, double sensitivity) {
        if (!freeLookActive) return;
        
        var config = SimpleLookClient.getInstance().getConfig();
        
        // Apply sensitivity (same as vanilla)
        float yawDelta = (float) (deltaX * sensitivity * 0.15);
        float pitchDelta = (float) (deltaY * sensitivity * 0.15);
        
        // Add to offsets
        yawOffset += yawDelta;
        pitchOffset += pitchDelta;
        
        // Clamp to configured limits
        yawOffset = MathHelper.clamp(yawOffset, -config.maxYaw, config.maxYaw);
        pitchOffset = MathHelper.clamp(pitchOffset, -config.maxPitch, config.maxPitch);
    }
    
    /**
     * Get the interpolated yaw offset for rendering
     */
    public static float getYawOffset(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevYawOffset, smoothYawOffset);
    }
    
    /**
     * Get the interpolated pitch offset for rendering
     */
    public static float getPitchOffset(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevPitchOffset, smoothPitchOffset);
    }
    
    /**
     * Check if we have any camera offset (for determining if we need to modify rendering)
     */
    public static boolean hasOffset() {
        return Math.abs(smoothYawOffset) > 0.001f || Math.abs(smoothPitchOffset) > 0.001f;
    }
    
    /**
     * Get stored yaw from when free look started
     */
    public static float getStoredYaw() {
        return storedYaw;
    }
    
    /**
     * Get stored pitch from when free look started
     */
    public static float getStoredPitch() {
        return storedPitch;
    }
    
    /**
     * Force reset the free look state (e.g., when player dies or changes dimension)
     */
    public static void reset() {
        freeLookActive = false;
        toggleState = false;
        yawOffset = 0.0f;
        pitchOffset = 0.0f;
        smoothYawOffset = 0.0f;
        smoothPitchOffset = 0.0f;
        prevYawOffset = 0.0f;
        prevPitchOffset = 0.0f;
    }
}
