
package net.specialattack.modjam.tileentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
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
import net.specialattack.modjam.logic.Monster;
import net.specialattack.modjam.logic.SpawnerLogic;
import net.specialattack.modjam.packet.PacketHandler;

@SuppressWarnings("rawtypes")
public class TileEntitySpawner extends TileEntity {

    public static final Random rand = new Random();

    // TODO: find a way to make this one save
    public List<Entity> spawnedEntities;
    public int spawnQueue;
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
    public Monster currentMonster;
    public Monster currentBoss;

    public ChunkCoordinates target;
    public List<ChunkCoordinates> towers;

    public TileEntitySpawner() {
        this.spawnedEntities = new ArrayList<Entity>();
        this.spawnQueue = 0;
        this.boosters = new ArrayList<Booster>();
        this.playername = null;
        this.waveActive = false;
        this.active = false;
        this.spawning = false;
        this.timer = 0;
        this.interval = 30;
        this.score = 0;
        this.wave = 0;
        this.monsterCount = 10;
        this.currentMonster = null;
        this.currentBoss = null;
        this.towers = new ArrayList<ChunkCoordinates>();
    }

    public String getActiveUser() {
        return this.playername;
    }

    public void setActiveUser(String playername) {
        if (this.playername != null) {
            EntityPlayer player = CommonProxy.getPlayer(this.playername);
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveInfo(null));
            }
        }
        this.playername = playername;
        this.waveActive = false;
        this.spawning = false;
        this.spawnQueue = 0;
        this.boosters.clear();
        this.timer = 0;
        this.score = 0;
        this.wave = 0;
        this.monsterCount = 10;
        this.currentMonster = null;
        this.currentBoss = null;
        this.active = playername != null;

        if (playername != null) {
            Collection collection = this.worldObj.getScoreboard().func_96520_a(Objects.scoreTDCriteria);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
                Score score = this.worldObj.getScoreboard().func_96529_a(this.playername, scoreobjective);
                score.func_96647_c(0);
            }

            this.prepareWave();
        }

        for (Entity entity : this.spawnedEntities) {
            entity.worldObj.removeEntity(entity);
        }
        this.spawnedEntities.clear();

        Iterator<ChunkCoordinates> i = this.towers.iterator();
        while (i.hasNext()) {
            ChunkCoordinates coords = i.next();
            TileEntity tile = this.worldObj.getBlockTileEntity(coords.posX, coords.posY, coords.posZ);
            if (tile != null && tile instanceof TileEntityTower) {
                ((TileEntityTower) tile).reset();
            }
            else {
                i.remove();
            }
        }

        TileEntity tile = this.worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);
        if (tile instanceof TileEntityTarget) {
            ((TileEntityTarget) tile).health = 100;
        }

        this.onInventoryChanged();
    }

    public void updateScore() {
        Collection collection = this.worldObj.getScoreboard().func_96520_a(Objects.scoreTDCriteria);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
            Score score = this.worldObj.getScoreboard().func_96529_a(this.playername, scoreobjective);
            score.func_96647_c(this.score);
        }

        EntityPlayer player = CommonProxy.getPlayer(this.playername);
        if (player != null) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveUpdate(this, 1));
            }
        }
    }

    public void onTargetDamaged(TileEntityTarget target) {
        EntityPlayer player = CommonProxy.getPlayer(this.playername);
        if (player != null) {
            if (player instanceof EntityPlayerMP) {
                widthwidth((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveUpdate(this, 3));
            }

            if (target.health == 0) {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("This is the end, sadfully :("));
            }
            else {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("A monster passed! Remaining health: " + target.health));
            }
        }

        this.score--;
        this.updateScore();

        if (target.health == 0) {
            this.setActiveUser(null);
        }
    }

    public void setTarget(TileEntityTarget newTarget) {
        if (this.target != null) {
            TileEntity tile = this.worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);
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

    public void addTower(TileEntityTower tile) {
        this.towers.add(new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord));
        this.onInventoryChanged();
    }

    public boolean canWork() {
        return this.target != null;
    }

    private void prepareWave() {
        this.wave++;
        this.boosters = SpawnerLogic.getRandomBoosters(TileEntitySpawner.rand, this.worldObj, this.wave);
        if (TileEntitySpawner.rand.nextInt(2) == 1) {
            this.monsterCount += 5;
        }
        if (TileEntitySpawner.rand.nextInt(3) == 1) {
            this.monsterCount -= 10;
        }
        if (this.monsterCount <= 10) {
            this.monsterCount = 10;
        }

        this.spawnQueue = this.monsterCount;

        this.currentMonster = SpawnerLogic.getRandomMonster(TileEntitySpawner.rand);

        if (this.wave % 5 == 0) {
            this.currentBoss = SpawnerLogic.getRandomBoss(TileEntitySpawner.rand);
        }
        else {
            this.currentBoss = null;
        }

        EntityPlayer player = CommonProxy.getPlayer(this.playername);

        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveInfo(this));
        }
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
                    if (this.spawnQueue > 0) {
                        EntityPlayer player = CommonProxy.getPlayer(this.playername);

                        TileEntity tile = this.worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);

                        if (tile != null && tile instanceof TileEntityTarget) {
                            EntityLiving entity = this.currentMonster.createNew(this.worldObj);
                            entity.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, 0.0F, 0.0F);

                            if (this.currentMonster.supportsHat) {
                                entity.setCurrentItemOrArmor(4, SpawnerLogic.monsterAccessoires.get(TileEntitySpawner.rand.nextInt(SpawnerLogic.monsterAccessoires.size())).copy());
                                entity.setEquipmentDropChance(4, 0.0F);
                            }
                            entity.func_110163_bv();

                            entity.targetTasks.taskEntries.clear();
                            entity.tasks.taskEntries.clear();
                            Vec3 target = Vec3.createVectorHelper(this.target.posX, this.target.posY, this.target.posZ);
                            entity.tasks.addTask(0, new EntityTargetLocation(entity, target, (TileEntityTarget) tile, 1.0D));

                            for (Booster booster : this.boosters) {
                                booster.applyBooster(entity);
                            }

                            this.worldObj.spawnEntityInWorld(entity);
                            this.spawnedEntities.add(entity);
                        }
                        else {
                            if (player != null) {
                                CommonProxy.getPlayer(this.playername).sendChatToPlayer(ChatMessageComponent.createFromText("Whoops! Something went wrong :<"));
                            }
                            this.setActiveUser(null);
                        }
                        this.spawnQueue--;
                    }
                    else {
                        if (this.currentBoss != null) {
                            EntityPlayer player = CommonProxy.getPlayer(this.playername);

                            TileEntity tile = this.worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);

                            if (tile != null && tile instanceof TileEntityTarget) {
                                EntityLiving entity = this.currentBoss.createNew(this.worldObj);
                                entity.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, 0.0F, 0.0F);

                                if (this.currentBoss.supportsHat) {
                                    entity.setCurrentItemOrArmor(4, SpawnerLogic.monsterAccessoires.get(TileEntitySpawner.rand.nextInt(SpawnerLogic.monsterAccessoires.size())).copy());
                                    entity.setEquipmentDropChance(4, 0.0F);
                                }
                                entity.func_110163_bv();

                                entity.targetTasks.taskEntries.clear();
                                entity.tasks.taskEntries.clear();
                                Vec3 target = Vec3.createVectorHelper(this.target.posX, this.target.posY, this.target.posZ);
                                entity.tasks.addTask(0, new EntityTargetLocation(entity, target, (TileEntityTarget) tile, 1.0D));

                                this.worldObj.spawnEntityInWorld(entity);
                                this.spawnedEntities.add(entity);
                            }
                            else {
                                if (player != null) {
                                    CommonProxy.getPlayer(this.playername).sendChatToPlayer(ChatMessageComponent.createFromText("Whoops! Something went wrong :<"));
                                }
                                this.setActiveUser(null);
                            }
                        }
                        this.spawning = false;
                    }
                }

                if (this.spawnedEntities.isEmpty() && !this.spawning) {
                    this.waveActive = false;

                    EntityPlayer player = CommonProxy.getPlayer(this.playername);
                    if (player != null) {
                        if (player instanceof EntityPlayerMP) {
                            ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveUpdate(this, 2));
                        }
                    }

                    this.timer = 0;
                    this.spawnQueue = 0;

                    this.prepareWave();

                    if (player != null) {
                        player.sendChatToPlayer(ChatMessageComponent.createFromText("Wave over!"));
                    }
                }
                else {
                    Iterator<Entity> i = this.spawnedEntities.iterator();
                    boolean removed = false;
                    while (i.hasNext()) {
                        Entity entity = i.next();
                        if (entity.isDead) {
                            i.remove();

                            this.score++;
                            this.updateScore();

                            removed = true;
                        }
                    }
                    if (removed) {
                        EntityPlayer player = CommonProxy.getPlayer(this.playername);
                        if (player != null) {
                            if (player instanceof EntityPlayerMP) {
                                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveUpdate(this, 0));
                            }
                        }
                    }
                }
            }
            else {
                if (this.timer % 20 == 0) {
                    EntityPlayer player = CommonProxy.getPlayer(this.playername);
                    if (player != null) {
                        if (player instanceof EntityPlayerMP) {
                            ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveUpdate(this, 2));
                        }
                    }
                }
                if (this.timer >= 1200) {
                    this.waveActive = true;
                    this.timer = 0;

                    EntityPlayer player = CommonProxy.getPlayer(this.playername);
                    if (player != null) {
                        if (player instanceof EntityPlayerMP) {
                            ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveUpdate(this, 2));
                        }
                    }

                    TileEntity tile = this.worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);

                    if (tile != null && tile instanceof TileEntityTarget) {
                        this.spawning = true;

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
        compound.setInteger("spawnQueue", this.spawnQueue);

        if (this.target != null) {
            NBTTagCompound target = new NBTTagCompound();
            target.setInteger("posX", this.target.posX);
            target.setInteger("posY", this.target.posY);
            target.setInteger("posZ", this.target.posZ);
            compound.setCompoundTag("target", target);
        }

        NBTTagList boosters = new NBTTagList();
        for (Booster booster : this.boosters) {
            boosters.appendTag(new NBTTagInt("", booster.id));
        }
        compound.setTag("boosters", boosters);

        if (this.currentMonster != null) {
            compound.setInteger("currentMonster", this.currentMonster.id);
        }

        if (this.currentBoss != null) {
            compound.setInteger("currentBoss", this.currentBoss.id);
        }

        NBTTagList towers = new NBTTagList();
        for (ChunkCoordinates coord : this.towers) {
            NBTTagCompound tower = new NBTTagCompound();
            tower.setInteger("posX", coord.posX);
            tower.setInteger("posY", coord.posY);
            tower.setInteger("posZ", coord.posZ);
            towers.appendTag(tower);
        }
        compound.setTag("towers", towers);
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
        this.spawnQueue = compound.getInteger("spawnQueue");

        if (compound.hasKey("target")) {
            NBTTagCompound target = compound.getCompoundTag("target");
            this.target = new ChunkCoordinates(target.getInteger("posX"), target.getInteger("posY"), target.getInteger("posZ"));
        }

        NBTTagList boosters = compound.getTagList("boosters");
        for (int i = 0; i < boosters.tagCount(); i++) {
            NBTTagInt booster = (NBTTagInt) boosters.tagAt(i);
            this.boosters.add(SpawnerLogic.getBooster(booster.data));
        }

        if (compound.hasKey("currentMonster")) {
            this.currentMonster = SpawnerLogic.getMonster(compound.getInteger("currentMonster"));
        }

        if (compound.hasKey("currentBoss")) {
            this.currentBoss = SpawnerLogic.getMonster(compound.getInteger("currentBoss"));
        }

        NBTTagList towers = compound.getTagList("towers");
        for (int i = 0; i < towers.tagCount(); i++) {
            NBTTagCompound tower = (NBTTagCompound) towers.tagAt(i);
            this.towers.add(new ChunkCoordinates(tower.getInteger("posX"), tower.getInteger("posY"), tower.getInteger("posZ")));
        }
    }

}
