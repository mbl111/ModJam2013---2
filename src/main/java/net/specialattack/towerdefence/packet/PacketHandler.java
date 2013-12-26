
package net.specialattack.towerdefence.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.specialattack.towerdefence.Objects;
import net.specialattack.towerdefence.logic.Booster;
import net.specialattack.towerdefence.logic.SpawnerLogic;
import net.specialattack.towerdefence.logic.WaveInfo;
import net.specialattack.towerdefence.tileentity.TileEntitySpawner;
import net.specialattack.towerdefence.tileentity.TileEntityTarget;
import net.specialattack.towerdefence.tileentity.TileEntityTower;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

    public static PacketHandler instance;

    public PacketHandler() {
        PacketHandler.instance = this;
    }

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        ByteArrayInputStream bis = new ByteArrayInputStream(packet.data);
        DataInputStream in = new DataInputStream(bis);

        int id;
        try {
            id = in.readInt();
            switch (id) {
            case 0:
                this.handlePacketBlank(in);
            break;
            case 1:
                this.handlePacketWaveInfo(in);
            break;
            case 2:
                this.handlePacketTowerInfo(in, player);
            break;
            case 3:
                this.handlePacketWaveUpdate(in);
            break;
            case 4:
                this.handlePacketSpawnParticles(in, player);
            break;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void sendToAllPlayers(Packet250CustomPayload packet) {
        if (packet == null) {
            return;
        }

        MinecraftServer server = MinecraftServer.getServer();

        if (server != null) {
            List<EntityPlayerMP> players = server.getConfigurationManager().playerEntityList;
            for (EntityPlayerMP player : players) {
                player.playerNetServerHandler.sendPacketToPlayer(packet);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void sendToAllPlayersInWorld(Packet250CustomPayload packet, World world) {
        if (packet == null) {
            return;
        }

        MinecraftServer server = MinecraftServer.getServer();

        if (server != null) {
            List<EntityPlayerMP> players = server.getConfigurationManager().playerEntityList;
            for (EntityPlayerMP player : players) {
                if (player.worldObj == world) {
                    player.playerNetServerHandler.sendPacketToPlayer(packet);
                }
            }
        }
    }

    public static void sendToAllPlayersWatchingBlock(Packet250CustomPayload packet, World world, int x, int y, int z) {
        if (packet == null) {
            return;
        }

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        if (world instanceof WorldServer) {
            PlayerInstance chunkWatcher = ((WorldServer) world).getPlayerManager().getOrCreateChunkWatcher(chunkX, chunkZ, false);

            if (chunkWatcher != null) {
                chunkWatcher.sendToAllPlayersWatchingChunk(packet);
            }
        }
    }

    public static void resendTileInfo(TileEntity tile) {
        if (tile == null) {
            return;
        }

        tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    public static Packet250CustomPayload createPacketBlank() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32767);
        DataOutputStream out = new DataOutputStream(bos);

        try {
            out.writeInt(0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(Objects.MOD_CHANNEL, bos.toByteArray());

        return packet;
    }

    public void handlePacketBlank(DataInputStream in) throws IOException {}

    public static Packet250CustomPayload createPacketWaveInfo(TileEntitySpawner spawner) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32767);
        DataOutputStream out = new DataOutputStream(bos);

        try {
            out.writeInt(1);
            out.writeBoolean(spawner != null);
            if (spawner != null) {
                out.writeInt(spawner.wave);
                out.writeInt(spawner.coins);
                out.writeInt(spawner.monsterCount);
                out.writeInt(spawner.spawnQueue + spawner.spawnedEntities.size());
                TileEntityTarget target = spawner.getTarget();
                if (target != null) {
                    out.writeInt(target.health);
                }
                else {
                    out.writeInt(0);
                }
                out.writeInt(spawner.boosters.size());
                for (Booster booster : spawner.boosters) {
                    out.writeInt(booster.id);
                }
                if (spawner.currentMonster != null) {
                    out.writeInt(spawner.currentMonster.id);
                }
                else {
                    out.writeInt(0);
                }
                if (spawner.currentBoss != null) {
                    out.writeInt(spawner.currentBoss.id);
                }
                else {
                    out.writeInt(0);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(Objects.MOD_CHANNEL, bos.toByteArray());

        return packet;
    }

    public void handlePacketWaveInfo(DataInputStream in) throws IOException {
        boolean show = in.readBoolean();
        WaveInfo.shouldRender = show;
        if (show) {
            WaveInfo.wave = in.readInt();
            WaveInfo.coins = in.readInt();
            WaveInfo.monsterCount = in.readInt();
            WaveInfo.monstersAlive = in.readInt();
            WaveInfo.health = in.readInt();
            WaveInfo.boosters.clear();
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                WaveInfo.boosters.add(SpawnerLogic.getBooster(in.readInt()));
            }
            WaveInfo.currentMonster = SpawnerLogic.getMonster(in.readInt());
            WaveInfo.currentBoss = SpawnerLogic.getMonster(in.readInt());
            WaveInfo.timer = 0;
        }
    }

    public static Packet250CustomPayload createPacketTowerInfo(TileEntityTower tower) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32767);
        DataOutputStream out = new DataOutputStream(bos);

        try {
            out.writeInt(2);
            out.writeInt(tower.xCoord);
            out.writeInt(tower.yCoord);
            out.writeInt(tower.zCoord);
            if (tower.towerInstance != null) {
                byte[] bytes = tower.towerInstance.getTowerType().getIdentifier().getBytes();
                out.writeInt(bytes.length);
                out.write(bytes);
            }
            else {
                out.writeInt(0);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(Objects.MOD_CHANNEL, bos.toByteArray());

        return packet;
    }

    public void handlePacketTowerInfo(DataInputStream in, Player iplayer) throws IOException {
        EntityPlayer player = (EntityPlayer) iplayer;

        World world = player.worldObj;

        int x = in.readInt();
        int y = in.readInt();
        int z = in.readInt();

        TileEntity tile = world.getBlockTileEntity(x, y, z);

        if (tile != null && tile instanceof TileEntityTower) {
            TileEntityTower tower = (TileEntityTower) tile;

            byte[] data = new byte[in.readInt()];
            if (data.length > 0) {
                in.readFully(data);
                tower.towerInstance = tower.getTowerBlock().getTower(new String(data)).createNewInstance(tower);
            }
            else {
                tower.towerInstance = null;
            }

            world.markBlockForRenderUpdate(x, y, z);
            world.markBlockForRenderUpdate(x, y + 1, z);
            world.markBlockForRenderUpdate(x, y + 2, z);
        }
    }

    public static Packet250CustomPayload createPacketWaveUpdate(TileEntitySpawner spawner, int id) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32767);
        DataOutputStream out = new DataOutputStream(bos);

        try {
            out.writeInt(3);
            out.writeInt(id);
            switch (id) {
            case 0: {
                out.writeInt(spawner.spawnQueue + spawner.spawnedEntities.size());
                break;
            }
            case 1: {
                out.writeInt(spawner.score);
                break;
            }
            case 2: {
                out.writeInt(spawner.waveActive ? -1 : ((spawner.interval - spawner.timer) / 20));
                break;
            }
            case 3: {
                TileEntityTarget target = spawner.getTarget();
                if (target != null) {
                    out.writeInt(target.health);
                }
                else {
                    out.writeInt(0);
                }
                break;
            }
            case 4: {
                out.writeInt(spawner.coins);
                break;
            }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(Objects.MOD_CHANNEL, bos.toByteArray());

        return packet;
    }

    public void handlePacketWaveUpdate(DataInputStream in) throws IOException {
        int id = in.readInt();
        switch (id) {
        case 0: {
            WaveInfo.monstersAlive = in.readInt();
            break;
        }
        case 1: {
            WaveInfo.score = in.readInt();
            break;
        }
        case 2: {
            WaveInfo.timer = in.readInt();
            break;
        }
        case 3: {
            WaveInfo.health = in.readInt();
            break;
        }
        case 4: {
            WaveInfo.coins = in.readInt();
            break;
        }
        }
    }

    public static Packet250CustomPayload createPacketSpawnParticles(TileEntityTower tower, int type) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32767);
        DataOutputStream out = new DataOutputStream(bos);

        try {
            out.writeInt(4);
            out.writeInt(tower.xCoord);
            out.writeInt(tower.yCoord);
            out.writeInt(tower.zCoord);
            out.writeInt(type);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload(Objects.MOD_CHANNEL, bos.toByteArray());

        return packet;
    }

    public void handlePacketSpawnParticles(DataInputStream in, Player iplayer) throws IOException {
        EntityPlayer player = (EntityPlayer) iplayer;

        World world = player.worldObj;

        int x = in.readInt();
        int y = in.readInt();
        int z = in.readInt();

        TileEntity tile = world.getBlockTileEntity(x, y, z);

        if (tile != null && tile instanceof TileEntityTower) {
            TileEntityTower tower = (TileEntityTower) tile;

            if (tower.towerInstance != null) {
                tower.towerInstance.spawnParticles(in.readInt());
            }
        }
    }

}
