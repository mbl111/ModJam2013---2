package net.specialattack.towerdefence.logic;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomItem;
import net.specialattack.towerdefence.Assets;

public abstract class Booster extends WeightedRandomItem {

    public final int id;
    public int iconIndex;
    public int minLevel;
    public int maxLevel;
    public double minU;
    public double maxU;
    public double minV;
    public double maxV;

    public Booster(int id, int weight, int minLevel, int maxLevel, int iconIndex) {
        super(weight);
        this.id = id;
        this.iconIndex = iconIndex;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;

        int u = iconIndex % 14;
        int v = (iconIndex - u) / 14;

        this.minU = u * 18.0D / 256.0D;
        this.maxU = (u + 1) * 18.0D / 256.0D;
        this.minV = v * 18.0D / 256.0D;
        this.maxV = (v + 1) * 18.0D / 256.0D;
    }

    public ResourceLocation getResourceLocation() {
        return Assets.SHEET_OVERLAY;
    }

    public abstract void applyBooster(EntityLiving entity);

    public abstract boolean conflicts(Booster other);

    public abstract String getDisplay();

}
