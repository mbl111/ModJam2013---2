
package net.specialattack.modjam.tileentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    public List<Entity> spawnedEntities;
    public int spawnQueue;

    public boolean waveActive;
    public boolean waveStarted;
    public boolean active;
    public boolean spawning;
    public boolean gameOver;

    public int interval;
    public int timer;
    public int score;
    public int coins;
    public int wave;

    public int monsterCount;
    public Monster currentMonster;
    public List<Booster> boosters;
    public Monster currentBoss;

    private String playername;
    private ChunkCoordinates multiplayerController;
    private ChunkCoordinates target;
    private Set<ChunkCoordinates> towers;

    public TileEntitySpawner() {
        this.spawnedEntities = new ArrayList<Entity>();
        this.spawnQueue = 0;
        this.boosters = new ArrayList<Booster>();
        this.playername = null;
        this.waveActive = false;
        this.waveStarted = false;
        this.active = false;
        this.spawning = false;
        this.timer = 0;
        this.interval = 30;
        this.score = 0;
        this.coins = 200;
        this.wave = 0;
        this.monsterCount = 10;
        this.currentMonster = null;
        this.currentBoss = null;
        this.towers = new TreeSet<ChunkCoordinates>();
    }

    public String getActiveUser() {
        return this.playername;
    }

    public void setActiveUser(String playername) {
        if (this.playername != null) {
            CommonProxy.spawners.remove(playername);
            EntityPlayer player = CommonProxy.getPlayer(this.playername);
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveInfo(null));
            }
        }
        CommonProxy.spawners.put(playername, this);
        this.playername = playername;
        this.waveActive = false;
        this.waveStarted = false;
        this.spawning = false;
        this.spawnQueue = 0;
        this.boosters.clear();
        this.timer = 0;
        this.score = 0;
        this.coins = 200;
        this.wave = 0;
        this.monsterCount = 10;
        this.currentMonster = null;
        this.currentBoss = null;

        TileEntityMultiplayerController controller = this.getController();

        this.active = playername != null && controller == null;

        if (playername != null) {
            Collection collection = this.worldObj.getScoreboard().func_96520_a(Objects.criteriaScore);
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
                Score score = this.worldObj.getScoreboard().func_96529_a(this.playername, scoreobjective);
                score.func_96647_c(0);
            }

            if (controller == null) {
                this.prepareWave();
            }
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

        TileEntityTarget target = this.getTarget();
        if (target != null) {
            target.health = 100;
        }

        if (controller != null) {
            controller.updateActiveSpawners();
        }

        this.markDirty();
    }

    public void sendChatToPlayer(String message) {
        EntityPlayer player = CommonProxy.getPlayer(this.playername);
        if (player != null) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText(message));
        }
    }

    public void markDirty() {
        if (this.worldObj != null) {
            this.worldObj.markTileEntityChunkModified(this.xCoord, this.yCoord, this.zCoord, this);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        CommonProxy.spawners.remove(this.playername);
    }

    public void updateScore() {
        Collection collection = this.worldObj.getScoreboard().func_96520_a(Objects.criteriaScore);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
            Score score = this.worldObj.getScoreboard().func_96529_a(this.playername, scoreobjective);
            score.func_96647_c(this.score);
        }

        this.updateStat(1);
    }

    public void updateStat(int stat) {
        EntityPlayer player = CommonProxy.getPlayer(this.playername);
        if (player != null) {
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveUpdate(this, stat));
            }
        }
        this.markDirty();
    }

    public void onTargetDamaged(TileEntityTarget target) {
        Collection collection = this.worldObj.getScoreboard().func_96520_a(Objects.criteriaHealth);
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreObjective scoreobjective = (ScoreObjective) iterator.next();
            Score score = this.worldObj.getScoreboard().func_96529_a(this.playername, scoreobjective);
            score.func_96647_c(this.score);
        }

        this.updateStat(3);

        if (target.health == 0) {
            this.sendChatToPlayer("This is the end, sadfully :(");
            this.active = false;

            for (Entity entity : this.spawnedEntities) {
                entity.worldObj.removeEntity(entity);
            }
            this.spawnedEntities.clear();
        }
        else {
            this.sendChatToPlayer("A monster passed! Remaining health: " + target.health);
        }

        this.score--;
        this.updateScore();

        if (target.health == 0) {
            //this.setActiveUser(null);
            this.active = false;
        }
    }

    public void setTarget(TileEntityTarget newTarget) {
        TileEntityTarget target = this.getTarget();
        if (target != null) {
            target.spawner = null;
            target.onInventoryChanged();
            this.target = null;
        }

        if (newTarget != null) {
            this.target = new ChunkCoordinates(newTarget.xCoord, newTarget.yCoord, newTarget.zCoord);
            newTarget.spawner = new ChunkCoordinates(this.xCoord, this.yCoord, this.zCoord);

            this.markDirty();
            newTarget.onInventoryChanged();
        }

        this.setActiveUser(null);
    }

    public TileEntityTarget getTarget() {
        if (this.target != null) {
            TileEntity tile = this.worldObj.getBlockTileEntity(this.target.posX, this.target.posY, this.target.posZ);
            if (tile instanceof TileEntityTarget) {
                return ((TileEntityTarget) tile);
            }
        }
        return null;
    }

    public void setController(TileEntityMultiplayerController newController) {
        if (newController != null) {
            this.multiplayerController = new ChunkCoordinates(newController.xCoord, newController.yCoord, newController.zCoord);
        }
        else {
            this.multiplayerController = null;
        }
    }

    public TileEntityMultiplayerController getController() {
        if (this.multiplayerController != null) {
            TileEntity tile = this.worldObj.getBlockTileEntity(this.multiplayerController.posX, this.multiplayerController.posY, this.multiplayerController.posZ);
            if (tile instanceof TileEntityMultiplayerController) {
                return ((TileEntityMultiplayerController) tile);
            }
        }
        return null;
    }

    public void addTower(TileEntityTower tile) {
        this.towers.add(new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord));
        this.markDirty();
    }

    public void removeTower(TileEntityTower tile) {
        this.towers.remove(new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord));
        this.markDirty();
    }

    public List<TileEntityTower> getAllTowers() {
        List<TileEntityTower> towers = new ArrayList<TileEntityTower>();

        Iterator<ChunkCoordinates> i = this.towers.iterator();
        while (i.hasNext()) {
            ChunkCoordinates coords = i.next();
            TileEntity tile = this.worldObj.getBlockTileEntity(coords.posX, coords.posY, coords.posZ);
            if (tile != null && tile instanceof TileEntityTower) {
                towers.add((TileEntityTower) tile);
            }
            else {
                i.remove();
            }
        }

        return towers;
    }

    public void addCoins(int amount) {
        this.coins += amount;
        this.updateStat(4);
    }

    public boolean removeCoins(int amount) {
        if (this.coins < amount) {
            return false;
        }
        this.coins -= amount;
        this.updateStat(4);
        return true;
    }

    public boolean canWork() {
        return this.target != null;
    }

    public boolean hasController() {
        return this.multiplayerController != null;
    }

    private void prepareWave() {
        this.interval = 600;
        this.wave++;
        this.boosters = SpawnerLogic.getRandomBoosters(CommonProxy.rand, this.worldObj, this.wave);
        if (CommonProxy.rand.nextInt(2) == 1) {
            this.monsterCount += 5;
        }

        this.addCoins(this.monsterCount * 10);

        this.spawnQueue = this.monsterCount;

        this.currentMonster = SpawnerLogic.getRandomMonster(CommonProxy.rand);

        if (this.wave % 5 == 0) {
            this.currentBoss = SpawnerLogic.getRandomBoss(CommonProxy.rand);
            this.addCoins(this.wave * 10);
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
            if (this.active && (this.multiplayerController != null || CommonProxy.isPlayerLoggedIn(this.playername))) {
                this.timer++;
                this.markDirty();
            }

            if (this.waveActive) {
                if (this.spawning && this.timer >= this.interval) {
                    this.timer = 0;
                    if (this.spawnQueue > 0) {
                        TileEntityTarget target = this.getTarget();

                        if (target != null) {
                            EntityLiving entity = this.currentMonster.createNew(this.worldObj);
                            entity.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, 0.0F, 0.0F);

                            if (this.currentMonster.supportsHat) {
                                entity.setCurrentItemOrArmor(4, SpawnerLogic.monsterAccessoires.get(CommonProxy.rand.nextInt(SpawnerLogic.monsterAccessoires.size())).copy());
                                entity.setEquipmentDropChance(4, 0.0F);
                            }
                            entity.func_110163_bv();

                            entity.targetTasks.taskEntries.clear();
                            entity.tasks.taskEntries.clear();
                            Vec3 targetPos = Vec3.createVectorHelper(this.target.posX, this.target.posY, this.target.posZ);
                            entity.tasks.addTask(0, new EntityTargetLocation(entity, targetPos, target, 1.0D));

                            for (Booster booster : this.boosters) {
                                booster.applyBooster(entity);
                            }

                            this.worldObj.spawnEntityInWorld(entity);
                            this.spawnedEntities.add(entity);
                        }
                        else {
                            this.sendChatToPlayer("Whoops! Something went wrong :<");
                            // this.setActiveUser(null);
                            this.active = false;

                            for (Entity entity : this.spawnedEntities) {
                                entity.worldObj.removeEntity(entity);
                            }
                            this.spawnedEntities.clear();
                        }
                        this.spawnQueue--;
                        this.markDirty();
                    }
                    else {
                        if (this.currentBoss != null) {
                            TileEntityTarget target = this.getTarget();

                            if (target != null) {
                                EntityLiving entity = this.currentBoss.createNew(this.worldObj);
                                entity.setLocationAndAngles(this.xCoord + 0.5D, this.yCoord + 1.0D, this.zCoord + 0.5D, 0.0F, 0.0F);

                                if (this.currentBoss.supportsHat) {
                                    entity.setCurrentItemOrArmor(4, SpawnerLogic.monsterAccessoires.get(CommonProxy.rand.nextInt(SpawnerLogic.monsterAccessoires.size())).copy());
                                    entity.setEquipmentDropChance(4, 0.0F);
                                }
                                entity.func_110163_bv();

                                entity.targetTasks.taskEntries.clear();
                                entity.tasks.taskEntries.clear();
                                Vec3 targetPos = Vec3.createVectorHelper(this.target.posX, this.target.posY, this.target.posZ);
                                entity.tasks.addTask(0, new EntityTargetLocation(entity, targetPos, target, 1.0D));

                                this.worldObj.spawnEntityInWorld(entity);
                                this.spawnedEntities.add(entity);
                            }
                            else {
                                this.sendChatToPlayer("Whoops! Something went wrong :<");
                                // this.setActiveUser(null);
                                this.active = false;

                                for (Entity entity : this.spawnedEntities) {
                                    entity.worldObj.removeEntity(entity);
                                }
                                this.spawnedEntities.clear();
                            }
                        }
                        this.spawning = false;
                        this.markDirty();
                    }
                }

                if (this.spawnedEntities.isEmpty() && !this.spawning) {
                    this.waveActive = false;

                    this.updateStat(2);

                    this.timer = 0;
                    this.spawnQueue = 0;

                    if (this.multiplayerController == null) {
                        this.prepareWave();
                        this.waveStarted = false;
                    }

                    this.markDirty();

                    this.sendChatToPlayer("Wave complete!");
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
                        this.updateStat(0);
                        this.markDirty();
                    }
                }
            }
            else {
                if ((this.interval - this.timer) % 20 == 0) {
                    this.updateStat(2);
                }
                if (this.multiplayerController == null) {
                    if (this.timer >= this.interval) {
                        this.waveActive = true;
                        this.waveStarted = true;
                        this.timer = 0;
                        this.interval = 30;

                        this.updateStat(2);

                        if (this.getTarget() != null) {
                            this.spawning = true;

                            this.sendChatToPlayer("The next wave is starting!");
                        }
                        else {
                            this.sendChatToPlayer("Whoops! Something went wrong :<");
                            //this.setActiveUser(null);
                            this.active = false;

                            for (Entity entity : this.spawnedEntities) {
                                entity.worldObj.removeEntity(entity);
                            }
                            this.spawnedEntities.clear();
                        }
                        this.markDirty();
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
        compound.setBoolean("waveStarted", this.waveStarted);
        compound.setBoolean("active", this.active);
        compound.setBoolean("spawning", this.spawning);
        compound.setBoolean("gameOver", this.gameOver);
        compound.setInteger("timer", this.timer);
        compound.setInteger("interval", this.interval);
        compound.setInteger("spawnQueue", this.spawnQueue);
        compound.setInteger("score", this.score);
        compound.setInteger("coins", this.coins);
        compound.setInteger("monsterCount", this.monsterCount);
        compound.setInteger("wave", this.wave);

        if (this.target != null) {
            NBTTagCompound target = new NBTTagCompound();
            target.setInteger("posX", this.target.posX);
            target.setInteger("posY", this.target.posY);
            target.setInteger("posZ", this.target.posZ);
            compound.setCompoundTag("target", target);
        }

        if (this.multiplayerController != null) {
            NBTTagCompound controller = new NBTTagCompound();
            controller.setInteger("posX", this.multiplayerController.posX);
            controller.setInteger("posY", this.multiplayerController.posY);
            controller.setInteger("posZ", this.multiplayerController.posZ);
            compound.setCompoundTag("multiplayerController", controller);
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
        this.waveStarted = compound.getBoolean("waveStarted");
        this.active = compound.getBoolean("active");
        this.spawning = compound.getBoolean("spawning");
        this.gameOver = compound.getBoolean("gameOver");
        this.timer = compound.getInteger("timer");
        this.interval = compound.getInteger("interval");
        this.spawnQueue = compound.getInteger("spawnQueue");
        this.score = compound.getInteger("score");
        this.coins = compound.getInteger("coins");
        this.monsterCount = compound.getInteger("monsterCount");
        this.wave = compound.getInteger("wave");

        if (compound.hasKey("target")) {
            NBTTagCompound target = compound.getCompoundTag("target");
            this.target = new ChunkCoordinates(target.getInteger("posX"), target.getInteger("posY"), target.getInteger("posZ"));
        }

        if (compound.hasKey("multiplayerController")) {
            NBTTagCompound controller = compound.getCompoundTag("multiplayerController");
            this.multiplayerController = new ChunkCoordinates(controller.getInteger("posX"), controller.getInteger("posY"), controller.getInteger("posZ"));
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

        CommonProxy.spawners.put(playername, this);
    }

}
