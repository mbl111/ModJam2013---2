package net.specialattack.towerdefence.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.specialattack.towerdefence.blocks.BlockTower;
import net.specialattack.towerdefence.packet.PacketHandler;
import net.specialattack.towerdefence.towers.ITowerInstance;

public class TileEntityTower extends TileEntity {

    public ITowerInstance towerInstance;
    private ChunkCoordinates spawner;
    private int cooldown;

    public void setSpawnerCoords(ChunkCoordinates coords) {
        TileEntity tile = this.worldObj.getBlockTileEntity(coords.posX, coords.posY, coords.posZ);

        if (tile != null && tile instanceof TileEntitySpawner) {
            ((TileEntitySpawner) tile).addTower(this);
        }

        this.spawner = coords;
    }

    public TileEntitySpawner getSpawner() {
        if (this.worldObj == null || this.spawner == null) {
            return null;
        }
        TileEntity tile = this.worldObj.getBlockTileEntity(this.spawner.posX, this.spawner.posY, this.spawner.posZ);

        if (tile != null && tile instanceof TileEntitySpawner) {
            return (TileEntitySpawner) tile;
        }

        return null;
    }

    public void reset() {
        this.towerInstance = null;
        PacketHandler.resendTileInfo(this);
        this.onInventoryChanged();
        this.cooldown = 0;
    }

    public boolean tryBuy(String playername, int cost) {
        if (this.worldObj == null || this.spawner == null) {
            return false;
        }
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
        if (this.worldObj == null || this.spawner == null) {
            return false;
        }
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

    public BlockTower getTowerBlock() {
        if (this.getBlockType() instanceof BlockTower) {
            return (BlockTower) this.blockType;
        }
        return null;
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
    public void updateEntity() {
        if (this.worldObj.isRemote) {
            return;
        }

        if (this.towerInstance != null) {
            if (this.cooldown > 0) {
                this.cooldown--;
            } else {
                if (this.towerInstance.tick()) {
                    this.cooldown = this.towerInstance.getSpeed();
                }
            }
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
    public Packet getDescriptionPacket() {
        return PacketHandler.createPacketTowerInfo(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().offset(0.0D, 1.0D, 0.0D).expand(0.5D, 1.5D, 0.5D);
    }

}
