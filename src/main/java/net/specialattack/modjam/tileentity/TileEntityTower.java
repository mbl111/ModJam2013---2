
package net.specialattack.modjam.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.specialattack.modjam.client.render.TowerRenderer;
import net.specialattack.modjam.client.render.timeentity.TileEntityTowerRenderer;

public class TileEntityTower extends TileEntity {

    private static TowerRenderer renderer;

    public TileEntityTowerRenderer getRender() {
        if (TileEntityTower.renderer == null) {
            TileEntityTower.renderer = new TowerRenderer();
        }
        return null;
    }

}
