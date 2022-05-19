package com.floweytf.mobopacity.mixin;

import com.floweytf.mobopacity.AlphaStatus;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderType.class)
public class RenderTypeMixin extends RenderStateShard {
    @Shadow
    private static RenderType.CompositeRenderType create(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, RenderType.CompositeState compositeState) {
        throw new AssertionError();
    }

    public RenderTypeMixin() {
        super(null, null, null);
    }

    @Inject(at = @At("HEAD"), method = "entitySolid", cancellable = true)
    private static void handleEntitySo1lid(ResourceLocation resourceLocation, CallbackInfoReturnable<RenderType> cir) {
        if(AlphaStatus.enabled())
            cir.setReturnValue(RenderType.entityTranslucent(resourceLocation));
    }

    @Inject(at = @At("HEAD"), method = "armorCutoutNoCull", cancellable = true)
    private static void handleArmorCutoutNoCull(ResourceLocation resourceLocation, CallbackInfoReturnable<RenderType> cir) {
        if(AlphaStatus.enabled()) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setWriteMaskState(COLOR_DEPTH_WRITE)
                .setCullState(CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true);
            cir.setReturnValue(create("armor_transparent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState));
        }
    }
}
