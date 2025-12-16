package com.simplelook.mixin;

import com.simplelook.FreeLookHandler;
import com.simplelook.SimpleLookClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
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
@Mixin(Mouse.class)
public abstract class MouseMixin {
    
    @Shadow
    @Final
    private MinecraftClient client;
    
    @Shadow
    private double cursorDeltaX;
    
    @Shadow
    private double cursorDeltaY;
    
    /**
     * Inject at the head of updateMouse to intercept mouse movement
     * and redirect it to our free look handler when active.
     */
    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
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
        if (this.client.player == null) {
            return;
        }
        
        // Get mouse sensitivity
        double sensitivity = this.client.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
        double adjustedSensitivity = sensitivity * sensitivity * sensitivity * 8.0;
        
        // Apply the mouse delta to our free look handler
        FreeLookHandler.applyMouseDelta(this.cursorDeltaX, this.cursorDeltaY, adjustedSensitivity);
        
        // Clear the delta so vanilla doesn't also process it
        this.cursorDeltaX = 0;
        this.cursorDeltaY = 0;
        
        // Cancel the vanilla mouse update - we've handled it
        ci.cancel();
    }
}
