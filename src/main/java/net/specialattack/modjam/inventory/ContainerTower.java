package net.specialattack.modjam.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.specialattack.modjam.tileentity.TileEntityTower;

public class ContainerTower extends Container{

    private TileEntityTower tower;

    public ContainerTower(TileEntityTower tower) {
        this.tower = tower;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer arg0) {
        return true;
    }

    
    
}
