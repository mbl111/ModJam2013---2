
package net.specialattack.modjam.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.specialattack.modjam.logic.Booster;
import net.specialattack.modjam.logic.WaveInfo;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOverlay extends Gui {

    public static GuiOverlay instance = new GuiOverlay();

    public FontRenderer font;

    public GuiOverlay() {
        this.font = Minecraft.getMinecraft().fontRenderer;
    }

    @SuppressWarnings("unused")
    public void drawScreen(Minecraft mc, ScaledResolution resolution) {
        if (!WaveInfo.shouldRender) {
            return;
        }

        GL11.glEnable(GL11.GL_BLEND);

        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        int x = 2;
        int y = 2;

        this.font.drawStringWithShadow("Wave: " + WaveInfo.wave, x, y, 0xFFFFFF);
        y += 10;
        this.font.drawStringWithShadow("Score: " + WaveInfo.score, x, y, 0xFFFFFF);
        y += 10;
        this.font.drawStringWithShadow("Health: " + WaveInfo.health + "/100", x, y, 0xFFFFFF);
        y += 10;
        if (WaveInfo.timer > 0) {
            int time = (1200 - WaveInfo.timer) / 20;
            this.font.drawStringWithShadow("Next wave: " + time, x, y, 0xFFFFFF);
        }
        else {
            this.font.drawStringWithShadow("Remaining Monsters: " + WaveInfo.monstersAlive, x, y, 0xFFFFFF);
        }
        y += 10;

        Tessellator tess = Tessellator.instance;

        if (WaveInfo.currentMonster != null) {
            mc.renderEngine.bindTexture(WaveInfo.currentMonster.getResourceLocation());

            tess.startDrawingQuads();
            tess.addVertexWithUV(x, y + WaveInfo.currentMonster.iconHeight, 0.0D, WaveInfo.currentMonster.minU, WaveInfo.currentMonster.maxV);
            tess.addVertexWithUV(x + WaveInfo.currentMonster.iconWidth, y + WaveInfo.currentMonster.iconHeight, 0.0D, WaveInfo.currentMonster.maxU, WaveInfo.currentMonster.maxV);
            tess.addVertexWithUV(x + WaveInfo.currentMonster.iconWidth, y, 0.0D, WaveInfo.currentMonster.maxU, WaveInfo.currentMonster.minV);
            tess.addVertexWithUV(x, y, 0.0D, WaveInfo.currentMonster.minU, WaveInfo.currentMonster.minV);
            tess.draw();

            this.font.drawStringWithShadow("" + WaveInfo.monsterCount, x, y + WaveInfo.currentMonster.iconHeight - 2, 0xFFFF00);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);

            x += WaveInfo.currentMonster.iconWidth + 4;
        }

        for (Booster booster : WaveInfo.boosters) {
            mc.renderEngine.bindTexture(booster.getResourceLocation());

            tess.startDrawingQuads();
            tess.addVertexWithUV(x, y + 18.0D, 0.0D, booster.minU, booster.maxV);
            tess.addVertexWithUV(x + 18.0D, y + 18.0D, 0.0D, booster.maxU, booster.maxV);
            tess.addVertexWithUV(x + 18.0D, y, 0.0D, booster.maxU, booster.minV);
            tess.addVertexWithUV(x, y, 0.0D, booster.minU, booster.minV);
            tess.draw();

            String data = booster.getDisplay();
            if (data != null && !data.isEmpty()) {
                this.font.drawStringWithShadow(data, x, y + 14, 0xFFFF00);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }

            x += 20;
        }

        if (!WaveInfo.boosters.isEmpty()) {
            x = 2;
            y += 20;
        }
    }
}
