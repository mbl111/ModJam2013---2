
package net.specialattack.modjam.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

public class TileEntityMultiplayerController extends TileEntity {

    public List<ChunkCoordinates> spawners;

    public boolean active;

    public TileEntityMultiplayerController() {
        this.spawners = new ArrayList<ChunkCoordinates>();
    }

    @Override
    public void updateEntity() {
        // TODO Auto-generated method stub
        super.updateEntity();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList spawners = new NBTTagList();
        for (ChunkCoordinates coord : this.spawners) {
            NBTTagCompound spawner = new NBTTagCompound();
            spawner.setInteger("posX", coord.posX);
            spawner.setInteger("posY", coord.posY);
            spawner.setInteger("posZ", coord.posZ);
            spawners.appendTag(spawner);
        }
        compound.setTag("spawners", spawners);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        NBTTagList spawners = compound.getTagList("spawners");
        for (int i = 0; i < spawners.tagCount(); i++) {
            NBTTagCompound spawner = (NBTTagCompound) spawners.tagAt(i);
            this.spawners.add(new ChunkCoordinates(spawner.getInteger("posX"), spawner.getInteger("posY"), spawner.getInteger("posZ")));
        }
    }

}
