//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package walkingdead.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import reptiles.common.Reptiles;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenEnd;
import net.minecraft.world.biome.BiomeGenHell;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod (
	modid = WalkingDead.modid,
	name = WalkingDead.name,
	version = WalkingDead.version
)

@NetworkMod (
	clientSideRequired = true,
	serverSideRequired = false,
	versionBounds = "[1.4.7]"
)

public class WalkingDead {
	
	public String getVersion() {
		return WalkingDead.version;
	}
	
	@Instance
	public static WalkingDead instance;
	
	public static final String version = "1.4.7";
	public static final String modid = "WalkingDeadMod";
	public static final String name = "WalkingDead Mod";
	
	private int walkerSpawnProb;
	private int walkerSpawns;
	private boolean spawnCreepers;
	private boolean spawnZombies;
	private boolean spawnSkeletons;
	private boolean spawnEnderman;
	private boolean spawnSpiders;
	private boolean spawnSlime;
	
	private Logger logger;
	
	@SidedProxy(
		clientSide = "walkingdead.client.ClientProxyWalkingDead",
		serverSide = "walkingdead.common.CommonProxyWalkingDead"
	)
	
	public static CommonProxyWalkingDead proxy;
	
	@PreInit
	public void preLoad(FMLPreInitializationEvent event) {
		logger = Logger.getLogger(WalkingDead.modid);
		logger.setParent(FMLLog.getLogger());
		
		String generalComments = WalkingDead.name + " Config\nMichael Sheppard (crackedEgg)\n";
		String spawnProbComment = "walkerSpawnProb adjust to probability of walkers spawning,\n"
						+ "although the custom spawning most likely overrides this. the higher the\n"
						+ "the number the more likely walkers will spawn.";
		String walkerComment = "walkerSpawns adjusts the number of walkers spawned, play\n"
				        + "with it to see what you like. The higher the number the more\n"
				        + "walkers will spawn.";
		String creeperComment = "creeperSpawns, set to false to disable creeper spawning, set to true\n"
						+ "if you want to spawn creepers";
		String skeletonComment = "skeletonSpawns, set to false to disable skeleton spawning, set to true\n"
						+ "if you want to spawn skeletons";
		String zombieComment = "zombieSpawns, set to false to disable zombie spawning, set to true\n"
						+ "if you want to spawn zombies. Note that spawning zombies and other monsters\n"
						+ "will cause the number of walkers spawned to be reduced.";
		String endermanComment = "endermanSpawns, set to false to disable enderman spawning, set to true\n"
						+ "if you want to spawn enderman";
		String spiderComment = "spiderSpawns, set to false to disable spider spawning, set to true\n"
						+ "if you want to spawn spiders";
		String slimeComment = "slimeSpawns, set to false to disable slime spawning, set to true\n"
				+ "if you want to spawn slimes";
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		walkerSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "walkerSpawnProb", 10, spawnProbComment).getInt();
		walkerSpawns = config.get(Configuration.CATEGORY_GENERAL, "walkerSpawns", 60, walkerComment).getInt();
		spawnCreepers = config.get(Configuration.CATEGORY_GENERAL, "spawnCreepers", false, creeperComment).getBoolean(false);
		spawnSkeletons = config.get(Configuration.CATEGORY_GENERAL, "spawnSkeletons", false, skeletonComment).getBoolean(false);
		spawnZombies = config.get(Configuration.CATEGORY_GENERAL, "spawnZombies", false, zombieComment).getBoolean(false);
		spawnEnderman = config.get(Configuration.CATEGORY_GENERAL, "spawnEnderman", false, endermanComment).getBoolean(false);
		spawnSpiders = config.get(Configuration.CATEGORY_GENERAL, "spawnSpiders", true, spiderComment).getBoolean(false);
		spawnSlime = config.get(Configuration.CATEGORY_GENERAL, "spawnSlime", false, slimeComment).getBoolean(false);
		
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);
		
		config.save();
		
		proxy.registerRenderers();
		proxy.registerServerTickHandler();
	}
	
	@Init
	public void load(FMLInitializationEvent evt) {
		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityWalkingDead.class, "WalkingDead", id);
		EntityRegistry.registerModEntity(EntityWalkingDead.class, "WalkingDead", id, this, 80, 3, true);
		EntityList.entityEggs.put(Integer.valueOf(id), new EntityEggInfo(id, 0x00AFAF, 0x799C45));
		LanguageRegistry.instance().addStringLocalization("entity.WalkingDead.name", "Walker");
		
		// placing this function here may allow the walkers to spawn in biomes
		// created by other mods provided those mods are loaded before this one.
		logger.info("*** Scanning for available biomes");
		BiomeGenBase[] biomes = getBiomeList();
		
		EntityRegistry.addSpawn(EntityWalkingDead.class, walkerSpawnProb, 2, 10, EnumCreatureType.monster, biomes);
		
		// walkers should spawn in dungeon spawners
		DungeonHooks.addDungeonMob("WalkingDead", 200);
		// add steel swords to the loot. you may need these.
		ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(Item.swordSteel), 1, 1, 4));
		
		// optionally remove creeper, skeleton, and zombie spawns for these biomes
		if (!spawnCreepers) {
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
			logger.info("*** Removing creeper spawns");
		}
		if (!spawnSkeletons) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			logger.info("*** Removing skeleton spawns");
		}
		if (!spawnZombies) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			logger.info("*** Removing zombie spawns");
		}
		if (!spawnEnderman) {
			EntityRegistry.removeSpawn(EntityEnderman.class, EnumCreatureType.monster, biomes);
			logger.info("*** Removing enderman spawns");
		}
		if (!spawnSpiders) {
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			logger.info("*** Removing spider spawns");
		}
		if (!spawnSlime) {
			EntityRegistry.removeSpawn(EntitySlime.class, EnumCreatureType.monster, biomes);
			logger.info("*** Removing slime spawns");
		}
		
	}
	
	// This function should get all biomes that are derived from BiomeGenBase,
	// even those from other mods.
	public BiomeGenBase[] getBiomeList() {
		LinkedList linkedlist = new LinkedList();
		for (BiomeGenBase biomegenbase : BiomeGenBase.biomeList) {
			if (biomegenbase == null) {
				continue;
			}
			if (!(biomegenbase instanceof BiomeGenHell) && !(biomegenbase instanceof BiomeGenEnd)) {
				logger.info(" >>> Adding " + biomegenbase.biomeName + " for spawning");
				linkedlist.add(biomegenbase);
			}
		}
		return (BiomeGenBase[]) linkedlist.toArray(new BiomeGenBase[0]);
	}
	
	public int getWalkerSpawns() {
		return walkerSpawns;
	}
	
}
