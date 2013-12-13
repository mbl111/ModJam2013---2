package net.specialattack.modjam;

import net.specialattack.modjam.blocks.BlockTower;
import net.specialattack.modjam.creativetab.ModJamCreativeTab;
import net.specialattack.modjam.items.ItemBlockTower;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		//Register Blocks
		Objects.blockTower = new BlockTower(ModJam.blockTowerId.getInt());
		GameRegistry.registerBlock(Objects.blockTower, ItemBlockTower.class, Objects.MOD_ID + ".blockTower");
	}

	public void init(FMLInitializationEvent event) {
		//Set block features, creative tabs, tile entity mappings
		
		Objects.creativeTab = new ModJamCreativeTab("modjam-bleigh");
		
	}

	public void postInit(FMLPostInitializationEvent event) {
		//Recipes
	}
	
}
