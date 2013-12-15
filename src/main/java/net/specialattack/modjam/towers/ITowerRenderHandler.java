
package net.specialattack.modjam.towers;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ITowerRenderHandler {

    @SideOnly(Side.CLIENT)
    void renderDynamic(TileEntity tile, double x, double y, double z, float partialTicks);

    @SideOnly(Side.CLIENT)
    boolean renderStatic(TileEntity tile, IBlockAccess world, int x, int y, int z, RenderBlocks renderer);

}
