package it.nonlodico34.ironbatteries.block.port;

import it.nonlodico34.ironbatteries.block.batteryslot.BatterySlotBlockEntity;
import it.nonlodico34.ironbatteries.network.NetworkNodeBlockEntity;
import it.nonlodico34.ironbatteries.network.NetworkRootBlockEntity;
import it.nonlodico34.ironbatteries.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import it.nonlodico34.ironbatteries.bigleagues.IBigEnergyStorage;
import java.math.BigInteger;

public class PortBlockEntity extends NetworkNodeBlockEntity {

    private final IBigEnergyStorage energyStorage = new PortEnergyStorageImpl();

    public PortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PORT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public IBigEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PortBlockEntity be) {
        if (level.isClientSide || !state.hasProperty(PortBlock.MODE)) return;

        PortBlock.PortMode mode = state.getValue(PortBlock.MODE);
        if (mode == PortBlock.PortMode.BOTH) return;

        IBigEnergyStorage myEnergy = be.getEnergyStorage();
        if (myEnergy == null) return;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            if (level.getBlockEntity(neighborPos) instanceof BatterySlotBlockEntity) continue;

            IEnergyStorage neighborEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, dir.getOpposite());
            if (neighborEnergy != null) {
                if (mode == PortBlock.PortMode.INPUT) {
                    int toPull = neighborEnergy.extractEnergy(Integer.MAX_VALUE, true);
                    if (toPull > 0) {
                        BigInteger accepted = myEnergy.receiveEnergy(BigInteger.valueOf(toPull), false);
                        if (accepted.compareTo(BigInteger.ZERO) > 0) {
                            neighborEnergy.extractEnergy(accepted.intValue(), false);
                        }
                    }
                } else if (mode == PortBlock.PortMode.OUTPUT) {
                    BigInteger toPush = myEnergy.extractEnergy(BigInteger.valueOf(Integer.MAX_VALUE), true);
                    if (toPush.compareTo(BigInteger.ZERO) > 0) {
                        int accepted = neighborEnergy.receiveEnergy(toPush.intValue(), false);
                        if (accepted > 0) {
                            myEnergy.extractEnergy(BigInteger.valueOf(accepted), false);
                        }
                    }
                }
            }
        }
    }

    private class PortEnergyStorageImpl implements IBigEnergyStorage {

        private NetworkRootBlockEntity getRoot() {
            return PortBlockEntity.this.getRootEntity();
        }

        private BlockState getCurrentState() {
            if (PortBlockEntity.this.level == null) {
                return PortBlockEntity.this.getBlockState();
            }
            return PortBlockEntity.this.level.getBlockState(PortBlockEntity.this.worldPosition);
        }

        private boolean canInteract(boolean receiving, boolean extracting) {
            BlockState state = getCurrentState();

            if (!state.hasProperty(PortBlock.MODE)) {
                return false;
            }

            PortBlock.PortMode mode = state.getValue(PortBlock.MODE);

            boolean allowsInput =
                    mode == PortBlock.PortMode.BOTH ||
                            mode == PortBlock.PortMode.INPUT;

            boolean allowsOutput =
                    mode == PortBlock.PortMode.BOTH ||
                            mode == PortBlock.PortMode.OUTPUT;

            return (!receiving || allowsInput) && (!extracting || allowsOutput);
        }

        @Override
        public BigInteger receiveEnergy(BigInteger maxReceive, boolean simulate) {
            NetworkRootBlockEntity root = getRoot();

            if (root == null || !canInteract(true, false)) {
                return BigInteger.ZERO;
            }

            return root.getEnergyStorage().receiveEnergy(maxReceive, simulate);
        }

        @Override
        public BigInteger extractEnergy(BigInteger maxExtract, boolean simulate) {
            NetworkRootBlockEntity root = getRoot();

            if (root == null || !canInteract(false, true)) {
                return BigInteger.ZERO;
            }

            return root.getEnergyStorage().extractEnergy(maxExtract, simulate);
        }

        @Override
        public boolean canReceive() {
            NetworkRootBlockEntity root = getRoot();

            return root != null
                    && canInteract(true, false)
                    && root.getEnergyStorage().canReceive();
        }

        @Override
        public boolean canExtract() {
            NetworkRootBlockEntity root = getRoot();

            return root != null
                    && canInteract(false, true)
                    && root.getEnergyStorage().canExtract();
        }

        @Override
        public BigInteger getEnergyStored() {
            NetworkRootBlockEntity root = getRoot();
            return root != null ? root.getEnergyStorage().getEnergyStored() : BigInteger.ZERO;
        }

        @Override
        public BigInteger getMaxEnergyStored() {
            NetworkRootBlockEntity root = getRoot();
            return root != null ? root.getEnergyStorage().getMaxEnergyStored() : BigInteger.ZERO;
        }
    }
}
