package io.github.thatrobin.cobblemorigins.client;

import io.github.apace100.apoli.power.PowerType;
import io.github.thatrobin.cobblemorigins.component.MoveHolderComponent;
import io.github.thatrobin.cobblemorigins.utils.MoveData;
import io.github.thatrobin.cobblemorigins.utils.MoveManager;
import io.github.thatrobin.cobblemorigins.utils.RenderHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

@SuppressWarnings("cast")
public class MoveOverlay implements HudRenderCallback {
    private static final Identifier portraitBackground = new Identifier("cobblemorigins","textures/gui/party/party_slot_portrait_background.png");
    private static final Identifier moveSlot = new Identifier("cobblemorigins", "textures/gui/move/move_slot.png");
    private static final Identifier moveSlotActive = new Identifier("cobblemorigins", "textures/gui/move/move_slot_active.png");

    public static Pair<Float, Float> getDepletableRedGreen(float ratio, float yellowRatio, float redRatio) {
        int m = -2;
        float r = ratio > redRatio ? (float)m * ratio - (float)m : 1.0f;
        float g = ratio > yellowRatio ? 1.0f : (ratio > redRatio ? ratio / yellowRatio : 0.0f);
        return new Pair<>(r, g);
    }

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraft.textRenderer;

        int panelX = minecraft.getWindow().getScaledWidth();
            MatrixStack matrices = context.getMatrices();

            int totalHeight = 4 * 30;
            int midY = minecraft.getWindow().getScaledHeight() / 2;
            int startY = midY - totalHeight / 2 - 10;
            int portraitFrameOffsetX = -22;
            int portraitFrameOffsetY = 2;
            if(MinecraftClient.getInstance().player != null) {
                MoveHolderComponent component = MoveHolderComponent.KEY.get(MinecraftClient.getInstance().player);
                int selectedSlot = component.getMoveSelection();

                for (int i = 0; i <= component.getUsedSlotCount(); i++) {
                    PowerType<?> move = component.getPowerType(i);
                    if(move != null) {
                        int selectedOffsetX;
                        int indexOffsetY;
                        int indexY;
                        MoveData moveData = MoveManager.getMoveData(move.getIdentifier());
                        if (moveData != null) {
                            Identifier typeIdTex = moveData.moveTexture();
                            selectedOffsetX = selectedSlot == i ? -6 : 0;
                            indexOffsetY = 34 * i;

                            indexY = startY + indexOffsetY + portraitFrameOffsetY;
                            context.drawTexture(portraitBackground, panelX + portraitFrameOffsetX + selectedOffsetX - 21, indexY, 0, 0, 21, 21, 21, 21);
                            context.drawTexture(typeIdTex, panelX - 56 + selectedOffsetX, indexY - 2, 0, 0, 62, 30, 62, 30);
                            context.enableScissor(panelX + portraitFrameOffsetX + selectedOffsetX, indexY, panelX + portraitFrameOffsetX + selectedOffsetX + 21, indexY + 21);
                            matrices.push();
                            matrices.translate((double) (panelX + portraitFrameOffsetX + selectedOffsetX) + 10.5 - 1.0, (double) indexY - (double) 12, 0.0);
                            context.drawTexture(portraitBackground, panelX + portraitFrameOffsetX + selectedOffsetX - 21, indexY, 0, 0, 21, 21, 21, 21);
                            matrices.pop();
                            context.disableScissor();

                            selectedOffsetX = selectedSlot == i ? -6 : 0;
                            indexOffsetY = 34 * i;
                            indexY = startY + indexOffsetY;
                            Identifier slotTexture;
                            if (selectedSlot == i) {
                                slotTexture = moveSlotActive;
                            } else {
                                slotTexture = moveSlot;
                            }
                            context.drawTexture(slotTexture, panelX - 62, indexY, 0, 0, 62, 30, 62, 30);

                            MutableText var10002 = Text.translatable("origins_pokemon.ui.pp");
                            var10002 = Text.literal("15/15 PP");
                            matrices.scale(0.5f, 0.5f, 0.5f);
                            int textWidth = textRenderer.getWidth(move.getName());
                            RenderHelper.drawScaledText(context, null, move.getName(), ((panelX + selectedOffsetX - 5) + 3.5F - (textWidth) / 2), (indexY + 25), 0.5F, 1f, 0, 0, false, false);

                            context.drawText(textRenderer, var10002.asOrderedText(), (((panelX + selectedOffsetX - 16) * 2)), (indexY + 10) * 2, 0xFFFFFF, true);
                            context.drawText(textRenderer, move.getName(), (((panelX + selectedOffsetX - 5) * 2) + 3 - textWidth), (indexY + 25) * 2, 0xFFFFFF, true);
                            matrices.scale(2f, 2f, 2f);
                            //RenderHelper.drawScaledText(context, null, var10002, (((panelX + selectedOffsetX - 16) + 6.5F), indexY + 10.5f, 0.5F, 1f, 0, 0, true, true);
                            //int textWidth = textRenderer.getWidth(move.getName());
                            //RenderHelper.drawScaledText(context, null, , ((panelX + selectedOffsetX - 5) + 3.5F - (textWidth) / 2), (indexY + 25), 0.5F, 1f, 0, 0, false, false);

                            //float hpRatio = (float) pokemon.getCurrentHealth() / (float) pokemon.getHp();
                            float hpRatio = 0f;
                            int barHeightMax = 18;
                            int hpBarWidth = 2;
                            float hpBarHeight = hpRatio * (float) barHeightMax;
                            Pair<Float, Float> var31 = getDepletableRedGreen(hpRatio, 0.0F, 0.0F);
                            float red = var31.getLeft();
                            float green = var31.getRight();
                            Identifier var34 = new Identifier("cobblemorigins", "textures/gui/white.png");
                            int expToNextLevel = panelX + selectedOffsetX + 46;
                            float expRatio = (float) indexY + ((float) barHeightMax - hpBarHeight) + (float) 5;
                            float var37 = hpBarHeight / hpRatio;
                            float expBarHeight = (float) barHeightMax - hpBarHeight;
                            float var39 = red * 0.8F;
                            float var40 = green * 0.8F;
                            context.drawTexture(var34, expToNextLevel, (int) expRatio, (float) 0, (float) 0, (int) hpBarWidth, (int) hpBarHeight, 16, (int) var37);
                            //GuiUtils.blitk(matrices, var34, expToNextLevel, expRatio, hpBarHeight, Integer.valueOf(hpBarWidth), 0, expBarHeight, 16, var37, 0, var39, var40, 0.27F, 1, false, 1.0F);
                            //int expForThisLevel = pokemon.getExperience() - (pokemon.getLevel() == 1 ? 0 : pokemon.getExperienceGroup().getExperience(pokemon.getLevel()));
                            int expForThisLevel = 0;
                            //expToNextLevel = pokemon.getExperienceGroup().getExperience(pokemon.getLevel() + 1) - pokemon.getExperienceGroup().getExperience(pokemon.getLevel());
                            expToNextLevel = 0;
                            expRatio = (float) expForThisLevel / (float) expToNextLevel;
                            int expBarWidth = 1;
                            expBarHeight = expRatio * (float) barHeightMax;
                            Identifier ballIcon = new Identifier("cobblemorigins", "textures/gui/white.png");
                            int var62 = panelX + selectedOffsetX + 49;
                            float var41 = (float) indexY + ((float) barHeightMax - expBarHeight) + (float) 5;
                            float var42 = expBarHeight / expRatio;
                            float var43 = (float) barHeightMax - expBarHeight;
                            context.drawTexture(ballIcon, var62, (int) var41, (float) 0, var43, expBarWidth, (int) expBarHeight, 16, (int) var42);
                            //GuiUtils.blitk(matrices, ballIcon, var62, var41, expBarHeight, Integer.valueOf(expBarWidth), 0, var43, 16, var42, 0, 0.2f, 0.65f, 0.84f, 1, false, 1.0F);
                        }
                    }
                }
            }

    }
}
