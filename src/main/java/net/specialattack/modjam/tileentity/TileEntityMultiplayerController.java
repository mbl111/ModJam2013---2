
package net.specialattack.modjam.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.specialattack.modjam.CommonProxy;
import net.specialattack.modjam.logic.Booster;
import net.specialattack.modjam.logic.Monster;
import net.specialattack.modjam.logic.SpawnerLogic;
import net.specialattack.modjam.packet.PacketHandler;

public class TileEntityMultiplayerController extends TileEntity {

    public List<ChunkCoordinates> spawners;
    public List<ChunkCoordinates> activeSpawners;

    public boolean active;

    public boolean waveActive;
    public int wave;
    public int monsterCount;
    public Monster currentMonster;
    public List<Booster> boosters;
    public Monster currentBoss;

    public TileEntityMultiplayerController() {
        this.spawners = new ArrayList<ChunkCoordinates>();
    }

    @Override
    public void updateEntity() {
        if (this.active) {
            List<TileEntitySpawner> spawners = this.getActiveSpawners();

            boolean wavesActive = false;
            for (TileEntitySpawner spawner : spawners) {
                if (spawner.waveActive) {
                    wavesActive = true;
                }
            }

            if (!wavesActive) {
                this.prepareWave(600);
                this.updateActiveSpawners();
            }
        }
    }

    private void prepareWave(int time) {
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

        List<TileEntitySpawner> spawners = this.getAllSpawners();
        for (TileEntitySpawner spawner : spawners) {
            if (spawner.getTarget() == null) {
                continue;
            }
            if (spawner.getActiveUser() == null) {
                continue;
            }
            spawner.wave = this.wave;
            spawner.boosters = this.boosters;
            spawner.monsterCount = this.monsterCount;
            spawner.addCoins(coins);
            spawner.spawnQueue = this.monsterCount;
            spawner.currentMonster = this.currentMonster;
            spawner.currentBoss = this.currentBoss;

            spawner.sendChatToPlayer("Prepare for the next wave!");

            EntityPlayer player = CommonProxy.getPlayer(spawner.getActiveUser());
            if (player != null && player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(PacketHandler.createPacketWaveInfo(spawner));
            }
            spawner.active = true;
        }
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

        if (players < 2) {
            return false;
        }

        this.active = true;
        this.prepareWave(1200);
        this.updateActiveSpawners();

        return true;
    }

    public void updateActiveSpawners() {
        this.activeSpawners.clear();

        Iterator<ChunkCoordinates> i = this.spawners.iterator();
        while (i.hasNext()) {
            ChunkCoordinates coords = i.next();
            TileEntity tile = this.worldObj.getBlockTileEntity(coords.posX, coords.posY, coords.posZ);
            if (tile != null && tile instanceof TileEntitySpawner) {
                TileEntitySpawner spawner = (TileEntitySpawner) tile;
                if (!spawner.active) {
                    continue;
                }
                this.activeSpawners.add(coords);
            }
            else {
                i.remove();
            }
        }
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
