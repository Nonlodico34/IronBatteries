package it.nonlodico34.ironbatteries.block.redstonelink;

import it.nonlodico34.ironbatteries.network.NetworkNodeBlockEntity;
import it.nonlodico34.ironbatteries.network.NetworkRootBlockEntity;
import it.nonlodico34.ironbatteries.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import it.nonlodico34.ironbatteries.bigleagues.IBigEnergyStorage;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class RedstoneLinkBlockEntity extends NetworkNodeBlockEntity {

    private int lastSignal = -1;

    public RedstoneLinkBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REDSTONE_LINK_BLOCK_ENTITY.get(), pos, state);
    }

    public int getRedstoneSignal() {
        NetworkRootBlockEntity root = getRootEntity();
        if (root != null && root.hasInfiniteBattery()) {
            return 0;
        }

        IBigEnergyStorage energy = getEnergyStorage();
        if (energy != null && energy.getMaxEnergyStored().compareTo(BigInteger.ZERO) > 0) {
            BigDecimal stored = new BigDecimal(energy.getEnergyStored());
            BigDecimal max = new BigDecimal(energy.getMaxEnergyStored());
            double ratio = stored.divide(max, 4, RoundingMode.HALF_UP).doubleValue();
            return (int) Math.round(ratio * 15.0);
        }
        return 0;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        
        int currentSignal = getRedstoneSignal();
        if (currentSignal != lastSignal) {
            lastSignal = currentSignal;
            BlockState currentState = getBlockState();
            if (currentState.hasProperty(RedstoneLinkBlock.POWER) && currentState.getValue(RedstoneLinkBlock.POWER) != currentSignal) {
                level.setBlock(worldPosition, currentState.setValue(RedstoneLinkBlock.POWER, currentSignal), 3);
            } else {
                level.updateNeighborsAt(worldPosition, currentState.getBlock());
            }
        }
    }
}
