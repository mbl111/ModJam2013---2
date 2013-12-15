
package net.specialattack.modjam.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.specialattack.modjam.towers.ITowerInstance;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTower extends TileEntity {

    public boolean active = false;
    public ITowerInstance towerInstance;

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.active = compound.getBoolean("active");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("active", this.active);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().offset(0.0D, 1.0D, 0.0D).expand(0.5D, 1.5D, 0.5D);
    }

}
