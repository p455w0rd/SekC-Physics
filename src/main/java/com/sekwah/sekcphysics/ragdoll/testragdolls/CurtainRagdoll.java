package com.sekwah.sekcphysics.ragdoll.testragdolls;

import com.sekwah.sekcphysics.ragdoll.BaseRagdoll;
import com.sekwah.sekcphysics.ragdoll.parts.AnchoredSkeletonPoint;
import com.sekwah.sekcphysics.ragdoll.parts.Constraint;
import com.sekwah.sekcphysics.ragdoll.parts.Skeleton;
import com.sekwah.sekcphysics.ragdoll.parts.SkeletonPoint;

/**
 * Created by sekawh on 8/5/2015.
 */
public class CurtainRagdoll extends BaseRagdoll {

    private int width = 4;

    private int height = 7;

    private float spacing = 0.25f;

    SkeletonPoint[][] leftPoints = new SkeletonPoint[width][height];

    SkeletonPoint[][] rightPoints = new SkeletonPoint[width][height];

    public CurtainRagdoll(){
        super(1.4f);
        this.skeleton = new Skeleton();

        this.centerHeightOffset = 24;

        this.setPasses(10);


        // Top row (anchor points)
        for(int x = 0; x < this.width; x++){
            for(int y = 0; y < this.height; y++){
                if(y == 0){
                    this.leftPoints[x][y] = new AnchoredSkeletonPoint(x * spacing, -y * spacing, 0, false);
                }
                else{
                    this.leftPoints[x][y] = new SkeletonPoint(x * spacing, -y * spacing, 0, false);
                }
                this.skeleton.points.add(leftPoints[x][y]);
            }
        }

        for(int x = 0; x < this.width; x++){
            for(int y = 0; y < this.height; y++){
                if(x < this.width - 1){
                    this.skeleton.constraints.add(new Constraint(leftPoints[x][y],leftPoints[x + 1][y]));
                }
                if(y < height - 1){
                    skeleton.constraints.add(new Constraint(leftPoints[x][y],leftPoints[x][y + 1]));
                }
            }
        }

        // Top row (anchor points)
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(y == 0){
                    rightPoints[x][y] = new AnchoredSkeletonPoint(-x * spacing, -y * spacing, 0, false);
                }
                else{
                    rightPoints[x][y] = new SkeletonPoint(-x * spacing, -y * spacing, 0, false);
                }
                skeleton.points.add(rightPoints[x][y]);
            }
        }

        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(x < width - 1){
                    skeleton.constraints.add(new Constraint(rightPoints[x][y],rightPoints[x + 1][y]));
                }
                if(y < height - 1){
                    skeleton.constraints.add(new Constraint(rightPoints[x][y],rightPoints[x][y + 1]));
                }
            }
        }
    }

}
