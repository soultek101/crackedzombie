//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; modversion 2 dated June, 1991.
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

import static com.crackedzombie.common.ConfigHandler.updateConfigInfo;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod( modid = CrackedZombie.modid, name = CrackedZombie.name, version = CrackedZombie.modversion, guiFactory = CrackedZombie.guifactory )

public class CrackedZombie {

	public static final String mcversion = "1.8.0";
	public static final String modversion = "3.1.0";
	public static final String modid = "crackedzombiemod";
	public static final String name = "Cracked Zombie Mod";
	public static final String zombieName = "CrackedZombie";
	public static final String guifactory = "com.crackedzombie.client.CrackedZombieConfigGUIFactory";
	
	@Mod.Instance(modid)
	public static CrackedZombie instance;

	@SidedProxy(
			clientSide = "com.crackedzombie.client.ClientProxyCrackedZombie",
			serverSide = "com.crackedzombie.common.CommonProxyCrackedZombie"
	)

	public static CommonProxyCrackedZombie proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigHandler.startConfig(event);

		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityCrackedZombie.class, zombieName, id, 0x00AFAF, 0x799C45);
	}

	@Mod.EventHandler
	public void Init(FMLInitializationEvent evt)
	{
		FMLCommonHandler.instance().bus().register(CrackedZombie.instance);
		
		proxy.registerRenderers();
		// zombies should spawn in dungeon spawners
		DungeonHooks.addDungeonMob(zombieName, 200);
		// add steel swords to the loot. you may need these.
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(Items.iron_sword), 1, 1, 4));
	}
	
    @Mod.EventHandler
	public void PostInit(FMLPostInitializationEvent event)
	{
		BiomeDictionary.registerAllBiomesAndGenerateEvents();
		
		proxy.info("*** Scanning for available biomes");
		BiomeGenBase[] allBiomes = Iterators.toArray(Iterators.filter(Iterators.forArray(BiomeGenBase.getBiomeGenArray()),	Predicates.notNull()), BiomeGenBase.class);
		printBiomeList(allBiomes);

		int zombieSpawnProb = ConfigHandler.getZombieSpawnProbility();
		int minSpawn = ConfigHandler.getMinSpawn();
		int maxSpawn = ConfigHandler.getMaxSpawn();
		EntityRegistry.addSpawn(EntityCrackedZombie.class, zombieSpawnProb, minSpawn, maxSpawn, EnumCreatureType.MONSTER, allBiomes);
		
		// remove zombie spawning, we are replacing Minecraft zombies with CrackedZombies!
		if (!ConfigHandler.getZombieSpawns()) {
			proxy.info("*** Disabling default zombie spawns for all biomes");
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Zombie");
		} else {
			proxy.info("NOT disabling default zombie spawns, there will be fewer crackedZombies!");
		}
		
		// optionally remove creeper, skeleton, enderman, spiders and slime spawns for these biomes
		if (!ConfigHandler.getSpawnCreepers()) {
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.info("*** Removing creeper spawns");
		}
		if (!ConfigHandler.getSpawnSkeletons()) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Skeleton");
			proxy.info("*** Removing skeleton spawns and dungeon spawners");
		}
		if (!ConfigHandler.getSpawnEnderman()) {
			EntityRegistry.removeSpawn(EntityEnderman.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.info("*** Removing enderman spawns");
		}
		if (!ConfigHandler.getSpawnSpiders()) {
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Spider");
			proxy.info("*** Removing spider spawns and dungeon spawners");
		}
		if (!ConfigHandler.getSpawnSlime()) {
			EntityRegistry.removeSpawn(EntitySlime.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.info("*** Removing slime spawns");
		}
		
		if (!ConfigHandler.getSpawnWitches()) {
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.info("*** Removing witch spawns");
		}
	}
	
	public void printBiomeList(BiomeGenBase[] biomes)
	{
		for (BiomeGenBase bgb : biomes) {
			proxy.info("  >>> Including biome " + bgb.biomeName + " for spawning");
		}
	}
	
	// user has changed entries in the GUI config. save the results.
	@SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(CrackedZombie.modid)) {
			if (event.requiresMcRestart) {
				CrackedZombie.proxy.info("The configuration changes require a Minecraft restart!");
			}
			CrackedZombie.proxy.info("Configuration changes have been updated for the " + CrackedZombie.name);
            updateConfigInfo();
		}
    }
	
}
