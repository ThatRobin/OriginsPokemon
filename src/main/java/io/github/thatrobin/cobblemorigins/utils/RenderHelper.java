package io.github.thatrobin.cobblemorigins.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenderHelper {

    public static void drawScaledText(@NotNull DrawContext context, @Nullable Identifier font, @NotNull MutableText text, float x, float y, float scale, float opacity, int maxCharacterWidth, int colour, boolean centered, boolean shadow) {
        if (!(opacity < 0.05F)) {
            int textWidth = MinecraftClient.getInstance().textRenderer.getWidth((font != null ? text.setStyle(text.getStyle().withFont(font)) : text));
            float extraScale = textWidth < maxCharacterWidth ? 1.0F : (float)maxCharacterWidth / (float)textWidth;
            int fontHeight = font == null ? 5 : 6;
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.scale(scale * extraScale, scale * extraScale, 1.0F);
            GuiUtils.drawText(context, font, text, (int) (x / (scale * extraScale)), (int) (y / (scale * extraScale) + ((float)1 - extraScale) * (float)fontHeight * scale), centered, colour, shadow);
            matrices.pop();
        }
    }
}
