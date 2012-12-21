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
				if (world.rand.nextBoolean()) {
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
				if (world.rand.nextBoolean()) {
					SpawnerWalkingDead.findChunksForSpawning(world);
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
