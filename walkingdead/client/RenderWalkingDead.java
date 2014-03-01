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
package com.walkingdead.client;

import com.walkingdead.common.EntityWalkingDead;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

@SideOnly(Side.CLIENT)
public class RenderWalkingDead extends RenderBiped {

	private final ModelBiped currentModel;
	private ModelWalkingDeadVillager walkerVillager;
	protected ModelBiped adultWalker;
	protected ModelBiped childWalker;
	protected ModelBiped adultWalkerVillager;
	protected ModelBiped childWalkerVillager;
	private int nZombieVillagers = 1;

	private static final ResourceLocation walkerSkins[] = new ResourceLocation[6];
	private static final ResourceLocation villager_walkerSkins[] = new ResourceLocation[3];

	private static final ResourceLocation walker0 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker0.png");
	private static final ResourceLocation walker1 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker1.png");
	private static final ResourceLocation walker2 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker2.png");
	private static final ResourceLocation walker3 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker3.png");
	private static final ResourceLocation walker4 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker4.png");
	private static final ResourceLocation walker5 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker5.png");

	private static final ResourceLocation villager_walker0 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker_villager0.png");
	private static final ResourceLocation villager_walker1 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker_villager1.png");
	private static final ResourceLocation villager_walker2 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker_villager2.png");

	public RenderWalkingDead()
	{
		super(new ModelWalkingDead(), 0.5F, 1.0F);
		currentModel = modelBipedMain;
		walkerVillager = new ModelWalkingDeadVillager();

		walkerSkins[0] = walker0;
		walkerSkins[1] = walker1;
		walkerSkins[2] = walker2;
		walkerSkins[3] = walker3;
		walkerSkins[4] = walker4;
		walkerSkins[5] = walker5;

		villager_walkerSkins[0] = villager_walker0;
		villager_walkerSkins[1] = villager_walker1;
		villager_walkerSkins[2] = villager_walker2;
	}

	@Override
	protected void func_82421_b()
	{
		field_82423_g = new ModelWalkingDead(1.0F, true);
		field_82425_h = new ModelWalkingDead(0.5F, true);
		adultWalker = field_82423_g;
		childWalker = field_82425_h;
		adultWalkerVillager = new ModelWalkingDeadVillager(1.0F, 0.0F, true);
		childWalkerVillager = new ModelWalkingDeadVillager(0.5F, 0.0F, true);
	}

	private void func_82427_a(EntityWalkingDead entityWalkingDead)
	{
		if (entityWalkingDead.isVillager()) {
			if (nZombieVillagers != walkerVillager.getMaxWalkingDeadVillagers()) {
				walkerVillager = new ModelWalkingDeadVillager();
				nZombieVillagers = walkerVillager.getMaxWalkingDeadVillagers();
				adultWalkerVillager = new ModelWalkingDeadVillager(1.0F, 0.0F, true);
				childWalkerVillager = new ModelWalkingDeadVillager(0.5F, 0.0F, true);
			}

			mainModel = walkerVillager;
			field_82423_g = adultWalkerVillager;
			field_82425_h = childWalkerVillager;
		} else {
			mainModel = currentModel;
			field_82423_g = adultWalker;
			field_82425_h = childWalker;
		}

		modelBipedMain = (ModelBiped) mainModel;
	}

	protected void rotateCorpse(EntityWalkingDead entityWalkingDead, float par2, float par3, float par4)
	{
		if (entityWalkingDead.isConverting()) {
			par3 += (float) (Math.cos((double) entityWalkingDead.ticksExisted * 3.25D) * Math.PI * 0.25D);
		}

		super.rotateCorpse(entityWalkingDead, par2, par3, par4);
	}

	@Override
	protected void rotateCorpse(EntityLivingBase entityLivingBase, float par2, float par3, float par4)
	{
		this.rotateCorpse((EntityWalkingDead) entityLivingBase, par2, par3, par4);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
		int idx = ((EntityWalkingDead) entity).getSkinIndex();
		return ((EntityWalkingDead) entity).isVillager() ? villager_walkerSkins[idx] : walkerSkins[idx];
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entityLiving, float par2)
	{
		func_82427_a((EntityWalkingDead) entityLiving);
		super.renderEquippedItems(entityLiving, par2);
	}

	@Override
	protected int shouldRenderPass(EntityLiving entityLiving, int par2, float par3)
	{
		func_82427_a((EntityWalkingDead) entityLiving);
		return super.shouldRenderPass(entityLiving, par2, par3);
	}

	@Override
	public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.doRender((EntityWalkingDead) entity, par2, par4, par6, par8, par9);
	}

}
