
package net.specialattack.modjam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.specialattack.modjam.blocks.BlockColoredAvoiding;
import net.specialattack.modjam.blocks.BlockSpawner;
import net.specialattack.modjam.blocks.BlockTower;
import net.specialattack.modjam.creativetab.CreativeTabModjam;
import net.specialattack.modjam.items.ItemBlockColoredAvoiding;
import net.specialattack.modjam.items.ItemBlockSpawner;
import net.specialattack.modjam.items.ItemBlockTower;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IConnectionHandler {

    public void preInit(FMLPreInitializationEvent event) {
        //Register Blocks
        Objects.blockTower = new BlockTower(Config.blockTowerId);
        GameRegistry.registerBlock(Objects.blockTower, ItemBlockTower.class, Objects.MOD_ID + ".blockTower");

        Objects.blockSpawner = new BlockSpawner(Config.blockSpawnerId);
        GameRegistry.registerBlock(Objects.blockSpawner, ItemBlockSpawner.class, Objects.MOD_ID + ".blockSpawner");

        Objects.blockClayAvoiding = new BlockColoredAvoiding(Config.blockClayAvoidingId, Material.rock);
        GameRegistry.registerBlock(Objects.blockClayAvoiding, ItemBlockColoredAvoiding.class, Objects.MOD_ID + ".blockClayAvoiding");

        NetworkRegistry.instance().registerConnectionHandler(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void init(FMLInitializationEvent event) {
        //Set block features, creative tabs, tile entity mappings

        Objects.creativeTab = new CreativeTabModjam(Assets.PREFIX + "-bleigh");
        Objects.creativeTab.setIconItemStack(new ItemStack(Objects.blockTower));

        Objects.blockTower.setCreativeTab(Objects.creativeTab).setUnlocalizedName(Assets.PREFIX + "-tower").setTextureName(Assets.PREFIX + ":tower").setHardness(1.0F).setResistance(100.0F).setStepSound(Block.soundAnvilFootstep);
        Objects.blockSpawner.setCreativeTab(Objects.creativeTab).setUnlocalizedName(Assets.PREFIX + "-spawner").setTextureName(Assets.PREFIX + ":spawner").setHardness(1.0F).setResistance(100.0F).setStepSound(Block.soundPowderFootstep);
        Objects.blockClayAvoiding.setCreativeTab(Objects.creativeTab).setUnlocalizedName(Assets.PREFIX + "-avoiding-clay").setTextureName(Assets.PREFIX + ":avoiding-clay").setHardness(1.25F).setResistance(100.0F).setStepSound(Block.soundStoneFootstep);

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

    public static HashMap<INetworkManager, String> players = new HashMap<INetworkManager, String>();
    public static ArrayList<String> playernames = new ArrayList<String>();

    public static boolean isPlayerLoggedIn(String playername) {
        return playernames.contains(playername);
    }

    @SuppressWarnings("rawtypes")
    public static EntityPlayer getPlayer(String playername) {
        List players = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList;
        for (Object obj : players) {
            if (obj instanceof EntityPlayer) {
                if (((EntityPlayer) obj).username.equalsIgnoreCase(playername)) {
                    return (EntityPlayer) obj;
                }
            }
        }

        return null;
    }

    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
        if (player instanceof EntityPlayer) {
            String username = ((EntityPlayer) player).username;
            players.put(manager, username);
            playernames.add(username);
        }
    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {}

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {}

    @Override
    public void connectionClosed(INetworkManager manager) {
        if (!players.containsKey(manager)) {
            System.err.println("Player changed network manager?!");
            return;
        }
        playernames.remove(players.remove(manager));
    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {}

}
