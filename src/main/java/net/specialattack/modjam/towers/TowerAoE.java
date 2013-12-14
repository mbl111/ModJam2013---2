
package net.specialattack.modjam.towers;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.specialattack.modjam.Assets;
import net.specialattack.modjam.Objects;
import net.specialattack.modjam.tileentity.TileEntityTower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TowerAoE implements ITower {

    @Override
    public ITowerInstance createNewInstance(TileEntityTower tile) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIdentifier() {
        return "AoE";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ITowerRenderHandler getRenderHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    private Icon[] iconsTop;
    private Icon[] iconsBottom;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        this.iconsTop = new Icon[6];
        this.iconsBottom = new Icon[6];
        for (int i = 0; i < this.iconsTop.length; i++) {
            this.iconsTop[i] = register.registerIcon(Assets.DOMAIN + ":tower-aeo-top" + i);
        }
        for (int i = 0; i < this.iconsBottom.length; i++) {
            this.iconsBottom[i] = register.registerIcon(Assets.DOMAIN + ":tower-aeo-bottom" + i);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int side, boolean isTop) {
        return isTop ? this.iconsTop[side] : this.iconsBottom[side];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getIconLocation() {
        return Assets.SHEET_TOWERS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getIconU() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getIconV() {
        return 0;
    }

    public static class Instance implements ITowerInstance {

        public TileEntityTower tower;

        public Instance(TileEntityTower tower) {
            this.tower = tower;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            // TODO Auto-generated method stub

        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            // TODO Auto-generated method stub

        }

        @Override
        public ITower getTowerType() {
            return Objects.towerAoE;
        }

    }

}
