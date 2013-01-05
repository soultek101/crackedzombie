//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package walkingdead.common;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIMigrate extends EntityAIBase {

	private EntityCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private float speed;

    public EntityAIMigrate(EntityCreature entityCreature, float speed) {
        this.entity = entityCreature;
        this.speed = speed;
        this.setMutexBits(1);
    }

    public boolean shouldExecute() {
        if (this.entity.getAge() > 600) {
            return false;
        } else {
            Vec3 vec = RandomPositionGenerator.findRandomTarget(this.entity, 16, 8);

            if (vec == null) {
                return false;
            } else {
                this.xPosition = vec.xCoord;
                this.yPosition = vec.yCoord;
                this.zPosition = vec.zCoord;
                return true;
            }
        }
    }

    public boolean continueExecuting() {
        return !this.entity.getNavigator().noPath();
    }

    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

}
