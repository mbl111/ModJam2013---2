
package net.specialattack.modjam.towers;

import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ITowerInstance {

    void readFromNBT(NBTTagCompound compound);

    void writeToNBT(NBTTagCompound compound);

    ITower getTowerType();

    int getLevel();

    int getSpeed();

    int getSpeedLevel();

    int getRange();

    int getRangeLevel();

    int getDamage();

    int getDamageLevel();

    boolean tick();

    @SideOnly(Side.CLIENT)
    void spawnParticles(int type);

    int getPriceUpgrade(int id);

    void upgrade(int id);

}
