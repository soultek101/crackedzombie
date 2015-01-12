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
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.client;

import com.crackedzombie.common.EntityCrackedZombie;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelCrackedZombieVillager extends ModelBiped {

	private float armAngle;

	public ModelCrackedZombieVillager()
	{
		this(0.0F, 0.0F, false);
	}

	public ModelCrackedZombieVillager(float par1, float par2, boolean par3)
	{
		super(par1, 0.0F, 64, par3 ? 32 : 64);

		if (par3) {
			this.bipedHead = new ModelRenderer(this, 0, 0);
			this.bipedHead.addBox(-4.0F, -10.0F, -4.0F, 8, 6, 8, par1);
			this.bipedHead.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
		} else {
			this.bipedHead = new ModelRenderer(this);
			this.bipedHead.setRotationPoint(0.0F, 0.0F + par2, 0.0F);
			this.bipedHead.setTextureOffset(0, 32).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, par1);
			this.bipedHead.setTextureOffset(24, 32).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, par1);
		}
	}

	public int getMaxCrackedZombieVillagers()
	{
		return 10;
	}

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity)
	{
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
		float var8 = MathHelper.sin(swingProgress * (float) Math.PI);
		float var9 = MathHelper.sin((1.0F - (1.0F - swingProgress) * (1.0F - swingProgress)) * (float) Math.PI);
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
		this.bipedRightArm.rotateAngleY = -(0.1F - var8 * 0.6F);
		this.bipedLeftArm.rotateAngleY = 0.1F - var8 * 0.6F;
		bipedRightArm.rotateAngleX = armAngle;//-((float) Math.PI / 2F);	
		bipedLeftArm.rotateAngleX = armAngle;//-((float) Math.PI / 2F);
		this.bipedRightArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
		this.bipedLeftArm.rotateAngleX -= var8 * 1.2F - var9 * 0.4F;
		this.bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
		this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
		this.bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
		this.bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entityliving, float f, float f1, float f2)
	{
		EntityCrackedZombie zombie = (EntityCrackedZombie) entityliving;
		if (zombie.getHasTarget()) {
			armAngle = -((float) Math.PI / 2F);
		} else {
			armAngle = 0.0F;
		}
	}
}
