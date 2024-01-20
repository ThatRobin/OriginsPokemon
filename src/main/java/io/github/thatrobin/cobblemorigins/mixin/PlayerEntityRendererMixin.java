package io.github.thatrobin.cobblemorigins.mixin;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Redirect(
            method = "render*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    )
    private void redirectRender(LivingEntityRenderer<?,?> renderer, LivingEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        OriginComponent component = ModComponents.ORIGIN.get(entity);
        Identifier pokemonIdentifier = component.getOrigin(OriginLayers.getLayer(Origins.identifier("origin"))).getIdentifier();
        String path = pokemonIdentifier.getPath().replaceAll("generation\\d*/", "");
        Identifier newId = new Identifier(pokemonIdentifier.getNamespace(), path);
        Species species = PokemonSpecies.INSTANCE.getByIdentifier(newId);
        if(species != null) {
            PokemonEntity pokemonEntity = new PokemonEntity(entity.getWorld(), species.create(1), CobblemonEntities.POKEMON);
            pokemonEntity.limbAnimator.setSpeed(entity.limbAnimator.getSpeed());
            LimbAnimatorAccessor target = (LimbAnimatorAccessor) pokemonEntity.limbAnimator;
            LimbAnimatorAccessor source = (LimbAnimatorAccessor) entity.limbAnimator;
            target.setPrevSpeed(source.getPrevSpeed());
            target.setSpeed(source.getSpeed());
            target.setPos(source.getPos());
            pokemonEntity.handSwinging = entity.handSwinging;
            pokemonEntity.handSwingTicks = entity.handSwingTicks;
            pokemonEntity.lastHandSwingProgress = entity.lastHandSwingProgress;
            pokemonEntity.handSwingProgress = entity.handSwingProgress;
            pokemonEntity.bodyYaw = entity.bodyYaw;
            pokemonEntity.prevBodyYaw = entity.prevBodyYaw;
            pokemonEntity.headYaw = entity.headYaw;
            pokemonEntity.prevHeadYaw = entity.prevHeadYaw;
            pokemonEntity.age = entity.age;
            pokemonEntity.preferredHand = entity.preferredHand;
            pokemonEntity.setOnGround(entity.isOnGround());
            pokemonEntity.setVelocity(entity.getVelocity());
            //next two make the wolfEntity tamed (with the player themselves as the owner),
            //and their collar colour to light gray (which is harder to see on the wolf model)
            pokemonEntity.setOwner((PlayerEntity) entity);

            PokemonRenderer pokemonRenderer = (PokemonRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(pokemonEntity);
            pokemonRenderer.render(pokemonEntity, f, g, matrixStack, vertexConsumerProvider, i);
        } else {
            super.render((AbstractClientPlayerEntity) entity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

}
