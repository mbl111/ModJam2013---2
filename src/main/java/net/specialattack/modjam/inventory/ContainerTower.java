
package net.specialattack.modjam.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.specialattack.modjam.packet.PacketHandler;
import net.specialattack.modjam.tileentity.TileEntityTower;
import net.specialattack.modjam.towers.ITower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerTower extends Container {

    public TileEntityTower tile;
    public boolean prevActivated;
    public boolean activated;
    public int level;
    public int speed;
    public int range;
    public int damage;

    public ContainerTower(TileEntityTower tile) {
        this.tile = tile;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);

        crafting.sendProgressBarUpdate(this, 0, this.tile.towerInstance != null ? 1 : 0);
        if (this.tile.towerInstance != null) {
            crafting.sendProgressBarUpdate(this, 1, this.tile.towerInstance.getLevel());
            crafting.sendProgressBarUpdate(this, 2, this.tile.towerInstance.getSpeed());
            crafting.sendProgressBarUpdate(this, 3, this.tile.towerInstance.getRange());
            crafting.sendProgressBarUpdate(this, 4, this.tile.towerInstance.getDamage());
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean activated = this.tile.towerInstance != null;

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting) this.crafters.get(i);

            if (this.prevActivated != activated) {
                crafting.sendProgressBarUpdate(this, 0, activated ? 1 : 0);

                if (activated) {
                    crafting.sendProgressBarUpdate(this, 1, this.tile.towerInstance.getLevel());
                    crafting.sendProgressBarUpdate(this, 2, this.tile.towerInstance.getSpeed());
                    crafting.sendProgressBarUpdate(this, 3, this.tile.towerInstance.getRange());
                    crafting.sendProgressBarUpdate(this, 4, this.tile.towerInstance.getDamage());
                }
            }
        }

        this.prevActivated = activated;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0) {
            this.activated = value == 1;
        }
        else if (id == 1) {
            this.level = value;
        }
        else if (id == 2) {
            this.speed = value;
        }
        else if (id == 3) {
            this.range = value;
        }
        else if (id == 4) {
            this.damage = value;
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (this.tile.towerInstance == null) {
            ITower tower = this.tile.getTowerBlock().getTower(id);
            if (tower != null) {
                this.tile.towerInstance = tower.createNewInstance(this.tile);
                PacketHandler.resendTileInfo(this.tile);
                this.tile.onInventoryChanged();
            }
        }

        return true;
    }

}
