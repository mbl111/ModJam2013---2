
package net.specialattack.modjam.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.specialattack.modjam.Assets;
import net.specialattack.modjam.client.renderer.BlockRendererTower;
import net.specialattack.modjam.packet.PacketHandler;
import net.specialattack.modjam.pathfinding.IAvoided;
import net.specialattack.modjam.tileentity.TileEntityTower;
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

    public BlockTower(int blockId) {
        super(blockId, Material.anvil);
        this.renderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(this.renderId, new BlockRendererTower(this.renderId));
    }

    @Override
    public int getRenderType() {
        return this.renderId;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        this.base = register.registerIcon(Assets.DOMAIN + ":tower-base");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, int meta) {
        return this.base;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        for (int i = 0; i < 3; i++) {
            if (!world.isAirBlock(x, y + i, z)) {
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

        if (world.isRemote) {
            return false;
        }

        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile == null) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("Tower error!"));
            return false;
        }

        if (!(tile instanceof TileEntityTower)) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("Tower error!"));
            return false;
        }

        TileEntityTower tower = (TileEntityTower) tile;

        if (tower.getActive()) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("Tower already active!"));
            return true;
        }

        tower.setActive(true);
        Packet250CustomPayload packet = PacketHandler.createPacketTowerInfo(tower);
        PacketHandler.sendToAllPlayers(packet);
        player.sendChatToPlayer(ChatMessageComponent.createFromText("Tower now active!"));

        return true;
    }
}
