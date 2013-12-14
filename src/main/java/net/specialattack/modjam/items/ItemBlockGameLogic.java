
package net.specialattack.modjam.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockGameLogic extends ItemBlock {

    private Block block;

    public ItemBlockGameLogic(int itemId) {
        super(itemId);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.block = Block.blocksList[itemId + 256];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int meta) {
        return this.block.getIcon(2, meta);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + stack.getItemDamage();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extra) {
        list.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".description"), 200));
    }

}
