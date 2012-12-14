package walkingdead.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import walkingdead.common.CommonProxyWalkingDead;
import walkingdead.common.EntityWalkingDead;
import walkingdead.common.RenderWalkingDead;

public class ClientProxyWalkingDead extends CommonProxyWalkingDead {
	@Override
    public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityWalkingDead.class, new RenderWalkingDead());
	}
}
