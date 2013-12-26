
package net.specialattack.towerdefence.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;
import net.specialattack.towerdefence.CommonProxy;
import net.specialattack.towerdefence.client.gui.GuiOverlay;
import net.specialattack.towerdefence.client.gui.container.GuiMultiplayerController;
import net.specialattack.towerdefence.client.gui.container.GuiSpawner;
import net.specialattack.towerdefence.client.gui.container.GuiTower;
import net.specialattack.towerdefence.client.render.tileentity.TileEntityTowerRenderer;
import net.specialattack.towerdefence.tileentity.TileEntityMultiplayerController;
import net.specialattack.towerdefence.tileentity.TileEntitySpawner;
import net.specialattack.towerdefence.tileentity.TileEntityTower;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
            else if (ID == 2) {
                if (tile != null && tile instanceof TileEntityMultiplayerController) {
                    return new GuiMultiplayerController((TileEntityMultiplayerController) tile);
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
