
package net.specialattack.modjam.client.render.timeentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.specialattack.modjam.tileentity.TileEntityTower;

public class TileEntityTowerRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityTower)) {
            return;
        }
        
        
        TileEntityTower tower = (TileEntityTower) tile;
        TileEntityTowerRenderer renderer = tower.getRender();
        //renderer.render(tower);
        
    }

}
