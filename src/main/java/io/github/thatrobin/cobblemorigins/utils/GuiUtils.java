package io.github.thatrobin.cobblemorigins.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class GuiUtils {

    public static void blitk(@NotNull MatrixStack matrixStack, @Nullable Identifier texture, float x, float y, float height, float width, float uOffset, float vOffset, float textureWidth, float textureHeight, float blitOffset, float red, float green, float blue, float alpha, boolean blend, float scale) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        if (texture != null) {
            RenderSystem.setShaderTexture(0, texture);
        }

        if (blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        }

        RenderSystem.setShaderColor(red, green, blue, alpha);
        matrixStack.push();
        matrixStack.scale(scale, scale, 1.0F);
        Matrix4f var10000 = matrixStack.peek().getPositionMatrix();
        drawRectangle(var10000, x, y, x + width, y + height, blitOffset, uOffset / textureWidth, (uOffset + width) / textureWidth, vOffset / textureHeight, (vOffset + height) / textureHeight);
        matrixStack.pop();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawRectangle(@NotNull Matrix4f matrix, float x, float y, float endX, float endY, float blitOffset, float minU, float maxU, float minV, float maxV) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferbuilder.vertex(matrix, x, endY, blitOffset).texture(minU, maxV).next();
        bufferbuilder.vertex(matrix, endX, endY, blitOffset).texture(maxU, maxV).next();
        bufferbuilder.vertex(matrix, endX, y, blitOffset).texture(maxU, minV).next();
        bufferbuilder.vertex(matrix, x, y, blitOffset).texture(minU, minV).next();
        BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
    }

    public static void drawText(@NotNull DrawContext context, @Nullable Identifier font, @NotNull MutableText text, int x, int y, boolean centered, int colour, boolean shadow) {
        MutableText comp = font == null ? text : text.setStyle(text.getStyle().withFont(font));
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (centered) {
            int width = textRenderer.getWidth(comp);
            x = (int)((double)x - (double)(width / 2));
        }

        context.drawText(textRenderer, comp, x, y, colour, shadow);
    }

}
