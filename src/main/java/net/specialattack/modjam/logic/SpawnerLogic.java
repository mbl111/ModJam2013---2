
package net.specialattack.modjam.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public final class SpawnerLogic {

    public static List<Monster> monsters = new ArrayList<Monster>();
    public static List<Monster> bosses = new ArrayList<Monster>();
    public static List<ItemStack> monsterAccessoires = new ArrayList<ItemStack>();
    public static List<Booster> boosters = new ArrayList<Booster>();

    static {
        // Regular monsters
        Monster monster = new Monster(1, EntityZombie.class, 100, true);
        monster.setIcon(0, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        monster = new Monster(2, EntitySkeleton.class, 150, true);
        monster.setIcon(16, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        monster = new Monster(3, EntityCreeper.class, 150, false);
        monster.setIcon(32, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        monster = new Monster(4, EntityWitch.class, 70, false);
        monster.setIcon(48, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        monster = new Monster(5, EntitySnowman.class, 70, false);
        monster.setIcon(80, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        monster = new MonsterBaby(7, EntityZombie.class, 50, true) {
            @Override
            public void makeBaby(EntityLiving entity) {
                ((EntityZombie) entity).setChild(true);
            }
        };
        monster.setIcon(112, 36, 16, 16);
        SpawnerLogic.monsters.add(monster);

        // Bosses
        monster = new Monster(6, EntityIronGolem.class, 100, false);
        monster.setIcon(64, 36, 16, 16);
        SpawnerLogic.bosses.add(monster);

        monster = new Monster(8, EntityHorse.class, 70, false);
        monster.setIcon(96, 36, 16, 16);
        SpawnerLogic.bosses.add(monster);

        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.anvil));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.fence));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.netherFence));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.chest));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.dropper));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.dispenser));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.furnaceIdle));
        SpawnerLogic.monsterAccessoires.add(new ItemStack(Block.beacon));

        SpawnerLogic.boosters.add(new BoosterEmpty(1, 150, 0, -1, 0));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(2, Potion.moveSpeed, 0, 140, 5, 30, 1));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(3, Potion.moveSpeed, 1, 70, 20, 50, 1));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(4, Potion.moveSpeed, 2, 70, 40, -1, 1));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(5, Potion.moveSlowdown, 0, 80, 0, -1, 2));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(6, Potion.moveSlowdown, 1, 80, 0, -1, 2));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(7, Potion.invisibility, 0, 40, 20, -1, 4));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(8, Potion.resistance, 0, 80, 5, 10, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(9, Potion.resistance, 1, 120, 10, 30, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(10, Potion.resistance, 2, 120, 20, -1, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(11, Potion.resistance, 3, 60, 30, -1, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(12, Potion.resistance, 4, 30, 40, -1, 5));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(13, Potion.fireResistance, 0, 80, 10, -1, 6));
        // Health boost
        SpawnerLogic.boosters.add(new BoosterPotionEffect(14, Potion.field_76434_w, 0, 80, 5, -1, 7));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(15, Potion.field_76434_w, 1, 80, 15, -1, 7));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(16, Potion.field_76434_w, 2, 80, 25, -1, 7));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(17, Potion.field_76434_w, 3, 80, 35, -1, 7));
        SpawnerLogic.boosters.add(new BoosterPotionEffect(17, Potion.field_76434_w, 4, 80, 45, -1, 7));
    }

    public static Monster getMonster(int id) {
        for (Monster monster : SpawnerLogic.monsters) {
            if (monster.id == id) {
                return monster;
            }
        }
        for (Monster monster : SpawnerLogic.bosses) {
            if (monster.id == id) {
                return monster;
            }
        }
        return null;
    }

    public static Monster getRandomMonster(Random rand) {
        return (Monster) WeightedRandom.getRandomItem(rand, SpawnerLogic.monsters);
    }

    public static Monster getRandomBoss(Random rand) {
        return (Monster) WeightedRandom.getRandomItem(rand, SpawnerLogic.bosses);
    }

    public static Booster getBooster(int id) {
        for (Booster booster : SpawnerLogic.boosters) {
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
