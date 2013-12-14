
package net.specialattack.modjam.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.specialattack.modjam.pathfinding.PathFinderCustom;

public class EntityTargetLocation extends EntityAIBase {

    private EntityLiving entity;
    private double posX;
    private double posY;
    private double posZ;
    private double speed;
    private boolean running;

    public EntityTargetLocation(EntityLiving entity, Vec3 target, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setMutexBits(1);

        this.posX = target.xCoord;
        this.posY = target.yCoord;
        this.posZ = target.zCoord;
    }

    @Override
    public boolean continueExecuting() {
        return !this.entity.getNavigator().noPath();
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

        //running = false;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

}
