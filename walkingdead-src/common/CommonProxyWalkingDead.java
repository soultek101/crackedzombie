package walkingdead.common;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.registry.TickRegistry;

public class CommonProxyWalkingDead {
	public void registerRenderers() {}
	public void registerServerTickHandler(){
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}
}
