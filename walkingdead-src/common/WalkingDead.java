package walkingdead.common;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityMob;
import net.minecraft.src.EnumCreatureType;
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
	version = "1.4.5"
)

@NetworkMod (
	clientSideRequired = true,
	serverSideRequired = false
)

public class WalkingDead {
	
	public String getVersion() {
		return "1.4.5";
	}
	
	@Instance
	public static WalkingDead instance;
	
	private int walkerSpawnProb;
	
	@SidedProxy(
		clientSide = "walkingdead.client.ClientProxyWalkingDead",
		serverSide = "walkingdead.common.CommonProxyWalkingDead"
	)
	
	public static CommonProxyWalkingDead proxy;
	
	public WalkingDead() {
	}
	
	@PreInit
	public void preLoad(FMLPreInitializationEvent event) {
		String comments = " WalkingDead Mod Config\n Michael Sheppard (crackedEgg)\n";
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		walkerSpawnProb = config.get(Configuration.CATEGORY_GENERAL, "walkerSpawnProb", 32).getInt();
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, comments);
		
		config.save();
		
		proxy.registerRenderers();
		proxy.registerServerTickHandler();
	}
	
	@Init
	public void load(FMLInitializationEvent evt) {
		int id = EntityRegistry.findGlobalUniqueEntityId();
		EntityRegistry.registerGlobalEntityID(EntityWalkingDead.class, "WalkingDead", id, 0x00AFAF, 0x799C45);
		LanguageRegistry.instance().addStringLocalization("entity.WalkingDead.name", "Walker");
		EntityRegistry.addSpawn(EntityWalkingDead.class, walkerSpawnProb, 1, 8, EnumCreatureType.monster);
	}
	
}
