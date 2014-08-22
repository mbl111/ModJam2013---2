package net.specialattack.towerdefence.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.server.MinecraftServer;
import net.specialattack.towerdefence.tileentity.TileEntityMultiplayerController;
import net.specialattack.towerdefence.tileentity.TileEntitySpawner;

import java.util.List;

public class ContainerMultiplayerController extends Container {

    // Client + Server side
    public TileEntityMultiplayerController tile;
    // Server Side
    public boolean prevActive;
    public int prevConnections;
    public int prevActiveConnections;
    // Client side
    public boolean active;
    public boolean updated;
    public int connections;
    public int activeConnections;
    public boolean isOp;

    public ContainerMultiplayerController(TileEntityMultiplayerController tile) {
        this.tile = tile;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);

        crafting.sendProgressBarUpdate(this, 0, this.tile.active ? 1 : 0);
        crafting.sendProgressBarUpdate(this, 1, this.tile.getSpawnersCount());
        crafting.sendProgressBarUpdate(this, 2, this.tile.getActiveSpawnersCount());

        if (crafting instanceof EntityPlayer) {
            boolean isOp = MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(((EntityPlayer) crafting).username);
            crafting.sendProgressBarUpdate(this, 3, isOp ? 1 : 0);
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean active = this.tile.active;
        int connections = this.tile.getSpawnersCount();
        int activeConnections = this.tile.getActiveSpawnersCount();

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting) this.crafters.get(i);

            if (this.prevActive != active) {
                crafting.sendProgressBarUpdate(this, 0, active ? 1 : 0);
            }

            if (this.prevConnections != connections) {
                crafting.sendProgressBarUpdate(this, 1, connections);
            }

            if (this.prevActiveConnections != activeConnections) {
                crafting.sendProgressBarUpdate(this, 2, activeConnections);
            }
        }

        this.prevActive = active;
        this.prevConnections = connections;
        this.prevActiveConnections = activeConnections;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == 0) {
            if (this.tile.active) {
                this.tile.tryStop();
            } else {
                if (this.tile.getActiveSpawnersCount() >= 1) {
                    this.tile.tryStart();
                }
            }
        } else if (id == 1) {
            if (MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(player.username)) {
                List<TileEntitySpawner> spawners = this.tile.getAllSpawners();
                for (TileEntitySpawner spawner : spawners) {
                    spawner.setActiveUser(null);
                }
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value) {
        if (id == 0) {
            this.active = value == 1;
        }
        if (id == 1) {
            this.connections = value;
        }
        if (id == 2) {
            this.activeConnections = value;
        }
        if (id == 3) {
            this.isOp = value == 1;
        }
        this.updated = true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }
}
