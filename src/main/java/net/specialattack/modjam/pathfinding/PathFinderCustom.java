
package net.specialattack.modjam.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PathFinderCustom extends PathFinder {

    private boolean isWoodenDoorAllowed;
    private boolean isMovementBlockAllowed;
    private boolean isPathingInWater;

    public PathFinderCustom(IBlockAccess world, boolean isWoodenDoorAllowed, boolean isMovementBlockAllowed, boolean isPathingInWater, boolean canEntityDrown) {
        super(world, isWoodenDoorAllowed, isMovementBlockAllowed, isPathingInWater, canEntityDrown);

        this.isWoodenDoorAllowed = isWoodenDoorAllowed;
        this.isMovementBlockAllowed = isMovementBlockAllowed;
        this.isPathingInWater = isPathingInWater;
    }

    @Override
    public int getVerticalOffset(Entity entity, int posX, int posY, int posZ, PathPoint point) {
        return calulateVerticalOffset(entity, posX, posY, posZ, point, this.isPathingInWater, this.isMovementBlockAllowed, this.isWoodenDoorAllowed);
    }

    public static int calulateVerticalOffset(Entity entity, int posX, int posY, int posZ, PathPoint point, boolean isPathingInWater, boolean isMovementBlockAllowed, boolean isWoodenDoorAllowed) {
        boolean offset = false;

        for (int x = posX; x < posX + point.xCoord; ++x) {
            for (int y = posY; y < posY + point.yCoord; ++y) {
                for (int z = posZ; z < posZ + point.zCoord; ++z) {
                    int blockId = entity.worldObj.getBlockId(x, y, z);

                    if (blockId > 0) {
                        if (blockId == Block.trapdoor.blockID) {
                            offset = true;
                        }
                        else if (blockId != Block.waterMoving.blockID && blockId != Block.waterStill.blockID) {
                            if (!isWoodenDoorAllowed && blockId == Block.doorWood.blockID) {
                                return 0;
                            }
                        }
                        else {
                            if (isPathingInWater) {
                                return -1;
                            }

                            offset = true;
                        }

                        Block block = Block.blocksList[blockId];
                        int renderType = block.getRenderType();

                        if (entity.worldObj.blockGetRenderType(x, y, z) == 9) {
                            int i2 = MathHelper.floor_double(entity.posX);
                            int j2 = MathHelper.floor_double(entity.posY);
                            int k2 = MathHelper.floor_double(entity.posZ);

                            if (entity.worldObj.blockGetRenderType(i2, j2, k2) != 9 && entity.worldObj.blockGetRenderType(i2, j2 - 1, k2) != 9) {
                                return -3;
                            }
                        }
                        else if (block instanceof IAvoided) {
                            return -2;
                        }
                        else if (!block.getBlocksMovement(entity.worldObj, x, y, z) && (!isMovementBlockAllowed || blockId != Block.doorWood.blockID)) {
                            if (renderType == 11 || blockId == Block.fenceGate.blockID || renderType == 32) {
                                return -3;
                            }

                            if (blockId == Block.trapdoor.blockID) {
                                return -4;
                            }

                            Material material = block.blockMaterial;

                            if (material != Material.lava) {
                                return 0;
                            }

                            if (!entity.handleLavaMovement()) {
                                return -2;
                            }
                        }
                    }
                    else {
                        blockId = entity.worldObj.getBlockId(x, y - 1, z);

                        if (blockId > 0) {
                            Block block = Block.blocksList[blockId];
                            if (block instanceof IAvoided) {
                                return -3;
                            }
                        }
                    }
                }
            }
        }

        return offset ? 2 : 1;
    }

    public static PathEntity getEntityPathToXYZ(Entity entity, World world, int posX, int posY, int posZ, float range, boolean canPassOpenWoodenDoors, boolean canPassClosedWoodenDoors, boolean avoidsWater, boolean canSwim) {
        int l = MathHelper.floor_double(entity.posX);
        int i1 = MathHelper.floor_double(entity.posY);
        int j1 = MathHelper.floor_double(entity.posZ);
        int k1 = (int) (range + 8.0F);
        int l1 = l - k1;
        int i2 = i1 - k1;
        int j2 = j1 - k1;
        int k2 = l + k1;
        int l2 = i1 + k1;
        int i3 = j1 + k1;
        ChunkCache chunkcache = new ChunkCache(world, l1, i2, j2, k2, l2, i3, 0);
        PathEntity pathentity = (new PathFinderCustom(chunkcache, canPassOpenWoodenDoors, canPassClosedWoodenDoors, avoidsWater, canSwim)).createEntityPathTo(entity, posX, posY, posZ, range);
        return pathentity;
    }

}
