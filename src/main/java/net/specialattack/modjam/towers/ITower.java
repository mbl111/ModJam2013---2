
package net.specialattack.modjam.towers;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.specialattack.modjam.tileentity.TileEntityTower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ITower {

    ITowerInstance createNewInstance(TileEntityTower tile);

    String getIdentifier();

    @SideOnly(Side.CLIENT)
    ITowerRenderHandler getRenderHandler();

    @SideOnly(Side.CLIENT)
    void registerIcons(IconRegister register);

    @SideOnly(Side.CLIENT)
    Icon getIcon(int side, boolean isTop);

    @SideOnly(Side.CLIENT)
    ResourceLocation getIconLocation();

    @SideOnly(Side.CLIENT)
    int getIconU();

    @SideOnly(Side.CLIENT)
    int getIconV();

    int getBuyPrice();

}
