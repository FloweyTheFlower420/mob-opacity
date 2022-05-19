package com.floweytf.mobopacity.mixin;

import com.floweytf.mobopacity.AlphaStatus;
import com.floweytf.mobopacity.ModClientMain;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArrowLayer.class)
public class ArrowLayerMixin {
    @Redirect(at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
    ), method = "renderStuckItem")
    private void handleRenderStuckItem(EntityRenderDispatcher instance, Entity entity, double d, double e, double f,
                                       float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource,
                                       int i) {
        if(entity instanceof LivingEntity) {
            float opacity = ModClientMain.opacityFor(Registry.ENTITY_TYPE.getKey(entity.getType()));
            if(opacity == 0)
                return;
            if(opacity == 1f)
                instance.render(entity, d, e, f, g, h, poseStack, multiBufferSource, i);
            else {
                AlphaStatus.alpha(opacity);
                instance.render(entity, d, e, f, g, h, poseStack, multiBufferSource, i);
                AlphaStatus.end();
            }
        } else
            instance.render(entity, d, e, f, g, h, poseStack, multiBufferSource, i);
    }
}
