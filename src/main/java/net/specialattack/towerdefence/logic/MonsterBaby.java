
package net.specialattack.towerdefence.logic;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public abstract class MonsterBaby extends Monster {

    public MonsterBaby(int id, Class<? extends EntityLiving> clazz, int weight, boolean supportsHat) {
        super(id, clazz, weight, supportsHat);
    }

    @Override
    public EntityLiving createNew(World world) {
        try {
            EntityLiving entity = this.constructor.newInstance(world);
            this.makeBaby(entity);
            return entity;
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract void makeBaby(EntityLiving entity);

}
