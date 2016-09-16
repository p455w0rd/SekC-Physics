package com.sekwah.sekcphysics.ragdoll.parts;

import com.sekwah.sekcphysics.cliententity.EntityRagdoll;
import com.sekwah.sekcphysics.ragdoll.PointD;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by sekawh on 8/4/2015.
 *
 * // TODO add some detection to contstraints for when they are massively disshaped or something similar and try reversing it
 *  sometimes, should fix legs crossing over glitch but could be made worse if the entity is squashed for whatever reason.
 *  just add some tests to see if they work.
 *
 */
public class Skeleton {

    //public SkeletonPoint[] points;

    public List<SkeletonPoint> points = new ArrayList<SkeletonPoint>();

    //public Constraint[] constraints;

    public List<Constraint> constraints = new ArrayList<Constraint>();

    //public Triangle[] triangles;

    public List<Triangle> triangles = new ArrayList<Triangle>();

    private boolean frozen = false;

    // Store a velocity which is the last position take away the current position but also add it so you can add velocity
    //  because if its added for a single update itll carry on that motion. So stuff like explosions or an arrow to the
    //  knee. Also if a player walks around a ragdoll you can add sorta a magnetic push for parts near entities away from the player.
    //  but do that after all physics works :3 (also for collisions with blocks, use the moving points like arrows do and stuff
    //  try to use current mc stuff to figure out where it can and cant go.

    public Skeleton(){

    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean isFrozen) {
        this.frozen = isFrozen;
    }

    /**
     * Applies the physics before constraints are taken into account.
     * @param entity
     */
    public void update(EntityRagdoll entity){

        for(SkeletonPoint point : points){
            point.update(entity);
            //point.movePoint(entity);
        }

        // oldUpdate constraints
        for(int i = 0; i <= entity.ragdoll.getPasses(); i++){
            for(Constraint constraint: constraints){
                constraint.calc(entity);
                //point.movePoint(entity);
            }
        }

        for(SkeletonPoint point : points){
            point.updatePos(entity);
            //point.movePoint(entity);
        }

        // For finding the angle from the said norm use the dot product rearranged but base it on the angle between the reversed version
        //  of the vector rather than 2 vectors. (for when a triangle wouldnt work or yould have to add too many points to make it work :D)
        // formula 1  a � b = |a| � |b| � cos(?)
        // formula 2  a � b = ax � bx + ay � by + az � bz


    }

    /**
     * Stops points being too close or far from each other
     */
    public void updateLengthConstraints(){

    }

    /**
     * Moves the points if they are outside of the rotation bounds, we cant have a leg pushing into your torso.
     */
    public void updateRotationConstraints(){

    }

    /**
     * Renders all the constraints as lines, also maybe add the linked triangles next. Create a skleton for the
     */
    public void renderSkeletonDebug(){
        glDisable(GL_CULL_FACE);
        glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        for(Triangle triangle : triangles){
            glColor4f(0.0f, 0.8f, 0.1f, 0.5f);
            drawTriangle(triangle.points[0], triangle.points[1], triangle.points[2]);
            glColor3f(1f,1f,1f);
        }
        glEnable(GL_CULL_FACE);
        for(Constraint constraint : constraints){
            // getBrightness(float p_70013_1_) from entity
            glColor4f(0.0f, 1.0f, 0.2f, 1.0f);
            drawLine(constraint.end[0], constraint.end[1]);
            glColor4f(1f,1f,1f, 1.0f);

        }
        for(Triangle triangle : triangles){
            // getBrightness(float p_70013_1_) from entity
            glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
            PointD direction = triangle.getDirection();
            PointD normal = triangle.getNormal();
            PointD basePoint = triangle.points[0].toPoint();
            PointD directionPoint = basePoint.clone().add(direction);
            PointD normalPoint = basePoint.clone().add(normal.multiply(4));
            drawLine(basePoint, directionPoint);
            glColor4f(0f,0f,1f, 1.0f);
            drawLine(basePoint, normalPoint);
            glColor4f(1f,1f,1f, 1.0f);
        }
        glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public void drawLine(SkeletonPoint point, SkeletonPoint point2){
        glBegin(GL_LINE_STRIP);
        glVertex3d(point.posX, point.posY, point.posZ);
        glVertex3d(point2.posX, point2.posY, point2.posZ);
        glEnd();
    }

    public void drawLine(PointD point, PointD point2){
        glBegin(GL_LINE_STRIP);
        glVertex3d(point.getX(), point.getY(), point.getZ());
        glVertex3d(point2.getX(), point2.getY(), point2.getZ());
        glEnd();
    }

    public void drawTriangle(SkeletonPoint point, SkeletonPoint point2, SkeletonPoint point3){
        glBegin(GL_TRIANGLES);
        //glColor3f(0.1, 0.2, 0.3);
        glVertex3d(point.posX, point.posY, point.posZ);
        glVertex3d(point2.posX, point2.posY, point2.posZ);
        glVertex3d(point3.posX, point3.posY, point3.posZ);
        glEnd();
    }

    public void verifyPoints(EntityRagdoll entity) {
        for(SkeletonPoint point : points){
            point.verify(entity);
            //point.movePoint(entity);
        }
    }

    public void shiftPos(double x, double y, double z) {
        for(SkeletonPoint point : points){
            point.shiftPosition(x, y, z);
            //point.movePoint(entity);
        }
    }

    public void setVelocity(double motionX, double motionY, double motionZ) {
        for(SkeletonPoint point : points){
            point.setVelocity(motionX, motionY, motionZ);
            //point.movePoint(entity);
        }
    }

    public void addVelocity(double motionX, double motionY, double motionZ) {
        for(SkeletonPoint point : points){
            point.addVelocity(motionX,motionY,motionZ);
            //point.movePoint(entity);
        }
    }
}
