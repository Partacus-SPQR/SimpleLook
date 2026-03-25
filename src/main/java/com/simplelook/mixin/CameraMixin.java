package com.simplelook.mixin;

import com.simplelook.FreeLookHandler;
import com.simplelook.SimpleLookClient;
import net.minecraft.client.Camera;
//? if <26.1 {
/*import net.minecraft.world.entity.Entity;*/
//?}
//? if >=1.21.11 && <26.1
import net.minecraft.world.level.Level;
//? if <1.21.11
/*import net.minecraft.world.level.BlockGetter;*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    
    @Shadow
    private float yRot;
    
    @Shadow
    private float xRot;
    
    @Shadow
    protected abstract void setRotation(float yRot, float xRot);
    
    //? if >=26.1 {
    @Inject(method = "alignWithEntity", at = @At("RETURN"))
    private void onCameraUpdate(float tickDelta, CallbackInfo ci) {
    //?} elif >=1.21.11 {
    /*@Inject(method = "setup", at = @At("RETURN"))
    private void onCameraUpdate(Level area, Entity focusedEntity, boolean thirdPerson, 
                                 boolean inverseView, float tickDelta, CallbackInfo ci) {*/
    //?} else {
    /*@Inject(method = "setup", at = @At("RETURN"))
    private void onCameraUpdate(BlockGetter area, Entity focusedEntity, boolean thirdPerson, 
                                 boolean inverseView, float tickDelta, CallbackInfo ci) {*/
    //?}
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
        float newYaw = this.yRot + yawOffset;
        float newPitch = this.xRot + pitchOffset;
        
        // Clamp pitch to prevent flipping
        newPitch = Math.max(-90.0f, Math.min(90.0f, newPitch));
        
        // Update the camera rotation
        this.setRotation(newYaw, newPitch);
    }
}
