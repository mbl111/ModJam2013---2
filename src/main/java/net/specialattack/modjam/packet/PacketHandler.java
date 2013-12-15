
package net.specialattack.modjam.packet;

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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.specialattack.modjam.Objects;
import net.specialattack.modjam.logic.Booster;
import net.specialattack.modjam.logic.SpawnerLogic;
import net.specialattack.modjam.logic.WaveInfo;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import net.specialattack.modjam.tileentity.TileEntityTarget;
import net.specialattack.modjam.tileentity.TileEntityTower;
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
                out.writeInt(spawner.monsterCount);
                out.writeInt(spawner.spawnQueue + spawner.spawnedEntities.size());
                int x = spawner.target.posX;
                int y = spawner.target.posY;
                int z = spawner.target.posZ;
                TileEntity tile = spawner.worldObj.getBlockTileEntity(x, y, z);
                if (tile instanceof TileEntityTarget) {
                    out.writeInt(((TileEntityTarget) tile).health);
                }
                else {
                    out.writeInt(0);
                }
                out.writeInt(spawner.boosters.size());
                for (Booster booster : spawner.boosters) {
                    out.writeInt(booster.id);
                }
                out.writeInt(spawner.currentMonster.id);
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
            WaveInfo.monsterCount = in.readInt();
            WaveInfo.monstersAlive = in.readInt();
            WaveInfo.health = in.readInt();
            WaveInfo.boosters.clear();
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                WaveInfo.boosters.add(SpawnerLogic.getBooster(in.readInt()));
            }
            WaveInfo.currentMonster = SpawnerLogic.getMonster(in.readInt());
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
            case 0:
                out.writeInt(spawner.spawnQueue + spawner.spawnedEntities.size());
            break;
            case 1:
                out.writeInt(spawner.score);
            break;
            case 2:
                out.writeInt(spawner.waveActive ? -1 : spawner.timer);
            break;
            case 3:
                int x = spawner.target.posX;
                int y = spawner.target.posY;
                int z = spawner.target.posZ;
                TileEntity tile = spawner.worldObj.getBlockTileEntity(x, y, z);
                if (tile instanceof TileEntityTarget) {
                    out.writeInt(((TileEntityTarget) tile).health);
                }
                else {
                    out.writeInt(0);
                }
            break;
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
        case 0:
            WaveInfo.monstersAlive = in.readInt();
        break;
        case 1:
            WaveInfo.score = in.readInt();
        break;
        case 2:
            WaveInfo.timer = in.readInt();
        break;
        case 3:
            WaveInfo.health = in.readInt();
        break;
        }
    }

}
