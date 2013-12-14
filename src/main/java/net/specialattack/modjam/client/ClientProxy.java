
package net.specialattack.modjam.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.specialattack.modjam.CommonProxy;
import net.specialattack.modjam.client.gui.container.GuiSpawner;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        //Init blocks
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        //Init renderers
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        try {
            TileEntity tile = world.getBlockTileEntity(x, y, z);

            if (ID == 0) {
                if (tile != null && tile instanceof TileEntitySpawner) {
                    return new GuiSpawner((TileEntitySpawner) tile);
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
