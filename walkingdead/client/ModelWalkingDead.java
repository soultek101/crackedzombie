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
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.walkingdead.client;

import com.walkingdead.common.EntityWalkingDead;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelWalkingDead extends ModelBiped {

	private float armAngle;

	public ModelWalkingDead()
	{
		this(0.0F, false);
	}

	protected ModelWalkingDead(float par1, float par2, int par3, int par4)
	{
		super(par1, par2, par3, par4);
	}

	public ModelWalkingDead(float par1, boolean par2)
	{
		super(par1, 0.0F, 64, par2 ? 32 : 64);
	}

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity)
	{
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);

		float rightArmRotation = MathHelper.sin(onGround * (float) Math.PI);
		float leftARmRotation = MathHelper.sin((1.0F - (1.0F - onGround) * (1.0F - onGround)) * (float) Math.PI);
		bipedRightArm.rotateAngleZ = 0.0F;
		bipedLeftArm.rotateAngleZ = 0.0F;
		bipedRightArm.rotateAngleY = -(0.1F - rightArmRotation * 0.6F);
		bipedLeftArm.rotateAngleY = 0.1F - rightArmRotation * 0.6F;
		bipedRightArm.rotateAngleX = armAngle;//-((float) Math.PI / 2F);	
		bipedLeftArm.rotateAngleX = armAngle;//-((float) Math.PI / 2F);
		bipedRightArm.rotateAngleX -= rightArmRotation * 1.2F - leftARmRotation * 0.4F;
		bipedLeftArm.rotateAngleX -= rightArmRotation * 1.2F - leftARmRotation * 0.4F;
		bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entityliving, float f, float f1, float f2)
	{
		EntityWalkingDead walker = (EntityWalkingDead) entityliving;
		if (walker.getHasTarget()) {
			armAngle = -((float) Math.PI / 2F);
		} else {
			armAngle = 0.0F;
		}
	}

}
