
package net.specialattack.modjam;

import net.specialattack.modjam.blocks.BlockColoredAvoiding;
import net.specialattack.modjam.blocks.BlockGameLogic;
import net.specialattack.modjam.blocks.BlockTower;
import net.specialattack.modjam.creativetab.CreativeTabModjam;
import net.specialattack.modjam.items.ItemGameLogic;
import net.specialattack.modjam.scoreboard.ScoreTDCriteria;

public class Objects {

    public static final String MOD_ID = "ModjamBleigh";
    // Gradle can't fill this one in:
    // public static final String MOD_VERSION = "Pre-Alpha 0.0";
    public static final String MOD_NAME = "Team Bleigh's Modjam Entry";
    public static final String MOD_CHANNEL = "ModjamBleigh";

    public static final String CLIENT_PROXY = "net.specialattack.modjam.client.ClientProxy";
    public static final String SERVER_PROXY = "net.specialattack.modjam.CommonProxy";

    public static CreativeTabModjam creativeTab;

    public static BlockTower blockTower;
    public static BlockGameLogic blockGameLogic;
    public static BlockColoredAvoiding blockClayAvoiding;

    public static ItemGameLogic itemGameLogic;

    public static ScoreTDCriteria scoreTDCriteria;

}
