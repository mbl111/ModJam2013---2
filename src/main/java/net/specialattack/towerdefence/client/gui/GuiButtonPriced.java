
package net.specialattack.towerdefence.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.specialattack.towerdefence.logic.WaveInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonPriced extends GuiButtonOverlay {

    public int price;
    public boolean shouldDisable;

    public GuiButtonPriced(int id, int posX, int posY, String identifier, int price, ResourceLocation resourceLocation, int texU, int texV) {
        super(id, posX, posY, identifier, resourceLocation, texU, texV);
        this.resourceLocation = resourceLocation;
        this.texU = texU;
        this.texV = texV;
        this.price = price;
        this.shouldDisable = false;
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        this.enabled = !(this.shouldDisable || WaveInfo.coins < this.price || this.price <= 0);
        super.drawButton(minecraft, mouseX, mouseY);
    }

    @Override
    public List<String> getTooltipLines(Minecraft minecraft) {
        List<String> lines = super.getTooltipLines(minecraft);
        if (this.price > 0) {
            lines.add(1, I18n.getStringParams("container.towerdefence-tower.cost", this.price));
        }
        else {
            lines.add(1, I18n.getString("container.towerdefence-tower.nocost"));
        }

        return lines;
    }

}
