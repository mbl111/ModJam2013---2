
package net.specialattack.modjam.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.specialattack.modjam.pathfinding.IAvoided;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockColoredAvoiding extends Block implements IAvoided {

    @SideOnly(Side.CLIENT)
    private Icon[] icons;

    public BlockColoredAvoiding(int blockId, Material material) {
        super(blockId, material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta) {
        return this.icons[meta % this.icons.length];
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int itemId, CreativeTabs creativeTab, List list) {
        for (int j = 0; j < 16; ++j) {
            list.add(new ItemStack(itemId, 1, j));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        this.icons = new Icon[16];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = register.registerIcon(this.getTextureName() + "_" + ItemDye.dyeItemNames[BlockColoredAvoiding.getDyeFromBlock(i)]);
        }
    }

    public static int getDyeFromBlock(int meta) {
        return ~meta & 15;
    }

}
