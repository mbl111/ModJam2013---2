package net.specialattack.towerdefence.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.specialattack.towerdefence.pathfinding.PathFinderCustom;
import net.specialattack.towerdefence.tileentity.TileEntityTarget;

public class EntityTargetLocation extends EntityAIBase {

    private EntityLiving entity;
    private double posX;
    private double posY;
    private double posZ;
    private double speed;
    private boolean running;
    private TileEntityTarget tile;

    public EntityTargetLocation(EntityLiving entity, Vec3 target, TileEntityTarget tile, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setMutexBits(1);

        this.posX = (double) target.xCoord + 0.5D;
        this.posY = (double) target.yCoord + 0.5D;
        this.posZ = (double) target.zCoord + 0.5D;

        this.tile = tile;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.running && this.entity.onGround) {
            this.running = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean continueExecuting() {
        return !this.entity.getNavigator().noPath();
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void startExecuting() {
        PathNavigate navigator = this.entity.getNavigator();
        PathEntity pathentity = PathFinderCustom.getEntityPathToXYZ(this.entity, this.entity.worldObj, MathHelper.floor_double(this.posX), (int) this.posY, MathHelper.floor_double(this.posZ), 128.0F, true, false, false, true);
        if (!navigator.setPath(pathentity, this.speed)) {
            this.running = false;
        }
    }

    @Override
    public void resetTask() {
        super.resetTask();

        double distanceX = this.entity.posX - this.posX;
        double distanceZ = this.entity.posZ - this.posZ;
        double distance = distanceX * distanceX + distanceZ * distanceZ;

        if (distance < this.entity.width * this.entity.width * 8) {
            if (this.tile == null || this.tile.isInvalid()) {
                // Fail silently
                this.entity.worldObj.removeEntity(this.entity);
                this.entity = null;
                this.tile = null;
            } else {
                this.tile.damage(1);
                this.entity.worldObj.removeEntity(this.entity);
                this.entity = null;
                this.tile = null;
            }
        } else {
            this.running = false;
        }
    }

}
