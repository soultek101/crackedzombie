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
// Copyright 2011-2015 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.common;

import java.util.Calendar;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;

public class EntityCrackedZombie extends EntityMob {

	protected static final IAttribute reinforcements = (new RangedAttribute((IAttribute) null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).setDescription("Spawn Reinforcements Chance");
	private static final UUID uuid = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
	private static final AttributeModifier speedBoost = new AttributeModifier(uuid, "Baby speed boost", 0.1D, 0);

	private int conversionTime = 0;
	private final float attackDistance = 16.0F;
	private float zombieWidth = -1.0f;
	private float zombieHeight;

	public EntityCrackedZombie(World world)
	{
		super(world);

		((PathNavigateGround) getNavigator()).setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		if (ConfigHandler.getDoorBusting()) { // include the door breaking AI
			((PathNavigateGround) getNavigator()).setBreakDoors(true);
			tasks.addTask(6, new EntityAIBreakDoor(this));
		}
		tasks.addTask(2, aiAvoidExplodingCreepers);
		tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
		tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2, false));
		tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
		tasks.addTask(6, new EntityAIAttackOnCollide(this, EntityPig.class, 1.0, false));
		tasks.addTask(7, new EntityAIMigrate(this, 0.8));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		
		applyEntityAI();
		setSize(0.6F, 1.8F);
	}

	private void applyEntityAI()
	{
		tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
		tasks.addTask(4, new EntityAIAttackOnCollide(this, EntityIronGolem.class, 1.0D, true));
		tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {EntityPigZombie.class}));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPig.class, false));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0); // follow range
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25); // movement speed
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0);  // attack damage
		getAttributeMap().registerAttribute(reinforcements).setBaseValue(rand.nextDouble() * 0.1); // reinforcements
	}

	// used in model rendering, arms hang down when wandering about
	// arms go up when attacking another entity, i.e., has a target.
	public boolean getHasTarget()
	{
		return isAttackableEntity(this, attackDistance);
	}

	public boolean isAttackableEntity(EntityLivingBase entityLiving, double distance)
	{
		List list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().expand(distance, 4.0D, distance));

		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			Entity entity = (Entity) iter.next();
			EntityLivingBase target = (EntityLivingBase) entity;
			if (isGoodTarget(target)) {
				double dist = target.getDistanceSq(entityLiving.posX, entityLiving.posY, entityLiving.posZ);
				if (dist < distance * distance) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isGoodTarget(EntityLivingBase target)
	{
		if (target == null || target == this || !target.isEntityAlive()) {
			return false;
		} else {
			boolean player = (target instanceof EntityPlayer);
			boolean villager = (target instanceof EntityVillager);
			boolean pig = (target instanceof EntityPig);

			if (player) {
				if (((EntityPlayer) target).capabilities.isCreativeMode) {
					return false;
				}
			}
			if ((player || villager || pig) && canEntityBeSeen(target)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float damage)
	{
		if (super.attackEntityFrom(damageSource, damage)) {
			EntityLivingBase entitylivingbase = getAttackTarget();

			if (entitylivingbase == null && getAttackTarget() instanceof EntityLivingBase) {
				entitylivingbase = (EntityLivingBase) getAttackTarget();
			}

			if (entitylivingbase == null && damageSource.getEntity() instanceof EntityLivingBase) {
				entitylivingbase = (EntityLivingBase) damageSource.getEntity();
			}

			int posx = MathHelper.floor_double(posX);
			int posy = MathHelper.floor_double(posY);
			int posz = MathHelper.floor_double(posZ);

			if (entitylivingbase != null && this.worldObj.getDifficulty() == EnumDifficulty.HARD
					&& (double) this.rand.nextFloat() < this.getEntityAttribute(reinforcements).getAttributeValue()) {

				EntityCrackedZombie crackedZombie = new EntityCrackedZombie(worldObj);

				for (int l = 0; l < 50; ++l) {
					int x = posx + MathHelper.getRandomIntegerInRange(rand, 7, 40) * MathHelper.getRandomIntegerInRange(rand, -1, 1);
					int y = posy + MathHelper.getRandomIntegerInRange(rand, 7, 40) * MathHelper.getRandomIntegerInRange(rand, -1, 1);
					int z = posz + MathHelper.getRandomIntegerInRange(rand, 7, 40) * MathHelper.getRandomIntegerInRange(rand, -1, 1);

					BlockPos bp = new BlockPos(x, y - 1, z);
					if (World.doesBlockHaveSolidTopSurface(worldObj, bp)) {
						crackedZombie.setPosition((double) x, (double) y, (double) z);

						if (worldObj.checkNoEntityCollision(crackedZombie.getEntityBoundingBox())
								&& worldObj.getCollidingBoundingBoxes(crackedZombie, crackedZombie.getEntityBoundingBox()).isEmpty()
								&& !worldObj.isAnyLiquid(crackedZombie.getEntityBoundingBox())) {
							worldObj.spawnEntityInWorld(crackedZombie);
							crackedZombie.setAttackTarget(entitylivingbase);
							crackedZombie.onSpawnFirstTime(worldObj.getDifficultyForLocation(new BlockPos(crackedZombie)), (IEntityLivingData)null);
							getEntityAttribute(reinforcements).applyModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05D, 0));
							crackedZombie.getEntityAttribute(reinforcements).applyModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05D, 0));
							break;
						}
					}
				}
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if (super.attackEntityAsMob(entity)) {
			if (entity instanceof EntityLivingBase) {
				byte strength = 0;

				if (worldObj.getDifficulty() == EnumDifficulty.NORMAL) {
					strength = 7;
				} else if (worldObj.getDifficulty() == EnumDifficulty.HARD) {
					strength = 15;
				}

				if (ConfigHandler.getSickness()) {
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.id, strength * 20, 0));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onLivingUpdate()
	{
		if (worldObj.isDaytime() && !worldObj.isRemote && !isChild()) {
			float brightness = getBrightness(1.0F);
			BlockPos blockpos = new BlockPos(posX, (double) Math.round(posY), posZ);

			if (brightness > 0.5F && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F && worldObj.canSeeSky(blockpos)) {
				ItemStack itemstack = getEquipmentInSlot(4);

				if (itemstack != null) {
					if (itemstack.isItemStackDamageable()) {
						itemstack.setItemDamage(itemstack.getItemDamage() + rand.nextInt(2));

						if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
							renderBrokenItemStack(itemstack);
							setCurrentItemOrArmor(4, (ItemStack) null);
						}
					}
				}
			}
		}

		if (isRiding() && getAttackTarget() != null && ridingEntity instanceof EntityChicken) {
			((EntityLiving) ridingEntity).getNavigator().setPath(getNavigator().getPath(), 1.5D);
		}

		super.onLivingUpdate();
	}

	@Override
	protected boolean isValidLightLevel()
	{
		return true;
	}

	@Override
	public boolean getCanSpawnHere()
	{
		boolean notColliding = worldObj.getCollidingBoundingBoxes(this, getEntityBoundingBox()).isEmpty();
		boolean isLiquid = worldObj.isAnyLiquid(getEntityBoundingBox());
		// spawns on grass, sand, dirt, clay and very occasionally spawn on stone
		BlockPos bp = new BlockPos(posX, getEntityBoundingBox().minY - 1.0, posZ);
		Block block = worldObj.getBlockState(bp).getBlock();
		boolean isGrass = (block == Blocks.grass);
		boolean isSand = (block == Blocks.sand);
		boolean isClay = ((block == Blocks.hardened_clay) || (block == Blocks.stained_hardened_clay));
		boolean isDirt = (block == Blocks.dirt);
		boolean isStone = (rand.nextInt(8) == 0) && (block == Blocks.stone);

		return (isGrass || isSand || isStone || isClay || isDirt) && notColliding && !isLiquid;
	}

	public boolean checkForNearbyTorches()
	{
		boolean result = false;
		final double radius = 2.0;
		AxisAlignedBB aabb = getEntityBoundingBox();

		int xMin = MathHelper.floor_double((aabb.minX - radius) / 16.0D);
		int xMax = MathHelper.floor_double((aabb.maxX + radius) / 16.0D);
		int yMin = MathHelper.floor_double((aabb.minY - radius) / 16.0D);
		int yMax = MathHelper.floor_double((aabb.maxY + radius) / 16.0D);
		int zMin = MathHelper.floor_double((aabb.minZ - radius) / 16.0D);
		int zMax = MathHelper.floor_double((aabb.maxZ + radius) / 16.0D);

		for (int x = xMin; x <= xMax; x++) {
			for (int y = yMin; y <= yMax; y++) {
				for (int z = zMin; z <= zMax; z++) {
					Block block = worldObj.getBlockState(new BlockPos(x, y, z)).getBlock();
					if (block instanceof BlockTorch) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		getDataWatcher().addObject(12, (byte) 0);
		getDataWatcher().addObject(13, (byte) 0);
		getDataWatcher().addObject(14, (byte) 0);
	}

	@Override
	public int getTotalArmorValue()
	{
		int armor = super.getTotalArmorValue() + 2;

		if (armor > 20) {
			armor = 20;
		}

		return armor;
	}

	@Override
	protected boolean canDespawn()
	{
		return !isConverting();
	}

	@Override
	public boolean isChild()
	{
		return getDataWatcher().getWatchableObjectByte(12) == 1;
	}

	public void setChild(boolean childZombie)
	{
		getDataWatcher().updateObject(12, (byte) (childZombie ? 1 : 0));

		if (this.worldObj != null && !this.worldObj.isRemote) {
			IAttributeInstance attributeinstance = getEntityAttribute(SharedMonsterAttributes.movementSpeed);
			attributeinstance.removeModifier(speedBoost);

			if (childZombie) {
				attributeinstance.applyModifier(speedBoost);
			}
		}
		setChildSize(childZombie);
	}

	@Override
	protected final void setSize(float width, float height)
	{
		boolean isSizeValid = zombieWidth > 0.0F && zombieHeight > 0.0F;
		zombieWidth = width;
		zombieHeight = height;

		if (!isSizeValid) {
			multiplySize(1.0F);
		}
	}

	public void setChildSize(boolean isChild)
	{
		multiplySize(isChild ? 0.5F : 1.0F);
	}

	protected final void multiplySize(float size)
	{
		super.setSize(this.zombieWidth * size, this.zombieHeight * size);
	}

	public boolean isVillager()
	{
		return getDataWatcher().getWatchableObjectByte(13) == 1;
	}

	public void setVillager(boolean set)
	{
		getDataWatcher().updateObject(13, (byte) (set ? 1 : 0));
	}

	@Override
	public void onUpdate()
	{
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
	public void onStruckByLightning(EntityLightningBolt entityLightningBolt)
	{
		// A little surprise... BOOM!
		worldObj.newExplosion(this, posX, posY, posZ, 0.5F, true, true);
		dealFireDamage(5);
		setFire(8);
	}

	public int getAttackStrength(Entity entity)
	{
		ItemStack itemstack = getHeldItem();
		int strength = 4;

		if (itemstack != null) {
			strength += 2; // would be nice to add the held item's damage capability
		}

		return strength;
	}

	@Override
	protected String getLivingSound()
	{
		return "mob.zombie.say";
	}

	@Override
	protected String getHurtSound()
	{
		return "mob.zombie.hurt";
	}

	@Override
	protected String getDeathSound()
	{
		return "mob.zombie.death";
	}

	@Override
	protected void playStepSound(BlockPos p_180429_1_, Block p_180429_2_)
	{
		playSound("mob.zombie.step", 0.15F, 1.0F);
	}

	@Override
	protected Item getDropItem()
	{
		// returns the held item or armor
		ItemStack heldItem = getHeldItem();
		if (heldItem != null) {
			return heldItem.getItem();
		} else {
			return Items.rotten_flesh;
		}
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute()
	{
		return EnumCreatureAttribute.UNDEAD;
	}

	@Override
	protected void addRandomArmor()
	{
		switch (rand.nextInt(3)) {
			case 0:
				dropItem(Items.iron_ingot, 1);
				break;
			case 1:
				dropItem(Items.carrot, 1);
				break;
			case 2:
				dropItem(Items.potato, 1);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);

		if (isChild()) {
			nbt.setBoolean("IsBaby", true);
		}

		if (isVillager()) {
			nbt.setBoolean("IsVillager", true);
		}

		nbt.setInteger("ConversionTime", isConverting() ? conversionTime : -1);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
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
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
	{
		super.setEquipmentBasedOnDifficulty(difficulty);

		if (rand.nextFloat() < (worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
			switch (rand.nextInt(4)) {
				case 0:
					setCurrentItemOrArmor(0, new ItemStack(Items.diamond_sword));
					break;
				case 1:
					setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
					break;
				case 2:
					setCurrentItemOrArmor(0, new ItemStack(Items.diamond_shovel));
					break;
				case 3:
					setCurrentItemOrArmor(0, new ItemStack(Items.iron_shovel));
					break;
			}
		}
	}

	@Override
	public void onKillEntity(EntityLivingBase entityLiving)
	{
		super.onKillEntity(entityLiving);

		if (worldObj.getDifficulty().getDifficultyId() >= EnumDifficulty.NORMAL.getDifficultyId() && (entityLiving instanceof EntityVillager)) {
			if (worldObj.getDifficulty() == EnumDifficulty.NORMAL && rand.nextBoolean()) {
				return;
			}

			EntityCrackedZombie crackedZombie = new EntityCrackedZombie(worldObj);
			crackedZombie.copyLocationAndAnglesFrom(entityLiving);
			worldObj.removeEntity(entityLiving);
			crackedZombie.onSpawnFirstTime(this.worldObj.getDifficultyForLocation(new BlockPos(crackedZombie)), (IEntityLivingData) null);
			crackedZombie.setVillager(true);

			if (entityLiving.isChild()) {
				crackedZombie.setChild(true);
			}

			worldObj.spawnEntityInWorld(crackedZombie);
			worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1016, new BlockPos(posX, posY, posZ), 0);
		}
	}

	public boolean isConverting()
	{
		return this.getDataWatcher().getWatchableObjectByte(14) == 1;
	}

	@Override
	public IEntityLivingData onSpawnFirstTime(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
		Object zombieGroupData = super.onSpawnFirstTime(difficulty, livingdata);
		float additionalDifficulty = difficulty.getClampedAdditionalDifficulty();
		setCanPickUpLoot(rand.nextFloat() < 0.55F * additionalDifficulty);

		if (zombieGroupData == null) {
			zombieGroupData = new EntityCrackedZombie.GroupData(worldObj.rand.nextFloat() < ForgeModContainer.zombieBabyChance, worldObj.rand.nextFloat() < 0.05F, null);
		}

		if (zombieGroupData instanceof EntityCrackedZombie.GroupData) {
			EntityCrackedZombie.GroupData groupdata = (EntityCrackedZombie.GroupData) zombieGroupData;

			if (groupdata.isVillager) {
				setVillager(true);
			}

			if (groupdata.isChild) {
				setChild(true);

				if ((double) worldObj.rand.nextFloat() < 0.05D) {
					List list = worldObj.getEntitiesWithinAABB(EntityChicken.class, getEntityBoundingBox().expand(5.0D, 3.0D, 5.0D), IEntitySelector.IS_STANDALONE);

					if (!list.isEmpty()) {
						EntityChicken entitychicken = (EntityChicken) list.get(0);
						entitychicken.setChickenJockey(true);
						mountEntity(entitychicken);
					}
				} else if ((double) worldObj.rand.nextFloat() < 0.05D) {
					EntityChicken chicken = new EntityChicken(worldObj);
					chicken.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
					chicken.onSpawnFirstTime(difficulty, (IEntityLivingData) null);
					chicken.setChickenJockey(true);
					
					worldObj.spawnEntityInWorld(chicken);
					mountEntity(chicken);
				}
			}
		}

		setEquipmentBasedOnDifficulty(difficulty);
		setEnchantmentBasedOnDifficulty(difficulty);

        if (getEquipmentInSlot(4) == null) {
            Calendar calendar = this.worldObj.getCurrentDate();
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int month = calendar.get(Calendar.MONTH) + 1 ;

            if (month == 10 && day == 31 && rand.nextFloat() < 0.25F) { // halloween
                setCurrentItemOrArmor(4, new ItemStack(rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                equipmentDropChances[4] = 0.0F;
            }
        }
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).applyModifier(new AttributeModifier("Random spawn bonus", rand.nextDouble() * 0.05D, 0));
		double spawnBonus = rand.nextDouble() * 1.5D * (double) additionalDifficulty;

		if (spawnBonus > 1.0D) {
			getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random zombie-spawn bonus", spawnBonus, 2));
		}

		if (rand.nextFloat() < additionalDifficulty * 0.05F) {
			getEntityAttribute(reinforcements).applyModifier(new AttributeModifier("Leader zombie bonus", rand.nextDouble() * 0.25D + 0.5D, 0));
			getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(new AttributeModifier("Leader zombie bonus", rand.nextDouble() * 3.0D + 1.0D, 2));
		}

		return (IEntityLivingData) zombieGroupData;
	}

	@Override
	public boolean interact(EntityPlayer entityPlayer)
	{
		ItemStack equippedItem = entityPlayer.getCurrentEquippedItem();

		if (equippedItem != null && equippedItem.getItem() == Items.golden_apple && equippedItem.getItemDamage() == 0 && isVillager() && isPotionActive(Potion.weakness)) {
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

	protected void startConversion(int conTime)
	{
		conversionTime = conTime;
		getDataWatcher().updateObject(14, (byte) 1);
		removePotionEffect(Potion.weakness.id);
		addPotionEffect(new PotionEffect(Potion.damageBoost.id, conTime, Math.min(worldObj.getDifficulty().getDifficultyId() - 1, 0)));
		worldObj.setEntityState(this, (byte) 16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleHealthUpdate(byte health)
	{
		if (health == 16) {
			worldObj.playSound(posX + 0.5D, posY + 0.5D, posZ + 0.5D, "mob.zombie.remedy", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		} else {
			super.handleHealthUpdate(health);
		}
	}

	protected void convertToVillager()
	{
		EntityVillager villager = new EntityVillager(worldObj);
		villager.copyLocationAndAnglesFrom(this);
		villager.onSpawnFirstTime(this.worldObj.getDifficultyForLocation(new BlockPos(villager)), (IEntityLivingData) null);
		villager.setLookingForHome();

		if (isChild()) {
			villager.setGrowingAge(-24000);
		}

		worldObj.removeEntity(this);
		worldObj.spawnEntityInWorld(villager);
		villager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
		worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1017, new BlockPos(posX, posY, posZ), 0);
	}

	protected int getConversionTimeBoost()
	{
		int boostTime = 1;

		if (rand.nextFloat() < 0.01F) {
			int count = 0;

			for (double x = posX - 4; x < posX + 4 && count < 14; ++x) {
				for (double y = posY - 4; y < posY + 4 && count < 14; ++y) {
					for (double z = posZ - 4; z < posZ + 4 && count < 14; ++z) {
						Block block = worldObj.getBlockState(new BlockPos(x, y, z)).getBlock();

						if (block == Blocks.iron_bars || block == Blocks.bed) {
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

	class GroupData implements IEntityLivingData {

		public boolean isChild;
		public boolean isVillager;

		private GroupData(boolean setChild, boolean setVillager)
		{
			this.isChild = false;
			this.isVillager = false;
			this.isChild = setChild;
			this.isVillager = setVillager;
		}

		GroupData(boolean setChild, boolean setVillager, Object object)
		{
			this(setChild, setVillager);
		}
	}

}
