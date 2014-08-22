package net.specialattack.towerdefence.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StatCollector;
import net.specialattack.towerdefence.logic.Booster;
import net.specialattack.towerdefence.logic.Monster;
import net.specialattack.towerdefence.logic.WaveInfo;
import org.lwjgl.opengl.GL11;

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

        this.font.drawStringWithShadow(StatCollector.translateToLocalFormatted("gui.overlay.wave", WaveInfo.wave), x, y, 0xFFFFFF);
        y += 10;
        this.font.drawStringWithShadow(StatCollector.translateToLocalFormatted("gui.overlay.score", WaveInfo.score), x, y, 0xFFFFFF);
        y += 10;
        this.font.drawStringWithShadow(StatCollector.translateToLocalFormatted("gui.overlay.coins", WaveInfo.coins), x, y, 0xFFFFFF);
        y += 10;
        this.font.drawStringWithShadow(StatCollector.translateToLocalFormatted("gui.overlay.health", WaveInfo.health, 100), x, y, 0xFFFFFF);
        y += 10;
        if (WaveInfo.timer >= 0) {
            this.font.drawStringWithShadow(StatCollector.translateToLocalFormatted("gui.overlay.next", WaveInfo.timer), x, y, 0xFFFFFF);
        } else {
            this.font.drawStringWithShadow(StatCollector.translateToLocalFormatted("gui.overlay.remaining", WaveInfo.monstersAlive), x, y, 0xFFFFFF);
        }
        y += 10;

        Tessellator tess = Tessellator.instance;

        if (WaveInfo.currentMonster != null) {
            Monster monster = WaveInfo.currentMonster;
            mc.renderEngine.bindTexture(monster.getResourceLocation());

            tess.startDrawingQuads();
            tess.addVertexWithUV(x, y + monster.iconHeight, 0.0D, monster.minU, monster.maxV);
            tess.addVertexWithUV(x + monster.iconWidth, y + monster.iconHeight, 0.0D, monster.maxU, monster.maxV);
            tess.addVertexWithUV(x + monster.iconWidth, y, 0.0D, monster.maxU, monster.minV);
            tess.addVertexWithUV(x, y, 0.0D, monster.minU, monster.minV);
            tess.draw();

            this.font.drawStringWithShadow("" + WaveInfo.monsterCount, x, y + 12, 0xFFFF00);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);

            x += monster.iconWidth + 4;
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
                this.font.drawStringWithShadow(data, x, y + 12, 0xFFFF00);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }

            x += 20;
        }

        if (WaveInfo.currentMonster != null || WaveInfo.boosters.size() > 0) {
            x = 2;
            y += 20;
        }

        if (WaveInfo.currentBoss != null) {
            Monster monster = WaveInfo.currentBoss;
            mc.renderEngine.bindTexture(monster.getResourceLocation());

            tess.startDrawingQuads();
            tess.addVertexWithUV(x, y + monster.iconHeight, 0.0D, monster.minU, monster.maxV);
            tess.addVertexWithUV(x + monster.iconWidth, y + monster.iconHeight, 0.0D, monster.maxU, monster.maxV);
            tess.addVertexWithUV(x + monster.iconWidth, y, 0.0D, monster.maxU, monster.minV);
            tess.addVertexWithUV(x, y, 0.0D, monster.minU, monster.minV);
            tess.draw();

            x += monster.iconWidth + 4;
            y += 20;
        }

    }
}
