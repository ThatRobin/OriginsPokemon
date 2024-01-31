package io.github.thatrobin.cobblemorigins.mixin;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModels models;

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void getModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel model = null;
        if (stack.hasNbt() && stack.getNbt() != null && stack.getNbt().contains("CustomItem")) {
            Identifier id = Identifier.tryParse(stack.getNbt().getString("CustomItem"));
            if(id != null) {
                model = this.models.getModelManager().getModel(new ModelIdentifier(id, "inventory"));
            }
        }
        if(model != null) {
            cir.setReturnValue(model);
        }
    }



}
