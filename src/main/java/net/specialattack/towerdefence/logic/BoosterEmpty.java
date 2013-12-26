
package net.specialattack.towerdefence.logic;

import net.minecraft.entity.EntityLiving;

public class BoosterEmpty extends Booster {

    public BoosterEmpty(int id, int weight, int minLevel, int maxLevel, int iconIndex) {
        super(id, weight, minLevel, maxLevel, iconIndex);
    }

    @Override
    public void applyBooster(EntityLiving entity) {}

    @Override
    public boolean conflicts(Booster other) {
        return true;
    }

    @Override
    public String getDisplay() {
        return "";
    }

}
