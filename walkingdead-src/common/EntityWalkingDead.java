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


import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import walkingdead.client.RenderWalkingDead;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class EntityWalkingDead extends EntityMob
{
	private static final UUID uuid = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final AttributeModifier speedBoost = new AttributeModifier(uuid, "Baby speed boost", 0.1D, 0);
	
	private int conversionTime = 0;
	private String villager_texture;
	private final float attackDistance = 16.0F;
	private int walkerSkinIndex;
	private int villagerSkinIndex;
	
	public EntityWalkingDead(World world) {
		super(world);
		
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		if (WalkingDead.instance.getDoorBusting()) { // include the door breaking AI
			getNavigator().setBreakDoors(true);
			tasks.addTask(1, new EntityAIBreakDoor(this));
		}
		tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
		tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
		tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
		tasks.addTask(5, new EntityAIAttackOnCollide(this, EntityChicken.class, 1.0, false));
		tasks.addTask(6, new EntityAIAttackOnCollide(this, EntityPig.class, 1.0, false));
//		tasks.addTask(7, new EntityAIMoveTowardsRestriction(this, 1.0D));
//		tasks.addTask(8, new EntityAIMoveThroughVillage(this, 1.0, false));
		tasks.addTask(7, new EntityAIMigrate(this, 0.8));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityChicken.class, 8, false));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPig.class, 8, false));
		
		rand = new Random(System.currentTimeMillis());
	}
	
	protected void func_110147_ax() {
        super.func_110147_ax();
        func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(32.0); // follow range
        func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.23); // movement speed
        func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(3.0);  // attack damage
    }
	
	// used in model rendering, arms hang down when wandering about
	// arms go up when attacking another entity, i.e., has a target.
	public boolean getHasTarget() {
		return isAttackableEntity(this, attackDistance);
	}
	
	public boolean isAttackableEntity(EntityLivingBase entityLiving, double distance) {
        List list = worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, boundingBox.expand(distance, 4.0D, distance), (IEntitySelector)null);

        Iterator iter = list.iterator();
        while (iter.hasNext()) {
        	Entity entity = (Entity) iter.next();
        	EntityLivingBase target = (EntityLivingBase)entity;
        	if (isGoodTarget(target)) {
        		double dist = target.getDistanceSq(entityLiving.posX, entityLiving.posY, entityLiving.posZ);
        		if (dist < distance * distance) {
        			return true;
        		}
        	}
        }
        return false;
	}
	
	public boolean isGoodTarget(EntityLivingBase target) {
		if (target == null) {
            return false;
        } else if (target == this) {
            return false;
        } else if (!target.isEntityAlive()) {
            return false;
        } else {
        	boolean player = (target instanceof EntityPlayer);
        	boolean villager = (target instanceof EntityVillager);
        	boolean chicken = (target instanceof EntityChicken);
        	boolean pig = (target instanceof EntityPig);
        	
        	if (player) {
        		if (((EntityPlayer)target).capabilities.isCreativeMode) {
        			return false;
        		}
        	}
        	if ((player || villager || chicken || pig) && canEntityBeSeen(target)) {
            	return true;
            }
        }
		
		return false;
	}
	
	@Override
	protected boolean isMovementCeased() {
		return false;
	}
	
	public boolean attackEntityFrom(DamageSource damageSource, float damage) {
		if (isEntityInvulnerable()) {
            return false;
        } else if (super.attackEntityFrom(damageSource, damage)) {
        	if (damageSource.isProjectile()) {
        		// apply random damage multiplier to arrows (range 1 - 3)
        		damage *= (rand.nextInt(3) + 1);
        		damageEntity(damageSource, damage);
        		return true;
        	}
        }
		return false;
	}

	public void onLivingUpdate() {
		if (worldObj.isDaytime() && !worldObj.isRemote && !isChild()) {
			float brightness = getBrightness(1.0F);

			int x = MathHelper.floor_double(posX);
			int y = MathHelper.floor_double(posY);
			int z = MathHelper.floor_double(posZ);
			boolean canSeeBlock = worldObj.canBlockSeeTheSky(x, y,	z);
			
			if (brightness > 0.5F && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && canSeeBlock) {
				ItemStack itemstack = getCurrentItemOrArmor(4);

				if (itemstack != null && itemstack.isItemStackDamageable()) {
					itemstack.setItemDamage(itemstack.getItemDamageForDisplay() + rand.nextInt(2));

					if (itemstack.getItemDamageForDisplay() >= itemstack.getMaxDamage()) {
						renderBrokenItemStack(itemstack);
						setCurrentItemOrArmor(4, (ItemStack) null);
					}
				}
			}
		}
		super.onLivingUpdate();
	}
	
	@Override
	protected boolean isValidLightLevel() {
		return true;
	}
	
	@Override
	public boolean getCanSpawnHere() {
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(boundingBox.minY);
		int z = MathHelper.floor_double(posZ);
        
//		boolean isClear = worldObj.checkIfAABBIsClear(boundingBox);
		boolean notColliding = worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty();
		boolean isLiquid = worldObj.isAnyLiquid(boundingBox);
		// spawns on grass, sand and very occasionally spawn on stone
		boolean isGrass = worldObj.getBlockId(x, y - 1, z) == Block.grass.blockID;
		boolean isSand = worldObj.getBlockId(x, y - 1, z) == Block.sand.blockID;
		boolean isStone = (rand.nextInt(16) == 0) && (worldObj.getBlockId(x, y - 1, z) == Block.stone.blockID);
		
        return (isGrass || isSand || isStone) /*&& isClear*/ && notColliding && !isLiquid;
    }
	
	protected void entityInit() {
		super.entityInit();
		getDataWatcher().addObject(12, Byte.valueOf((byte) 0));
		getDataWatcher().addObject(13, Byte.valueOf((byte) 0));
		getDataWatcher().addObject(14, Byte.valueOf((byte) 0));
		
		walkerSkinIndex = rand.nextInt(6);
		villagerSkinIndex = rand.nextInt(3);
		getDataWatcher().addObject(24, Byte.valueOf((byte) walkerSkinIndex));
		getDataWatcher().addObject(25, Byte.valueOf((byte) villagerSkinIndex));
	}

	public int getMaxHealth() {
		return 20;
	}

	public int getTotalArmorValue() {
		int armor = super.getTotalArmorValue() + 2;

		if (armor > 20) {
			armor = 20;
		}

		return armor;
	}
	
	@Override
	protected boolean canDespawn() {
		return true;
	}

	protected boolean isAIEnabled() {
		return true;
	}

	public boolean isChild() {
		return getDataWatcher().getWatchableObjectByte(12) == 1;
	}

	public void setChild(boolean unused) {
//		getDataWatcher().updateObject(12, Byte.valueOf((byte) 1));
		getDataWatcher().updateObject(12, Byte.valueOf((byte)(unused ? 1 : 0)));

        if (this.worldObj != null && !this.worldObj.isRemote) {
            AttributeInstance attributeinstance = func_110148_a(SharedMonsterAttributes.field_111263_d);
            attributeinstance.func_111124_b(speedBoost);

            if (unused) {
                attributeinstance.func_111121_a(speedBoost);
            }
        }
	}

	public boolean isVillager() {
		return getDataWatcher().getWatchableObjectByte(13) == 1;
	}
	
	public void setVillager(boolean set) {
		getDataWatcher().updateObject(13, Byte.valueOf((byte) (set ? 1 : 0)));
	}
	
	public void onUpdate() {
		if (!worldObj.isRemote && isConverting()) {
			int boost = getConversionTimeBoost();
			conversionTime -= boost;

			if (conversionTime <= 0) {
				convertToVillager();
			}
		}

		super.onUpdate();
	}
	
	@Override
	public void onStruckByLightning(EntityLightningBolt entityLightningBolt) {
		// A little surprise... BOOM!
		worldObj.newExplosion(this, posX, posY, posZ, 0.5F, true, true);
		dealFireDamage(5);
        setFire(8);
	}
	
	public int getAttackStrength(Entity entity) {
		ItemStack itemstack = getHeldItem();
		int strength = 4;

		if (itemstack != null) {
			strength += 2; // would be nice to add the held item's damage capability 
		}

		return strength;
	}

	protected String getLivingSound() {
		return "mob.zombie.say";
	}

	protected String getHurtSound() {
		return "mob.zombie.hurt";
	}

	protected String getDeathSound() {
		return "mob.zombie.death";
	}

	protected void playStepSound(int par1, int par2, int par3, int par4) {
		playSound("mob.zombie.step", 0.20F, 1.0F);
	}

	protected int getDropItemId() {
		// returns the held item or armor
		ItemStack heldItem = getHeldItem();
		if (heldItem != null) {
			return heldItem.itemID;
		} else {
			return Item.rottenFlesh.itemID;
		}
	}
	
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	protected void dropRareDrop(int unused) {
		switch (rand.nextInt(3)) {
		case 0:
			dropItem(Item.ingotIron.itemID, 1);
			break;
		case 1:
			dropItem(Item.carrot.itemID, 1);
			break;
		case 2:
			dropItem(Item.potato.itemID, 1);
		}
	}
	
//	protected void setHeldItem() {
//		super.func_82162_bC();
//
//		int maxInt = worldObj.difficultySetting > 1 ? 16 : 32;
//		if (rand.nextInt(maxInt) == 0) {
//			switch (rand.nextInt(8)) {
//			case 0:
//				setCurrentItemOrArmor(0, new ItemStack(Item.swordDiamond));
//				break;
//			case 1:
//				setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));
//				break;
//			case 2:
//				setCurrentItemOrArmor(0, new ItemStack(Item.shovelDiamond));
//				break;
//			case 3:
//				setCurrentItemOrArmor(0, new ItemStack(Item.shovelIron));
//				break;
//			default:
//				return;
//			}
//		}
//	}

	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		if (isChild()) {
			nbt.setBoolean("IsBaby", true);
		}

		if (isVillager()) {
			nbt.setBoolean("IsVillager", true);
		}

		nbt.setInteger("ConversionTime", isConverting() ? conversionTime : -1);
		
		nbt.setInteger("WalkerSkinIdx", getWalkerSkinIndex());
		nbt.setInteger("VillagerSkinIdx", getVillagerSkinIndex());
	}

	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		if (nbt.getBoolean("IsBaby")) {
			setChild(true);
		}

		if (nbt.getBoolean("IsVillager")) {
			setVillager(true);
		}

		if (nbt.hasKey("ConversionTime") && nbt.getInteger("ConversionTime") > -1) {
			startConversion(nbt.getInteger("ConversionTime"));
		}
		
		walkerSkinIndex = nbt.getInteger("WalkerSkinIdx");
		villagerSkinIndex = nbt.getInteger("VillagererSkinIdx");
	}
	
	public EntityLivingData func_110161_a(EntityLivingData entityLivingData)
    {
        entityLivingData = super.func_110161_a(entityLivingData);
        float f = worldObj.func_110746_b(posX, posY, posZ);
        setCanPickUpLoot(rand.nextFloat() < 0.55F * f);
        
        float nf = worldObj.rand.nextFloat();
		if (nf < 0.05F) {
			setVillager(true);
		} else if (nf >= 0.05 && nf <= 0.08) {
			setChild(true);
		}

        addRandomArmor();
        enchantEquipment(); //func_82162_bC();
        
        return entityLivingData;
    }
	
	protected void addRandomArmor() {
        super.addRandomArmor();
        
        if (this.rand.nextFloat() < 0.05F) {
            switch(rand.nextInt(6)) {
	        case 0:
				setCurrentItemOrArmor(0, new ItemStack(Item.swordDiamond));
				break;
			case 1:
				setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));
				break;
			case 2:
				setCurrentItemOrArmor(0, new ItemStack(Item.shovelDiamond));
				break;
			case 3:
				setCurrentItemOrArmor(0, new ItemStack(Item.shovelIron));
				break;
			default:
				return;
			}
        }
    }

	public void onKillEntity(EntityLivingBase entityLiving) {
		super.onKillEntity(entityLiving);

		if (worldObj.difficultySetting >= 2 && entityLiving instanceof EntityVillager) {
			if (worldObj.difficultySetting == 2 && rand.nextBoolean()) {
				return;
			}

			EntityWalkingDead walker = new EntityWalkingDead(worldObj);
			walker.copyLocationAndAnglesFrom/*func_82149_j*/(entityLiving);
			worldObj.removeEntity(entityLiving);
			walker.func_110161_a((EntityLivingData)null);
			walker.setVillager(true);

			if (entityLiving.isChild()) {
				walker.setChild(true);
			}

			worldObj.spawnEntityInWorld(walker);
			worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1016, (int) posX, (int) posY, (int) posZ, 0);
		}
	}
	
	public boolean isConverting() {
        return this.getDataWatcher().getWatchableObjectByte(14) == 1;
    }

	public boolean interact(EntityPlayer entityPlayer) {
		ItemStack equippedItem = entityPlayer.getCurrentEquippedItem();

		if (equippedItem != null && equippedItem.getItem() == Item.appleGold && equippedItem.getItemDamage() == 0 && isVillager() && isPotionActive(Potion.weakness)) {
			if (!entityPlayer.capabilities.isCreativeMode) {
				--equippedItem.stackSize;
			}

			if (equippedItem.stackSize <= 0) {
				entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, (ItemStack) null);
			}

			if (!worldObj.isRemote) {
				startConversion(rand.nextInt(2401) + 3600);
			}

			return true;
		} else {
			return false;
		}
	}

	protected void startConversion(int conTime) {
		conversionTime = conTime;
		getDataWatcher().updateObject(14, Byte.valueOf((byte) 1));
		removePotionEffect(Potion.weakness.id);
		addPotionEffect(new PotionEffect(Potion.damageBoost.id, conTime, Math.min(worldObj.difficultySetting - 1, 0)));
		worldObj.setEntityState(this, (byte) 16);
	}

	@SideOnly(Side.CLIENT)
	public void handleHealthUpdate(byte health) {
		if (health == 16) {
			worldObj.playSound(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "mob.zombie.remedy", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		} else {
			super.handleHealthUpdate(health);
		}
	}

	protected void convertToVillager() {
		EntityVillager villager = new EntityVillager(worldObj);
		villager.copyLocationAndAnglesFrom/*func_82149_j*/(this);
		villager.func_110161_a((EntityLivingData)null);
		villager.func_82187_q();

		if (isChild()) {
			villager.setGrowingAge(-24000);
		}

		worldObj.removeEntity(this);
		worldObj.spawnEntityInWorld(villager);
		villager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
		worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1017, (int) posX, (int) posY, (int) posZ, 0);
		System.out.println("Converted walker to villager!");
	}

	protected int getConversionTimeBoost() {
		int boostTime = 1;

		if (rand.nextFloat() < 0.01F) {
			int count = 0;

			for (int x = (int) posX - 4; x < (int) posX + 4 && count < 14; ++x) {
				for (int y = (int) posY - 4; y < (int) posY + 4 && count < 14; ++y) {
					for (int z = (int) posZ - 4; z < (int) posZ + 4 && count < 14; ++z) {
						int blockID = worldObj.getBlockId(x, y, z);

						if (blockID == Block.fenceIron.blockID || blockID == Block.bed.blockID) {
							if (rand.nextFloat() < 0.3F) {
								++boostTime;
							}

							++count;
						}
					}
				}
			}
		}

		return boostTime;
	}
	
	public int getWalkerSkinIndex() {
		return getDataWatcher().getWatchableObjectByte(24);
	}
	
	public int getVillagerSkinIndex() {
		return getDataWatcher().getWatchableObjectByte(25);
	}
	
	@SideOnly(Side.CLIENT)
    public int getSkinIndex() {
		return isVillager() ? villagerSkinIndex : walkerSkinIndex;
    }

}
