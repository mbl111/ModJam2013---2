
package net.specialattack.modjam.tileentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.specialattack.modjam.CommonProxy;
import net.specialattack.modjam.Objects;
import net.specialattack.modjam.entity.ai.EntityTargetLocation;
import net.specialattack.modjam.logic.Booster;
import net.specialattack.modjam.logic.SpawnerLogic;

@SuppressWarnings("rawtypes")
public class TileEntitySpawner extends TileEntity {

    public static final Random rand = new Random();

    // TODO: find a way to make this one save
    private List<Entity> spawnedEntities;
    private List<Entity> spawnQueue;
    private String playername;

    public boolean waveActive;
    public boolean active;
    private boolean spawning;
    public int timer;
    public int monsterCount;
    private int interval;

    public int score;
    public int wave;
    public List<Booster> boosters;

    protected ChunkCoordinates target;

    public TileEntitySpawner() {
        this.spawnedEntities = new ArrayList<Entity>();
        this.spawnQueue = new ArrayList<Entity>();
        this.boosters = new ArrayList<Booster>();
        this.playername = null;
        this.waveActive = false;
        this.active = false;
        this.spawning = false;
        this.timer = 0;
        this.interval = 30;
        this.score = 0;
    }

    public String getActiveUser() {
        return this.playername;
    }

    public void setActiveUser(String playername) {
        this.playername = playername;
        this.waveActive = false;
        this.spawning = false;
        this.spawnQueue.clear();
        this.boosters.clear();
        this.timer = 0;
        this.score = 0;
        this.active = playername != null;

        if (playername != null) {
            Collection collection = this.worldObj.getScoreboard().func_96520_a(Objects.scoreTDCriteria);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
                Score score = this.worldObj.getScoreboard().func_96529_a(this.playername, scoreobjective);
                score.func_96647_c(0);
            }
        }

        for (Entity entity : this.spawnedEntities) {
            entity.worldObj.removeEntity(entity);
        }
        this.spawnedEntities.clear();

        // Clean up the playground

        TileEntity tile = worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);
        if (tile instanceof TileEntityTarget) {
            ((TileEntityTarget) tile).health = 100;
        }

        this.onInventoryChanged();
    }

    public void targetDamaged(TileEntityTarget target) {
        EntityPlayer player = CommonProxy.getPlayer(this.playername);
        if (player != null) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("A monster passed! Remaining health: " + target.health));
        }

        Collection collection = this.worldObj.getScoreboard().func_96520_a(Objects.scoreTDCriteria);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
            Score score = this.worldObj.getScoreboard().func_96529_a(this.playername, scoreobjective);
            score.func_96647_c(--this.score);
        }
    }

    public void target(TileEntityTarget newTarget) {
        if (this.target != null) {
            TileEntity tile = worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);
            if (tile != null && tile instanceof TileEntityTarget) {
                ((TileEntityTarget) tile).spawner = null;
            }
            this.target = null;
        }

        if (newTarget != null) {
            this.target = new ChunkCoordinates(newTarget.xCoord, newTarget.yCoord, newTarget.zCoord);
            newTarget.spawner = new ChunkCoordinates(this.xCoord, this.yCoord, this.zCoord);

            this.onInventoryChanged();
            newTarget.onInventoryChanged();
        }
    }

    public boolean canWork() {
        return this.target != null;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        if (this.playername != null) {
            if (CommonProxy.isPlayerLoggedIn(this.playername)) {
                this.timer++;
                this.onInventoryChanged();
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

                    EntityPlayer player = CommonProxy.getPlayer(this.playername);
                    if (player != null) {
                        player.sendChatToPlayer(ChatMessageComponent.createFromText("Wave over!"));
                    }
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

                            Collection collection = this.worldObj.getScoreboard().func_96520_a(Objects.scoreTDCriteria);
                            Iterator iterator = collection.iterator();

                            while (iterator.hasNext()) {
                                ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
                                Score score = this.worldObj.getScoreboard().func_96529_a(this.playername, scoreobjective);
                                score.func_96647_c(++this.score);
                            }
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

                    Vec3 target = Vec3.createVectorHelper(this.target.posX, this.target.posY, this.target.posZ);
                    TileEntity tile = worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);

                    EntityPlayer player = CommonProxy.getPlayer(this.playername);

                    if (tile != null && tile instanceof TileEntityTarget) {
                        for (int i = 0; i < 1; i++) {
                            EntityLiving entity = SpawnerLogic.createRandomMob(rand, this.worldObj, wave);
                            entity.tasks.addTask(0, new EntityTargetLocation(entity, target, (TileEntityTarget) tile, 1.0D));
                            entity.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, 0.0F, 0.0F);
                            this.spawnQueue.add(entity);
                        }

                        this.spawning = true;

                        // Announce start
                        if (player != null) {
                            CommonProxy.getPlayer(this.playername).sendChatToPlayer(ChatMessageComponent.createFromText("The next wave is starting!"));
                        }
                    }
                    else {
                        if (player != null) {
                            CommonProxy.getPlayer(this.playername).sendChatToPlayer(ChatMessageComponent.createFromText("Whoops! Something went wrong :<"));
                        }
                        this.setActiveUser(null);
                    }
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
        compound.setBoolean("active", this.active);
        compound.setBoolean("spawning", this.spawning);
        compound.setInteger("timer", this.timer);
        compound.setInteger("interval", this.interval);

        NBTTagList spawnQueue = new NBTTagList();
        for (Entity entity : this.spawnQueue) {
            NBTTagCompound entityCompound = new NBTTagCompound();
            entity.writeToNBT(entityCompound);
            spawnQueue.appendTag(entityCompound);
        }
        compound.setTag("spawnQueue", spawnQueue);

        if (this.target != null) {
            NBTTagCompound target = new NBTTagCompound();
            target.setInteger("posX", this.target.posX);
            target.setInteger("posY", this.target.posY);
            target.setInteger("posZ", this.target.posZ);
            compound.setCompoundTag("target", target);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.playername = compound.getString("playername");
        if (this.playername.isEmpty()) {
            this.playername = null;
        }
        this.waveActive = compound.getBoolean("waveActive");
        this.active = compound.getBoolean("active");
        this.spawning = compound.getBoolean("spawning");
        this.timer = compound.getInteger("timer");
        this.interval = compound.getInteger("interval");

        NBTTagList spawnQueue = compound.getTagList("spawnQueue");
        for (int i = 0; i < spawnQueue.tagCount(); i++) {
            NBTTagCompound entityCompound = (NBTTagCompound) spawnQueue.tagAt(i);
            Entity entity = EntityList.createEntityFromNBT(entityCompound, this.worldObj);
            this.spawnQueue.add(entity);
        }

        if (compound.hasKey("target")) {
            NBTTagCompound target = compound.getCompoundTag("target");
            this.target = new ChunkCoordinates(target.getInteger("posX"), target.getInteger("posY"), target.getInteger("posXZ"));
        }
    }

}
