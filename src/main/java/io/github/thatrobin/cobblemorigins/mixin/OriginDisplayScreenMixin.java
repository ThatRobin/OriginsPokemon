package io.github.thatrobin.cobblemorigins.mixin;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.origins.badge.Badge;
import io.github.apace100.origins.badge.BadgeManager;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.screen.OriginDisplayScreen;
import io.github.apace100.origins.screen.ViewOriginScreen;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(OriginDisplayScreen.class)
public abstract class OriginDisplayScreenMixin {

    @Shadow @Final protected static int windowWidth;

    @Shadow public abstract Origin getCurrentOrigin();

    @Shadow protected int guiLeft;

    @Shadow protected int guiTop;

    @Shadow @Final protected static int windowHeight;

    @Shadow protected int scrollPos;

    @Shadow private boolean isOriginRandom;

    @Shadow private Text randomOriginText;

    @Shadow private int currentMaxScroll;

    @Inject(method = "renderOriginContent", at = @At("HEAD"), cancellable = true)
    private void renderContent(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        OriginDisplayScreen screen = (OriginDisplayScreen) (Object) this;
        if(screen instanceof ViewOriginScreen) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int textWidth = windowWidth - 48;
            // Without this code, the text may not cover the whole width of the window
            // if the scrollbar isn't shown. However with this code, you'll see 1 frame
            // of misaligned text because the text length (and whether scrolling is enabled)
            // is only evaluated on first render. :(
        /*if(!canScroll()) {
            textWidth += 12;
        }*/

            Origin origin = getCurrentOrigin();
            int x = guiLeft + 18;
            int y = guiTop + 50;
            int startY = y;
            int endY = y - 72 + windowHeight;
            y -= scrollPos;

            Text orgDesc = origin.getDescription();
            List<OrderedText> descLines = textRenderer.wrapLines(orgDesc, textWidth);
            for(OrderedText line : descLines) {
                if(y >= startY - 18 && y <= endY + 12) {
                    context.drawText(textRenderer, line, x + 2, y - 6, 0xCCCCCC, false);
                }
                y += 12;
            }

            if(isOriginRandom) {
                List<OrderedText> drawLines = textRenderer.wrapLines(randomOriginText, textWidth);
                for(OrderedText line : drawLines) {
                    y += 12;
                    if(y >= startY - 24 && y <= endY + 12) {
                        context.drawText(textRenderer, line, x + 2, y, 0xCCCCCC, false);
                    }
                }
                y += 14;
            } else {
                String path = origin.getIdentifier().getPath().replaceAll("generation\\d*/", "");
                Identifier newId = new Identifier(origin.getIdentifier().getNamespace(), path);
                Species species = PokemonSpecies.INSTANCE.getByIdentifier(newId);
                if(species != null) {
                    if (MinecraftClient.getInstance().player != null) {
                        MoveHolderComponent component = MoveHolderComponent.KEY.get(MinecraftClient.getInstance().player);
                        for (PowerType<?> p : component.getPowerTypes(false)) {
                            if (p.isHidden()) {
                                continue;
                            }
                            OrderedText name = Language.getInstance().reorder(textRenderer.trimToWidth(p.getName().formatted(Formatting.UNDERLINE), textWidth));
                            Text desc = p.getDescription();
                            List<OrderedText> drawLines = textRenderer.wrapLines(desc, textWidth);
                            if (y >= startY - 24 && y <= endY + 12) {
                                context.drawText(textRenderer, name, x, y, 0xFFFFFF, false);
                                int tw = textRenderer.getWidth(name);
                                List<Badge> badges = BadgeManager.getPowerBadges(p.getIdentifier());
                                int xStart = x + tw + 4;
                                int bi = 0;
                                for (Badge badge : badges) {
                                    //OriginDisplayScreen.RenderedBadge renderedBadge = new OriginDisplayScreen.RenderedBadge(p, badge,xStart + 10 * bi, y - 1);
                                    //renderedBadges.add(renderedBadge);
                                    context.drawTexture(badge.spriteId(), xStart + 10 * bi, y - 1, 0, 0, 9, 9, 9, 9);
                                    bi++;
                                }
                            }
                            for (OrderedText line : drawLines) {
                                y += 12;
                                if (y >= startY - 24 && y <= endY + 12) {
                                    context.drawText(textRenderer, line, x + 2, y, 0xCCCCCC, false);
                                }
                            }

                            y += 14;

                        }
                    }
                }
            }
            y += scrollPos;
            currentMaxScroll = y - 14 - (guiTop + 158);
            if(currentMaxScroll < 0) {
                currentMaxScroll = 0;
            }
            ci.cancel();
        }
    }
}
