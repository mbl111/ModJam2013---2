package net.specialattack.towerdefence;

import net.minecraft.util.DamageSource;
import net.specialattack.towerdefence.blocks.BlockGameLogic;
import net.specialattack.towerdefence.blocks.BlockTower;
import net.specialattack.towerdefence.creativetab.CreativeTabTowerDefence;
import net.specialattack.towerdefence.items.ItemGameLogic;
import net.specialattack.towerdefence.scoreboard.ScoreTDCriteria;
import net.specialattack.towerdefence.towers.TowerAoE;
import net.specialattack.towerdefence.towers.TowerArrow;

public class Objects {

    public static final String MOD_ID = "TowerDefence";
    // Gradle can't fill this one in:
    // public static final String MOD_VERSION = "Pre-Alpha 0.0";
    public static final String MOD_NAME = "Tower Defence";
    public static final String MOD_CHANNEL = "TowerDefence";

    public static final String CLIENT_PROXY = "net.specialattack.towerdefence.client.ClientProxy";
    public static final String SERVER_PROXY = "net.specialattack.towerdefence.CommonProxy";

    public static CreativeTabTowerDefence creativeTab;

    public static BlockTower blockTower;
    public static BlockGameLogic blockGameLogic;

    public static ItemGameLogic itemGameLogic;

    public static ScoreTDCriteria criteriaScore;
    public static ScoreTDCriteria criteriaHealth;

    public static TowerAoE towerAoE;
    public static TowerArrow towerArrow;

    public static DamageSource damageSourceTower;

}
