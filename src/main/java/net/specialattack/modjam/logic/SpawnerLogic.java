
package net.specialattack.modjam.logic;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public final class SpawnerLogic {

    public static List<Class<? extends EntityLiving>> spawnableEntities = new ArrayList<Class<? extends EntityLiving>>();
    public static List<ItemStack> monsterAccessoires = new ArrayList<ItemStack>();
    public static List<Booster> boosters = new ArrayList<Booster>();

    static {
        spawnableEntities.add(EntityZombie.class);
        spawnableEntities.add(EntitySkeleton.class);
        spawnableEntities.add(EntityWitch.class);
        spawnableEntities.add(EntityCreeper.class);

        monsterAccessoires.add(new ItemStack(Block.anvil));
        monsterAccessoires.add(new ItemStack(Block.fence));
        monsterAccessoires.add(new ItemStack(Block.netherFence));
        monsterAccessoires.add(new ItemStack(Block.chest));
        monsterAccessoires.add(new ItemStack(Block.dropper));
        monsterAccessoires.add(new ItemStack(Block.dispenser));
        monsterAccessoires.add(new ItemStack(Block.furnaceIdle));
        monsterAccessoires.add(new ItemStack(Block.beacon));

        boosters.add(new BoosterEmpty(1, 200, 5, 30, 0));
        boosters.add(new BoosterPotionEffect(2, Potion.moveSpeed, 0, 100, 5, 30, 1));
        boosters.add(new BoosterPotionEffect(3, Potion.moveSpeed, 1, 50, 20, 50, 1));
        boosters.add(new BoosterPotionEffect(4, Potion.moveSpeed, 2, 50, 40, -1, 1));
        boosters.add(new BoosterPotionEffect(5, Potion.moveSlowdown, 0, 10, 0, -1, 2));
        boosters.add(new BoosterPotionEffect(6, Potion.moveSlowdown, 0, 1, 0, -1, 2));
        // Health Boosters
        boosters.add(new BoosterPotionEffect(7, Potion.invisibility, 0, 50, 20, -1, 4));
        boosters.add(new BoosterPotionEffect(8, Potion.resistance, 0, 80, 5, 10, 5));
        boosters.add(new BoosterPotionEffect(9, Potion.resistance, 1, 100, 10, 30, 5));
        boosters.add(new BoosterPotionEffect(10, Potion.resistance, 2, 120, 20, 50, 5));
        boosters.add(new BoosterPotionEffect(11, Potion.resistance, 3, 120, 40, -1, 5));
        boosters.add(new BoosterPotionEffect(12, Potion.fireResistance, 0, 80, 10, -1, 6));
    }

    public static EntityLiving createRandomMob(Random rand, World world, int level) {
        try {
            Class<? extends EntityLiving> clazz = spawnableEntities.get(rand.nextInt(spawnableEntities.size()));
            Constructor<? extends EntityLiving> constructor = clazz.getConstructor(World.class);

            EntityLiving entity = constructor.newInstance(world);

            entity.targetTasks.taskEntries.clear();
            entity.tasks.taskEntries.clear();
            entity.setCurrentItemOrArmor(4, monsterAccessoires.get(rand.nextInt(monsterAccessoires.size())).copy());
            entity.setEquipmentDropChance(4, 0.0F);
            entity.func_110163_bv();

            return entity;
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return new EntitySlime(world);
    }

    public static List<Booster> getRandomBoosters(Random rand, World world, int level) {
        List<Booster> boosters = new ArrayList<Booster>();

        while (level > 0) {
            Booster booster = (Booster) WeightedRandom.getRandomItem(rand, boosters);

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
                    if (rand.nextInt(level) > level / 3) {
                        continue;
                    }
                    else {
                        break;
                    }
                }
            }

            boosters.add(booster);

            if (rand.nextInt(level) > level / 3) {
                continue;
            }
            else {
                break;
            }
        }

        return boosters;
    }

}
