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

package walkingdead.client;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import walkingdead.common.EntityWalkingDead;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.renderer.texture.TextureManager;
//import net.minecraft.client.resources.ResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;

@SideOnly(Side.CLIENT)
public class RenderWalkingDead extends RenderBiped {

	private ModelBiped currentModel;
	private ModelWalkingDeadVillager walkerVillager;
	protected ModelBiped adultWalker;
	protected ModelBiped childWalker;
	protected ModelBiped adultWalkerVillager;
	protected ModelBiped childWalkerVillager;
	private int nZombieVillagers = 1;
	
	private static final Map textureMap = Maps.newHashMap();
	
	private static ResourceLocation walkerSkins[] = new ResourceLocation[6];
	private static ResourceLocation villager_walkerSkins[] = new ResourceLocation[3];
	
	private static final ResourceLocation walker0 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker0.png");
	private static final ResourceLocation walker1 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker1.png");
	private static final ResourceLocation walker2 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker2.png");
	private static final ResourceLocation walker3 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker3.png");
	private static final ResourceLocation walker4 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker4.png");
	private static final ResourceLocation walker5 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker5.png");
	
	private static final ResourceLocation villager_walker0 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker_villager0.png");
	private static final ResourceLocation villager_walker1 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker_villager1.png");
	private static final ResourceLocation villager_walker2 = new ResourceLocation("walkingdeadmod", "textures/entity/walkers/walker_villager2.png");
	
	public RenderWalkingDead() {
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
	
	protected void func_82421_b() {
		field_82423_g = new ModelWalkingDead(1.0F, true);
		field_82425_h = new ModelWalkingDead(0.5F, true);
		adultWalker = field_82423_g;
		childWalker = field_82425_h;
		adultWalkerVillager = new ModelWalkingDeadVillager(1.0F, 0.0F, true);
		childWalkerVillager = new ModelWalkingDeadVillager(0.5F, 0.0F, true);
	}

	protected int func_82429_a(EntityWalkingDead entityWalkingDead, int par2, float par3) {
		func_82427_a(entityWalkingDead);
		return super.func_130006_a(entityWalkingDead, par2, par3);
	}

	public void func_82426_a(EntityWalkingDead entityWalkingDead, double par2, double par4, double par6, float par8, float par9) {
		func_82427_a(entityWalkingDead);
		super.doRenderLiving(entityWalkingDead, par2, par4, par6, par8, par9);
	}

	protected void func_82428_a(EntityWalkingDead entityWalkingDead, float par2) {
		func_82427_a(entityWalkingDead);
		super.func_130005_c(entityWalkingDead, par2);
	}

	private void func_82427_a(EntityWalkingDead entityWalkingDead) {
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

	protected void func_82430_a(EntityWalkingDead entityWalkingDead, float par2, float par3, float par4) {
		if (entityWalkingDead.isConverting()) {
			par3 += (float) (Math .cos((double) entityWalkingDead.ticksExisted * 3.25D) * Math.PI * 0.25D);
		}

		super.rotateCorpse(entityWalkingDead, par2, par3, par4);
	}
	
	protected void func_130005_c(EntityLiving entityLiving, float par2) {
        func_82428_a((EntityWalkingDead)entityLiving, par2);
    }
	
	protected int func_130006_a(EntityLiving entityLiving, int par2, float par3) {
        return func_82429_a((EntityWalkingDead)entityLiving, par2, par3);
    }
	
	public void renderPlayer(EntityLivingBase par1, double par2, double par4, double par6, float par8, float par9) {
        func_82426_a((EntityWalkingDead)par1, par2, par4, par6, par8, par9);
    }
	
    protected ResourceLocation func_110775_a(Entity entity) {
    	int idx = ((EntityWalkingDead)entity).getSkinIndex();
        return ((EntityWalkingDead)entity).isVillager() ?  villager_walkerSkins[idx] : walkerSkins[idx];
    }
    
	protected void renderEquippedItems(EntityLivingBase entityLiving, float par2) {
		func_82428_a((EntityWalkingDead) entityLiving, par2);
	}

	public void doRenderLiving(EntityLiving entityLiving, double par2, double par4, double par6, float par8, float par9) {
		func_82426_a((EntityWalkingDead) entityLiving, par2, par4, par6, par8, par9);
	}

	protected int shouldRenderPass(EntityLiving entityLiving, int par2, float par3) {
		return func_82429_a((EntityWalkingDead) entityLiving, par2, par3);
	}

	protected void rotateCorpse(EntityLivingBase entityLiving, float par2, float par3, float par4) {
		func_82430_a((EntityWalkingDead) entityLiving, par2, par3, par4);
	}

	public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9) {
		func_82426_a((EntityWalkingDead) entity, par2, par4, par6, par8, par9);
	}
	
}
