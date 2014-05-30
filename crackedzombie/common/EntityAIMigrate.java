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
//
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.common;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIMigrate extends EntityAIBase {

	private final EntityCreature entity;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private final double speed;

	public EntityAIMigrate(EntityCreature entityCreature, double speed)
	{
		entity = entityCreature;
		this.speed = speed;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if (entity.getAge() > 600) {
			return false;
		} else if (entity.getRNG().nextInt(120) != 0) {
			return false;
		} else {
			Vec3 vec = RandomPositionGenerator.findRandomTarget(entity, 16, 8);

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

	@Override
	public boolean continueExecuting()
	{
		return !entity.getNavigator().noPath();
	}

	@Override
	public void startExecuting()
	{
		entity.getNavigator().tryMoveToXYZ(xPosition, yPosition, zPosition, speed);
	}
	
//	@Override
//	public void resetTask()
//    {
//        if (entity.getNavigator().noPath() || entity.getDistanceSq(xPosition, yPosition, zPosition) < 16.0D) {
//            Vec3 vec = RandomPositionGenerator.findRandomTarget(entity, 16, 8);
//			if (vec != null) {
//				xPosition = vec.xCoord;
//				yPosition = vec.yCoord;
//				zPosition = vec.zCoord;
//			}
//        }
//    }

}
