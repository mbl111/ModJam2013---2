
package net.specialattack.modjam.towers;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
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
        return new Instance(tile);
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
        public int level;
        public int speed;

        public Instance(TileEntityTower tower) {
            this.tower = tower;
            this.level = 1;
            this.speed = 50;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            this.level = compound.getInteger("level");
            this.speed = compound.getInteger("speed");
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            compound.setInteger("level", level);
            compound.setInteger("speed", speed);
        }

        @Override
        public ITower getTowerType() {
            return Objects.towerAoE;
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        @Override
        public int getSpeed() {
            return this.speed;
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean tick() {
            double posX = this.tower.xCoord;
            double posY = this.tower.yCoord;
            double posZ = this.tower.zCoord;
            List list = this.tower.worldObj.selectEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D).expand(2.0D, 2.0D, 2.0D), IEntitySelector.selectAnything);

            if (list.isEmpty()) {
                return false;
            }

            for (Object obj : list) {
                if (obj instanceof EntityLiving) {
                    EntityLiving entity = (EntityLiving) obj;
                    entity.attackEntityFrom(Objects.damageSourceTower, level * 3.0F);
                }
            }

            return true;
        }

    }

}
