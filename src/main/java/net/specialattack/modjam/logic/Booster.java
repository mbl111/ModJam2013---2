
package net.specialattack.modjam.logic;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomItem;
import net.specialattack.modjam.Assets;

public abstract class Booster extends WeightedRandomItem {

    public final int id;
    public int iconIndex;
    public int minLevel;
    public int maxLevel;
    public float minU;
    public float maxU;
    public float minV;
    public float maxV;

    public Booster(int id, int weight, int minLevel, int maxLevel, int iconIndex) {
        super(weight);
        this.id = id;
        this.iconIndex = iconIndex;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;

        int u = iconIndex % 14;
        int v = (iconIndex - u) / 14;

        this.minU = u * 18.0F / 255.0F;
        this.maxU = (u + 1) * 18.0F / 255.0F;
        this.minV = v * 18.0F / 255.0F;
        this.maxV = (v + 1) * 18.0F / 255.0F;
    }

    public ResourceLocation getResourceLocation() {
        return Assets.GUI_OVERLAY;
    }

    public abstract void applyBooster(EntityLiving entity);

    public abstract boolean conflicts(Booster other);

    public abstract String getDisplay();

}
