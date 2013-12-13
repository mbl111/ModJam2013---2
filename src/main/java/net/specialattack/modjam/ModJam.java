package net.specialattack.modjam;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Objects.MOD_ID, version = Objects.MOD_VERSION, name=Objects.MOD_NAME)
public class ModJam {

	@Instance
	public static ModJam instance;

	public Configuration config;
	
	@SidedProxy(clientSide=Objects.PROXY_CLIENT, serverSide=Objects.PROXY_COMMON)
	public static CommonProxy proxy;

	public static Property blockTowerId;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.config = new Configuration(event.getSuggestedConfigurationFile());
		blockTowerId = this.config.get("ids", "BlockTowerid", 1337);

		proxy.preInit(event);
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
