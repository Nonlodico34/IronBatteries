package it.nonlodico34.ironbatteries.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public abstract class NetworkRootBlock extends NetworkNodeBlock {
    public NetworkRootBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            rebuildConnectedComponent(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            List<BlockPos> seeds = new ArrayList<>();
            for (Direction dir : Direction.values()) {
                seeds.add(pos.relative(dir));
            }

            super.onRemove(state, level, pos, newState, isMoving);

            if (!level.isClientSide) {
                rebuildFromSeeds(level, seeds);
            }
        }
    }

    public static void globalRebuild(Level level, BlockPos rootPos) {
        rebuildConnectedComponent(level, rootPos);
    }

    public static void rebuildConnectedComponent(Level level, BlockPos seedPos) {
        NetworkRootBlockEntity.rebuildNetwork(level, seedPos);
    }

    public static void rebuildFromSeeds(Level level, Collection<BlockPos> seeds) {
        NetworkRootBlockEntity.rebuildFromSeeds(level, seeds);
    }
}
