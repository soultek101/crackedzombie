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
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.client;

import com.crackedzombie.common.EntityCrackedZombie;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCrackedZombie extends RenderBiped {

	private final ModelBiped currentModel;
	private ModelCrackedZombieVillager zombieVillager;
	protected ModelBiped adultCrackedZombie;
	protected ModelBiped childCrackedZombie;
	protected ModelBiped adultZombieVillager;
	protected ModelBiped childZombieVillager;
	private int nZombieVillagers = 1;

	private static final ResourceLocation zombieSkin = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation zombieVillagerSkin = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
	

	public RenderCrackedZombie()
	{
		super(new ModelCrackedZombie(), 0.5F, 1.0F);
		currentModel = modelBipedMain;
		zombieVillager = new ModelCrackedZombieVillager();
	}

	@Override
	protected void func_82421_b()
	{
		field_82423_g = new ModelCrackedZombie(1.0F, true);
		field_82425_h = new ModelCrackedZombie(0.5F, true);
		adultCrackedZombie = field_82423_g;
		childCrackedZombie = field_82425_h;
		adultZombieVillager = new ModelCrackedZombieVillager(1.0F, 0.0F, true);
		childZombieVillager = new ModelCrackedZombieVillager(0.5F, 0.0F, true);
	}

	private void setModel(EntityCrackedZombie entityCrackedZombie)
	{
		if (entityCrackedZombie.isVillager()) {
			if (nZombieVillagers != zombieVillager.getMaxCrackedZombieVillagers()) {
				zombieVillager = new ModelCrackedZombieVillager();
				nZombieVillagers = zombieVillager.getMaxCrackedZombieVillagers();
				adultZombieVillager = new ModelCrackedZombieVillager(1.0F, 0.0F, true);
				childZombieVillager = new ModelCrackedZombieVillager(0.5F, 0.0F, true);
			}

			mainModel = zombieVillager;
			field_82423_g = adultZombieVillager;
			field_82425_h = childZombieVillager;
		} else {
			mainModel = currentModel;
			field_82423_g = adultCrackedZombie;
			field_82425_h = childCrackedZombie;
		}

		modelBipedMain = (ModelBiped) mainModel;
	}

	protected void rotateCorpse(EntityCrackedZombie entityCrackedZombie, float par2, float par3, float par4)
	{
		if (entityCrackedZombie.isConverting()) {
			par3 += (float) (Math.cos((double) entityCrackedZombie.ticksExisted * 3.25D) * Math.PI * 0.25D);
		}

		super.rotateCorpse(entityCrackedZombie, par2, par3, par4);
	}

	@Override
	protected void rotateCorpse(EntityLivingBase entityLivingBase, float par2, float par3, float par4)
	{
		rotateCorpse((EntityCrackedZombie) entityLivingBase, par2, par3, par4);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		return ((EntityCrackedZombie) entity).isVillager() ? zombieVillagerSkin : zombieSkin;
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entityLiving, float par2)
	{
		setModel((EntityCrackedZombie) entityLiving);
		super.renderEquippedItems(entityLiving, par2);
	}

	@Override
	protected int shouldRenderPass(EntityLiving entityLiving, int par2, float par3)
	{
		setModel((EntityCrackedZombie) entityLiving);
		return super.shouldRenderPass(entityLiving, par2, par3);
	}

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float par8, float par9)
	{
		this.doRender((EntityCrackedZombie) entity, x, y, z, par8, par9);
	}
	
	public void doRender(EntityCrackedZombie entity, double x, double y, double z, float par8, float par9)
	{
		setModel(entity);
		super.doRender((EntityLiving)entity, x, y, z, par8, par9);
	}

}
