package it.nonlodico34.ironbatteries.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

public abstract class NetworkNodeBlock extends Block {
    public NetworkNodeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            NetworkRootBlock.rebuildConnectedComponent(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            java.util.List<BlockPos> seeds = new java.util.ArrayList<>();
            for (Direction dir : Direction.values()) {
                seeds.add(pos.relative(dir));
            }

            super.onRemove(state, level, pos, newState, isMoving);

            if (!level.isClientSide) {
                NetworkRootBlock.rebuildFromSeeds(level, seeds);
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof NetworkNodeBlockEntity be) {
                NetworkRootBlockEntity rootEntity = be.getRootEntity();
                if (rootEntity != null) {
                    BlockPos root = rootEntity.getBlockPos();
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("Controller pos: ")
                                    .append(net.minecraft.network.chat.Component.literal(root.getX() + ", " + root.getY() + ", " + root.getZ())
                                            .withStyle(net.minecraft.ChatFormatting.AQUA)),
                            true
                    );
                } else {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("No controller connected!")
                                    .withStyle(net.minecraft.ChatFormatting.RED),
                            true
                    );
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        return InteractionResult.SUCCESS;
    }
}
