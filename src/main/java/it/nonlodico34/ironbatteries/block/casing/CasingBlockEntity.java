package it.nonlodico34.ironbatteries.block.casing;

import it.nonlodico34.ironbatteries.network.NetworkNodeBlockEntity;
import it.nonlodico34.ironbatteries.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CasingBlockEntity extends NetworkNodeBlockEntity {
    public CasingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CASING_BLOCK_ENTITY.get(), pos, state);
    }
}

