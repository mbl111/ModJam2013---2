
package net.specialattack.modjam.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.specialattack.modjam.tileentity.TileEntityMultiplayerController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerMultiplayerController extends Container {

    // Client + Server side
    public TileEntityMultiplayerController tile;
    // Server Side
    public boolean prevActive;
    public int prevConnections;
    // Client side
    public boolean active;
    public boolean updated;
    public int connections;

    public ContainerMultiplayerController(TileEntityMultiplayerController tile) {
        this.tile = tile;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);

        crafting.sendProgressBarUpdate(this, 0, this.tile.active ? 1 : 0);
        crafting.sendProgressBarUpdate(this, 1, this.tile.spawners.size());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean active = this.tile.active;
        int connections = this.tile.spawners.size();

        for (int i = 0; i < this.crafters.size(); i++) {
            ICrafting crafting = (ICrafting) this.crafters.get(i);

            if (this.prevActive != active) {
                crafting.sendProgressBarUpdate(this, 0, active ? 1 : 0);
            }

            if (this.prevConnections != connections) {
                crafting.sendProgressBarUpdate(this, 1, connections);
            }
        }

        this.prevActive = active;
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
        this.updated = true;
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int id) {
        if (id == 0) {

        }
        return true;
    }

}
