
package net.specialattack.modjam;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
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

        Objects.creativeTab = new CreativeTabModjam(Assets.PREFIX + "-bleigh");
        Objects.creativeTab.setIconItemStack(new ItemStack(Objects.blockTower));

        Objects.blockTower.setUnlocalizedName(Assets.PREFIX + "-tower").setTextureName(Assets.PREFIX + ":tower").setHardness(1.0F).setCreativeTab(Objects.creativeTab).setStepSound(Block.soundAnvilFootstep);
        Objects.blockSpawner.setUnlocalizedName(Assets.PREFIX + "-spawner").setTextureName(Assets.PREFIX + ":spawner").setHardness(1.0F).setCreativeTab(Objects.creativeTab).setStepSound(Block.soundPowderFootstep);

        TileEntity.addMapping(TileEntitySpawner.class, "Modjam3-Spawner");
    }

    public void postInit(FMLPostInitializationEvent event) {
        //Recipes

        //Spinning turret
        //CIRCUIT		BASE	BARREL (Weapon Barrel)
        //PLATE			SERVO	PLATE
        //IRONBAR		BASE	IRONBAR

        //Barrels of different types for different attack types...
        //Generic - Shoots projectiles at a steady pace - Iron
        //Spread - Shoots a spread of projectiles - 
        //HighSpeed - Rapid fire projectiles - Gold
        //Explosive - Creates an explosion on impact only damaging entities - TNT
        //High Damage - Slow fire rate, high damage - 

        //Barrel
        //AIR	SPECIAL	SPECIAL
        //IRON	AIR		AIR
        //AIR	SPECIAL	SPECIAL

        //Different plates give the towers different strengths. Stone, Iron, Diamond

        //The circuit can either be a basic one that would trigger the tower on a redstone pulse.
        //Perhaps two redstone inputs if there is a servo and the value from 0-15 controls the towers rotation

        //A Smart Circuit would have features where the tower would shoot the nearest entity. The tower would require a
        //redstone signal to be activated. Can be configured to either shoot hostile mobs, friendly mobs, players or any 
        //combo of those

        //A base would just be a generic block, made using iron and stone possibly.

        //Spinning turrets have a servo in the middle. Static turrets would just have another base block, the tower would stay pointed where you place it. 
        //Players could orient the tower using some device (Such as in DiscoTek).

        //Spinning towers could perhaps have an origin point, that could be set using the orienter. 
        //The redstone signal (if a basic curcuit is used) would have that rotation as the origin.

    }

}
