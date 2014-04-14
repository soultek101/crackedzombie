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
//
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.client;

import com.crackedzombie.common.CommonProxyCrackedZombie;
import com.crackedzombie.common.EntityCrackedZombie;
import cpw.mods.fml.client.registry.RenderingRegistry;
//import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
//import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxyCrackedZombie extends CommonProxyCrackedZombie {

	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityCrackedZombie.class, new RenderCrackedZombie());
	}
	
	@Override
	public void registerHandlers()
	{
		// allow this mod to load if there are missing mappings
//		FMLClientHandler.instance().setDefaultMissingAction(FMLMissingMappingsEvent.Action.IGNORE);
	}
}
