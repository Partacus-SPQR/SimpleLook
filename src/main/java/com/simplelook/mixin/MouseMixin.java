package com.simplelook.mixin;

import com.simplelook.FreeLookHandler;
import com.simplelook.SimpleLookClient;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to intercept mouse movement and redirect it to free look when active.
 * This prevents the player's body from rotating while free looking.
 */
@Mixin(MouseHandler.class)
public abstract class MouseMixin {
    
    @Shadow
    @Final
    private Minecraft minecraft;
    
    @Shadow
    private double accumulatedDX;
    
    @Shadow
    private double accumulatedDY;
    
    /**
     * Inject at the head of turnPlayer to intercept mouse movement
     * and redirect it to our free look handler when active.
     */
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateMouse(CallbackInfo ci) {
        // Check if mod is enabled
        var config = SimpleLookClient.getInstance().getConfig();
        if (!config.enabled) {
            return;
        }
        
        // If free look is not active, let vanilla handle it
        if (!FreeLookHandler.isFreeLookActive()) {
            return;
        }
        
        // Free look is active - handle the mouse movement ourselves
        if (this.minecraft.player == null) {
            return;
        }
        
        // Get mouse sensitivity
        double sensitivity = this.minecraft.options.sensitivity().get() * 0.6 + 0.2;
        double adjustedSensitivity = sensitivity * sensitivity * sensitivity * 8.0;
        
        // Apply the mouse delta to our free look handler
        FreeLookHandler.applyMouseDelta(this.accumulatedDX, this.accumulatedDY, adjustedSensitivity);
        
        // Clear the delta so vanilla doesn't also process it
        this.accumulatedDX = 0;
        this.accumulatedDY = 0;
        
        // Cancel the vanilla mouse update - we've handled it
        ci.cancel();
    }
}
