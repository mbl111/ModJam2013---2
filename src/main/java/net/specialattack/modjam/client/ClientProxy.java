
package net.specialattack.modjam.client;

import net.specialattack.modjam.CommonProxy;
import net.specialattack.modjam.Objects;
import net.specialattack.modjam.client.render.timeentity.TileEntityTowerRenderer;
import net.specialattack.modjam.tileentity.TileEntityTower;
import cpw.mods.fml.client.registry.ClientRegistry;
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
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTower.class, new TileEntityTowerRenderer());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

        //Init renderers
    }

}
