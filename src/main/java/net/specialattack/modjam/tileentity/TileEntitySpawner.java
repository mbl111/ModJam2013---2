
package net.specialattack.modjam.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Vec3;
import net.specialattack.modjam.CommonProxy;
import net.specialattack.modjam.SpawnerLogic;
import net.specialattack.modjam.entity.ai.EntityTargetLocation;

public class TileEntitySpawner extends TileEntity {

    public static final Random rand = new Random();

    // TODO: find a way to make this one save
    private List<Entity> spawnedEntities;
    private List<Entity> spawnQueue;
    private String playername;

    private boolean waveActive;
    private boolean spawning;
    private int timer;
    private int interval;

    public TileEntitySpawner() {
        this.spawnedEntities = new ArrayList<Entity>();
        this.spawnQueue = new ArrayList<Entity>();
        this.playername = null;
        this.waveActive = false;
        this.spawning = false;
        this.timer = 0;
        this.interval = 30;
    }

    public String getActiveUser() {
        return this.playername;
    }

    public void setActiveUser(String playername) {
        this.playername = playername;
        this.waveActive = false;
        this.spawning = false;
        this.spawnQueue.clear();
        this.timer = 0;

        for (Entity entity : this.spawnedEntities) {
            entity.worldObj.removeEntity(entity);
        }

        // Clean up the playground

        this.onInventoryChanged();
    }

    public void startWaves() {

    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        if (this.playername != null) {
            if (CommonProxy.isPlayerLoggedIn(this.playername)) {
                this.timer++;
            }

            if (this.waveActive) {
                if (this.spawning && this.timer >= this.interval) {
                    this.timer = 0;
                    if (!this.spawnQueue.isEmpty()) {
                        Entity entity = this.spawnQueue.remove(0);
                        this.worldObj.spawnEntityInWorld(entity);
                        this.spawnedEntities.add(entity);
                    }
                    else {
                        this.spawning = false;
                    }
                }

                if (this.spawnedEntities.isEmpty() && !this.spawning) {
                    this.waveActive = false;

                    CommonProxy.getPlayer(this.playername).sendChatToPlayer(ChatMessageComponent.createFromText("Wave over!"));
                    this.timer = 0;
                    this.spawnQueue.clear();
                    // Award, start new wave
                }
                else {
                    Iterator<Entity> i = this.spawnedEntities.iterator();
                    while (i.hasNext()) {
                        Entity entity = i.next();
                        if (entity.isDead) {
                            i.remove();
                        }
                    }
                }
            }
            else {
                if (this.timer >= 100) {
                    this.waveActive = true;
                    this.timer = 0;
                    // Prepare wave
                    //  - Create entities
                    //    - Zombie
                    //    - Skeleton
                    //    - Witch - Likes to pause to heal
                    //    - Creeper (extra damage)
                    //    - Spider - Said fuck you
                    //    - Giant (rare) - Old AI
                    //    - Ghast (flying) - Nope
                    //    - Blaze (flying) - Wow, such broke, very annoy
                    //    - Zombie Pigman - DOESN'T WORK
                    //    - Big slime - Nope
                    //  - Set AI for entities
                    //  - Set spawning interval

                    for (int i = 0; i < 1; i++) {
                        EntityLiving entity = SpawnerLogic.createRandomMob(rand, this.worldObj);
                        Vec3 target = Vec3.createVectorHelper(this.xCoord + 20.0D, this.yCoord, this.zCoord);
                        entity.tasks.addTask(0, new EntityTargetLocation(entity, target, 1.0D));
                        entity.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, 0.0F, 0.0F);
                        this.spawnQueue.add(entity);
                    }

                    this.spawning = true;

                    // Announce start
                    CommonProxy.getPlayer(this.playername).sendChatToPlayer(ChatMessageComponent.createFromText("The next wave is starting!"));
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (this.playername != null && !this.playername.isEmpty()) {
            compound.setString("playername", this.playername);
        }
        compound.setBoolean("waveActive", this.waveActive);
        compound.setBoolean("spawning", this.spawning);
        compound.setInteger("timer", this.timer);
        compound.setInteger("interval", this.interval);

        NBTTagList spawnQueue = new NBTTagList();
        for (Entity entity : this.spawnedEntities) {
            NBTTagCompound entityCompound = new NBTTagCompound();
            entity.writeToNBT(entityCompound);
            spawnQueue.appendTag(entityCompound);
        }
        compound.setTag("spawnQueue", spawnQueue);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.playername = compound.getString("playername");
        if (this.playername.isEmpty()) {
            this.playername = null;
        }
        this.waveActive = compound.getBoolean("waveActive");
        this.spawning = compound.getBoolean("spawning");
        this.timer = compound.getInteger("timer");
        this.interval = compound.getInteger("interval");

        NBTTagList spawnQueue = compound.getTagList("spawnQueue");
        for (int i = 0; i < spawnQueue.tagCount(); i++) {
            NBTTagCompound entityCompound = (NBTTagCompound) spawnQueue.tagAt(i);
            Entity entity = EntityList.createEntityFromNBT(entityCompound, this.worldObj);
            this.spawnQueue.add(entity);
        }
    }

}
