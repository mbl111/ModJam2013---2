
package net.specialattack.modjam.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonTinyOverlay extends GuiButton {

    public String identifier;
    public ResourceLocation resourceLocation;
    public int texU;
    public int texV;

    public GuiButtonTinyOverlay(int id, int posX, int posY,int width, String identifier, ResourceLocation resourceLocation, int texU, int texV) {
        super(id, posX, posY, width, 20, "");
        this.identifier = identifier;
        this.resourceLocation = resourceLocation;
        this.texU = texU;
        this.texV = texV;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        super.drawButton(minecraft, mouseX, mouseY);
        if (this.drawButton) {
            minecraft.getTextureManager().bindTexture(this.resourceLocation);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            this.drawTexturedModalRect(this.xPosition + ((this.width - minecraft.fontRenderer.getStringWidth(identifier))/2), this.yPosition, this.texU, this.texV, this.width, this.height);
        }
    }
    
    @Override
    public void drawCenteredString(FontRenderer arg0, String arg1, int arg2, int arg3, int arg4) {
        super.drawCenteredString(arg0, arg1, arg2, arg3, arg4);
    }

    @SuppressWarnings("unchecked")
    public void drawTooltips(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.field_82253_i) {
            List<String> lines = new ArrayList<String>();
            lines.add(I18n.getString("tower." + identifier + ".name"));
            lines.addAll(minecraft.fontRenderer.listFormattedStringToWidth(I18n.getString("tower." + identifier + ".description"), 150));

            this.drawHoveringText(lines, mouseX, mouseY, minecraft.fontRenderer);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
        }
    }

    public void drawHoveringText(List<String> lines, int mouseX, int mouseY, FontRenderer font) {
        if (!lines.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int width = 0;
            Iterator<String> iterator = lines.iterator();

            while (iterator.hasNext()) {
                String line = iterator.next();
                int currentWidth = font.getStringWidth(line);

                if (currentWidth > width) {
                    width = currentWidth;
                }
            }

            int startX = mouseX + 12;
            int startY = mouseY - 12;
            int height = 8;

            if (lines.size() > 1) {
                height += 2 + (lines.size() - 1) * 10;
            }

            this.zLevel = 300.0F;
            int borderColor = 0xF0100010;
            this.drawGradientRect(startX - 3, startY - 4, startX + width + 3, startY - 3, borderColor, borderColor);
            this.drawGradientRect(startX - 3, startY + height + 3, startX + width + 3, startY + height + 4, borderColor, borderColor);
            this.drawGradientRect(startX - 3, startY - 3, startX + width + 3, startY + height + 3, borderColor, borderColor);
            this.drawGradientRect(startX - 4, startY - 3, startX - 3, startY + height + 3, borderColor, borderColor);
            this.drawGradientRect(startX + width + 3, startY - 3, startX + width + 4, startY + height + 3, borderColor, borderColor);
            int backgroundPrimary = 0x505000FF;
            int backgroundSecondary = (backgroundPrimary & 0xFEFEFE) >> 1 | backgroundPrimary & 0xFF000000;
            this.drawGradientRect(startX - 3, startY - 3 + 1, startX - 3 + 1, startY + height + 3 - 1, backgroundPrimary, backgroundSecondary);
            this.drawGradientRect(startX + width + 2, startY - 3 + 1, startX + width + 3, startY + height + 3 - 1, backgroundPrimary, backgroundSecondary);
            this.drawGradientRect(startX - 3, startY - 3, startX + width + 3, startY - 3 + 1, backgroundPrimary, backgroundPrimary);
            this.drawGradientRect(startX - 3, startY + height + 2, startX + width + 3, startY + height + 3, backgroundSecondary, backgroundSecondary);

            for (int i = 0; i < lines.size(); ++i) {
                String line = (String) lines.get(i);

                if (i != 0) {
                    line = EnumChatFormatting.GRAY + line;
                }

                font.drawStringWithShadow(line, startX, startY, 0xFFFFFFFF);

                if (i == 0) {
                    startY += 2;
                }

                startY += 10;
            }

            this.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

}
