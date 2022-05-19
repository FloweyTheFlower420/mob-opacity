package com.floweytf.mobopacity.mixin;

import com.floweytf.mobopacity.AlphaStatus;
import com.mojang.blaze3d.vertex.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin {
    @ModifyVariable(at = @At("HEAD"), method = "vertex", index = 7, argsOnly = true)
    private float handleVertex(float value) {
        if(AlphaStatus.enabled())
            return AlphaStatus.alpha();
        return value;
    }
}
