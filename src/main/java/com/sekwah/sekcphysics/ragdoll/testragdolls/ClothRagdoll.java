package com.sekwah.sekcphysics.ragdoll.testragdolls;

import com.sekwah.sekcphysics.ragdoll.BaseRagdoll;
import com.sekwah.sekcphysics.ragdoll.parts.AnchoredSkeletonPoint;
import com.sekwah.sekcphysics.ragdoll.parts.Constraint;
import com.sekwah.sekcphysics.ragdoll.parts.Skeleton;
import com.sekwah.sekcphysics.ragdoll.parts.SkeletonPoint;

/**
 * Created by sekawh on 8/5/2015.
 */
public class ClothRagdoll extends BaseRagdoll {

    private int width = 30;

    private int height = 30;

    private float spacing = 0.20f;

    SkeletonPoint[][] points = new SkeletonPoint[width][height];

    public ClothRagdoll(){
        super(1.4f);
        this.skeleton = new Skeleton();

        this.centerHeightOffset = 24;

        this.setPasses(10);

        // Top row (anchor points)
        for(int x = 0; x < this.width; x++){
            for(int y = 0; y < this.height; y++){
                if(y == 0){
                    this.points[x][y] = new AnchoredSkeletonPoint(x * spacing, -y * spacing, 0, false);
                }
                else{
                    this.points[x][y] = new SkeletonPoint(x * spacing, -y * spacing, 0, false);
                }
                this.skeleton.points.add(points[x][y]);
            }
        }

        for(int x = 0; x < this.width; x++){
            for(int y = 0; y < this.height; y++){
                if(x < this.width - 1){
                    this.skeleton.constraints.add(new Constraint(points[x][y],points[x + 1][y]));
                }
                if(y < height - 1){
                    this.skeleton.constraints.add(new Constraint(points[x][y],points[x][y + 1]));
                }
            }
        }
    }

}
