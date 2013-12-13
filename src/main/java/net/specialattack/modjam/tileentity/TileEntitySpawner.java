
package net.specialattack.modjam.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySpawner extends TileEntity {

    private List<Entity> spawnedEntities;
    private List<Entity> spawnQueue;

    private boolean waveActive;
    private boolean spawning;
    private int timer;
    private int interval;

    public TileEntitySpawner() {
        spawnedEntities = new ArrayList<Entity>();
        spawnQueue = new ArrayList<Entity>();
        waveActive = false;
        spawning = false;
        timer = 0;
        interval = 30;
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }

        timer++;

        if (waveActive) {
            if (spawning && timer >= interval) {
                timer = 0;
                if (!spawnQueue.isEmpty()) {
                    Entity entity = spawnQueue.remove(0);
                    worldObj.spawnEntityInWorld(entity);
                }
                else {
                    spawning = false;
                }
            }
        }
        else {
            if (timer >= 300) {
                waveActive = true;
                timer = 0;
                // Prepare wave

                // Announce start
            }
        }

        if (spawnedEntities.isEmpty()) {
            waveActive = false;
            // Award, start new wave
        }
        else {
            Iterator<Entity> i = spawnedEntities.iterator();
            while (i.hasNext()) {
                Entity entity = i.next();
                if (entity.isDead) {
                    i.remove();
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

}
