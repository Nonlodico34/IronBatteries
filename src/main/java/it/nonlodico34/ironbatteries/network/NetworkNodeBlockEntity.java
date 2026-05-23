package it.nonlodico34.ironbatteries.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import it.nonlodico34.ironbatteries.bigleagues.IBigEnergyStorage;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkNodeBlockEntity extends BlockEntity {

    protected BlockPos rootPos;

    protected NetworkNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    public NetworkRootBlockEntity getRootEntity() {
        if (level == null || rootPos == null) {
            return null;
        }

        BlockEntity be = level.getBlockEntity(rootPos);

        if (be instanceof NetworkRootBlockEntity root) {
            return root;
        }

        return null;
    }

    public void setRootEntity(@Nullable NetworkRootBlockEntity rootEntity) {
        this.rootPos = rootEntity != null ? rootEntity.getBlockPos() : null;

        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Nullable
    public BlockPos getRootPos() {
        return rootPos;
    }

    public boolean isConnectedToNetwork() {
        return getRootEntity() != null;
    }

    @Nullable
    public IBigEnergyStorage getEnergyStorage() {
        NetworkRootBlockEntity root = getRootEntity();
        return root != null ? root.getEnergyStorage() : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (rootPos != null) {
            tag.put("rootPos", NbtUtils.writeBlockPos(rootPos));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("rootPos")) {
            rootPos = NbtUtils.readBlockPos(tag, "rootPos").orElse(null);
        } else {
            rootPos = null;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}