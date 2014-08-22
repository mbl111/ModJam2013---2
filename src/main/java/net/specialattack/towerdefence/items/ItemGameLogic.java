package net.specialattack.towerdefence.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.specialattack.towerdefence.tileentity.TileEntityMultiplayerController;
import net.specialattack.towerdefence.tileentity.TileEntitySpawner;
import net.specialattack.towerdefence.tileentity.TileEntityTarget;

import java.util.List;

public class ItemGameLogic extends Item implements IPassClick {

    @SideOnly(Side.CLIENT)
    private Icon[] icons;

    public ItemGameLogic(int itemId) {
        super(itemId);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int meta) {
        return this.icons[meta % this.icons.length];
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float posX, float posY, float posZ) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);

        int meta = stack.getItemDamage();

        if (tile != null) {
            if (world.isRemote) {
                return true;
            }

            if (meta == 0) {
                if (tile instanceof TileEntitySpawner) {
                    TileEntitySpawner spawner = (TileEntitySpawner) tile;

                    if (stack.stackTagCompound == null) {
                        player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".no-target")));
                    } else {
                        int blockX = stack.stackTagCompound.getInteger("blockX");
                        int blockY = stack.stackTagCompound.getInteger("blockY");
                        int blockZ = stack.stackTagCompound.getInteger("blockZ");

                        TileEntity otherTile = world.getBlockTileEntity(blockX, blockY, blockZ);

                        if (otherTile instanceof TileEntityTarget) {
                            TileEntityTarget target = (TileEntityTarget) otherTile;

                            spawner.setTarget(target);

                            player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".linked")));
                        } else {
                            player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".target-gone")));
                        }

                        stack.stackTagCompound = null;
                    }
                } else if (tile instanceof TileEntityTarget) {
                    stack.stackTagCompound = new NBTTagCompound("tag");
                    stack.stackTagCompound.setInteger("blockX", x);
                    stack.stackTagCompound.setInteger("blockY", y);
                    stack.stackTagCompound.setInteger("blockZ", z);

                    player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".target-set")));
                }
            } else if (meta == 1) {
                if (tile instanceof TileEntityMultiplayerController) {
                    TileEntityMultiplayerController controller = (TileEntityMultiplayerController) tile;

                    if (stack.stackTagCompound == null) {
                        player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".no-spawners")));
                    } else {
                        NBTTagList locations = stack.stackTagCompound.getTagList("locations");

                        int success = 0;
                        int fail = 0;
                        for (int i = 0; i < locations.tagCount(); i++) {
                            NBTTagCompound location = (NBTTagCompound) locations.tagAt(i);

                            int blockX = location.getInteger("blockX");
                            int blockY = location.getInteger("blockY");
                            int blockZ = location.getInteger("blockZ");

                            TileEntity otherTile = world.getBlockTileEntity(blockX, blockY, blockZ);

                            if (otherTile instanceof TileEntitySpawner) {
                                TileEntitySpawner spawner = (TileEntitySpawner) otherTile;

                                controller.addSpawner(spawner);

                                success++;
                            } else {
                                fail++;
                            }
                        }

                        player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocalFormatted(this.getUnlocalizedName(stack) + ".linked", success, fail)));

                        stack.stackTagCompound = null;
                    }
                } else if (tile instanceof TileEntitySpawner) {
                    if (stack.stackTagCompound == null) {
                        stack.stackTagCompound = new NBTTagCompound("tag");
                    }
                    NBTTagList locations = stack.stackTagCompound.getTagList("locations");
                    NBTTagCompound location = new NBTTagCompound();
                    location.setInteger("blockX", x);
                    location.setInteger("blockY", y);
                    location.setInteger("blockZ", z);
                    locations.appendTag(location);
                    stack.stackTagCompound.setTag("locations", locations);

                    player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocalFormatted(this.getUnlocalizedName(stack) + ".spawner-added", locations.tagCount())));
                }
            }
        }

        return true;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + stack.getItemDamage();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extra) {
        if (stack.stackTagCompound != null) {
            NBTTagList locations = stack.stackTagCompound.getTagList("locations");
            list.add(StatCollector.translateToLocalFormatted(this.getUnlocalizedName(stack) + ".linked", locations.tagCount()));
        } else {
            list.add(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".unlinked"));
        }
        list.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(StatCollector.translateToLocal(this.getUnlocalizedName(stack) + ".description"), 200));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return stack.stackTagCompound == null ? EnumRarity.common : EnumRarity.uncommon;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int itemId, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < this.icons.length; ++i) {
            list.add(new ItemStack(itemId, 1, i));
        }
    }

    @Override
    public void registerIcons(IconRegister register) {
        this.icons = new Icon[2];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = register.registerIcon(this.getIconString() + i);
        }
    }

}
