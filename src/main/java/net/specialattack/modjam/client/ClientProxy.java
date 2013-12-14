
package net.specialattack.modjam.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;
import net.specialattack.modjam.CommonProxy;
import net.specialattack.modjam.client.gui.GuiOverlay;
import net.specialattack.modjam.client.gui.container.GuiSpawner;
import net.specialattack.modjam.client.gui.container.GuiTower;
import net.specialattack.modjam.client.render.timeentity.TileEntityTowerRenderer;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
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

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        try {
            TileEntity tile = world.getBlockTileEntity(x, y, z);

            if (ID == 0) {
                if (tile != null && tile instanceof TileEntitySpawner) {
                    return new GuiSpawner((TileEntitySpawner) tile);
                }
            }
            else if (ID == 1) {
                if (tile != null && tile instanceof TileEntityTower) {
                    return new GuiTower((TileEntityTower) tile);
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @ForgeSubscribe(receiveCanceled = true)
    public void renderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.type == ElementType.PORTAL) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution resolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
            GuiOverlay.instance.drawScreen(mc, resolution);
        }
    }
}
