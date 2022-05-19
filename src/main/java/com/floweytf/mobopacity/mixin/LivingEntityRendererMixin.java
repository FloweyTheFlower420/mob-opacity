package com.floweytf.mobopacity.mixin;

import com.floweytf.mobopacity.AlphaStatus;
import com.floweytf.mobopacity.ModClientMain;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer {
    @Shadow
    protected boolean isBodyVisible(LivingEntity livingEntity) {
        throw new AssertionError();
    }

    protected LivingEntityRendererMixin() {
        super(null);
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", cancellable = true)
    private void handleInvokeHead(LivingEntity livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (!this.isBodyVisible(livingEntity) && !livingEntity.isInvisibleTo(Minecraft.getInstance().player)) {
            return;
        }

        if (!ModClientMain.options.enabled) {
            return;
        }

        float a = ModClientMain.opacityFor(Registry.ENTITY_TYPE.getKey(livingEntity.getType()));
        if (a == 0f) {
            ci.cancel();
            return;
        }

        if (a == 1f)
            return;

        AlphaStatus.alpha(a);
    }

    @Inject(at = @At(value = "RETURN"), method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void handleInvokeReturn(LivingEntity livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        AlphaStatus.end();
    }

    @Inject(at = @At("HEAD"), method = "getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;", cancellable = true)
    private void handleGetRenderType(LivingEntity livingEntity, boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<RenderType> cir) {
        ResourceLocation id = Registry.ENTITY_TYPE.getKey(livingEntity.getType());
        if (ModClientMain.opacityFor(id) != 1f)
            cir.setReturnValue(RenderType.entityTranslucentCull(getTextureLocation(livingEntity)));
    }
}