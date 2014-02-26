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
package com.walkingdead.common;

import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.gameevent.TickEvent;

import net.minecraft.world.WorldServer;

public class WorldTickHandler {

	@Subscribe
	public void onTick(TickEvent.WorldTickEvent event)
	{
		WorldServer world = (WorldServer) event.world;
		if (event.phase.equals(TickEvent.Phase.START)) {
			if (world.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
				if (world.rand.nextInt(8) == 0) {
					SpawnerWalkingDead.despawnWalker(world, WalkingDead.class);
				}
			}
		} else if (event.phase.equals(TickEvent.Phase.END)) {
			if (world.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
				if (world.rand.nextInt(8) == 4) {
					SpawnerWalkingDead.SpawnWalkers(world);
				}
			}
		}

	}

}
