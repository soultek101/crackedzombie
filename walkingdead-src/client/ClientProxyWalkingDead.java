//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

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
