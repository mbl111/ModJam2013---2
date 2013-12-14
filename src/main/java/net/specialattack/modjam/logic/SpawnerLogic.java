
package net.specialattack.modjam.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public final class SpawnerLogic {

    public static List<Monster> monsters = new ArrayList<Monster>();
    public static List<ItemStack> monsterAccessoires = new ArrayList<ItemStack>();
    public static List<Booster> boosters = new ArrayList<Booster>();

    static {
        Monster monster = new Monster(1, EntityZombie.class, 200, true);
        monster.setIcon(0, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        monster = new Monster(2, EntitySkeleton.class, 100, true);
        monster.setIcon(16, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        monster = new Monster(3, EntityCreeper.class, 100, true);
        monster.setIcon(32, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        monster = new Monster(4, EntityWitch.class, 50, true);
        monster.setIcon(48, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.anvil));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.fence));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.netherFence));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.chest));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.dropper));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.dispenser));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.furnaceIdle));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.beacon));

        SpawnerLogic.boosters.add(new BoosterEmpty(1, 150, 0, -1, 0));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(2, Potion.moveSpeed, 0, 100, 5, 30, 1));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(3, Potion.moveSpeed, 1, 50, 20, 50, 1));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(4, Potion.moveSpeed, 2, 50, 40, -1, 1));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(5, Potion.moveSlowdown, 0, 50, 0, -1, 2));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(6, Potion.moveSlowdown, 1, 30, 0, -1, 2));
        // Health Boosters
        SpawnerLogic.boosters.add(new BoosterPotionEffect(7, Potion.invisibility, 0, 50, 20, -1, 4));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(8, Potion.resistance, 0, 80, 5, 10, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(9, Potion.resistance, 1, 100, 10, 30, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(10, Potion.resistance, 2, 120, 20, 50, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(11, Potion.resistance, 3, 120, 40, -1, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(12, Potion.fireResistance, 0, 80, 10, -1, 6));
    }

    public static Monster getMonster(int id) {
        for (Monster monster : monsters) {
            if (monster.id == id) {
                return monster;
            }
        }
        return null;
    }

    public static Monster getRandomMonster(Random rand) {
        return (Monster) WeightedRandom.getRandomItem(rand, SpawnerLogic.monsters);
    }

    public static Booster getBooster(int id) {
        for (Booster booster : boosters) {
            if (booster.id == id) {
                return booster;
            }
        }
        return null;
    }

    public static List<Booster> getRandomBoosters(Random rand, World world, int level) {
        List<Booster> boosters = new ArrayList<Booster>();

        main:
        {
            while (level > 0) {
                loop:
                {
                    Booster booster = (Booster) WeightedRandom.getRandomItem(rand, SpawnerLogic.boosters);

                    if (booster == null) {
                        break;
                    }

                    if (level < booster.minLevel) {
                        continue;
                    }
                    if (booster.maxLevel != -1 && level > booster.maxLevel) {
                        continue;
                    }

                    for (Booster other : boosters) {
                        if (booster.conflicts(other) || other.conflicts(booster)) {
                            if (rand.nextInt(level) < level / 2) {
                                break loop;
                            }
                            else {
                                break main;
                            }
                        }
                    }

                    boosters.add(booster);

                    if (rand.nextInt(level) < level / 2) {
                        continue;
                    }
                    else {
                        break;
                    }
                }
            }
        }

        return boosters;
    }

}
