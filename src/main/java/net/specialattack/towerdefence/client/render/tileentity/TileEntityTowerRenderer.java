
package net.specialattack.towerdefence.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.specialattack.towerdefence.tileentity.TileEntityTower;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityTowerRenderer extends TileEntitySpecialRenderer {

    //private ModelTowerBase towerBase = new ModelTowerBase();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityTower)) {
            return;
        }

        TileEntityTower tower = (TileEntityTower) tile;

        if (tower.towerInstance != null) {
            return;
        }

        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y + 1, (float) z);

        //this.towerBase.renderAll();

        GL11.glPopMatrix();
    }

}
