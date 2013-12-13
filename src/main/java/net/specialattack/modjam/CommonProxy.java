
package net.specialattack.modjam;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.specialattack.modjam.blocks.BlockSpawner;
import net.specialattack.modjam.blocks.BlockTower;
import net.specialattack.modjam.creativetab.CreativeTabModjam;
import net.specialattack.modjam.items.ItemBlockSpawner;
import net.specialattack.modjam.items.ItemBlockTower;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        //Register Blocks
        Objects.blockTower = new BlockTower(Config.blockTowerId);
        GameRegistry.registerBlock(Objects.blockTower, ItemBlockTower.class, Objects.MOD_ID + ".blockTower");

        Objects.blockSpawner = new BlockSpawner(Config.blockSpawnerId);
        GameRegistry.registerBlock(Objects.blockSpawner, ItemBlockSpawner.class, Objects.MOD_ID + ".blockSpawner");
    }

    public void init(FMLInitializationEvent event) {
        //Set block features, creative tabs, tile entity mappings

        Objects.creativeTab = new CreativeTabModjam("modjam-bleigh");

        Objects.blockTower.setUnlocalizedName(Assets.PREFIX + "-tower").setTextureName(Assets.PREFIX + ":tower").setHardness(1.0F).setCreativeTab(Objects.creativeTab).setStepSound(Block.soundAnvilFootstep);
        Objects.blockSpawner.setUnlocalizedName(Assets.PREFIX + "-spawner").setTextureName(Assets.PREFIX + ":spawner").setHardness(1.0F).setCreativeTab(Objects.creativeTab).setStepSound(Block.soundPowderFootstep);

        TileEntity.addMapping(TileEntitySpawner.class, "Modjam3-Spawner");
    }

    public void postInit(FMLPostInitializationEvent event) {
        //Recipes
    }

}
