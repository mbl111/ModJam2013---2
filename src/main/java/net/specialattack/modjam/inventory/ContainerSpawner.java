
package net.specialattack.modjam.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.server.MinecraftServer;
import net.specialattack.modjam.CommonProxy;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerSpawner extends Container {

    // Client + Server side
    public TileEntitySpawner tile;
    // Server Side
    public boolean prevIsMultiplayer;
    public boolean prevActive;
    public boolean prevCanWork;
    // Client side
    public boolean active;
    public boolean isMyName;
    public boolean canWork;
    public boolean isMultiplayer;
    public boolean updated;
    public boolean isOp;
    public boolean canIJoin;

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

        boolean active = this.tile.getActiveUser() != null;
        crafting.sendProgressBarUpdate(this, 0, active ? 1 : 0);
        if (crafting instanceof EntityPlayer) {
            crafting.sendProgressBarUpdate(this, 1, active ? (this.tile.getActiveUser().equalsIgnoreCase(((EntityPlayer) crafting).username) ? 1 : 0) : 0);

            boolean isOp = MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(((EntityPlayer) crafting).username);
            crafting.sendProgressBarUpdate(this, 4, isOp ? 1 : 0);

            crafting.sendProgressBarUpdate(this, 5, !CommonProxy.isPlayerInGame(((EntityPlayer) crafting).username) ? 1 : 0);
        }
        crafting.sendProgressBarUpdate(this, 2, this.tile.canWork() ? 1 : 0);
        crafting.sendProgressBarUpdate(this, 3, this.tile.hasController() ? 1 : 0);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean active = this.tile.getActiveUser() != null;
        boolean canWork = this.tile.canWork();
        boolean isMultiplayer = this.tile.hasController();

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting) this.crafters.get(i);

            if (this.prevActive != active) {
                crafting.sendProgressBarUpdate(this, 0, active ? 1 : 0);

                if (crafting instanceof EntityPlayer) {
                    crafting.sendProgressBarUpdate(this, 1, active ? (this.tile.getActiveUser().equalsIgnoreCase(((EntityPlayer) crafting).username) ? 1 : 0) : 0);
                }
            }

            if (this.prevCanWork != canWork) {
                crafting.sendProgressBarUpdate(this, 2, canWork ? 1 : 0);
            }

            if (this.prevIsMultiplayer != isMultiplayer) {
                crafting.sendProgressBarUpdate(this, 3, isMultiplayer ? 1 : 0);
            }
        }

        this.prevIsMultiplayer = isMultiplayer;
        this.prevActive = active;
        this.prevCanWork = active;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0) {
            this.active = value == 1;
        }
        else if (id == 1) {
            this.isMyName = value == 1;
        }
        else if (id == 2) {
            this.canWork = value == 1;
        }
        else if (id == 3) {
            this.isMultiplayer = value == 1;
        }
        else if (id == 4) {
            this.isOp = value == 1;
        }
        else if (id == 5) {
            this.canIJoin = value == 1;
        }
        this.updated = true;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == 0) {
            if (this.tile.getActiveUser() == null) {
                this.tile.setActiveUser(player.username);
            }
            else {
                if (this.tile.getActiveUser().equalsIgnoreCase(player.username)) {
                    this.tile.setActiveUser(null);
                }
            }
        }
        else if (id == 1) {
            if (this.tile.getActiveUser() != null && this.tile.getActiveUser().equalsIgnoreCase(player.username)) {
                //Start the game now
                this.tile.timer = 600 - 20 * 3;
            }
        }
        else if (id == 2) {
            if (MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(player.username)) {
                this.tile.setActiveUser(null);
            }
        }
        return true;
    }

}
