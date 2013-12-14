
package net.specialattack.modjam.logic;

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class BoosterPotionEffect extends Booster {

    public Potion potion;
    public int level;

    public BoosterPotionEffect(int id, Potion potion, int level, int weight, int minLevel, int maxLevel, int iconIndex) {
        super(id, weight, minLevel, maxLevel, iconIndex);

        this.potion = potion;
        this.level = level;
    }

    @Override
    public void applyBooster(EntityLiving entity) {
        PotionEffect effect = new PotionEffect(potion.id, 30, level, false);
        effect.setPotionDurationMax(true);
        entity.addPotionEffect(effect);
    }

    @Override
    public boolean conflicts(Booster other) {
        if (other instanceof BoosterPotionEffect) {
            return ((BoosterPotionEffect) other).potion != this.potion;
        }
        return false;
    }

    @Override
    public String getDisplay() {
        return "" + (level + 1);
    }

}
