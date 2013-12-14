
package net.specialattack.modjam.towers;

import net.minecraft.nbt.NBTTagCompound;

public interface ITowerInstance {

    void readFromNBT(NBTTagCompound compound);

    void writeToNBT(NBTTagCompound compound);

    ITower getTowerType();

}
