//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//

//
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
    private double speed;

    public EntityAIMigrate(EntityCreature entityCreature, double speed) {
        entity = entityCreature;
        this.speed = speed;
        setMutexBits(1);
    }

    public boolean shouldExecute() {
        if (entity.getAge() > 600) {
            return false;
        } else {
            Vec3 vec = RandomPositionGenerator.findRandomTarget(this.entity, 16, 8);

            if (vec == null) {
                return false;
            } else {
                xPosition = vec.xCoord;
                yPosition = vec.yCoord;
                zPosition = vec.zCoord;
                return true;
            }
        }
    }

    public boolean continueExecuting() {
        return !entity.getNavigator().noPath();
    }

    public void startExecuting() {
        entity.getNavigator().tryMoveToXYZ(xPosition, yPosition, zPosition, speed);
    }

}
