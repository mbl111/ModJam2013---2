
package net.specialattack.towerdefence.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.specialattack.towerdefence.CommonProxy;
import net.specialattack.towerdefence.logic.Booster;
import net.specialattack.towerdefence.logic.Monster;
import net.specialattack.towerdefence.logic.SpawnerLogic;
import net.specialattack.towerdefence.packet.PacketHandler;

public class TileEntityMultiplayerController extends TileEntity {

    private Set<ChunkCoordinates> spawners;
    private Set<ChunkCoordinates> activeSpawners;

    public boolean active;
    public boolean waveStarted;

    public int interval;
    public int timer;

    public int wave;
    public int monsterCount;
    public Monster currentMonster;
    public List<Booster> boosters;
    public Monster currentBoss;

    public TileEntityMultiplayerController() {
        this.spawners = new TreeSet<ChunkCoordinates>();
        this.activeSpawners = new TreeSet<ChunkCoordinates>();
        this.boosters = new ArrayList<Booster>();
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) {
            return;
        }
        if (this.active) {
            List<TileEntitySpawner> spawners = this.getActiveSpawners();

            boolean wavesActive = false;
            for (TileEntitySpawner spawner : spawners) {
                if (spawner.waveStarted && spawner.waveActive && spawner.active) {
                    wavesActive = true;
                }
                else if (!spawner.waveStarted && waveStarted && spawner.active) {
                    wavesActive = true;
                }
            }

            if (!wavesActive && waveStarted) {
                this.updateActiveSpawners();
                if (!this.prepareWave(600)) {
                    this.active = false;
                }
            }

            if (!wavesActive && !waveStarted) {
                this.timer++;
                if (this.timer >= this.interval) {
                    this.waveStarted = true;

                    for (TileEntitySpawner spawner : spawners) {
                        spawner.waveActive = true;
                        spawner.waveStarted = true;
                        spawner.timer = 0;
                        spawner.interval = 30;

                        spawner.updateStat(2);

                        if (spawner.getTarget() != null) {
                            spawner.spawning = true;

                            spawner.sendChatToPlayer(StatCollector.translateToLocal("towerdefence.wave.start"));
                        }
                        else {
                            spawner.sendChatToPlayer(StatCollector.translateToLocal("towerdefence.error"));
                            spawner.active = false;
                        }
                        spawner.markDirty();
                    }
                }
            }
        }
    }

    private boolean prepareWave(int time) {
        if (this.activeSpawners.size() < 1) {
            return false;
        }

        this.wave++;
        this.boosters = SpawnerLogic.getRandomBoosters(CommonProxy.rand, this.worldObj, this.wave);
        if (CommonProxy.rand.nextInt(2) == 1) {
            this.monsterCount += 5;
        }

        int coins = this.monsterCount * 10;

        this.currentMonster = SpawnerLogic.getRandomMonster(CommonProxy.rand);

        if (this.wave % 5 == 0) {
            this.currentBoss = SpawnerLogic.getRandomBoss(CommonProxy.rand);
            coins += this.wave * 10;
        }
        else {
            this.currentBoss = null;
        }

        List<TileEntitySpawner> spawners = this.getActiveSpawners();
        for (TileEntitySpawner spawner : spawners) {
            if (spawner.getTarget() == null) {
                continue;
            }
            if (spawner.getActiveUser() == null) {
                continue;
            }
            spawner.interval = time;
            spawner.wave = this.wave;
            spawner.boosters = this.boosters;
            spawner.monsterCount = this.monsterCount;
            spawner.addCoins(coins);
            spawner.spawnQueue = this.monsterCount;
            spawner.currentMonster = this.currentMonster;
            spawner.currentBoss = this.currentBoss;

            spawner.sendChatToPlayer(StatCollector.translateToLocal("towerdefence.wave.prepare"));

            EntityPlayer player = CommonProxy.getPlayer(spawner.getActiveUser());
            if (player != null && player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveInfo(spawner));
            }
            spawner.active = true;
            spawner.waveStarted = false;
        }

        this.timer = 0;
        this.interval = time;
        this.waveStarted = false;

        return true;
    }

    public boolean tryStart() {
        if (this.spawners.size() <= 0) {
            return false;
        }

        int players = 0;

        List<TileEntitySpawner> spawners = this.getAllSpawners();
        for (TileEntitySpawner spawner : spawners) {
            if (spawner.getTarget() == null) {
                continue;
            }
            if (spawner.getActiveUser() == null) {
                continue;
            }
            players++;
        }

        if (players < 1) {
            return false;
        }

        this.waveStarted = false;
        this.boosters.clear();
        this.timer = 0;
        this.wave = 0;
        this.monsterCount = 10;
        this.currentMonster = null;
        this.currentBoss = null;
        this.active = true;
        this.prepareWave(1200);
        this.updateActiveSpawners();

        return true;
    }

    public void tryStop() {
        this.waveStarted = false;
        this.boosters.clear();
        this.timer = 0;
        this.wave = 0;
        this.monsterCount = 10;
        this.currentMonster = null;
        this.currentBoss = null;
        this.active = false;
        List<TileEntitySpawner> spawners = this.getAllSpawners();
        for (TileEntitySpawner spawner : spawners) {
            spawner.setActiveUser(spawner.getActiveUser());
        }
    }

    public void updateActiveSpawners() {
        this.activeSpawners.clear();

        Iterator<ChunkCoordinates> i = this.spawners.iterator();
        while (i.hasNext()) {
            ChunkCoordinates coords = i.next();
            TileEntity tile = this.worldObj.getBlockTileEntity(coords.posX, coords.posY, coords.posZ);
            if (tile != null && tile instanceof TileEntitySpawner) {
                TileEntitySpawner spawner = (TileEntitySpawner) tile;
                if (spawner.getActiveUser() == null) {
                    continue;
                }
                this.activeSpawners.add(coords);
            }
            else {
                i.remove();
            }
        }
    }

    public int getActiveSpawnersCount() {
        return this.activeSpawners.size();
    }

    public List<TileEntitySpawner> getActiveSpawners() {
        List<TileEntitySpawner> spawners = new ArrayList<TileEntitySpawner>();

        Iterator<ChunkCoordinates> i = this.activeSpawners.iterator();
        while (i.hasNext()) {
            ChunkCoordinates coords = i.next();
            TileEntity tile = this.worldObj.getBlockTileEntity(coords.posX, coords.posY, coords.posZ);
            if (tile != null && tile instanceof TileEntitySpawner) {
                spawners.add((TileEntitySpawner) tile);
            }
            else {
                i.remove();
            }
        }

        return spawners;
    }

    public int getSpawnersCount() {
        return this.spawners.size();
    }

    public List<TileEntitySpawner> getAllSpawners() {
        List<TileEntitySpawner> spawners = new ArrayList<TileEntitySpawner>();

        Iterator<ChunkCoordinates> i = this.spawners.iterator();
        while (i.hasNext()) {
            ChunkCoordinates coords = i.next();
            TileEntity tile = this.worldObj.getBlockTileEntity(coords.posX, coords.posY, coords.posZ);
            if (tile != null && tile instanceof TileEntitySpawner) {
                spawners.add((TileEntitySpawner) tile);
            }
            else {
                i.remove();
            }
        }

        return spawners;
    }

    public void addSpawner(TileEntitySpawner tile) {
        this.spawners.add(new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord));
        tile.setController(this);
        this.onInventoryChanged();
    }

    public void removeSpawner(TileEntitySpawner tile) {
        this.spawners.remove(new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord));
        tile.setController(null);
        this.onInventoryChanged();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList spawners = new NBTTagList();
        for (ChunkCoordinates coord : this.spawners) {
            NBTTagCompound spawner = new NBTTagCompound();
            spawner.setInteger("posX", coord.posX);
            spawner.setInteger("posY", coord.posY);
            spawner.setInteger("posZ", coord.posZ);
            spawners.appendTag(spawner);
        }
        compound.setTag("spawners", spawners);

        NBTTagList activeSpawners = new NBTTagList();
        for (ChunkCoordinates coord : this.activeSpawners) {
            NBTTagCompound spawner = new NBTTagCompound();
            spawner.setInteger("posX", coord.posX);
            spawner.setInteger("posY", coord.posY);
            spawner.setInteger("posZ", coord.posZ);
            activeSpawners.appendTag(spawner);
        }
        compound.setTag("activeSpawners", activeSpawners);

        compound.setBoolean("waveStarted", this.waveStarted);
        compound.setBoolean("active", this.active);
        compound.setInteger("timer", this.timer);
        compound.setInteger("interval", this.interval);
        compound.setInteger("monsterCount", this.monsterCount);
        compound.setInteger("wave", this.wave);

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
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList spawners = compound.getTagList("spawners");
        for (int i = 0; i < spawners.tagCount(); i++) {
            NBTTagCompound spawner = (NBTTagCompound) spawners.tagAt(i);
            this.spawners.add(new ChunkCoordinates(spawner.getInteger("posX"), spawner.getInteger("posY"), spawner.getInteger("posZ")));
        }

        NBTTagList activeSpawners = compound.getTagList("activeSpawners");
        for (int i = 0; i < activeSpawners.tagCount(); i++) {
            NBTTagCompound spawner = (NBTTagCompound) activeSpawners.tagAt(i);
            this.activeSpawners.add(new ChunkCoordinates(spawner.getInteger("posX"), spawner.getInteger("posY"), spawner.getInteger("posZ")));
        }

        this.waveStarted = compound.getBoolean("waveStarted");
        this.active = compound.getBoolean("active");
        this.timer = compound.getInteger("timer");
        this.interval = compound.getInteger("interval");
        this.monsterCount = compound.getInteger("monsterCount");
        this.wave = compound.getInteger("wave");

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
    }

}
