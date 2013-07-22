//  
//  =====GPL=============================================================
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; version 2 dated June, 1991.
// 
//  This program is distributed in the hope that it will be useful, 
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
// 
//  You should have received a copy of the GNU General Public License
//  along with this program;  if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave., Cambridge, MA 02139, USA.
//  =====================================================================
//

//
//

package walkingdead.common;

import java.util.EnumSet;

import net.minecraft.world.WorldServer;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ServerTickHandler implements ITickHandler {
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if (type.contains(TickType.WORLD)) {
			WorldServer world = (WorldServer)tickData[0];
			if (world.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
				if (world.rand.nextInt(8) == 0) { // rand.nextBoolean()
					SpawnerWalkingDead.despawnWalker(world, WalkingDead.class);
				}
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.contains(TickType.WORLD)) {
			WorldServer world = (WorldServer)tickData[0];
			if (world.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
				if (world.rand.nextInt(8) == 4) { // rand.nextBoolean()
					SpawnerWalkingDead.SpawnWalkers(world);
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}

	@Override
	public String getLabel() {
		return "WalkingDead";
	}

}
