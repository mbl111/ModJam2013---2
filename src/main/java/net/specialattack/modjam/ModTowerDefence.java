
package net.specialattack.modjam;

import net.minecraftforge.common.Configuration;
import net.specialattack.modjam.packet.PacketHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = Objects.MOD_ID, name = Objects.MOD_NAME)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { Objects.MOD_CHANNEL }, packetHandler = PacketHandler.class)
public class ModTowerDefence {

    @Instance
    public static ModTowerDefence instance;

    @SidedProxy(clientSide = Objects.CLIENT_PROXY, serverSide = Objects.SERVER_PROXY)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModTowerDefence.proxy.preInit(event);

        Config.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModTowerDefence.proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModTowerDefence.proxy.postInit(event);
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        CommonProxy.players.clear();
    }

}
