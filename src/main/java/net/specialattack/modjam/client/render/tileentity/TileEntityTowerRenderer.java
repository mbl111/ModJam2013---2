
package net.specialattack.modjam.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.specialattack.modjam.client.render.models.ModelTowerBase;
import net.specialattack.modjam.tileentity.TileEntityTower;

import org.lwjgl.opengl.GL11;

public class TileEntityTowerRenderer extends TileEntitySpecialRenderer {

    private ModelTowerBase towerBase = new ModelTowerBase();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityTower)) {
            return;
        }

        TileEntityTower tower = (TileEntityTower) tile;

        if (!tower.getActive()) {
            return;
        }

        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y + 1, (float) z);

        this.towerBase.renderAll();

        GL11.glPopMatrix();
    }

}
