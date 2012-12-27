package walkingdead.common;

import javax.swing.text.html.parser.Entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTwardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWalkingDead extends EntityMob {
	
	private int conversionTime = 0;

	public EntityWalkingDead(World world) {
		super(world);

		texture = "/mob/zombie.png";
		moveSpeed = 0.25F;
		getNavigator().setBreakDoors(true);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIBreakDoor(this));
		tasks.addTask(2, new EntityAILeapAtTarget(this, moveSpeed + 0.01F));
		tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, moveSpeed, false));
		tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, moveSpeed, true));
		tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityChicken.class, moveSpeed, false));
		tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityPig.class, moveSpeed, false));
		tasks.addTask(4, new EntityAIMoveTwardsRestriction(this, moveSpeed));
		tasks.addTask(5, new EntityAIMoveThroughVillage(this, moveSpeed, false));
		tasks.addTask(6, new EntityAIWander(this, moveSpeed));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 16.0F, 0, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 16.0F, 0, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityChicken.class, 16.0F, 0, false));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPig.class, 16.0F, 0, false));
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
	
	public boolean getCanSpawnHere() {
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(boundingBox.minY);
		int z = MathHelper.floor_double(posZ);
        
		boolean isClear = worldObj.checkIfAABBIsClear(boundingBox);
		boolean notColliding = worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty();
		boolean isLiquid = worldObj.isAnyLiquid(boundingBox);
		boolean isGrass = worldObj.getBlockId(x, y - 1, z) == Block.grass.blockID;
		boolean isSand = worldObj.getBlockId(x, y - 1, z) == Block.sand.blockID;
		
        return (isGrass || isSand) && isClear && notColliding && !isLiquid;
    }
	
	public float getSpeedModifier() {
		return super.getSpeedModifier() * (isChild() ? 1.5F : 1.0F);
	}

	protected void entityInit() {
		super.entityInit();
		getDataWatcher().addObject(12, Byte.valueOf((byte) 0));
		getDataWatcher().addObject(13, Byte.valueOf((byte) 0));
		getDataWatcher().addObject(14, Byte.valueOf((byte) 0));
	}

	@SideOnly(Side.CLIENT)
	public String getTexture() {
		return isVillager() ? "/mob/zombie_villager.png"	: "/mob/zombie.png";
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
		getDataWatcher().updateObject(12, Byte.valueOf((byte) 1));
	}

	public boolean isVillager() {
		return getDataWatcher().getWatchableObjectByte(13) == 1;
	}

	public void setIsVillager(boolean set) {
		getDataWatcher().updateObject(13, Byte.valueOf((byte) (set ? 1 : 0)));
	}

	public void onUpdate() {
		if (!worldObj.isRemote && getEnchantment()) {
			int boost = getConversionTimeBoost();
			conversionTime -= boost;

			if (conversionTime <= 0) {
				convertToVillager();
			}
		}

		super.onUpdate();
	}

	public int getAttackStrength(Entity entity) {
		ItemStack itemstack = getHeldItem();
		int strength = 4;

		if (itemstack != null) {
			strength += itemstack.getDamageVsEntity(this);
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
		func_85030_a("mob.zombie.step", 0.15F, 1.0F);
	}

	protected int getDropItemId() {
		return Item.rottenFlesh.shiftedIndex;
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	protected void dropRareDrop(int unused) {
		switch (rand.nextInt(3)) {
		case 0:
			dropItem(Item.ingotIron.shiftedIndex, 1);
			break;
		case 1:
			dropItem(Item.carrot.shiftedIndex, 1);
			break;
		case 2:
			dropItem(Item.potato.shiftedIndex, 1);
		}
	}

	protected void SetHeldItem() {
		super.func_82164_bB();

		if (rand.nextFloat() < (worldObj.difficultySetting == 2 ? 0.05F : 0.01F)) {
			if (rand.nextInt(3) == 0) {
				setCurrentItemOrArmor(0, new ItemStack(Item.swordSteel));
			} else {
				setCurrentItemOrArmor(0, new ItemStack(Item.shovelSteel));
			}
		}
	}

	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		if (isChild()) {
			nbt.setBoolean("IsBaby", true);
		}

		if (isVillager()) {
			nbt.setBoolean("IsVillager", true);
		}

		nbt.setInteger("ConversionTime", getEnchantment() ? conversionTime : -1);
	}

	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		if (nbt.getBoolean("IsBaby")) {
			setChild(true);
		}

		if (nbt.getBoolean("IsVillager")) {
			setIsVillager(true);
		}

		if (nbt.hasKey("ConversionTime") && nbt.getInteger("ConversionTime") > -1) {
			startConversion(nbt.getInteger("ConversionTime"));
		}
	}

	public void onKillEntity(EntityLiving entityLiving) {
		super.onKillEntity(entityLiving);

		if (worldObj.difficultySetting >= 2 && entityLiving instanceof EntityVillager) {
			if (worldObj.difficultySetting == 2 && rand.nextBoolean()) {
				return;
			}

			EntityWalkingDead walker = new EntityWalkingDead(worldObj);
			walker.func_82149_j(entityLiving);
			worldObj.setEntityDead(entityLiving);
			walker.initCreature();
			walker.setIsVillager(true);

			if (entityLiving.isChild()) {
				walker.setChild(true);
			}

			worldObj.spawnEntityInWorld(walker);
			worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1016, (int) posX, (int) posY, (int) posZ, 0);
		}
	}

	public void initCreature() {
		canPickUpLoot = rand.nextFloat() < pickUpLootProability[worldObj.difficultySetting]; // 1.4.6 - pickUpLootProability

		if (worldObj.rand.nextFloat() < 0.05F) {
			setIsVillager(true);
		}

		SetHeldItem();
		func_82162_bC();
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

	public boolean getEnchantment() {
		return getDataWatcher().getWatchableObjectByte(14) == 1;
	}

	protected void convertToVillager() {
		EntityVillager villager = new EntityVillager(worldObj);
		villager.func_82149_j(this);
		villager.initCreature();
		villager.func_82187_q();

		if (isChild()) {
			villager.setGrowingAge(-24000);
		}

		worldObj.setEntityDead(this);
		worldObj.spawnEntityInWorld(villager);
		villager.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
		worldObj.playAuxSFXAtEntity((EntityPlayer) null, 1017, (int) posX, (int) posY, (int) posZ, 0);
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

}
