package it.nonlodico34.ironbatteries.block.monitor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class MultiblockMonitorHelper {

    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 32;

    public enum BlockPosition implements StringRepresentable {
        SINGLE("single"),
        TOP_LEFT("top_left"),
        TOP_RIGHT("top_right"),
        BOTTOM_LEFT("bottom_left"),
        BOTTOM_RIGHT("bottom_right"),
        TOP_EDGE("top_edge"),
        BOTTOM_EDGE("bottom_edge"),
        LEFT_EDGE("left_edge"),
        RIGHT_EDGE("right_edge"),
        INNER("inner");

        private final String name;

        BlockPosition(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static class MultiblockData {
        public final int size;
        public final BlockPos masterPos;
        public final Direction facing;
        public final Set<BlockPos> allPositions;

        public MultiblockData(int size, BlockPos masterPos, Direction facing, Set<BlockPos> allPositions) {
            this.size = size;
            this.masterPos = masterPos;
            this.facing = facing;
            this.allPositions = allPositions;
        }
    }

    public static MultiblockData detectMultiblockStructure(Level level, BlockPos pos, Direction facing) {
        if (level.isClientSide) return null;

        BlockState startState = level.getBlockState(pos);
        if (!(startState.getBlock() instanceof MonitorBlock)) return null;
        if (startState.getValue(MonitorBlock.FACING) != facing) return null;

        Direction rightDir = getDirectionsFromFacing(facing)[0];

        Set<BlockPos> connected = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        connected.add(pos);
        queue.add(pos);

        int minX = 0, maxX = 0;
        int minY = 0, maxY = 0;

        int maxIter = MAX_SIZE * MAX_SIZE * 2;
        int iters = 0;

        while(!queue.isEmpty() && iters < maxIter) {
            BlockPos curr = queue.poll();
            iters++;

            int dx = 0;
            int dy = curr.getY() - pos.getY();

            if (rightDir == Direction.EAST) dx = curr.getX() - pos.getX();
            else if (rightDir == Direction.WEST) dx = pos.getX() - curr.getX();
            else if (rightDir == Direction.SOUTH) dx = curr.getZ() - pos.getZ();
            else if (rightDir == Direction.NORTH) dx = pos.getZ() - curr.getZ();

            if (dx < minX) minX = dx;
            if (dx > maxX) maxX = dx;
            if (dy < minY) minY = dy;
            if (dy > maxY) maxY = dy;

            BlockPos[] neighbors = {
                curr.above(), curr.below(),
                curr.relative(rightDir), curr.relative(rightDir.getOpposite())
            };

            for (BlockPos n : neighbors) {
                if (!connected.contains(n)) {
                    BlockState nState = level.getBlockState(n);
                    if (nState.getBlock() instanceof MonitorBlock && nState.getValue(MonitorBlock.FACING) == facing) {
                        connected.add(n);
                        queue.add(n);
                    }
                }
            }
        }

        int width = maxX - minX + 1;
        int height = maxY - minY + 1;

        if (width != height || width > MAX_SIZE || width < MIN_SIZE) {
            return null;
        }

        if (connected.size() != width * height) {
            return null;
        }

        BlockPos masterPos = pos.relative(rightDir, minX).above(minY);
        return new MultiblockData(width, masterPos, facing, connected);
    }

    public static Direction[] getDirectionsFromFacing(Direction facing) {
        return switch (facing) {
            case NORTH -> new Direction[]{Direction.WEST, Direction.NORTH};
            case SOUTH -> new Direction[]{Direction.EAST, Direction.SOUTH};
            case EAST -> new Direction[]{Direction.NORTH, Direction.EAST};
            case WEST -> new Direction[]{Direction.SOUTH, Direction.WEST};
            default -> new Direction[]{Direction.WEST, Direction.NORTH};
        };
    }

    public static BlockPosition getBlockPosition(BlockPos blockPos, BlockPos masterPos, int size, Direction facing) {
        if (size == 1 || masterPos == null) {
            return BlockPosition.SINGLE;
        }

        Direction[] dirs = getDirectionsFromFacing(facing);
        Direction rightDir = dirs[0];

        int yPos = blockPos.getY() - masterPos.getY();
        int xPos = -1;

        for (int x = 0; x < size; x++) {
            if (masterPos.relative(rightDir, x).above(yPos).equals(blockPos)) {
                xPos = x;
                break;
            }
        }

        if (xPos == -1) {
            return BlockPosition.SINGLE;
        }

        boolean isTop = yPos == size - 1;
        boolean isBottom = yPos == 0;

        boolean isLeft = xPos == 0;
        boolean isRight = xPos == size - 1;

        if (isTop && isLeft) return BlockPosition.TOP_LEFT;
        if (isTop && isRight) return BlockPosition.TOP_RIGHT;
        if (isBottom && isLeft) return BlockPosition.BOTTOM_LEFT;
        if (isBottom && isRight) return BlockPosition.BOTTOM_RIGHT;

        if (isTop) return BlockPosition.TOP_EDGE;
        if (isBottom) return BlockPosition.BOTTOM_EDGE;
        if (isLeft) return BlockPosition.LEFT_EDGE;
        if (isRight) return BlockPosition.RIGHT_EDGE;

        return BlockPosition.INNER;
    }

    public static BlockPos getMasterBlockPos(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MonitorBlockEntity)) {
            return null;
        }

        BlockState state = level.getBlockState(pos);
        Direction facing = state.getValue(MonitorBlock.FACING);

        MultiblockData data = detectMultiblockStructure(level, pos, facing);
        if (data != null && data.size > 1) {
            return data.masterPos;
        }

        return null;
    }
}
