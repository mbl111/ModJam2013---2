
package net.specialattack.modjam.towers;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.specialattack.modjam.CommonProxy;
import net.specialattack.modjam.packet.PacketHandler;
import net.specialattack.modjam.tileentity.TileEntityTower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TowerArrow extends TowerBase {

    public TowerArrow(String identifier, int id, int cost, int u, int v) {
        super(identifier, id, cost, u, v);
    }

    @Override
    public ITowerInstance createNewInstance(TileEntityTower tile) {
        return new Instance(tile, this);
    }

    @SideOnly(Side.CLIENT)
    private ITowerRenderHandler renderHandler;

    @Override
    @SideOnly(Side.CLIENT)
    public ITowerRenderHandler getRenderHandler() {
        if (this.renderHandler == null) {
            this.renderHandler = new TowerRenderer();
        }
        return this.renderHandler;
    }

    public static class Instance extends TowerBase.Instance {

        private EntityLiving target;

        public Instance(TileEntityTower tower, ITower type) {
            super(tower, type);
        }

        @Override
        public int getPriceUpgrade(int id) {
            switch (id) {
            case 0:
                if (this.level >= 10) {
                    return -1;
                }
                return 60 + 20 * this.level;
            case 1:
                if (this.level <= this.damage || this.damage >= 10) {
                    return -1;
                }
                return 60 + 70 * this.damage * this.damage - 20 * (this.level - this.damage);
            case 2:
                if (this.level <= this.speed || this.speed >= 7) {
                    return -1;
                }
                return 50 + 50 * this.speed * this.speed - 10 * (this.level - this.speed);
            case 3:
                if (this.level <= this.range || this.range >= 5) {
                    return -1;
                }
                return 45 + 50 * this.range * this.range - 20 * (this.level - this.range);
            }
            return -1;
        }

        @Override
        public int getSpeed() {
            return (int) (40.0F - (30.0F / (6 - this.speed)));
        }

        @Override
        public int getRange() {
            return 2 + this.range * 2;
        }

        @Override
        public int getDamage() {
            return (int) (1.9F * this.damage);
        }

        @Override
        public boolean tick() {
            List<EntityLiving> list = this.getTargetableEntities();

            if (list.isEmpty()) {
                this.target = null;
                return false;
            }

            if (this.target != null && !list.contains(this.target)) {
                this.target = null;
            }

            if (this.target == null) {
                // Select the closest entity to attack
                double distance = 0.0D;
                boolean first = true;
                for (EntityLiving living : list) {
                    double currentDistance = (living.posX - this.tower.xCoord) * (living.posX - this.tower.xCoord);
                    currentDistance += (living.posY - this.tower.yCoord) * (living.posY - this.tower.yCoord);
                    currentDistance += (living.posZ - this.tower.zCoord) * (living.posZ - this.tower.zCoord);

                    if (first) {
                        distance = currentDistance;
                        this.target = living;
                        first = false;
                    }
                    else {
                        if (currentDistance < distance) {
                            distance = currentDistance;
                            this.target = living;
                        }
                    }
                }
            }

            EntityArrow arrow = new EntityArrow(this.tower.worldObj);

            arrow.renderDistanceWeight = 10.0D;
            arrow.posY = this.tower.yCoord + 1.5D;
            double distanceX = this.target.posX - this.tower.xCoord;
            double distanceY = this.target.boundingBox.minY + this.target.height / 3.0F - arrow.posY;
            double distanceZ = this.target.posZ - this.tower.zCoord;
            double distance = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);

            if (distance >= 1.0E-7D) {
                float yaw = (float) (Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
                float pitch = (float) (-(Math.atan2(distanceY, distance) * 180.0D / Math.PI));
                double offsetX = distanceX / distance;
                double offsetZ = distanceZ / distance;
                arrow.setLocationAndAngles(this.tower.xCoord + offsetX, arrow.posY, this.tower.zCoord + offsetZ, yaw, pitch);
                arrow.yOffset = 0.0F;
                float boostY = (float) distance * 0.2F;
                arrow.setThrowableHeading(distanceX, distanceY + boostY, distanceZ, 1.6F, 1.0F);
            }

            arrow.setDamage(this.getDamage());
            arrow.setKnockbackStrength(0);

            this.tower.worldObj.spawnEntityInWorld(arrow);

            PacketHandler.sendToAllPlayersWatchingBlock(PacketHandler.createPacketSpawnParticles(this.tower, 0), this.tower.worldObj, this.tower.xCoord, this.tower.yCoord, this.tower.zCoord);

            return true;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void spawnParticles(int type) {
            Random rand = CommonProxy.rand;
            for (int i = 0; i < 10; i++) {
                this.tower.worldObj.spawnParticle("reddust", this.tower.xCoord + rand.nextDouble(), this.tower.yCoord + 2.0D + rand.nextDouble(), this.tower.zCoord + rand.nextDouble(), 1.0D, 0.0D, 0.0D);
            }
        }

    }

    @SideOnly(Side.CLIENT)
    public static class TowerRenderer implements ITowerRenderHandler {

        @Override
        @SideOnly(Side.CLIENT)
        public void renderDynamic(TileEntity tile, double x, double y, double z, float partialTicks) {
            // Render dynamic stuff
        }

        @Override
        @SideOnly(Side.CLIENT)
        public boolean renderStatic(TileEntity tile, boolean isTop, IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
            if (isTop) {
                // Render top block
            }
            else {
                // Render bottom block
            }
            return true;
        }

    }

}
