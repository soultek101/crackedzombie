package walkingdead.common;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.EnumHelper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.Configuration;
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
	modid = "WalkingDeadMod",
	name = "WalkingDead Mod",
	version = "1.4.6"
)

@NetworkMod (
	clientSideRequired = true,
	serverSideRequired = false
)

public class WalkingDead {
	
	public String getVersion() {
		return "1.4.6";
	}
	
	@Instance
	public static WalkingDead instance;
	
	private int walkerSpawnProb;
	private int walkerSpawns;
	private boolean spawnCreepers;
	private boolean spawnZombies;
	private boolean spawnSkeletons;
	
	@SidedProxy(
		clientSide = "walkingdead.client.ClientProxyWalkingDead",
		serverSide = "walkingdead.common.CommonProxyWalkingDead"
	)
	
	public static CommonProxyWalkingDead proxy;
//	public static EnumCreatureType walkerType = EnumHelper.addCreatureType("walker", EntityWalkingDead.class, 50, Material.air, false);
	
	public WalkingDead() {
	}
	
	@PreInit
	public void preLoad(FMLPreInitializationEvent event) {
		String generalComments = "WalkingDead Mod Config\nMichael Sheppard (crackedEgg)\n";
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
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		walkerSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "walkerSpawnProb", 10, spawnProbComment).getInt();
		walkerSpawns = config.get(Configuration.CATEGORY_GENERAL, "walkerSpawns", 30, walkerComment).getInt();
		spawnCreepers = config.get(Configuration.CATEGORY_GENERAL, "spawnCreepers", false, creeperComment).getBoolean(false);
		spawnSkeletons = config.get(Configuration.CATEGORY_GENERAL, "spawnSkeletons", false, skeletonComment).getBoolean(false);
		spawnZombies = config.get(Configuration.CATEGORY_GENERAL, "spawnZombies", false, zombieComment).getBoolean(false);
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, generalComments);
		
		config.save();
		
		proxy.registerRenderers();
		proxy.registerServerTickHandler();
	}
	
	@Init
	public void load(FMLInitializationEvent evt) {
		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityWalkingDead.class, "WalkingDead", id, 0x00AFAF, 0x799C45);
		LanguageRegistry.instance().addStringLocalization("entity.WalkingDead.name", "Walker");
		
//		EnumCreatureType walkerType = EnumHelper.addCreatureType("walker", EntityWalkingDead.class, 50, Material.air, false);
		
		EntityRegistry.addSpawn(EntityWalkingDead.class, walkerSpawnProb, 2, 10, /*walkerType*/EnumCreatureType.monster);
		
		// remove creeper, skeleton, and zombie spawns for these biomes
		BiomeGenBase[] biomes = { BiomeGenBase.beach, BiomeGenBase.desert, BiomeGenBase.desertHills,
				BiomeGenBase.extremeHills, BiomeGenBase.forest, BiomeGenBase.forestHills, BiomeGenBase.icePlains,
				BiomeGenBase.jungle, BiomeGenBase.plains, BiomeGenBase.river, BiomeGenBase.swampland, BiomeGenBase.taiga,
				BiomeGenBase.iceMountains, BiomeGenBase.icePlains, BiomeGenBase.jungleHills, BiomeGenBase.mushroomIsland,
				BiomeGenBase.mushroomIslandShore, BiomeGenBase.taigaHills
		};
		if (!spawnCreepers) {
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
		}
		if (!spawnSkeletons) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
		}
		if (!spawnZombies) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
		}
	}
	
	public int getWalkerSpawns() {
		return walkerSpawns;
	}
	
}
