
package net.specialattack.modjam.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTower extends TileEntity {

    public boolean active = false;

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

}
