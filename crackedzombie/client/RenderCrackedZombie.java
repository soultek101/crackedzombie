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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderCrackedZombie extends RenderBiped {

	private final ModelBiped currentModel;
	private final ModelCrackedZombieVillager zombieVillager;
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
				field_177189_c = new ModelCrackedZombie(0.5F, true);
				field_177186_d = new ModelCrackedZombie(1.0F, true);
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

	protected void rotateCorpse(EntityCrackedZombie entityCrackedZombie, float x, float y, float z)
	{
		if (entityCrackedZombie.isConverting()) {
			y += (float) (Math.cos((double) entityCrackedZombie.ticksExisted * 3.25D) * Math.PI * 0.25D);
		}

		super.rotateCorpse(entityCrackedZombie, x, y, z);
	}

	private void getRenderLayer(EntityCrackedZombie zombie)
	{
		if (zombie.isVillager()) {
			mainModel = zombieVillager;
			layerRenderers = list1;
		} else {
			mainModel = currentModel;
			layerRenderers = list2;
		}

		modelBipedMain = (ModelBiped)mainModel;
	}

	@Override
	protected void rotateCorpse(EntityLivingBase entityLivingBase, float x, float y, float z)
	{
		rotateCorpse((EntityCrackedZombie) entityLivingBase, x, y, z);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity)
	{
		return getEntitySkinType((EntityCrackedZombie) entity);
	}

	protected ResourceLocation getEntitySkinType(EntityCrackedZombie entity)
	{
		return entity.isVillager() ? zombieVillagerSkin : zombieSkin;
	}
	
//	@Override
//	public void doRender(Entity entity, double x, double y, double z, float facing, float partialTicks)
//    {
//        this.doRender((EntityCrackedZombie)entity, x, y, z, facing, partialTicks);
//    }

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float facing, float partialTicks)
	{
		this.doRender((EntityCrackedZombie) entity, x, y, z, facing, partialTicks);
	}
	
//	@Override
//	public void doRender(EntityLivingBase entity, double x, double y, double z, float facing, float partialTicks)
//    {
//        this.doRender((EntityCrackedZombie)entity, x, y, z, facing, partialTicks);
//    }

	public void doRender(EntityCrackedZombie zombie, double x, double y, double z, float facing, float partialTicks)
	{
		getRenderLayer(zombie);
		super.doRender(zombie, x, y, z, facing, partialTicks);
	}

}
