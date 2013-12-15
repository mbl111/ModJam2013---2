
package net.specialattack.modjam.towers;

import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.specialattack.modjam.Objects;
import net.specialattack.modjam.packet.PacketHandler;
import net.specialattack.modjam.tileentity.TileEntitySpawner;
import net.specialattack.modjam.tileentity.TileEntityTower;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TowerArrow extends TowerBase {

    public TowerArrow(String identifier, int cost, int u, int v) {
        super(identifier, cost, u, v);
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

        public Instance(TileEntityTower tower, ITower type) {
            super(tower, type);
            this.level = 1;
            this.speed = 50;
            this.range = 10;
            this.damage = 2;
        }

        @Override
        public boolean tick() {
            List<EntityLiving> list = this.getTargetableEntities();

            boolean attacked = false;

            for (Object obj : list) {
                if (obj instanceof EntityLiving) {
                    EntityLiving entity = (EntityLiving) obj;

                    if (entity.attackEntityFrom(Objects.damageSourceTower, this.damage)) {
                        attacked = true;
                    }
                }
            }

            if (!attacked) {
                return false;
            }

            PacketHandler.sendToAllPlayersWatchingBlock(PacketHandler.createPacketSpawnParticles(this.tower, 0), this.tower.worldObj, this.tower.xCoord, this.tower.yCoord, this.tower.zCoord);

            return true;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void spawnParticles(int type) {
            Random rand = TileEntitySpawner.rand;
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
