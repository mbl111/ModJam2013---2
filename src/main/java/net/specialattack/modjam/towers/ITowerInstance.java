
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

    int getRange();

    int getDamage();

    boolean tick();

    @SideOnly(Side.CLIENT)
    void spawnParticles(int type);

}
