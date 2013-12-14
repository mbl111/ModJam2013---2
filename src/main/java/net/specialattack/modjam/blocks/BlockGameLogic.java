
package net.specialattack.modjam.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.specialattack.modjam.ModModjam;
import net.specialattack.modjam.items.ItemGameLogic;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import net.specialattack.modjam.tileentity.TileEntityTarget;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGameLogic extends Block {

    @SideOnly(Side.CLIENT)
    private Icon[] icons;

    public BlockGameLogic(int blockId) {
        super(blockId, Material.piston);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int itemId, CreativeTabs creativeTab, List list) {
        for (int j = 0; j < this.icons.length; ++j) {
            list.add(new ItemStack(itemId, 1, j));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta) {
        return this.icons[meta % this.icons.length];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        this.icons = new Icon[2];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = register.registerIcon(this.getTextureName() + i);
        }
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata < 2;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntitySpawner();
        }
        else if (metadata == 1) {
            return new TileEntityTarget();
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float posX, float posY, float posZ) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);

        if (tile != null) {
            ItemStack item = player.getHeldItem();
            if (item != null && item.getItem() instanceof ItemGameLogic) {
                return false;
            }

            if (world.isRemote) {
                return true;
            }

            if (tile instanceof TileEntitySpawner) {
                player.openGui(ModModjam.instance, 0, world, x, y, z);
            }
            else if (tile instanceof TileEntityTarget) {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("Whack me with a Target Linker"));
            }
        }
        else {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("This block is broken :("));
        }

        return true;
    }

}
