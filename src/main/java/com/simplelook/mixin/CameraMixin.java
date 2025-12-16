package com.simplelook.mixin;

import com.simplelook.FreeLookHandler;
import com.simplelook.SimpleLookClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to modify the camera rotation for free look functionality.
 * This allows the camera to rotate independently of the player's body.
 */
@Mixin(Camera.class)
public abstract class CameraMixin {
    
    @Shadow
    private float yaw;
    
    @Shadow
    private float pitch;
    
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
    
    /**
     * Inject at the end of the update method to apply free look offset
     */
    @Inject(method = "update", at = @At("RETURN"))
    private void onCameraUpdate(World area, Entity focusedEntity, boolean thirdPerson, 
                                 boolean inverseView, float tickDelta, CallbackInfo ci) {
        // Check if mod is enabled
        var config = SimpleLookClient.getInstance().getConfig();
        if (!config.enabled) {
            return;
        }
        
        // Check if we have any offset to apply
        if (!FreeLookHandler.hasOffset() && !FreeLookHandler.isFreeLookActive()) {
            return;
        }
        
        // Get the interpolated offsets
        float yawOffset = FreeLookHandler.getYawOffset(tickDelta);
        float pitchOffset = FreeLookHandler.getPitchOffset(tickDelta);
        
        // Apply the offset to the camera rotation
        float newYaw = this.yaw + yawOffset;
        float newPitch = this.pitch + pitchOffset;
        
        // Clamp pitch to prevent flipping
        newPitch = Math.max(-90.0f, Math.min(90.0f, newPitch));
        
        // Update the camera rotation
        this.setRotation(newYaw, newPitch);
    }
}
