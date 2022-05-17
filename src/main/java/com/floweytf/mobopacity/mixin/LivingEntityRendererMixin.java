package com.floweytf.mobopacity.mixin;

import com.floweytf.mobopacity.ModClientMain;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer {
    protected LivingEntityRendererMixin() {
        super(null);
    }

    @Redirect(at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
    ), method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void handleInvoke(EntityModel<? extends LivingEntity> instance, PoseStack poseStack,
                              VertexConsumer vertexConsumer, int i, int j, float r, float g, float b, float a,
                              LivingEntity methodArgumentEntity) {
        if(a == 0.15f) {
            instance.renderToBuffer(poseStack, vertexConsumer, i, j, r, g, b, a);
            return;
        }

        if(!ModClientMain.options.enabled) {
            instance.renderToBuffer(poseStack, vertexConsumer, i, j, r, g, b, a);
            return;
        }

        ResourceLocation id = Registry.ENTITY_TYPE.getKey(methodArgumentEntity.getType());
        instance.renderToBuffer(poseStack, vertexConsumer, i, j, r, g, b, ModClientMain.opacityFor(id));
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;", cancellable = true)
    private void handleGetRenderType(LivingEntity livingEntity, boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<RenderType> cir) {
        ResourceLocation id = Registry.ENTITY_TYPE.getKey(livingEntity.getType());
        if(ModClientMain.opacityFor(id) != 1f)
            cir.setReturnValue(RenderType.entityTranslucent(getTextureLocation(livingEntity)));
    }
}