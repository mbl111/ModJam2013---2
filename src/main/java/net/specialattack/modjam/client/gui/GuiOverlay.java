
package net.specialattack.modjam.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.specialattack.modjam.logic.WaveInfo;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOverlay extends Gui {

    public static GuiOverlay instance = new GuiOverlay();

    public FontRenderer font;

    public GuiOverlay() {
        font = Minecraft.getMinecraft().fontRenderer;
    }

    public void drawScreen(Minecraft mc, ScaledResolution resolution) {
        if (!WaveInfo.shouldRender) {
            return;
        }

        GL11.glEnable(GL11.GL_BLEND);

        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        font.drawStringWithShadow("Wave: " + WaveInfo.wave, 2, 2, 0xFFFFFF);
        font.drawStringWithShadow("Monsters: " + WaveInfo.monsterCount, 2, 12, 0xFFFFFF);

    }
}
