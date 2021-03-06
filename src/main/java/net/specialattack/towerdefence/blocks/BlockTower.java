package net.specialattack.towerdefence.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.specialattack.towerdefence.Assets;
import net.specialattack.towerdefence.ModTowerDefence;
import net.specialattack.towerdefence.client.renderer.BlockRendererTower;
import net.specialattack.towerdefence.items.IPassClick;
import net.specialattack.towerdefence.pathfinding.IAvoided;
import net.specialattack.towerdefence.tileentity.TileEntitySpawner;
import net.specialattack.towerdefence.tileentity.TileEntityTower;
import net.specialattack.towerdefence.towers.ITower;
import net.specialattack.towerdefence.towers.ITowerRenderHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockTower extends Block implements IAvoided {

    // Empty multi-block until activated
    // Tower part or fake block (meta = 2)
    // Tower or fake block (meta = 1)
    // Base (in-ground) (meta = 0)

    // Metadata map:
    // 0 base
    // 1,2 fake block

    public List<ITower> towerTypes;
    private int renderId;
    @SideOnly(Side.CLIENT)
    private Icon base;
    private Map<String, ITower> towerMapping;

    public BlockTower(int blockId) {
        super(blockId, Material.anvil);
        this.renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(this.renderId, new BlockRendererTower(this.renderId));
        this.towerTypes = new ArrayList<ITower>();
        this.towerMapping = new HashMap<String, ITower>();
    }

    public void registerTower(ITower tower) {
        if (tower == null) {
            throw new IllegalArgumentException("tower");
        }
        this.towerTypes.add(tower);
        this.towerMapping.put(tower.getIdentifier(), tower);
    }

    public ITower getTower(int id) {
        for (ITower tower : this.towerTypes) {
            if (tower.getId() == id) {
                return tower;
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public ITowerRenderHandler getRenderHandler(String identifier) {
        ITower tower = this.getTower(identifier);
        return tower == null ? null : tower.getRenderHandler();
    }

    public ITower getTower(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("identifier");
        }
        return this.towerMapping.get(identifier);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return this.renderId;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata != 1 && metadata != 2) {
            return super.getBlockTexture(world, x, y, z, side);
        }

        TileEntityTower tower = this.getTowerTile(world, x, y, z);

        if (tower != null && tower.towerInstance != null) {
            return tower.towerInstance.getTowerType().getIcon(side, metadata == 2);
        }

        return super.getBlockTexture(world, x, y, z, side);
    }

    public TileEntityTower getTowerTile(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityTower) {
            return (TileEntityTower) tile;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta) {
        return this.base;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        if (entity instanceof EntityPlayer) {
            if (world.getBlockMetadata(x, y, z) != 0) {
                return;
            }
            super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
        }
        if (entity instanceof EntityLiving) {
            super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int meta) {
        for (int i = -meta; i < 3 - meta; i++) {
            if (world.getBlockId(x, y + i, z) == blockId) {
                world.setBlockToAir(x, y + i, z);
            }
        }

        TileEntity tile = world.getBlockTileEntity(x, y - meta, z);
        if (tile != null) {
            if (tile instanceof TileEntityTower) {
                TileEntityTower tower = (TileEntityTower) tile;
                TileEntitySpawner spawner = tower.getSpawner();

                if (spawner != null) {
                    spawner.removeTower(tower);
                }
            }
        }
        super.breakBlock(world, x, y, z, blockId, meta);

    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        if (world.getBlockMetadata(x, y, z) != 0) {
            return null;
        }

        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        for (int i = 0; i < 3; i++) {
            if (!super.canPlaceBlockAt(world, x, y + i, z)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float posX, float posY, float posZ) {
        ItemStack item = player.getHeldItem();
        if (item != null && item.getItem() instanceof IPassClick) {
            return false;
        }

        if (world.isRemote) {
            return true;
        }

        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile == null) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocal(super.getUnlocalizedName() + ".error")));
            return true;
        }

        if (!(tile instanceof TileEntityTower)) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText(StatCollector.translateToLocal(super.getUnlocalizedName() + ".error")));
            return true;
        }
        player.openGui(ModTowerDefence.instance, 1, world, x, y, z);

        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);

        world.setBlock(x, y + 1, z, this.blockID, 1, 3);
        world.setBlock(x, y + 2, z, this.blockID, 2, 3);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        this.base = register.registerIcon(Assets.DOMAIN + ":tower-base");

        for (ITower tower : this.towerTypes) {
            tower.registerIcons(register);
        }
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == 0;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntityTower();
        }
        return null;
    }

}
