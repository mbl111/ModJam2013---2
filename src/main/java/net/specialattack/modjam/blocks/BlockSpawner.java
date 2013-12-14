
package net.specialattack.modjam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.specialattack.modjam.tileentity.TileEntitySpawner;

public class BlockSpawner extends Block {

    public BlockSpawner(int blockId) {
        super(blockId, Material.piston);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntitySpawner();
    }

    private static TileEntitySpawner getTile(World world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntitySpawner) {
            return (TileEntitySpawner) tile;
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float posX, float posY, float posZ) {
        if (player.isSneaking()) {
            TileEntitySpawner tile = getTile(world, x, y, z);

            if (tile != null) {
                if (world.isRemote) {
                    return true;
                }

                if (tile.getActiveUser() == null) {
                    tile.setActiveUser(player.username);
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("Successfully linked to spawner!"));
                }
                else if (tile.getActiveUser().equalsIgnoreCase(player.username)) {
                    tile.setActiveUser(null);
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("Successfully unlinked from spawner!"));
                }
                else {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText("This spawner is already linked to a player"));
                }

                return true;
            }
            else {
                player.sendChatToPlayer(ChatMessageComponent.createFromText("This spawner is broken :("));
            }
        }

        return super.onBlockActivated(world, x, y, z, player, side, posX, posY, posZ);
    }

}
