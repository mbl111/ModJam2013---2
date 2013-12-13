
package net.specialattack.modjam;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Objects.MOD_ID, version = Objects.MOD_VERSION, name = Objects.MOD_NAME)
public class ModJam {

    @Instance
    public static ModJam instance;

    public Configuration config;

    @SidedProxy(clientSide = Objects.PROXY_CLIENT, serverSide = Objects.PROXY_COMMON)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        this.config = new Configuration(event.getSuggestedConfigurationFile());

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
