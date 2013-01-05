//
// This work is licensed under the Creative Commons
// Attribution-ShareAlike 3.0 Unported License. To view a copy of this
// license, visit http://creativecommons.org/licenses/by-sa/3.0/
//

package walkingdead.client;

import walkingdead.common.EntityWalkingDead;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeHooks;

@SideOnly(Side.CLIENT)
public class ModelWalkingDead extends ModelBiped {
	private float armAngle;
	
	public ModelWalkingDead() {
		this(0.0F, false);
	}

	protected ModelWalkingDead(float par1, float par2, int par3, int par4) {
		super(par1, par2, par3, par4);
	}

	public ModelWalkingDead(float par1, boolean par2) {
		super(par1, 0.0F, 64, par2 ? 32 : 64);
	}

	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity) {
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
		
		float rightArmRotation = MathHelper.sin(onGround * (float) Math.PI);
		float leftARmRotation = MathHelper.sin((1.0F - (1.0F - onGround) * (1.0F - onGround)) * (float) Math.PI);
		bipedRightArm.rotateAngleZ = 0.0F;
		bipedLeftArm.rotateAngleZ = 0.0F;
		bipedRightArm.rotateAngleY = -(0.1F - rightArmRotation * 0.6F);
		bipedLeftArm.rotateAngleY = 0.1F - rightArmRotation * 0.6F;
		bipedRightArm.rotateAngleX = armAngle;//-((float) Math.PI / 2F);	
		bipedLeftArm.rotateAngleX = armAngle;//-((float) Math.PI / 2F);
		bipedRightArm.rotateAngleX -= rightArmRotation * 1.2F - leftARmRotation * 0.4F;
		bipedLeftArm.rotateAngleX -= rightArmRotation * 1.2F - leftARmRotation * 0.4F;
		bipedRightArm.rotateAngleZ += MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
		bipedLeftArm.rotateAngleZ -= MathHelper.cos(par3 * 0.09F) * 0.05F + 0.05F;
		bipedRightArm.rotateAngleX += MathHelper.sin(par3 * 0.067F) * 0.05F;
		bipedLeftArm.rotateAngleX -= MathHelper.sin(par3 * 0.067F) * 0.05F;
	}
	
	public void setLivingAnimations(EntityLiving entityliving, float f, float f1, float f2) {
		EntityWalkingDead walker = (EntityWalkingDead) entityliving;
		if (walker.getHasTarget()) {
			armAngle = -((float) Math.PI / 2F);	
		} else {
			armAngle = 0.0F;
		}
	}
	
}
