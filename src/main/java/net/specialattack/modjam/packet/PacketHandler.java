
package net.specialattack.modjam.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.specialattack.modjam.Objects;
import net.specialattack.modjam.logic.Booster;
import net.specialattack.modjam.logic.SpawnerLogic;
import net.specialattack.modjam.logic.WaveInfo;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

    public static PacketHandler instance;

    public PacketHandler() {
        instance = this;
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
                handlePacketBlank(in);
            break;
            case 1:
            break;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
                out.writeInt(spawner.boosters.size());
                for (Booster booster : spawner.boosters) {
                    out.writeInt(booster.id);
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
            WaveInfo.monsterCount = in.readInt();
            WaveInfo.boosters.clear();
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                WaveInfo.boosters.add(SpawnerLogic.boosters.get(in.readInt()));
            }
        }
    }

}
