package io.github.thatrobin.cobblemorigins.client;

import com.cobblemon.mod.common.api.gui.GuiUtilsKt;
import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.client.CobblemonResources;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import io.github.apace100.apoli.power.PowerType;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings("cast")
public class MoveOverlay extends InGameHud {
    private static final Identifier portraitBackground = MiscUtilsKt.cobblemonResource("textures/gui/party/party_slot_portrait_background.png");
    private static final Identifier moveSlot = new Identifier("cobblemorigins", "textures/gui/move/move_slot.png");
    private static final Identifier moveSlotActive = new Identifier("cobblemorigins", "textures/gui/move/move_slot_active.png");


    public MoveOverlay(MinecraftClient client, ItemRenderer itemRenderer) {
        super(client, itemRenderer);
    }

    public void render(@NotNull DrawContext context, float partialDeltaTicks) {
        Intrinsics.checkNotNullParameter(context, "context");
        MinecraftClient minecraft = MinecraftClient.getInstance();

        if (!minecraft.options.debugEnabled) {
            int panelX = minecraft.getWindow().getScaledWidth();
            MatrixStack matrices = context.getMatrices();

            int totalHeight = 4 * 30;
            int midY = minecraft.getWindow().getScaledHeight() / 2;
            int startY = midY - totalHeight / 2 - 10;
            int portraitFrameOffsetX = -22;
            int portraitFrameOffsetY = 2;
            MoveHolderComponent component = MoveHolderComponent.KEY.get(Objects.requireNonNull(MinecraftClient.getInstance().player));
            int selectedSlot = component.getMoveSelection();

            for (int i = 0; i < Collections.max(component.getUsedSlots()); i++) {
                PowerType<?> move = component.getPowerType(i);
                int selectedOffsetX;
                int indexOffsetY;
                int indexY;
                if (move != null) {
                    MoveTemplate template = Moves.INSTANCE.getByName(move.getIdentifier().getPath());
                    String type = "normal";
                    if (template != null) {
                        type = template.getElementalType().getName().toLowerCase(Locale.ROOT);
                    }
                    Identifier typeIdTex = new Identifier("cobblemorigins", "textures/gui/move/move_type_" + type + ".png");
                    selectedOffsetX = selectedSlot == i ? -6 : 0;
                    indexOffsetY = 34 * i;

                    indexY = startY + indexOffsetY + portraitFrameOffsetY;
                    Intrinsics.checkNotNullExpressionValue(matrices, "matrices");
                    blitk$default(matrices, portraitBackground, panelX + portraitFrameOffsetX + selectedOffsetX- 21, (Number) indexY, (Number) 21, (Number) 21, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, false, 0.0F, 131008, (Object) null);
                    blitk$default(matrices, typeIdTex, (Number) Integer.valueOf(panelX - 56 + selectedOffsetX) , (Number) (indexY - 2), (Number) 30, (Number) 62, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, false, 0.0F, 131008, (Object) null);
                    context.enableScissor(panelX + portraitFrameOffsetX + selectedOffsetX, indexY, panelX + portraitFrameOffsetX + selectedOffsetX + 21, indexY + 21);
                    matrices.push();
                    matrices.translate((double) (panelX + portraitFrameOffsetX + selectedOffsetX) + 10.5 - 1.0, (double) indexY - (double) 12, 0.0);
                    blitk$default(matrices, portraitBackground, panelX + portraitFrameOffsetX + selectedOffsetX- 21, (Number) indexY, (Number) 21, (Number) 21, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, false, 0.0F, 131008, (Object) null);
                    //GuiUtilsKt.drawPortraitPokemon$default(pokemon.getSpecies(), pokemon.getAspects(), matrices, 0.0F, false, (PoseableEntityState)null, partialDeltaTicks, 56, (Object)null);
                    matrices.pop();
                    context.disableScissor();
                }

                selectedOffsetX = selectedSlot == i ? -6 : 0;
                indexOffsetY = 34 * i;
                indexY = startY + indexOffsetY;
                Identifier slotTexture = null;
                if (selectedSlot == i) {
                    slotTexture = moveSlotActive;
                } else {
                    slotTexture = moveSlot;
                }
                Intrinsics.checkNotNullExpressionValue(matrices, "matrices");
                blitk$default(matrices, slotTexture, (Number) Integer.valueOf(panelX - 62) , (Number) indexY, (Number) 30, (Number) 62, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, (Number) null, false, 0.0F, 131008, (Object) null);

                if (move != null) {
                    MutableText var10002 = LocalizationUtilsKt.lang("ui.pp", new Object[0]);
                    Intrinsics.checkNotNullExpressionValue(var10002, "lang(\"ui.pp\")");
                    var10002 = Text.literal("15/15 PP");
                    drawScaledText$default(context, (Identifier) null, var10002, (Number) (float) ((panelX + selectedOffsetX - 16) + 6.5F), (Number) ((double) indexY + 10.5), 0.5F, (Number) null, 0, 0, true, true, 450, (Object) null);
                    //RenderHelperKt.drawScaledText$default(context, (Identifier) null, TextKt.text(String.valueOf(pokemon.getLevel())), (Number) ((float) (panelX + selectedOffsetX) + 6.5F), (Number) (indexY + 18), 0.5F, (Number) null, 0, 0, true, true, 450, (Object) null);
                    int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(move.getName());
                    drawScaledText$default(context, (Identifier) null, move.getName(), (Number) ((panelX + selectedOffsetX - 5) + 3.5F - (textWidth)/2), (Number) (indexY + 25), 0.5F, (Number) null, 0, 0, false, false, 1986, (Object) null);

                    //float hpRatio = (float) pokemon.getCurrentHealth() / (float) pokemon.getHp();
                    float hpRatio = 0f;
                    int barHeightMax = 18;
                    int hpBarWidth = 2;
                    float hpBarHeight = hpRatio * (float) barHeightMax;
                    Pair var31 = getDepletableRedGreen$default(hpRatio, 0.0F, 0.0F, 6, (Object) null);
                    float red = ((Number) var31.component1()).floatValue();
                    float green = ((Number) var31.component2()).floatValue();
                    Identifier var34 = CobblemonResources.INSTANCE.getWHITE();
                    int expToNextLevel = panelX + selectedOffsetX + 46;
                    float expRatio = (float) indexY + ((float) barHeightMax - hpBarHeight) + (float) 5;
                    float var37 = hpBarHeight / hpRatio;
                    float expBarHeight = (float) barHeightMax - hpBarHeight;
                    float var39 = red * 0.8F;
                    float var40 = green * 0.8F;
                    blitk$default(matrices, var34, (Number) expToNextLevel, (Number) expRatio, (Number) hpBarHeight, (Number) Integer.valueOf(hpBarWidth), (Number) null, (Number) expBarHeight, (Number) null, (Number) var37, (Number) null, (Number) var39, (Number) var40, (Number) 0.27F, (Number) null, false, 0.0F, 116032, (Object) null);
                    //int expForThisLevel = pokemon.getExperience() - (pokemon.getLevel() == 1 ? 0 : pokemon.getExperienceGroup().getExperience(pokemon.getLevel()));
                    int expForThisLevel = 0;
                    //expToNextLevel = pokemon.getExperienceGroup().getExperience(pokemon.getLevel() + 1) - pokemon.getExperienceGroup().getExperience(pokemon.getLevel());
                    expToNextLevel = 0;
                    expRatio = (float) expForThisLevel / (float) expToNextLevel;
                    int expBarWidth = 1;
                    expBarHeight = expRatio * (float) barHeightMax;
                    Identifier ballIcon = CobblemonResources.INSTANCE.getWHITE();
                    int var62 = panelX + selectedOffsetX + 49;
                    float var41 = (float) indexY + ((float) barHeightMax - expBarHeight) + (float) 5;
                    float var42 = expBarHeight / expRatio;
                    float var43 = (float) barHeightMax - expBarHeight;
                    blitk$default(matrices, ballIcon, (Number) var62, (Number) var41, (Number) expBarHeight, (Number) Integer.valueOf(expBarWidth), (Number) null, (Number) var43, (Number) null, (Number) var42, (Number) null, (Number) 0.2, (Number) 0.65, (Number) 0.84, (Number) null, false, 0.0F, 116032, (Object) null);
                }
            }
        }
    }

    public static void blitk$default(MatrixStack var0, Identifier var1, Number var2, Number var3, Number var4, Number var5, Number var6, Number var7, Number var8, Number var9, Number var10, Number var11, Number var12, Number var13, Number var14, boolean var15, float var16, int var17, Object var18) {
        if ((var17 & 2) != 0) {
            var1 = null;
        }

        if ((var17 & 16) != 0) {
            var4 = (Number)0;
        }

        if ((var17 & 32) != 0) {
            var5 = (Number)0;
        }

        if ((var17 & 64) != 0) {
            var6 = (Number)0;
        }

        if ((var17 & 128) != 0) {
            var7 = (Number)0;
        }

        if ((var17 & 256) != 0) {
            var8 = var5;
        }

        if ((var17 & 512) != 0) {
            var9 = var4;
        }

        if ((var17 & 1024) != 0) {
            var10 = (Number)0;
        }

        if ((var17 & 2048) != 0) {
            var11 = (Number)1;
        }

        if ((var17 & 4096) != 0) {
            var12 = (Number)1;
        }

        if ((var17 & 8192) != 0) {
            var13 = (Number)1;
        }

        if ((var17 & 16384) != 0) {
            var14 = (Number)1.0F;
        }

        if ((var17 & '耀') != 0) {
            var15 = true;
        }

        if ((var17 & 65536) != 0) {
            var16 = 1.0F;
        }

        GuiUtilsKt.blitk(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
    }

    public static Pair getDepletableRedGreen$default(float var0, float var1, float var2, int var3, Object var4) {
        if ((var3 & 2) != 0) {
            var1 = 0.5F;
        }

        if ((var3 & 4) != 0) {
            var2 = 0.2F;
        }

        return RenderHelperKt.getDepletableRedGreen(var0, var1, var2);
    }

    public static void drawScaledText$default(DrawContext var0, Identifier var1, MutableText var2, Number var3, Number var4, float var5, Number var6, int var7, int var8, boolean var9, boolean var10, int var11, Object var12) {
        if ((var11 & 2) != 0) {
            var1 = null;
        }

        if ((var11 & 32) != 0) {
            var5 = 1.0F;
        }

        if ((var11 & 64) != 0) {
            var6 = (Number)1.0F;
        }

        if ((var11 & 128) != 0) {
            var7 = Integer.MAX_VALUE;
        }

        if ((var11 & 256) != 0) {
            var8 = 16777215 + ((int)(var6.floatValue() * (float)255) << 24);
        }

        if ((var11 & 512) != 0) {
            var9 = false;
        }

        if ((var11 & 1024) != 0) {
            var10 = false;
        }

        RenderHelperKt.drawScaledText(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
    }
}
