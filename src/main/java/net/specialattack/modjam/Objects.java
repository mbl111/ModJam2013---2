
package net.specialattack.modjam;

import net.specialattack.modjam.blocks.BlockSpawner;
import net.specialattack.modjam.blocks.BlockTower;
import net.specialattack.modjam.creativetab.CreativeTabModjam;

public class Objects {

    public static final String MOD_ID = "ModjamBleigh";
    // Gradle can't fill this one in:
    // public static final String MOD_VERSION = "Pre-Alpha 0.0";
    public static final String MOD_NAME = "Team Bleigh's Modjam Entry";

    public static final String CLIENT_PROXY = "net.specialattack.modjam.client.ClientProxy";
    public static final String SERVER_PROXY = "net.specialattack.modjam.CommonProxy";

    public static CreativeTabModjam creativeTab;

    public static BlockTower blockTower;
    public static BlockSpawner blockSpawner;

}
