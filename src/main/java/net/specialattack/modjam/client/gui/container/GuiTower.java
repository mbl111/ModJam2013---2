
package net.specialattack.modjam.client.gui.container;

import net.minecraft.client.gui.GuiScreen;
import net.specialattack.modjam.tileentity.TileEntityTower;

public class GuiTower extends GuiScreen {

    private TileEntityTower tower;

    public GuiTower(TileEntityTower tower) {
        this.tower = tower;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}
