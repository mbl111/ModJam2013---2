
package net.specialattack.modjam.client.gui.container;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.specialattack.modjam.inventory.ContainerTower;
import net.specialattack.modjam.tileentity.TileEntityTower;

public class GuiTower extends GuiContainer {

    public TileEntityTower tile;
    @SuppressWarnings("unused")
    private ContainerTower container;

    public GuiTower(TileEntityTower tile) {
        super(new ContainerTower(tile));
        this.tile = tile;
        this.container = (ContainerTower) this.inventorySlots;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {}

}
