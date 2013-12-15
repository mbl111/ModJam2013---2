
package net.specialattack.modjam.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.specialattack.modjam.Assets;
import net.specialattack.modjam.ModModjam;
import net.specialattack.modjam.client.renderer.BlockRendererTower;
import net.specialattack.modjam.items.IPassClick;
import net.specialattack.modjam.pathfinding.IAvoided;
import net.specialattack.modjam.tileentity.TileEntityTower;
import net.specialattack.modjam.towers.ITower;
import net.specialattack.modjam.towers.ITowerRenderHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTower extends Block implements IAvoided {

    // Empty multi-block until activated
    // Tower part or fake block (meta = 2)
    // Tower or fake block (meta = 1)
    // Base (in-ground) (meta = 0)

    // Metadata map:
    // 0 base
    // 1,2 fake block

    private int renderId;

    @SideOnly(Side.CLIENT)
    private Icon base;

    public List<ITower> towerTypes;
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

    public ITower getTower(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("identifier");
        }
        return this.towerMapping.get(identifier);
    }

    public ITower getTower(int hash) {
        for (ITower tower : towerTypes) {
            if (tower.getIdentifier().hashCode() == hash) {
                return tower;
            }
        }
        return null;
    }

    public TileEntityTower getTowerTile(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityTower) {
            return (TileEntityTower) tile;
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public ITowerRenderHandler getRenderHandler(String identifier) {
        ITower tower = this.getTower(identifier);
        return tower == null ? null : tower.getRenderHandler();
    }

    @Override
    public int getRenderType() {
        return this.renderId;
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
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta) {
        return this.base;
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
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);

        world.setBlock(x, y + 1, z, this.blockID, 1, 3);
        world.setBlock(x, y + 2, z, this.blockID, 2, 3);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int meta) {
        super.breakBlock(world, x, y, z, blockId, meta);
        for (int i = -meta; i < 3 - meta; i++) {
            if (world.getBlockId(x, y + i, z) == blockId) {
                world.setBlockToAir(x, y + i, z);
            }
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        if (entity instanceof EntityPlayer && world.getBlockMetadata(x, y, z) != 0) {
            return;
        }
        super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        if (world.getBlockMetadata(x, y, z) != 0) {
            return null;
        }

        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntityTower();
        }
        return null;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == 0;
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
            player.sendChatToPlayer(ChatMessageComponent.createFromText("Tower error!"));
            return true;
        }

        if (!(tile instanceof TileEntityTower)) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("Tower error!"));
            return true;
        }

        player.openGui(ModModjam.instance, 1, world, x, y, z);

        return true;
    }
}
