
package net.specialattack.modjam.logic;

import java.util.ArrayList;
import java.util.List;

public final class WaveInfo {

    public static boolean shouldRender = false;

    public static List<Booster> boosters = new ArrayList<Booster>();
    public static int wave = 0;
    public static Monster currentMonster = null;
    public static Monster currentBoss = null;
    public static int monsterCount = 0;

    public static int monstersAlive = 0;
    public static int score = 0;
    public static int timer = 0;
    public static int health = 100;

}
