package it.nonlodico34.ironbatteries.block.monitor;

import it.nonlodico34.ironbatteries.network.NetworkNodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class MonitorBlock extends NetworkNodeBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<MultiblockMonitorHelper.BlockPosition> TYPE = EnumProperty.create("type", MultiblockMonitorHelper.BlockPosition.class);

    public MonitorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, MultiblockMonitorHelper.BlockPosition.SINGLE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(TYPE, MultiblockMonitorHelper.BlockPosition.SINGLE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MonitorBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide ? null :
                (lvl, pos, st, be) -> {
                    if (be instanceof MonitorBlockEntity monitor) {
                        MonitorBlockEntity.tick(lvl, pos, st, monitor);
                    }
                };
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide) return;
        invalidateMultiblockStructure(level, pos, state.getValue(FACING));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && state.getBlock() instanceof MonitorBlock && !(newState.getBlock() instanceof MonitorBlock)) {
            invalidateMultiblockStructure(level, pos, state.getValue(FACING));
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private void invalidateMultiblockStructure(Level level, BlockPos startPos, Direction facing) {
        java.util.Queue<BlockPos> queue = new java.util.LinkedList<>();
        java.util.Set<BlockPos> visited = new java.util.HashSet<>();

        Direction[] dirs = MultiblockMonitorHelper.getDirectionsFromFacing(facing);
        Direction right = dirs[0];

        BlockPos[] adjacent = {
            startPos,
            startPos.relative(right), startPos.relative(right.getOpposite()),
            startPos.above(), startPos.below()
        };

        for (BlockPos adj : adjacent) {
            if (visited.add(adj)) {
                queue.add(adj);
            }
        }

        while (!queue.isEmpty()) {
            BlockPos curr = queue.poll();

            BlockState st = level.getBlockState(curr);
            if (!(st.getBlock() instanceof MonitorBlock)) continue;
            if (st.getValue(MonitorBlock.FACING) != facing) continue;

            BlockEntity be = level.getBlockEntity(curr);
            if (be instanceof MonitorBlockEntity monitor) {
                monitor.markForMultiblockUpdate();
            }

            BlockPos[] nextPos = {
                curr.relative(right), curr.relative(right.getOpposite()),
                curr.above(), curr.below()
            };

            for (BlockPos n : nextPos) {
                if (Math.abs(n.getX() - startPos.getX()) <= MultiblockMonitorHelper.MAX_SIZE &&
                    Math.abs(n.getY() - startPos.getY()) <= MultiblockMonitorHelper.MAX_SIZE &&
                    Math.abs(n.getZ() - startPos.getZ()) <= MultiblockMonitorHelper.MAX_SIZE) {
                    if (visited.add(n)) {
                        queue.add(n);
                    }
                }
            }
        }
    }
}
