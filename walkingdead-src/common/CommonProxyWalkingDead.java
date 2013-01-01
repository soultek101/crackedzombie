package walkingdead.common;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxyWalkingDead {
	public void registerRenderers() {}
	
	public void registerServerTickHandler() {
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}
	
}
