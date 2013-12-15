
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
import net.specialattack.modjam.tileentity.TileEntityTower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TowerBase implements ITower {

    private int u;
    private int v;
    private String identifier;
    private int cost;

    public TowerBase(String identifier, int cost, int u, int v) {
        this.u = u;
        this.v = v;
        this.identifier = identifier;
        this.cost = cost;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int getBuyPrice() {
        return this.cost;
    }

    private Icon[] iconsTop;
    private Icon[] iconsBottom;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        this.iconsTop = new Icon[6];
        this.iconsBottom = new Icon[6];
        for (int i = 0; i < this.iconsTop.length; i++) {
            this.iconsTop[i] = register.registerIcon(Assets.DOMAIN + ":tower-" + this.getIdentifier() + "-top" + i);
        }
        for (int i = 0; i < this.iconsBottom.length; i++) {
            this.iconsBottom[i] = register.registerIcon(Assets.DOMAIN + ":tower-" + this.getIdentifier() + "-bottom" + i);
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
        return u;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getIconV() {
        return v;
    }

    public abstract static class Instance implements ITowerInstance {

        public TileEntityTower tower;
        public int level;
        public int speed;
        public int range;
        public int damage;
        public ITower type;

        public Instance(TileEntityTower tower, ITower type) {
            this.tower = tower;
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        public List<EntityLiving> getTargetableEntities() {
            double posX = this.tower.xCoord;
            double posY = this.tower.yCoord;
            double posZ = this.tower.zCoord;
            List<EntityLiving> list = this.tower.worldObj.selectEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(posX, posY, posZ, posX + 1.0D, posY + 1.0D, posZ + 1.0D).expand(2.0D, 2.0D, 2.0D), IEntitySelector.selectAnything);

            return list;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            this.level = compound.getInteger("level");
            this.speed = compound.getInteger("speed");
            this.range = compound.getInteger("range");
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            compound.setInteger("level", this.level);
            compound.setInteger("speed", this.speed);
            compound.setInteger("range", this.range);
        }

        @Override
        public ITower getTowerType() {
            return type;
        }

        @Override
        public int getLevel() {
            return this.level;
        }

        @Override
        public int getSpeed() {
            return this.speed;
        }

        @Override
        public int getRange() {
            return this.range;
        }

        @Override
        public int getDamage() {
            return this.damage;
        }

    }

}
