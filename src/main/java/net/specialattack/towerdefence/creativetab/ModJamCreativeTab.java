
package net.specialattack.towerdefence.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ModJamCreativeTab extends CreativeTabs {

    private ItemStack iconItemStack;

    public ModJamCreativeTab(String label) {
        super(label);
    }

    @Override
    public ItemStack getIconItemStack() {
        return this.iconItemStack;
    }

    public void setIconItemStack(ItemStack iconItemStack) {
        this.iconItemStack = iconItemStack;
    }

}
