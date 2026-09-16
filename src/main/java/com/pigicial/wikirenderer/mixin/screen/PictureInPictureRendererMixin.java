package com.pigicial.wikirenderer.mixin.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pigicial.wikirenderer.WikiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PictureInPictureRenderer.class)
public class PictureInPictureRendererMixin {

    @Inject(method = "prepare", at = @At("HEAD"))
    private void wikirenderer$beginPipDraw(CallbackInfo ci) {
        if (WikiRenderer.inContainerScreenDraw) {
            WikiRenderer.inGuiPipDraw = true;
        }
    }

    // Container capture maps GUI pixels into NDC via the renderable model-view. PIP entity
    // rendering must not inherit that transform or the stand/player preview is projected away.
    @Inject(method = "prepare", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4fStack;pushMatrix()Lorg/joml/Matrix4fStack;", shift = At.Shift.AFTER))
    private void wikirenderer$identityModelViewForPip(CallbackInfo ci) {
        if (WikiRenderer.inContainerScreenDraw) {
            RenderSystem.getModelViewStack().identity();
        }
    }

    @Inject(method = "prepare", at = @At("RETURN"))
    private void wikirenderer$endPipDraw(CallbackInfo ci) {
        WikiRenderer.inGuiPipDraw = false;
    }
}
