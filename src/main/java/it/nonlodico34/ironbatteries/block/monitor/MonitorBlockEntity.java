package it.nonlodico34.ironbatteries.block.monitor;

import it.nonlodico34.ironbatteries.network.NetworkNodeBlockEntity;
import it.nonlodico34.ironbatteries.network.NetworkRootBlockEntity;
import it.nonlodico34.ironbatteries.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import it.nonlodico34.ironbatteries.bigleagues.IBigEnergyStorage;
import java.math.BigInteger;

public class MonitorBlockEntity extends NetworkNodeBlockEntity {

    private BigInteger storedFE = BigInteger.ZERO;
    private BigInteger capacityFE = BigInteger.ZERO;
    private boolean isConnected = false;
    private boolean hasInfiniteBattery = false;
    private int connectedBatteriesCount = 0;

    private int multiblockSize = 1;
    private BlockPos masterPos = null;
    private boolean needsMultiblockUpdate = true;

    public MonitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MONITOR_BLOCK_ENTITY.get(), pos, state);
        this.masterPos = pos;
    }

    public void markForMultiblockUpdate() {
        this.needsMultiblockUpdate = true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MonitorBlockEntity be) {
        if (level.isClientSide) return;

        boolean structureChanged = false;

        if (be.needsMultiblockUpdate) {
            be.needsMultiblockUpdate = false;

            Direction facing = state.getValue(MonitorBlock.FACING);
            MultiblockMonitorHelper.MultiblockData newData = MultiblockMonitorHelper.detectMultiblockStructure(level, pos, facing);

            int newSize = newData != null ? newData.size : 1;
            BlockPos newMaster = newData != null ? newData.masterPos : pos;

            structureChanged = (be.multiblockSize != newSize) || !newMaster.equals(be.masterPos);

            if (structureChanged) {
                be.multiblockSize = newSize;
                be.masterPos = newMaster;
            }

            MultiblockMonitorHelper.BlockPosition expectedType = MultiblockMonitorHelper.getBlockPosition(pos, be.masterPos, be.multiblockSize, facing);
            if (state.hasProperty(MonitorBlock.TYPE) && state.getValue(MonitorBlock.TYPE) != expectedType) {
                state = state.setValue(MonitorBlock.TYPE, expectedType);
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
        }

        NetworkRootBlockEntity root = be.getRootEntity();
        boolean newConnected = (root != null);
        BigInteger newStored = BigInteger.ZERO;
        BigInteger newCapacity = BigInteger.ZERO;
        boolean newHasInfiniteBattery = false;
        int newBatteriesCount = 0;

        if (newConnected) {
            IBigEnergyStorage energy = root.getEnergyStorage();
            if (energy != null) {
                newStored = energy.getEnergyStored();
                newCapacity = energy.getMaxEnergyStored();
                newHasInfiniteBattery = root.hasInfiniteBattery();
            }
            newBatteriesCount = root.getConnectedBatteriesCount();
        }

        if (be.isConnected != newConnected || !be.storedFE.equals(newStored) || !be.capacityFE.equals(newCapacity) || be.connectedBatteriesCount != newBatteriesCount || be.hasInfiniteBattery != newHasInfiniteBattery || structureChanged) {
            be.isConnected = newConnected;
            be.storedFE = newStored;
            be.capacityFE = newCapacity;
            be.connectedBatteriesCount = newBatteriesCount;
            be.hasInfiniteBattery = newHasInfiniteBattery;

            be.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("MonitorStored", this.storedFE.toString());
        tag.putString("MonitorCapacity", this.capacityFE.toString());
        tag.putBoolean("MonitorConnected", this.isConnected);
        tag.putBoolean("HasInfiniteBattery", this.hasInfiniteBattery);
        tag.putInt("ConnectedBatteriesCount", this.connectedBatteriesCount);
        tag.putInt("MultiblockSize", this.multiblockSize);
        if (this.masterPos != null) {
            tag.put("MasterPos", NbtUtils.writeBlockPos(this.masterPos));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("MonitorStored", net.minecraft.nbt.Tag.TAG_STRING)) {
            this.storedFE = new BigInteger(tag.getString("MonitorStored"));
        } else {
            this.storedFE = BigInteger.valueOf(tag.getInt("MonitorStored"));
        }

        if (tag.contains("MonitorCapacity", net.minecraft.nbt.Tag.TAG_STRING)) {
            this.capacityFE = new BigInteger(tag.getString("MonitorCapacity"));
        } else {
            this.capacityFE = BigInteger.valueOf(tag.getInt("MonitorCapacity"));
        }
        this.isConnected = tag.getBoolean("MonitorConnected");
        this.hasInfiniteBattery = tag.getBoolean("HasInfiniteBattery");
        this.connectedBatteriesCount = tag.getInt("ConnectedBatteriesCount");
        this.multiblockSize = tag.getInt("MultiblockSize");
        if (this.multiblockSize == 0) this.multiblockSize = 1;

        if (tag.contains("MasterPos")) {
            NbtUtils.readBlockPos(tag, "MasterPos").ifPresent(pos -> this.masterPos = pos);
        } else {
            this.masterPos = this.getBlockPos();
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean hasInfiniteBattery() {
        return hasInfiniteBattery;
    }

    public BigInteger getStoredFE() {
        return storedFE;
    }

    public BigInteger getCapacityFE() {
        return capacityFE;
    }

    public int getMultiblockSize() {
        return multiblockSize;
    }

    public BlockPos getMasterPos() {
        return masterPos != null ? masterPos : getBlockPos();
    }

    public boolean isMultiblockMaster() {
        return getBlockPos().equals(getMasterPos());
    }

    public int getConnectedBatteriesCount() {
        return connectedBatteriesCount;
    }
}
