package net.specialattack.towerdefence.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.specialattack.towerdefence.packet.PacketHandler;
import net.specialattack.towerdefence.tileentity.TileEntityTower;
import net.specialattack.towerdefence.towers.ITower;

public class ContainerTower extends Container {

    // Client + Server side
    public TileEntityTower tile;
    public boolean prevActivated;
    // Client side
    public boolean activated;
    public boolean isMyName;
    public int level;
    public int speed;
    public int range;
    public int damage;
    public int speedLevel;
    public int rangeLevel;
    public int damageLevel;
    public int[] prices = new int[4];
    public boolean updated;

    public ContainerTower(TileEntityTower tile) {
        this.tile = tile;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);

        int state = this.tile.towerInstance != null ? 1 : 0;
        if (crafting instanceof EntityPlayer) {
            state |= this.tile.isUserPermitted(((EntityPlayer) crafting).username) ? 2 : 0;
        }

        crafting.sendProgressBarUpdate(this, 0, state);
        if (this.tile.towerInstance != null) {
            crafting.sendProgressBarUpdate(this, 1, this.tile.towerInstance.getLevel());
            crafting.sendProgressBarUpdate(this, 2, this.tile.towerInstance.getSpeed());
            crafting.sendProgressBarUpdate(this, 3, this.tile.towerInstance.getRange());
            crafting.sendProgressBarUpdate(this, 4, this.tile.towerInstance.getDamage());
            crafting.sendProgressBarUpdate(this, 5, this.tile.towerInstance.getDamageLevel());
            crafting.sendProgressBarUpdate(this, 6, this.tile.towerInstance.getSpeedLevel());
            crafting.sendProgressBarUpdate(this, 7, this.tile.towerInstance.getRangeLevel());
            crafting.sendProgressBarUpdate(this, 8, this.tile.towerInstance.getPriceUpgrade(0));
            crafting.sendProgressBarUpdate(this, 9, this.tile.towerInstance.getPriceUpgrade(1));
            crafting.sendProgressBarUpdate(this, 10, this.tile.towerInstance.getPriceUpgrade(2));
            crafting.sendProgressBarUpdate(this, 11, this.tile.towerInstance.getPriceUpgrade(3));
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean activated = this.tile.towerInstance != null;

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting) this.crafters.get(i);

            if (this.prevActivated != activated || this.updated) {
                int state = activated ? 1 : 0;
                if (crafting instanceof EntityPlayer) {
                    state |= this.tile.isUserPermitted(((EntityPlayer) crafting).username) ? 2 : 0;
                }

                crafting.sendProgressBarUpdate(this, 0, state);

                if (activated) {
                    crafting.sendProgressBarUpdate(this, 1, this.tile.towerInstance.getLevel());
                    crafting.sendProgressBarUpdate(this, 2, this.tile.towerInstance.getSpeed());
                    crafting.sendProgressBarUpdate(this, 3, this.tile.towerInstance.getRange());
                    crafting.sendProgressBarUpdate(this, 4, this.tile.towerInstance.getDamage());
                    crafting.sendProgressBarUpdate(this, 5, this.tile.towerInstance.getDamageLevel());
                    crafting.sendProgressBarUpdate(this, 6, this.tile.towerInstance.getSpeedLevel());
                    crafting.sendProgressBarUpdate(this, 7, this.tile.towerInstance.getRangeLevel());
                    crafting.sendProgressBarUpdate(this, 8, this.tile.towerInstance.getPriceUpgrade(0));
                    crafting.sendProgressBarUpdate(this, 9, this.tile.towerInstance.getPriceUpgrade(1));
                    crafting.sendProgressBarUpdate(this, 10, this.tile.towerInstance.getPriceUpgrade(2));
                    crafting.sendProgressBarUpdate(this, 11, this.tile.towerInstance.getPriceUpgrade(3));
                }
            }
        }

        this.updated = false;
        this.prevActivated = activated;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (this.tile.towerInstance == null) {
            ITower tower = this.tile.getTowerBlock().getTower(id);
            if (tower != null) {
                int cost = tower.getBuyPrice();
                if (this.tile.tryBuy(player.username, cost)) {
                    this.tile.towerInstance = tower.createNewInstance(this.tile);
                    PacketHandler.resendTileInfo(this.tile);
                    this.tile.onInventoryChanged();
                }
            }
        } else {
            if (this.tile.tryBuy(player.username, this.tile.towerInstance.getPriceUpgrade(id))) {
                this.tile.towerInstance.upgrade(id);
                PacketHandler.resendTileInfo(this.tile);
                this.tile.onInventoryChanged();
                this.updated = true;
            }
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0) {
            this.activated = (value & 0x1) == 1;
            this.isMyName = (value & 0x2) == 2;
        } else if (id == 1) {
            this.level = value;
        } else if (id == 2) {
            this.speed = value;
        } else if (id == 3) {
            this.range = value;
        } else if (id == 4) {
            this.damage = value;
        } else if (id == 5) {
            this.damageLevel = value;
        } else if (id == 6) {
            this.speedLevel = value;
        } else if (id == 7) {
            this.rangeLevel = value;
        } else if (id > 7 && id < 12) {
            this.prices[id - 8] = value;
        }
        this.updated = true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

}
