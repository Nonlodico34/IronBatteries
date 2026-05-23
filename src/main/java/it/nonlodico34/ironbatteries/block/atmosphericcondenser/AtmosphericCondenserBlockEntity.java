package it.nonlodico34.ironbatteries.block.atmosphericcondenser;

import it.nonlodico34.ironbatteries.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class AtmosphericCondenserBlockEntity extends BlockEntity {
    private final EnergyStorage energyStorage = new EnergyStorage(1_000_000, 100_000, 100_000);

    private final IEnergyStorage exposedEnergy = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return energyStorage.extractEnergy(maxExtract, simulate); }
        @Override public int getEnergyStored() { return energyStorage.getEnergyStored(); }
        @Override public int getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }
        @Override public boolean canExtract() { return energyStorage.canExtract(); }
        @Override public boolean canReceive() { return false; }
    };

    public AtmosphericCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATMOSPHERIC_CONDENSER_BLOCK_ENTITY.get(), pos, state);
    }

    public EnergyStorage getInternalEnergyStorage() {
        return energyStorage;
    }

    public IEnergyStorage getExposedEnergy() {
        return exposedEnergy;
    }

    public void onLightningStrike() {
        energyStorage.receiveEnergy(100_000, false);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AtmosphericCondenserBlockEntity be) {
        if (level.isClientSide) return;

        if (be.energyStorage.getEnergyStored() > 0) {
            Direction baseDir = state.getValue(LightningRodBlock.FACING).getOpposite();
            BlockPos neighborPos = pos.relative(baseDir);

            IEnergyStorage neighborEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, baseDir.getOpposite());

            if (neighborEnergy != null && neighborEnergy.canReceive()) {
                int extractedSim = be.energyStorage.extractEnergy(10_000, true);
                if (extractedSim > 0) {
                    int accepted = neighborEnergy.receiveEnergy(extractedSim, false);
                    if (accepted > 0) {
                        be.energyStorage.extractEnergy(accepted, false);
                        be.setChanged();
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("energy", this.energyStorage.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("energy")) {
            this.energyStorage.deserializeNBT(registries, tag.get("energy"));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}