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

import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerJoinedWorldEventHandler {
	
	
	public PlayerJoinedWorldEventHandler()
	{
		CrackedZombie.proxy.info("PlayerJoinWorldEvent ctor");
	}
	
	@SubscribeEvent
	public void onPlayerJoinedEvent(EntityJoinWorldEvent event)
	{
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			if (!inventoryContainsSword(player.inventory)) {
				ItemStack itemstack = new ItemStack(chooseRandomSwordType());
				if (ConfigHandler.getEnchantSword()) { // you must like this player!
					itemstack.addEnchantment(Enchantment.unbreaking, 3);
					itemstack.addEnchantment(Enchantment.knockback, 2);
					itemstack.addEnchantment(Enchantment.flame, 2);
				}
				player.setCurrentItemOrArmor(0, itemstack);
			}
		}
	}
	
	// search inventory for a sword
	public static boolean inventoryContainsSword(InventoryPlayer inventory)
	{
		boolean result = false;
		for (ItemStack s : inventory.mainInventory) {
			if (s != null && s.getItem() instanceof ItemSword) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public Item chooseRandomSwordType()
	{
		Random rand = new Random();
		Item item;
		switch (rand.nextInt(5)) {
			case 0:
				item = Items.diamond_sword;
				break;
			case 1:
				item = Items.stone_sword;
				break;
			case 2:
				item = Items.wooden_sword;
				break;
			case 3:
				item = Items.iron_sword;
				break;
			case 4:
				item = Items.golden_sword;
				break;
			default:
				item = Items.iron_sword;
				break;
		}
		return item;
	}
}
