package it.nonlodico34.ironbatteries.block.atmosphericcondenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AtmosphericCondenserBlock extends LightningRodBlock implements EntityBlock {
    public AtmosphericCondenserBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onLightningStrike(BlockState state, Level level, BlockPos pos) {
        super.onLightningStrike(state, level, pos);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AtmosphericCondenserBlockEntity condenser) {
            condenser.onLightningStrike();
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AtmosphericCondenserBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof AtmosphericCondenserBlockEntity condenser) {
                AtmosphericCondenserBlockEntity.tick(lvl, pos, st, condenser);
            }
        };
    }
}
