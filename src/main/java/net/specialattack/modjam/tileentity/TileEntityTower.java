
package net.specialattack.modjam.tileentity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.specialattack.modjam.blocks.BlockTower;
import net.specialattack.modjam.packet.PacketHandler;
import net.specialattack.modjam.towers.ITowerInstance;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTower extends TileEntity {

    public ITowerInstance towerInstance;
    public ChunkCoordinates spawner;
    private int cooldown;

    public BlockTower getTowerBlock() {
        if (this.getBlockType() instanceof BlockTower) {
            return (BlockTower) this.blockType;
        }
        return null;
    }

    public void setSpawnerCoords(ChunkCoordinates coords) {
        TileEntity tile = this.worldObj.getBlockTileEntity(coords.posX, coords.posY, coords.posZ);

        if (tile != null && tile instanceof TileEntitySpawner) {
            ((TileEntitySpawner) tile).addTower(this);
        }

        this.spawner = coords;
    }

    public void reset() {
        this.towerInstance = null;
        PacketHandler.resendTileInfo(this);
        this.onInventoryChanged();
        this.cooldown = 0;
    }

    public boolean tryBuy(String playername, int cost) {
        TileEntity tile = this.worldObj.getBlockTileEntity(this.spawner.posX, this.spawner.posY, this.spawner.posZ);

        if (tile != null && tile instanceof TileEntitySpawner) {
            TileEntitySpawner spawner = (TileEntitySpawner) tile;

            if (spawner.getActiveUser() != null && spawner.getActiveUser().equalsIgnoreCase(playername)) {
                return spawner.removeCoins(cost);
            }
        }

        return false;
    }

    public boolean isUserPermitted(String playername) {
        TileEntity tile = this.worldObj.getBlockTileEntity(this.spawner.posX, this.spawner.posY, this.spawner.posZ);

        if (tile != null && tile instanceof TileEntitySpawner) {
            TileEntitySpawner spawner = (TileEntitySpawner) tile;

            if (spawner.getActiveUser() != null && spawner.getActiveUser().equalsIgnoreCase(playername)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj.isRemote) {
            return;
        }

        if (this.towerInstance != null) {
            if (this.cooldown > 0) {
                this.cooldown--;
            }
            else {
                if (this.towerInstance.tick()) {
                    this.cooldown = this.towerInstance.getSpeed();
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("metadata", this.getBlockMetadata());
        compound.setInteger("blockID", this.getBlockType().blockID);

        if (this.towerInstance != null) {
            compound.setString("towerType", this.towerInstance.getTowerType().getIdentifier());

            NBTTagCompound tower = new NBTTagCompound();
            this.towerInstance.writeToNBT(tower);
            compound.setCompoundTag("tower", tower);
        }

        if (this.spawner != null) {
            NBTTagCompound spawner = new NBTTagCompound();
            spawner.setInteger("posX", this.spawner.posX);
            spawner.setInteger("posY", this.spawner.posY);
            spawner.setInteger("posZ", this.spawner.posZ);
            compound.setCompoundTag("spawner", spawner);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.blockMetadata = compound.getInteger("metadata");
        this.setBlockType(compound.getInteger("blockID"));

        if (compound.hasKey("towerType")) {
            String towerType = compound.getString("towerType");
            this.towerInstance = this.getTowerBlock().getTower(towerType).createNewInstance(this);

            NBTTagCompound tower = compound.getCompoundTag("tower");
            this.towerInstance.readFromNBT(tower);
        }

        if (compound.hasKey("spawner")) {
            NBTTagCompound spawner = compound.getCompoundTag("spawner");
            this.spawner = new ChunkCoordinates(spawner.getInteger("posX"), spawner.getInteger("posY"), spawner.getInteger("posZ"));
        }
    }

    @Override
    public Block getBlockType() {
        if (this.worldObj == null && this.blockType == null) {
            return null;
        }
        return super.getBlockType();
    }

    public void setBlockType(int blockId) {
        this.blockType = Block.blocksList[blockId];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().offset(0.0D, 1.0D, 0.0D).expand(0.5D, 1.5D, 0.5D);
    }

    @Override
    public Packet getDescriptionPacket() {
        return PacketHandler.createPacketTowerInfo(this);
    }

}
