
package net.specialattack.modjam.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

public class TileEntityTarget extends TileEntity {

    public int health;
    public ChunkCoordinates spawner;

    public TileEntityTarget() {
        this.health = 100;
    }

    public void damage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }

        if (this.spawner != null) {
            TileEntity tile = this.worldObj.getBlockTileEntity(this.spawner.posX, this.spawner.posY, this.spawner.posZ);
            if (tile != null && tile instanceof TileEntitySpawner) {
                ((TileEntitySpawner) tile).onTargetDamaged(this);
            }
            else {
                this.spawner = null;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("health", this.health);

        if (this.spawner != null) {
            NBTTagCompound spawner = new NBTTagCompound();
            spawner.setInteger("posX", this.spawner.posX);
            spawner.setInteger("posY", this.spawner.posY);
            spawner.setInteger("posZ", this.spawner.posZ);
            compound.setCompoundTag("spawner", spawner);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.health = compound.getInteger("health");

        if (compound.hasKey("spawner")) {
            NBTTagCompound spawner = compound.getCompoundTag("spawner");
            this.spawner = new ChunkCoordinates(spawner.getInteger("posX"), spawner.getInteger("posY"), spawner.getInteger("posZ"));
        }
    }

}
