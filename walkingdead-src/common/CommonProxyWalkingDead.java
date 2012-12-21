package walkingdead.common;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxyWalkingDead {
	public void registerRenderers() {}
	public void registerServerTickHandler(){
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}
}
