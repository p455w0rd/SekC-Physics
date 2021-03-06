package com.sekwah.sekcphysics.ragdoll.parts;

import com.sekwah.sekcphysics.SekCPhysics;
import com.sekwah.sekcphysics.cliententity.EntityRagdoll;
import com.sekwah.sekcphysics.ragdoll.PointD;
import com.sekwah.sekcphysics.ragdoll.Ragdolls;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by sekawh on 8/5/2015.
 */
public class SkeletonPoint {

    private final float size;

    public double posX;
    public double posY;
    public double posZ;

    public double lastPosX;
    public double lastPosY;
    public double lastPosZ;

    public double newPosX;
    public double newPosY;
    public double newPosZ;

    // Push force multiplier
    public float pushability = 1;


    // basically distance from last point, just sounds nicer for when other forces interact with it or if you wanna set it
    //  from an explosion
    public double velX = 0;
    public double velY = 0;
    public double velZ = 0;

    private boolean onGround = false;

    /**
     * this will set positions to scale in terms of the model, also y coordinates on models are negative so reverse it manually
     */
    public SkeletonPoint(double x, double y, double z, float size){
        this(x,y,z,size,true);
    }

    public SkeletonPoint(double x, double y, double z, boolean shouldDoModelScale){
        this(x,y,z,0.15f,shouldDoModelScale);
    }

    public SkeletonPoint(double x, double y, double z){
        this(x,y,z,0.15f,true);
    }

    // note the position is in blocks not the model locations, and every 1 block is split into 16 for the model positions(i think)
    public SkeletonPoint(double x, double y, double z, float size, boolean shouldDoModelScale){
        this.setPosition(x,y,z);

        // Added to stop ragdolls becoming lines or acting in only 1 plane after hitting a wall
        float sizeRandom = (float) Math.random();
        float maxOffset = 0.001f;
        size += -maxOffset + maxOffset * 2f * sizeRandom;

        this.size = size;

        if(shouldDoModelScale){
            shiftPositionToModelScale();
        }
    }

    public void shiftPositionToModelScale(){
        this.setPosition(this.posX / 16f, this.posY / 16f, this.posZ / 16f);
    }

    public void shiftPositionToWorldScale(){
        this.setPosition(this.posX * 16f, this.posY * 16f, this.posZ * 16f);
    }

    public void setPosition(double x, double y, double z){
        this.lastPosX = this.newPosX = this.posX = x;
        this.lastPosY = this.newPosY = this.posY = y;
        this.lastPosZ = this.newPosZ = this.posZ = z;
    }

    public void movePoint(EntityRagdoll entity, double moveX, double moveY, double moveZ) {
        /*this.posX += moveX;
        this.posY += moveY;
        this.posZ += moveZ;*/

        /*if(posY < -24f / 16f){
            posY = -24f / 16f;
        }*/

        double pointPosX = entity.posX + this.posX;
        double pointPosY = entity.posY + this.posY;
        double pointPosZ = entity.posZ + this.posZ;

        AxisAlignedBB axisalignedbb = new AxisAlignedBB(pointPosX - size, pointPosY - size, pointPosZ - size,
                pointPosX + size, pointPosY + size, pointPosZ + size);

        //axisalignedbb.offset(this.posX, this.posY, this.posZ);

        List<AxisAlignedBB> list = entity.worldObj.getCollisionBoxes(entity, axisalignedbb.addCoord(moveX, moveY, moveZ));

        double oMoveY = moveY;

        for (int k = 0; k < list.size(); ++k)
        {
            moveY = list.get(k).calculateYOffset(axisalignedbb, moveY);
        }

        if(oMoveY < 0 && moveY != oMoveY){
            onGround = true;
        }

        axisalignedbb = axisalignedbb.offset(0.0D, moveY, 0.0D);

        for (int k = 0; k < list.size(); ++k)
        {
            moveX = list.get(k).calculateXOffset(axisalignedbb, moveX);
        }

        axisalignedbb = axisalignedbb.offset(moveX, 0.0D, 0.0D);

        for (int k = 0; k < list.size(); ++k)
        {
            moveZ = list.get(k).calculateZOffset(axisalignedbb, moveZ);
        }

        axisalignedbb = axisalignedbb.offset(0.0D, 0.0D, moveZ);

        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D - entity.posX;
        this.posY = (axisalignedbb.minY + axisalignedbb.maxY) / 2.0D - entity.posY;
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D - entity.posZ;

        /*System.out.println(this.posX);*/

        // TODO add collision checks for stuff, also try to animate ragdolls sliding between ticks or update each tick.
        // First get the physics done


       /* double d3 = this.posX;
        double d4 = this.posY;
        double d5 = this.posZ;

        AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(this.posX - size, this.posY - size,this.posZ - size, this.posX + size, this.posY + size,this.posZ + size);
*/


    }

    public void update(EntityRagdoll entity) {
        this.velX = this.posX - this.lastPosX;
        this.velY = this.posY - this.lastPosY;
        this.velZ = this.posZ - this.lastPosZ;

        float speedMulti = 0.9999f;

        this.velY *= speedMulti;

        if(onGround){

            float groundMulti = 0.93f;
            this.velX *= groundMulti;
            this.velZ *= groundMulti;
        }
        else{
            this.velX *= speedMulti;
            this.velZ *= speedMulti;
        }


        this.onGround = false;

        this.lastPosX = this.posX;
        this.lastPosY = this.posY;
        this.lastPosZ = this.posZ;

        double pointPosX = entity.posX + this.posX;
        double pointPosY = entity.posY + this.posY;
        double pointPosZ = entity.posZ + this.posZ;

        AxisAlignedBB axisalignedbb = new AxisAlignedBB(pointPosX - size, pointPosY - size, pointPosZ - size,
                pointPosX + size, pointPosY + size, pointPosZ + size);

        // TODO add code to properly do water velocity
        if (entity.worldObj.handleMaterialAcceleration(axisalignedbb.expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D), Material.WATER, entity)){
            this.addVelocity(0, 0.1f, 0);
            if(this.posY - this.lastPosY > 0.5){
                this.lastPosY = this.posY - 0.5;
            }
            //entity.setVelocity(0,0,0);
        }

        this.updateCollisions(entity);

        this.movePoint(entity, this.velX, this.velY - Ragdolls.gravity, this.velZ);

        //next_old_position = position             // This position is the next frame's old_position
       // position += position - old_position;     // Verlet integration
        //position += gravity;                     // gravity == (0,-0.01,0)

        this.newPosX = this.posX;
        this.newPosY = this.posY;
        this.newPosZ = this.posZ;
    }

    // Wont push other entities but make it get pushed by others.
    private void updateCollisions(EntityRagdoll entity) {
        double pointPosX = entity.posX + this.posX;
        double pointPosY = entity.posY + this.posY;
        double pointPosZ = entity.posZ + this.posZ;

        AxisAlignedBB axisalignedbb = new AxisAlignedBB(pointPosX - size, pointPosY - size, pointPosZ - size,
                pointPosX + size, pointPosY + size, pointPosZ + size);

        List list = entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, axisalignedbb.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

        if (list != null && !list.isEmpty())
        {
            for (int i = 0; i < list.size(); ++i)
            {
                Entity entityCol = (Entity)list.get(i);

                // Cant directly detect if in ground sadly. Try stuff like arrows though ;3
                if(entityCol.canBePushed()/* || (entityCol instanceof EntityArrow && entityCol.motionY != 0)*/){
                    collideWithEntity(entity, entityCol);
                }
            }
        }
    }

    private void collideWithEntity(EntityRagdoll entity, Entity entityCol) {
        double pointPosX = entity.posX + this.posX;
        double pointPosZ = entity.posZ + this.posZ;
        double d0 = pointPosX - entityCol.posX;
        double d1 = pointPosZ - entityCol.posZ;
        double d2 = MathHelper.abs_max(d0, d1);

        if (d2 >= 0.009999999776482582D)
        {
            d2 = (double)MathHelper.sqrt_double(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0D / d2;

            if (d3 > 1.0D)
            {
                d3 = 1.0D;
            }

            d0 *= d3;
            d1 *= d3;
            d0 *= 0.05000000074505806D;
            d1 *= 0.05000000074505806D;
            d0 *= (double)(1.0F - entityCol.entityCollisionReduction);
            d1 *= (double)(1.0F - entityCol.entityCollisionReduction);
            SekCPhysics.logger.info(entityCol.motionX);
            //entityCol.addVelocity(-d0, 0.0D, -d1);
            this.addVelocity(d0 + entityCol.motionX, 0.0D, d1 + entityCol.motionZ);
        }
    }

    public void setNewPos(double x, double y, double z){
        this.newPosX = x;
        this.newPosY = y;
        this.newPosZ = z;
    }

    public void updatePos(EntityRagdoll entity){
        moveTo(entity, this.newPosX, this.newPosY, this.newPosZ);
    }

    /*private boolean moveInWater(World worldObj, AxisAlignedBB boundingBox, Material material, EntityRagdoll entity) {
        int i = MathHelper.floor_double(boundingBox.minX);
        int j = MathHelper.floor_double(boundingBox.maxX + 1.0D);
        int k = MathHelper.floor_double(boundingBox.minY);
        int l = MathHelper.floor_double(boundingBox.maxY + 1.0D);
        int i1 = MathHelper.floor_double(boundingBox.minZ);
        int j1 = MathHelper.floor_double(boundingBox.maxZ + 1.0D);

        if (!worldObj.checkChunksExist(i, k, i1, j, l, j1))
        {
            return false;
        }
        else
        {
            boolean flag = false;
            Vec3 vec3 = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

            for (int k1 = i; k1 < j; ++k1)
            {
                for (int l1 = k; l1 < l; ++l1)
                {
                    for (int i2 = i1; i2 < j1; ++i2)
                    {
                        Block block = worldObj.getBlock(k1, l1, i2);

                        if (block.getMaterial() == material)
                        {
                            double d0 = (double)((float)(l1 + 1) - BlockLiquid.getLiquidHeightPercent(worldObj.getBlockMetadata(k1, l1, i2)));

                            if ((double)l >= d0)
                            {
                                flag = true;
                                block.velocityToAddToEntity(worldObj, k1, l1, i2, entity, vec3);
                            }
                        }
                    }
                }
            }

            if (vec3.lengthVector() > 0.0D)
            {
                vec3 = vec3.normalize();
                double d1 = 0.014D;
                this.addVelocity(vec3.xCoord * d1, vec3.yCoord * d1,vec3.zCoord * d1);
            }

            return flag;
        }
    }*/

    public void moveTo(EntityRagdoll entity, double x, double y, double z) {

        this.movePoint(entity, x - this.posX, y - this.posY, z - this.posZ);

        /*this.posX = x;
        this.posY = y;
        this.posZ = z;*/

    }


    public PointD toPoint() {
        return new PointD(this.posX, this.posY, this.posZ);
    }

    public void verify(EntityRagdoll entity) {
        double tempPosX = this.posX;
        double tempPosY = this.posY;
        double tempPosZ = this.posZ;

        this.posX = 0;
        this.posY = 0;
        this.posZ = 0;

        this.moveTo(entity, tempPosX, tempPosY, tempPosZ);

        this.lastPosX = this.posX;
        this.lastPosY = this.posY;
        this.lastPosZ = this.posZ;
    }

    public void shiftPosition(double x, double y, double z) {
        this.posX += x;
        this.posY += y;
        this.posZ += z;

        this.lastPosX += x;
        this.lastPosY += y;
        this.lastPosZ += z;
    }

    public void setVelocity(double motionX, double motionY, double motionZ) {
        this.lastPosX = this.posX - motionX;
        this.lastPosY = this.posY - motionY;
        this.lastPosZ = this.posZ - motionZ;
    }

    public void addVelocity(double motionX, double motionY, double motionZ) {
        this.lastPosX -= motionX;
        this.lastPosY -= motionY;
        this.lastPosZ -= motionZ;
    }
}
