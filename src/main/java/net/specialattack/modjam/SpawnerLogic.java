
package net.specialattack.modjam;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public final class SpawnerLogic {

    public static List<Class<? extends EntityLiving>> spawnableEntities = new ArrayList<Class<? extends EntityLiving>>();
    public static List<ItemStack> monsterAccessoires = new ArrayList<ItemStack>();

    static {
        spawnableEntities.add(EntityZombie.class);
        spawnableEntities.add(EntitySkeleton.class);
        spawnableEntities.add(EntityWitch.class);

        monsterAccessoires.add(new ItemStack(Block.anvil));
        monsterAccessoires.add(new ItemStack(Block.fence));
        monsterAccessoires.add(new ItemStack(Block.netherFence));
        monsterAccessoires.add(new ItemStack(Block.chest));
        monsterAccessoires.add(new ItemStack(Block.dropper));
        monsterAccessoires.add(new ItemStack(Block.dispenser));
        monsterAccessoires.add(new ItemStack(Block.furnaceIdle));
        monsterAccessoires.add(new ItemStack(Block.beacon));
    }

    public static EntityLiving createRandomMob(Random rand, World world) {
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

}
