package walkingdead.client;

import walkingdead.common.CommonProxyWalkingDead;
import walkingdead.common.EntityWalkingDead;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxyWalkingDead extends CommonProxyWalkingDead {
	@Override
    public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityWalkingDead.class, new RenderWalkingDead());
	}
}
