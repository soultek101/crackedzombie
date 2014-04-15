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
// Copyright 2011-2014 Michael Sheppard (crackedEgg)
//
package com.crackedzombie.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
//import net.minecraftforge.common.util.ForgeDirection;

public final class SpawnerCrackedZombie {

	private static final HashMap<ChunkCoordIntPair, Boolean> eligibleChunksForSpawning = new HashMap<ChunkCoordIntPair, Boolean>();
	private static final Random rand = new Random(System.currentTimeMillis());
	private static final double chunkSize = 16.0;

	protected static ChunkPosition getRandomSpawningPointInChunk(World world, int x, int z)
	{
		Chunk chunk = world.getChunkFromChunkCoords(x, z);
		int newX = x * 16 + world.rand.nextInt(16);
		int newY = world.rand.nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1);
		int newZ = z * 16 + world.rand.nextInt(16);

		return new ChunkPosition(newX, newY, newZ);
	}

	public static int SpawnCrackedZombies(WorldServer worldServer)
	{
		eligibleChunksForSpawning.clear();

		for (int entities = 0; entities < worldServer.playerEntities.size(); ++entities) {
			EntityPlayer entityPlayer = (EntityPlayer) worldServer.playerEntities.get(entities);
			int playerX = MathHelper.floor_double(entityPlayer.posX / chunkSize);
			int playerZ = MathHelper.floor_double(entityPlayer.posZ / chunkSize);
			int range = (int) (chunkSize / 2.0);

			for (int x = -range; x <= range; ++x) {
				for (int z = -range; z <= range; ++z) {
					boolean inRange = x == -range || x == range || z == -range || z == range;
					ChunkCoordIntPair chunkCoord = new ChunkCoordIntPair(x + playerX, z + playerZ);
					eligibleChunksForSpawning.put(chunkCoord, inRange);
				}
			}
		}

		int totalSpawned = 0;
		ChunkCoordinates spawnPoint = worldServer.getSpawnPoint();
		EnumCreatureType creatureType = EnumCreatureType.monster;
		// spawn random amount of zombies upto zombieSpawns count.
		int zombieSpawns = rand.nextInt(CrackedZombie.instance.getZombieSpawns());

		if (worldServer.countEntities(EntityCrackedZombie.class) < zombieSpawns) {
			Iterator iter;// = eligibleChunksForSpawning.keySet().iterator();
			ArrayList<ChunkCoordIntPair> tmp = new ArrayList<ChunkCoordIntPair>(eligibleChunksForSpawning.keySet());
			Collections.shuffle(tmp);
			iter = tmp.iterator();

			while (iter.hasNext()) { // iterate through the eligible chunks
				ChunkCoordIntPair chunkPair = (ChunkCoordIntPair) iter.next();

				if (!(eligibleChunksForSpawning.get(chunkPair))) {
					ChunkPosition chunkPos = getRandomSpawningPointInChunk(worldServer, chunkPair.chunkXPos, chunkPair.chunkZPos);
					int x = chunkPos.chunkPosX;
					int y = chunkPos.chunkPosY;
					int z = chunkPos.chunkPosZ;
					if (!worldServer.getBlock(x, y, z).isNormalCube() && worldServer.getBlock(x, y, z).getMaterial() == creatureType.getCreatureMaterial()) {
						int nSpawned = 0;
						int spawnCount = 0;

						while (spawnCount < 3) {
							int newX = chunkPos.chunkPosX;
							int newY = chunkPos.chunkPosY;
							int newZ = chunkPos.chunkPosZ;
							final byte chunkRange = 6;
							int spawnAttempts = 0;
							IEntityLivingData entityLivingData = null;

							while (true) {
								if (spawnAttempts < 4) {
									newX += worldServer.rand.nextInt(chunkRange) - worldServer.rand.nextInt(chunkRange);
									newY += worldServer.rand.nextInt(1) - worldServer.rand.nextInt(1);
									newZ += worldServer.rand.nextInt(chunkRange) - worldServer.rand.nextInt(chunkRange);

									if (canCreatureTypeSpawnAtLocation(creatureType, worldServer, newX, newY, newZ)) {
										float adjX = (float) newX + 0.5F;
										float adjY = (float) newY;
										float adjZ = (float) newZ + 0.5F;

										if (worldServer.getClosestPlayer((double) adjX, (double) adjY, (double) adjZ, 12.0D) == null) {
											float deltaX = adjX - (float) spawnPoint.posX;
											float deltaY = adjY - (float) spawnPoint.posY;
											float deltaZ = adjZ - (float) spawnPoint.posZ;
											float distance = MathHelper.sqrt_float(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

											if (distance >= 16.0F) {
												EntityCrackedZombie zombie = new EntityCrackedZombie(worldServer);
												zombie.setLocationAndAngles((double) adjX, (double) adjY, (double) adjZ, worldServer.rand.nextFloat() * 360.0F, 0.0F);

												if (zombie.getCanSpawnHere() && nSpawned < zombie.getMaxSpawnedInChunk()) {
													++nSpawned;
													worldServer.spawnEntityInWorld(zombie);
													entityLivingData = zombie.onSpawnWithEgg(entityLivingData);
													CrackedZombie.proxy.print("*** Spawned a CrackedZombie");
												}
												totalSpawned += nSpawned;
											}
										}
									}
									++spawnAttempts;
									continue;
								} // if spawnAttempts < 4
								++spawnCount;
								break;
							} // while true
						}
					}
				}
			}
		}
		return totalSpawned;
	}

	public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, int x, int y, int z)
	{
		if (!World.doesBlockHaveSolidTopSurface(world, x, y, z)) {//isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
			return false;
		} else {
			Block block = world.getBlock(x, y - 1, z);
			boolean spawnBlock = (block != null && block.canCreatureSpawn(creatureType, world, x, y - 1, z));
			return spawnBlock && block != Blocks.bedrock && !world.isBlockNormalCubeDefault(x, y, z, true) && !world.getBlock(x, y, z).getMaterial().isLiquid() && !world.isBlockNormalCubeDefault(x, y + 1, z, true);
		}
	}

	// This function is derived from DrZharks Custom Mob Spawner. It has been modified to suit my needs.
	// http://www.minecraftforum.net/topic/769339-10-custom-mob-spawner/
	public static int despawnCrackedZombie(WorldServer worldObj, Class cls)
	{
		int count = 0;

		for (Object loadedEntityList : worldObj.loadedEntityList) {
			Entity entity = (Entity) loadedEntityList;
			if (!(entity instanceof EntityCrackedZombie)) {
				continue;
			}
			count += entityDespawnCheck(worldObj, (EntityLivingBase) entity);
		}
		return count;
	}

	// This function is derived from DrZharks Custom Mob Spawner. It has been modified to suit my needs.
	// http://www.minecraftforum.net/topic/769339-10-custom-mob-spawner/
	protected static int entityDespawnCheck(WorldServer worldObj, EntityLivingBase entity)
	{
		EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(entity, -1D);
		if (entityplayer != null) {
			double deltaX = ((Entity) (entityplayer)).posX - entity.posX;
			double deltaY = ((Entity) (entityplayer)).posY - entity.posY;
			double deltaZ = ((Entity) (entityplayer)).posZ - entity.posZ;
			double distance = MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
			if (distance > 576.0) {
				entity.setDead();
				return 1;
			}
			if (entity.getAge() > 2400 && worldObj.rand.nextInt(800) == 0 && distance > 32.0) {
				entity.setDead();
				return 1;
			}
		}
		return 0;
	}

}
