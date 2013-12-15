
package net.specialattack.modjam.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import net.specialattack.modjam.tileentity.TileEntityTower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockTower extends ItemBlock implements IPassClick {

    public ItemBlockTower(int arg0) {
        super(arg0);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float posX, float posY, float posZ) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);

        if (tile != null) {
            if (tile instanceof TileEntitySpawner) {
                if (world.isRemote) {
                    return true;
                }

                stack.stackTagCompound = new NBTTagCompound("tag");
                stack.stackTagCompound.setInteger("blockX", x);
                stack.stackTagCompound.setInteger("blockY", y);
                stack.stackTagCompound.setInteger("blockZ", z);

                player.sendChatToPlayer(ChatMessageComponent.createFromText("This stack is bound to this Spawner now"));

                return true;
            }
        }

        int blockX = 0;
        int blockY = 0;
        int blockZ = 0;

        if (stack.stackTagCompound == null) {
            if (!world.isRemote) {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("You need to link this block first"));
            }

            return false;
        }
        else if (!world.isRemote) {

            blockX = stack.stackTagCompound.getInteger("blockX");
            blockY = stack.stackTagCompound.getInteger("blockY");
            blockZ = stack.stackTagCompound.getInteger("blockZ");

            TileEntity otherTile = world.getBlockTileEntity(blockX, blockY, blockZ);
            if (otherTile == null || !(otherTile instanceof TileEntitySpawner)) {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("The link on this block is no longer valid"));

                return false;
            }
        }

        int blockId = world.getBlockId(x, y, z);

        int tempX = x;
        int tempY = y;
        int tempZ = z;
        int tempSide = side;

        if (blockId == Block.snow.blockID && (world.getBlockMetadata(x, y, z) & 7) < 1) {
            side = 1;
        }
        else if (blockId != Block.vine.blockID && blockId != Block.tallGrass.blockID && blockId != Block.deadBush.blockID && (Block.blocksList[blockId] == null || !Block.blocksList[blockId].isBlockReplaceable(world, x, y, z))) {
            if (side == 0) {
                --y;
            }

            if (side == 1) {
                ++y;
            }

            if (side == 2) {
                --z;
            }

            if (side == 3) {
                ++z;
            }

            if (side == 4) {
                --x;
            }

            if (side == 5) {
                ++x;
            }
        }

        if (world.isRemote) {
            return false;
        }

        boolean result = super.onItemUse(stack, player, world, tempX, tempY, tempZ, tempSide, posX, posY, posZ);

        if (result && !world.isRemote) {
            tile = world.getBlockTileEntity(x, y, z);

            if (tile != null && tile instanceof TileEntityTower) {
                ((TileEntityTower) tile).setSpawnerCoords(new ChunkCoordinates(blockX, blockY, blockZ));
            }
        }

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extra) {
        if (stack.stackTagCompound != null) {
            int x = stack.stackTagCompound.getInteger("blockX");
            int y = stack.stackTagCompound.getInteger("blockY");
            int z = stack.stackTagCompound.getInteger("blockZ");
            list.add(StatCollector.translateToLocalFormatted(this.getUnlocalizedName(stack) + ".linked", x, y, z));
        }
        else {
            list.add(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".unlinked"));
        }
        list.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".description"), 200));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return stack.stackTagCompound == null ? EnumRarity.common : EnumRarity.uncommon;
    }

}
