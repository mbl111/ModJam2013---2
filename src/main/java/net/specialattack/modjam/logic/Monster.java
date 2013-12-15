
package net.specialattack.modjam.logic;

import java.lang.reflect.Constructor;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomItem;
import net.minecraft.world.World;
import net.specialattack.modjam.Assets;

public class Monster extends WeightedRandomItem {

    public final int id;
    public final Class<? extends EntityLiving> clazz;
    public Constructor<? extends EntityLiving> constructor;
    public boolean supportsHat;

    public int iconIndex;
    public double minU;
    public double maxU;
    public double minV;
    public double maxV;
    public int iconWidth;
    public int iconHeight;

    public Monster(int id, Class<? extends EntityLiving> clazz, int weight, boolean supportsHat) {
        super(weight);
        this.id = id;
        this.clazz = clazz;
        this.supportsHat = supportsHat;
        try {
            this.constructor = this.clazz.getConstructor(World.class);
        }
        catch (Throwable e) {
            throw new RuntimeException(this.clazz.getSimpleName() + " can't be instantiated with a World object only", e);
        }
    }

    public void setIcon(int left, int top, int width, int height) {
        this.minU = left / 256.0D;
        this.minV = top / 256.0D;
        this.maxU = (left + width) / 256.0D;
        this.maxV = (top + height) / 256.0D;
        this.iconWidth = width;
        this.iconHeight = height;
    }

    public ResourceLocation getResourceLocation() {
        return Assets.SHEET_OVERLAY;
    }

    public EntityLiving createNew(World world) {
        try {
            return this.constructor.newInstance(world);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
