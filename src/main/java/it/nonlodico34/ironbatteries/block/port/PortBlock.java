package it.nonlodico34.ironbatteries.block.port;

import it.nonlodico34.ironbatteries.network.NetworkNodeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class PortBlock extends NetworkNodeBlock implements EntityBlock {

    public static final EnumProperty<PortMode> MODE =
            EnumProperty.create("mode", PortMode.class);

    public PortBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(MODE, PortMode.BOTH)
        );
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (player.isShiftKeyDown()) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

        if (!stack.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            PortMode currentMode = state.getValue(MODE);

            PortMode nextMode = switch (currentMode) {
                case BOTH -> PortMode.INPUT;
                case INPUT -> PortMode.OUTPUT;
                case OUTPUT -> PortMode.BOTH;
            };

            level.setBlock(
                    pos,
                    state.setValue(MODE, nextMode),
                    Block.UPDATE_ALL
            );
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        super.createBlockStateDefinition(builder);
        builder.add(MODE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide
                ? null
                : (lvl, pos, st, be) -> {
            if (be instanceof PortBlockEntity portEntity) {
                PortBlockEntity.tick(lvl, pos, st, portEntity);
            }
        };
    }

    public enum PortMode implements StringRepresentable {
        BOTH("both"),
        INPUT("input"),
        OUTPUT("output");

        private final String name;

        PortMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
