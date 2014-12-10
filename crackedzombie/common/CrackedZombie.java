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
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.common;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
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
import net.minecraftforge.common.config.Configuration;

@Mod(
		modid = CrackedZombie.modid,
		name = CrackedZombie.name,
		version = CrackedZombie.version
)

public class CrackedZombie {

	public static final String version = "1.7.10";
	public static final String modid = "crackedzombiemod";
	public static final String name = "Cracked Zombie Mod";
	public static final String zombieName = "CrackedZombie";
	
	@Mod.Instance(modid)
	public static CrackedZombie instance;

	private int zombieSpawnProb;
	private boolean zombieSpawns;
	private boolean spawnCreepers;
	private boolean spawnSkeletons;
	private boolean spawnEnderman;
	private boolean spawnSpiders;
	private boolean spawnSlime;
	private boolean spawnWitches;
	private boolean randomSkins;
	private boolean doorBusting;
	private boolean sickness;
	private int minSpawn;
	private int maxSpawn;

	
	@SidedProxy(
			clientSide = "com.crackedzombie.client.ClientProxyCrackedZombie",
			serverSide = "com.crackedzombie.common.CommonProxyCrackedZombie"
	)

	public static CommonProxyCrackedZombie proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		String generalComments = CrackedZombie.name + " Config\nMichael Sheppard (crackedEgg)\n"
				+ " For Minecraft Version " + CrackedZombie.version + "\n";
		String spawnProbComment = "zombieSpawnProb adjust to probability of zombies spawning\n"
				+ "The higher the number the more likely zombies will spawn.";
		String zombieComment = "zombieSpawns allows/disallows default zombies spawns, default is false,\n"
				+ "no default minecraft zombies will spawn. Only the " + zombieName + "s will spawn.\n"
				+ "If set to true, fewer CrackedZombies will spawn.";
		String creeperComment = "creeperSpawns, set to false to disable creeper spawning, set to true\n"
				+ "if you want to spawn creepers";
		String skeletonComment = "skeletonSpawns, set to false to disable skeleton spawning, set to true\n"
				+ "if you want to spawn skeletons";
		String endermanComment = "endermanSpawns, set to false to disable enderman spawning, set to true\n"
				+ "if you want to spawn enderman";
		String spiderComment = "spiderSpawns, set to false to disable spider spawning, set to true\n"
				+ "if you want to spawn spiders";
		String slimeComment = "slimeSpawns, set to false to disable slime spawning, set to true\n"
				+ "if you want to spawn slimes";
		String witchComment = "witchSpawns, set to false to disable witch spawning, set to true\n"
				+ "if you want to spawn witches";
		String doorBustingComment = "doorBusting, set to true to have zombies try to break down doors,\n"
				+ "otherwise set to false. It's quieter.";
		String sicknessComment = "Sickness, set to true to have contact with zombies poison the player.";
		String minSpawnComment = "minSpawn, minimum number of crackedzombies per spawn event";
		String maxSpawnComment = "maxSpawn, maximum number of crackedzombies per spawn event";

		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		zombieSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "zombieSpawnProb", 15, spawnProbComment).getInt();
		zombieSpawns = config.get(Configuration.CATEGORY_GENERAL, "zombieSpawns", false, zombieComment).getBoolean(false);
		spawnCreepers = config.get(Configuration.CATEGORY_GENERAL, "spawnCreepers", false, creeperComment).getBoolean(false);
		spawnSkeletons = config.get(Configuration.CATEGORY_GENERAL, "spawnSkeletons", false, skeletonComment).getBoolean(false);
		spawnEnderman = config.get(Configuration.CATEGORY_GENERAL, "spawnEnderman", false, endermanComment).getBoolean(false);
		spawnSpiders = config.get(Configuration.CATEGORY_GENERAL, "spawnSpiders", true, spiderComment).getBoolean(true);
		spawnSlime = config.get(Configuration.CATEGORY_GENERAL, "spawnSlime", false, slimeComment).getBoolean(false);
		spawnWitches = config.get(Configuration.CATEGORY_GENERAL, "spawnWitches", true, witchComment).getBoolean(true);
		doorBusting = config.get(Configuration.CATEGORY_GENERAL, "doorBusting", false, doorBustingComment).getBoolean(false);
		sickness = config.get(Configuration.CATEGORY_GENERAL, "sickness", false, sicknessComment).getBoolean(false);
		minSpawn = config.get(Configuration.CATEGORY_GENERAL, "minSpawn", 2, minSpawnComment).getInt();
		maxSpawn = config.get(Configuration.CATEGORY_GENERAL, "maxSpawn", 10, maxSpawnComment).getInt();

		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);

		config.save();

		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityCrackedZombie.class, zombieName, id, 0x00AFAF, 0x799C45);
//		EntityRegistry.registerModEntity(EntityCrackedZombie.class, zombieName, id, this, 80, 3, true);

//		proxy.registerRenderers();
//		proxy.registerWorldHandler();
	}

	@EventHandler
	public void Init(FMLInitializationEvent evt)
	{
		proxy.registerRenderers();
		// zombies should spawn in dungeon spawners
		DungeonHooks.addDungeonMob(zombieName, 200);
		// add steel swords to the loot. you may need these.
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(Items.iron_sword), 1, 1, 4));
		
//		FMLCommonHandler.instance().bus().register(new WorldTickHandler());
	}
	
    @EventHandler
	public void PostInit(FMLPostInitializationEvent event)
	{
		BiomeDictionary.registerAllBiomesAndGenerateEvents();
		
		proxy.print("*** Scanning for available biomes");
		BiomeGenBase[] allBiomes = Iterators.toArray(Iterators.filter(Iterators.forArray(BiomeGenBase.getBiomeGenArray()),	Predicates.notNull()), BiomeGenBase.class);
		printBiomeList(allBiomes);

		EntityRegistry.addSpawn(EntityCrackedZombie.class, zombieSpawnProb, minSpawn, maxSpawn, EnumCreatureType.MONSTER, allBiomes);
		
		// remove zombie spawning, we are replacing Minecraft zombies with CrackedZombies!
		if (!zombieSpawns) {
			proxy.print("*** Disabling default zombie spawns for all biomes");
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Zombie");
		} else {
			proxy.print("NOT disabling default zombie spawns, there will be fewer crackedZombies!");
		}
		
		// optionally remove creeper, skeleton, enderman, spaiders and slime spawns for these biomes
		if (!spawnCreepers) {
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.print("*** Removing creeper spawns");
		}
		if (!spawnSkeletons) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Skeleton");
			proxy.print("*** Removing skeleton spawns and dungeon spawners");
		}
		if (!spawnEnderman) {
			EntityRegistry.removeSpawn(EntityEnderman.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.print("*** Removing enderman spawns");
		}
		if (!spawnSpiders) {
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, allBiomes);
			DungeonHooks.removeDungeonMob("Spider");
			proxy.print("*** Removing spider spawns and dungeon spawners");
		}
		if (!spawnSlime) {
			EntityRegistry.removeSpawn(EntitySlime.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.print("*** Removing slime spawns");
		}
		
		if (!spawnWitches) {
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, allBiomes);
			proxy.print("*** Removing witch spawns");
		}
	}
	
	public void printBiomeList(BiomeGenBase[] biomes)
	{
		for (BiomeGenBase bgb : biomes) {
			proxy.print("  >>> Including biome " + bgb.biomeName + " for spawning");
		}
	}
	
	public boolean getRandomSkins()
	{
		return randomSkins;
	}

	public boolean getDoorBusting()
	{
		return doorBusting;
	}
	
	public boolean getSickness()
	{
		return sickness;
	}

}
