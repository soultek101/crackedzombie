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
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCrackedZombie extends RenderBiped {

	private final ModelBiped currentModel;
	private final ModelCrackedZombieVillager zombieVillager;
	protected ModelBiped adultCrackedZombie;
	protected ModelBiped childCrackedZombie;
	protected ModelBiped adultZombieVillager;
	protected ModelBiped childZombieVillager;
	private final List list1;
	private final List list2;

	private static final ResourceLocation zombieSkin = new ResourceLocation("textures/entity/zombie/zombie.png");
	private static final ResourceLocation zombieVillagerSkin = new ResourceLocation("textures/entity/zombie/zombie_villager.png");

	@SuppressWarnings("unchecked")
	public RenderCrackedZombie(RenderManager rm)
	{
		super(rm, new ModelCrackedZombie(), 0.5F, 1.0F);
		LayerRenderer layerrenderer = (LayerRenderer) this.layerRenderers.get(0);
		currentModel = modelBipedMain;
		zombieVillager = new ModelCrackedZombieVillager();
		addLayer(new LayerHeldItem(this));
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
			@Override
			protected void func_177177_a()
			{
				childCrackedZombie = new ModelCrackedZombie(0.5F, true);
				adultCrackedZombie = new ModelCrackedZombie(1.0F, true);
			}
		};
		addLayer(layerbipedarmor);
		list1 = Lists.newArrayList(layerRenderers);

		if (layerrenderer instanceof LayerCustomHead) {
			removeLayer(layerrenderer);
			addLayer(new LayerCustomHead(zombieVillager.bipedHead));
		}

		removeLayer(layerbipedarmor);
		addLayer(new LayerVillagerArmor(this));
		list2 = Lists.newArrayList(layerRenderers);
	}

	protected void rotateCorpse(EntityCrackedZombie entityCrackedZombie, float par2, float par3, float par4)
	{
		if (entityCrackedZombie.isConverting()) {
			par3 += (float) (Math.cos((double) entityCrackedZombie.ticksExisted * 3.25D) * Math.PI * 0.25D);
		}

		super.rotateCorpse(entityCrackedZombie, par2, par3, par4);
	}

	private void getRenderLayer(EntityCrackedZombie zombie)
	{
		if (zombie.isVillager()) {
			mainModel = adultZombieVillager;
			layerRenderers = list1;
		} else {
			mainModel = currentModel;
			layerRenderers = list2;
		}

		modelBipedMain = (ModelBiped)mainModel;
	}

	@Override
	protected void rotateCorpse(EntityLivingBase entityLivingBase, float par2, float par3, float par4)
	{
		rotateCorpse((EntityCrackedZombie) entityLivingBase, par2, par3, par4);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity)
	{
		return func_180578_a((EntityCrackedZombie) entity);
	}

	protected ResourceLocation func_180578_a(EntityCrackedZombie entity)
	{
		return entity.isVillager() ? zombieVillagerSkin : zombieSkin;
	}

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float par8, float par9)
	{
		this.doRender((EntityCrackedZombie) entity, x, y, z, par8, par9);
	}

	public void doRender(EntityCrackedZombie entity, double x, double y, double z, float par8, float par9)
	{
		getRenderLayer(entity);
		super.doRender((EntityLiving) entity, x, y, z, par8, par9);
	}

}
