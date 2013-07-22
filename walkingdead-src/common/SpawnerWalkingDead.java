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


package walkingdead.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerWalkingDead {
	private static HashMap eligibleChunksForSpawning = new HashMap();
    private static Random rand = new Random(System.currentTimeMillis());

	protected static ChunkPosition getRandomSpawningPointInChunk(World world, int x, int z) {
		Chunk chunk = world.getChunkFromChunkCoords(x, z);
		int newX = x * 16 + world.rand.nextInt(16);
		int newY = world.rand.nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1);
		int newZ = z * 16 + world.rand.nextInt(16);
		
		return new ChunkPosition(newX, newY, newZ);
	}
	
	public static int SpawnWalkers(WorldServer worldServer) {
		eligibleChunksForSpawning.clear();

        for (int entities = 0; entities < worldServer.playerEntities.size(); ++entities) {
            EntityPlayer entityPlayer = (EntityPlayer)worldServer.playerEntities.get(entities);
            int playerX = MathHelper.floor_double(entityPlayer.posX / 16.0D);
            int playerZ = MathHelper.floor_double(entityPlayer.posZ / 16.0D);
            byte range = 8;

            for (int x = -range; x <= range; ++x) {
                for (int z = -range; z <= range; ++z) {
                    boolean inRange = x == -range || x == range || z == -range || z == range;
                    ChunkCoordIntPair chunkCoord = new ChunkCoordIntPair(x + playerX, z + playerZ);

                    if (!inRange)  {
                        eligibleChunksForSpawning.put(chunkCoord, Boolean.valueOf(false));
                    } else if (!eligibleChunksForSpawning.containsKey(chunkCoord)) {
                        eligibleChunksForSpawning.put(chunkCoord, Boolean.valueOf(true));
                    }
                }
            }
        }
        
        int eligibleChunks = 0;
        ChunkCoordinates spawnPoint = worldServer.getSpawnPoint();
        EnumCreatureType creatureType = EnumCreatureType.monster;
        // spawn random amount of walkers upto walkerSpawns count.
		int walkerSpawns = rand.nextInt(WalkingDead.instance.getWalkerSpawns());

        if (worldServer.countEntities(EntityWalkingDead.class) < walkerSpawns) {
	        Iterator iter = eligibleChunksForSpawning.keySet().iterator();
	        ArrayList<ChunkCoordIntPair> tmp = new ArrayList(eligibleChunksForSpawning.keySet());
	        Collections.shuffle(tmp);
	        iter = tmp.iterator();
	            
	        while (iter.hasNext()) { // iterate through the eligible chunks
	            ChunkCoordIntPair chunkPair = (ChunkCoordIntPair)iter.next();
	                
	            if (!((Boolean)eligibleChunksForSpawning.get(chunkPair)).booleanValue()) {
	                ChunkPosition chunkPos = getRandomSpawningPointInChunk(worldServer, chunkPair.chunkXPos, chunkPair.chunkZPos);
	
	                boolean normalBlock = worldServer.isBlockNormalCube(chunkPos.x, chunkPos.y, chunkPos.z);
	                Material material = worldServer.getBlockMaterial(chunkPos.x, chunkPos.y, chunkPos.z);
	                if (!normalBlock && material == creatureType.getCreatureMaterial()) {
	                    int nSpawned = 0;
	                    int spawnCount = 0;
	
	                    while (spawnCount < 3) {
	                        int newX = chunkPos.x;
	                        int newY = chunkPos.y;
	                        int newZ = chunkPos.z;
	                        final byte chunkRange = 6;
	                        int spawnAttempts = 0;
	                        EntityLivingData entityLivingData = null;
	
	                        while (true) {
	                            if (spawnAttempts < 4) {
	                                newX += worldServer.rand.nextInt(chunkRange) - worldServer.rand.nextInt(chunkRange);
	                                newY += worldServer.rand.nextInt(1) - worldServer.rand.nextInt(1);
	                                newZ += worldServer.rand.nextInt(chunkRange) - worldServer.rand.nextInt(chunkRange);
	
	                                if (canCreatureTypeSpawnAtLocation(creatureType, worldServer, newX, newY, newZ)) {
	                                    float adjX = (float)newX + 0.5F;
	                                    float adjY = (float)newY;
	                                    float adjZ = (float)newZ + 0.5F;
	
	                                    if (worldServer.getClosestPlayer((double)adjX, (double)adjY, (double)adjZ, 12.0D) == null) {
	                                        float deltaX = adjX - (float)spawnPoint.posX;
	                                        float deltaY = adjY - (float)spawnPoint.posY;
	                                        float deltaZ = adjZ - (float)spawnPoint.posZ;
	                                        float distance = MathHelper.sqrt_float(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	
	                                        if (distance >= 16.0F) {
	                                            EntityWalkingDead walker = new EntityWalkingDead(worldServer);
	                                            walker.setLocationAndAngles((double)adjX, (double)adjY, (double)adjZ, worldServer.rand.nextFloat() * 360.0F, 0.0F);
	
	                                            if (walker.getCanSpawnHere() && nSpawned < walker.getMaxSpawnedInChunk()) {
	                                                ++nSpawned;
	                                                worldServer.spawnEntityInWorld(walker);
	                                                entityLivingData = walker.func_110161_a(entityLivingData);
//                                                	System.out.println("Spawned a walker: " + adjX + ", " + adjY + ", " + adjZ + " (" + nSpawned + ")");
	                                            }
	                                            eligibleChunks += nSpawned;
	                                        }
	                                    }
	                                }
	                                ++spawnAttempts;
	                                continue;
	                            }
	                            ++spawnCount;
	                            break;
	                        }
	                    }
	                }
	            }
	        }
        }
        return eligibleChunks;
    }

	public static boolean canCreatureTypeSpawnAtLocation(EnumCreatureType creatureType, World world, int x, int y, int z) {
		if (!world.doesBlockHaveSolidTopSurface(x, y - 1, z)) {
			return false;
		} else {
			int blockID = world.getBlockId(x, y - 1, z);
			boolean spawnBlock = (Block.blocksList[blockID] != null && Block.blocksList[blockID].canCreatureSpawn(creatureType, world, x, y - 1, z));
			return spawnBlock && blockID != Block.bedrock.blockID && !world.isBlockNormalCube(x, y, z) && !world.getBlockMaterial(x, y, z).isLiquid() && !world.isBlockNormalCube(x, y + 1, z);
		}
	}
	
	// This function is derived from DrZharks Custom Mob Spawner. It has been modified to suit my needs.
	// http://www.minecraftforum.net/topic/769339-10-custom-mob-spawner/
	public static int despawnWalker(WorldServer worldObj, Class cls) {
    	int count = 0;
    	
    	for (int j = 0; j < worldObj.loadedEntityList.size(); j++) {
            Entity entity = (Entity)worldObj.loadedEntityList.get(j);
            if (!(entity instanceof EntityWalkingDead)) {
            	continue;
            }
            count += entityDespawnCheck(worldObj, (EntityLivingBase)entity);
        }
        return count;
    }
	
	// This function is derived from DrZharks Custom Mob Spawner. It has been modified to suit my needs.
	// http://www.minecraftforum.net/topic/769339-10-custom-mob-spawner/
	protected static int entityDespawnCheck(WorldServer worldObj, EntityLivingBase entity) {
        EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(entity, -1D);
        if (entityplayer != null) {
            double deltaX = ((Entity) (entityplayer)).posX - entity.posX;
            double deltaY = ((Entity) (entityplayer)).posY - entity.posY;
            double deltaZ = ((Entity) (entityplayer)).posZ - entity.posZ;
            double distance = MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance > 128.0) {
            	entity.setDead();
//            	System.out.println("Walker has been set dead (distance: " + distance + ")");
            	return 1;
            }
            int age = entity.getAge();
            if (age > 600 && worldObj.rand.nextInt(800) == 0 && distance > 32.0) {
            	entity.setDead();
//            	System.out.println("Walker has been set dead (age: " + age +")");
            	return 1;
            }
        }
        return 0;
    }

}
