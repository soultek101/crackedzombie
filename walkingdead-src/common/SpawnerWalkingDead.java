package walkingdead.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpecialSpawnEvent;

public final class SpawnerWalkingDead {
	private static HashMap eligibleChunksForSpawning = new HashMap();

	protected static ChunkPosition getRandomSpawningPointInChunk(World world, int x, int z) {
		Chunk chunk = world.getChunkFromChunkCoords(x, z);
		int newX = x * 16 + world.rand.nextInt(16);
		int newY = world.rand.nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 16 - 1);
		int newZ = z * 16 + world.rand.nextInt(16);
		
		return new ChunkPosition(newX, newY, newZ);
	}
	
	public static final int findChunksForSpawning(WorldServer worldServer) {
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
		final int walkerSpawns = WalkingDead.instance.getWalkerSpawns();
//		int maxMonsters = creatureType.getMaxNumberOfCreature() * eligibleChunksForSpawning.size() / 256;

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
	                                        float sqrDistance = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	
	                                        if (sqrDistance >= 256.0F) {
	                                            EntityWalkingDead walker = new EntityWalkingDead(worldServer);
	                                            walker.setLocationAndAngles((double)adjX, (double)adjY, (double)adjZ, worldServer.rand.nextFloat() * 360.0F, 0.0F);
	
	                                            if (walker.getCanSpawnHere() && nSpawned < walker.getMaxSpawnedInChunk()) {
	                                                ++nSpawned;
	                                                boolean spawned = worldServer.spawnEntityInWorld(walker);
	                                                walker.initCreature();
	                                                if (spawned) {
	                                                	System.out.println("Spawned a walker: " + adjX + ", " + adjY + ", " + adjZ + "(" + nSpawned + ")");
	                                                }
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
	
	public static int despawnWalker(WorldServer worldObj, Class cls) {
    	int count = 0;
    	
    	for (int j = 0; j < worldObj.loadedEntityList.size(); j++) {
            Entity entity = (Entity)worldObj.loadedEntityList.get(j);
            if (!(entity instanceof EntityWalkingDead)) {
            	continue;
            }
            count += entityDespawnCheck(worldObj, (EntityLiving)entity);
        }
        return count;
    }
	
	protected static int entityDespawnCheck(WorldServer worldObj, EntityLiving entity) {
        EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(entity, -1D);
        if (entityplayer != null) {
            double d = ((Entity) (entityplayer)).posX - entity.posX;
            double d1 = ((Entity) (entityplayer)).posY - entity.posY;
            double d2 = ((Entity) (entityplayer)).posZ - entity.posZ;
            double d3 = d * d + d1 * d1 + d2 * d2;
            if (d3 > 16384D) {
            	entity.setDead();
            	System.out.println("Walker has been set dead (distance)");
            	return 1;
            }
            if (entity.getAge() > 600 && worldObj.rand.nextInt(800) == 0) {
                if (d3 < 1024D) {
                	entity.attackEntityFrom(null, 0);
                	System.out.println("Walker has been attackEntityFrom'd");
                } else {
                	entity.setDead();
                	System.out.println("Walker has been set dead (age)");
                	return 1;
                }
            }
        }
        return 0;
    }

}
