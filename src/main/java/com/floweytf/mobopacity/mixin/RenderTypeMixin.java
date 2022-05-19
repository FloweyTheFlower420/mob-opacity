package com.floweytf.mobopacity.mixin;

import com.floweytf.mobopacity.AlphaStatus;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static net.minecraft.client.renderer.RenderStateShard.*;

@Mixin(RenderType.class)
public class RenderTypeMixin extends RenderStateShard {
    public RenderTypeMixin() {
        super(null, null, null);
    }

    @Inject(at = @At("HEAD"), method = "entitySolid", cancellable = true)
    private static void handleEntitySolid(ResourceLocation resourceLocation, CallbackInfoReturnable<RenderType> cir) {
        if(AlphaStatus.enabled())
            cir.setReturnValue(RenderType.entityTranslucent(resourceLocation));
    }

    @Inject(at = @At("HEAD"), method = "armorCutoutNoCull", cancellable = true)
    private static void handleArmorCutoutNoCull(ResourceLocation resourceLocation, CallbackInfoReturnable<RenderType> cir) {
        if(AlphaStatus.enabled()) {
            RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setDiffuseLightingState(DIFFUSE_LIGHTING)
                .setAlphaState(DEFAULT_ALPHA)
                .setCullState(CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true);
            cir.setReturnValue(RenderType.create("armor_cutout_no_cull", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, compositeState));
        }
    }
}
