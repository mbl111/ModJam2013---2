
package net.specialattack.modjam.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerSpawner extends Container {

    public TileEntitySpawner tile;
    public boolean prevWaveActive;
    public boolean prevActive;
    public boolean prevCanWork;
    public boolean isMyName;
    public boolean canWork;

    public ContainerSpawner(TileEntitySpawner tile) {
        this.tile = tile;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);

        crafting.sendProgressBarUpdate(this, 0, this.tile.waveActive ? 1 : 0);
        crafting.sendProgressBarUpdate(this, 1, this.tile.active ? 1 : 0);
        if (crafting instanceof EntityPlayer) {
            crafting.sendProgressBarUpdate(this, 2, this.tile.active ? (this.tile.getActiveUser().equalsIgnoreCase(((EntityPlayer) crafting).username) ? 1 : 0) : 0);
        }

        crafting.sendProgressBarUpdate(this, 3, this.tile.canWork() ? 1 : 0);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean waveActive = this.tile.waveActive;
        boolean active = this.tile.active;
        boolean canWork = this.tile.canWork();

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting) this.crafters.get(i);

            if (this.prevWaveActive != waveActive) {
                crafting.sendProgressBarUpdate(this, 0, waveActive ? 1 : 0);
            }

            if (this.prevActive != active) {
                crafting.sendProgressBarUpdate(this, 1, active ? 1 : 0);

                if (crafting instanceof EntityPlayer) {
                    crafting.sendProgressBarUpdate(this, 2, this.tile.active ? (this.tile.getActiveUser().equalsIgnoreCase(((EntityPlayer) crafting).username) ? 1 : 0) : 0);
                }
            }

            if (this.prevCanWork != canWork) {
                crafting.sendProgressBarUpdate(this, 3, canWork ? 1 : 0);
            }
        }

        this.prevWaveActive = waveActive;
        this.prevActive = active;
        this.prevCanWork = active;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0) {
            this.tile.waveActive = value == 1;
        }
        else if (id == 1) {
            this.tile.active = value == 1;
        }
        else if (id == 2) {
            this.isMyName = value == 1;
        }
        else if (id == 3) {
            this.canWork = value == 1;
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == 0) {
            if (!this.tile.active) {
                this.tile.setActiveUser(player.username);
            }
            else {
                if (this.tile.getActiveUser().equalsIgnoreCase(player.username)) {
                    this.tile.setActiveUser(null);
                }
            }
        }
        else if (id == 1) {
            if (!this.tile.active) {
                this.tile.setActiveUser(player.username);
            }
            else {
                if (this.tile.getActiveUser().equalsIgnoreCase(player.username)) {
                    //Start the game now
                    this.tile.timer = 1200 - 20 * 3;
                }
            }
        }
        return true;
    }

}
