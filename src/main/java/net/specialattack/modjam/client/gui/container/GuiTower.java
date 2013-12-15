
package net.specialattack.modjam.client.gui.container;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.specialattack.modjam.inventory.ContainerTower;
import net.specialattack.modjam.tileentity.TileEntityTower;

public class GuiTower extends GuiContainer {


    private TileEntityTower tower;

    public GuiTower(TileEntityTower tower) {
        super(new ContainerTower(tower));
        this.tower = tower;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        
    }
    
}
