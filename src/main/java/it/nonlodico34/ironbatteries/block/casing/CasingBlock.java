package it.nonlodico34.ironbatteries.block.casing;

import it.nonlodico34.ironbatteries.network.NetworkNodeBlock;
import it.nonlodico34.ironbatteries.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CasingBlock extends NetworkNodeBlock implements EntityBlock {
    public CasingBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CasingBlockEntity(pos, state);
    }
}

