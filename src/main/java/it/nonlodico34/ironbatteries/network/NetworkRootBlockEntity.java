package it.nonlodico34.ironbatteries.network;

import it.nonlodico34.ironbatteries.block.batteryslot.BatterySlotBlockEntity;
import it.nonlodico34.ironbatteries.item.InfiniteBattery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import it.nonlodico34.ironbatteries.bigleagues.IBigEnergyStorage;
import java.math.BigInteger;

import java.util.*;

public abstract class NetworkRootBlockEntity extends NetworkNodeBlockEntity {
    private boolean networkInitialized = false;
    private final Set<NetworkNodeBlockEntity> connectedNodes = new HashSet<>();

    protected NetworkRootBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.networkInitialized = false;
    }

    @Override
    public NetworkRootBlockEntity getRootEntity() {
        return this;
    }

    @Override
    public void setRootEntity(NetworkRootBlockEntity rootEntity) {
    }

    @Override
    public IBigEnergyStorage getEnergyStorage() {
        return new AggregateEnergyStorage(getConnectedBatteries());
    }

    public AggregateEnergyStorage getVirtualEnergyStorage() {
        return new AggregateEnergyStorage(getConnectedBatteries());
    }

    protected static void rebuildNetwork(Level level, BlockPos seedPos) {
        if (level.isClientSide) return;

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        List<NetworkNodeBlockEntity> foundNodes = new ArrayList<>();
        List<NetworkRootBlockEntity> foundRoots = new ArrayList<>();
        Map<String, Integer> blockCounts = new HashMap<>();

        queue.add(seedPos);

        while (!queue.isEmpty()) {
            BlockPos currentPos = queue.poll();
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);

            BlockEntity be = level.getBlockEntity(currentPos);
            if (be instanceof NetworkNodeBlockEntity node) {
                foundNodes.add(node);

                if (node instanceof NetworkRootBlockEntity root) {
                    foundRoots.add(root);
                }

                String blockName = level.getBlockState(currentPos).getBlock().getDescriptionId();
                blockCounts.put(blockName, blockCounts.getOrDefault(blockName, 0) + 1);

                for (Direction dir : Direction.values()) {
                    queue.add(currentPos.relative(dir));
                }
            }
        }

        if (foundRoots.isEmpty()) {
            for (NetworkNodeBlockEntity node : foundNodes) {
                node.setRootEntity(null);
            }
            return;
        }

        NetworkRootBlockEntity mainRoot = foundRoots.get(0);
        if (foundRoots.size() > 1) {
            for (int i = 1; i < foundRoots.size(); i++) {
                level.destroyBlock(foundRoots.get(i).getBlockPos(), true);
                foundNodes.remove(foundRoots.get(i));
            }
        }

        mainRoot.connectedNodes.clear();
        for (NetworkNodeBlockEntity node : foundNodes) {
            node.setRootEntity(mainRoot);
            mainRoot.connectedNodes.add(node);
        }

        mainRoot.networkInitialized = true;

        System.out.println("========== [IronBatteries] Network Rebuild Summary ==========");
        System.out.println("Main Root at: " + mainRoot.getBlockPos());
        System.out.println("Total Nodes: " + mainRoot.connectedNodes.size());
        blockCounts.forEach((name, count) -> System.out.println(" - " + name + ": " + count));
        System.out.println("========================================================");
    }

    protected static void rebuildFromSeeds(Level level, Collection<BlockPos> seeds) {
        Set<BlockPos> handled = new HashSet<>();
        for (BlockPos seed : seeds) {
            if (!handled.add(seed)) continue;
            rebuildNetwork(level, seed);
        }
    }

    protected Set<NetworkNodeBlockEntity> getConnectedNodes() {
        if (!this.networkInitialized && this.level != null && !this.level.isClientSide) {
            this.networkInitialized = true;
            rebuildNetwork(this.level, this.getBlockPos());
        }
        return Collections.unmodifiableSet(this.connectedNodes);
    }

    protected List<ItemStack> getConnectedBatteries() {
        List<ItemStack> batteries = new ArrayList<>();
        for (NetworkNodeBlockEntity node : getConnectedNodes()) {
            if (node instanceof BatterySlotBlockEntity batterySlotBE) {
                batteries.addAll(batterySlotBE.getBatteries());
            }
        }
        return batteries;
    }

    public boolean hasInfiniteBattery() {
        List<ItemStack> batteries = getConnectedBatteries();
        for (ItemStack battery : batteries) {
            if (battery.getItem() instanceof InfiniteBattery) {
                return true;
            }
        }
        return false;
    }

    public int getConnectedBatteriesCount() {
        return getConnectedBatteries().size();
    }

    public IBigEnergyStorage restrict(boolean canExtract, boolean canReceive) {
        IBigEnergyStorage baseStorage = this.getEnergyStorage();
        return new IBigEnergyStorage() {
            @Override
            public BigInteger receiveEnergy(BigInteger maxReceive, boolean simulate) {
                return canReceive ? baseStorage.receiveEnergy(maxReceive, simulate) : BigInteger.ZERO;
            }

            @Override
            public BigInteger extractEnergy(BigInteger maxExtract, boolean simulate) {
                return canExtract ? baseStorage.extractEnergy(maxExtract, simulate) : BigInteger.ZERO;
            }

            @Override
            public boolean canReceive() {
                return canReceive && baseStorage.canReceive();
            }

            @Override
            public boolean canExtract() {
                return canExtract && baseStorage.canExtract();
            }

            @Override
            public BigInteger getEnergyStored() {
                return baseStorage.getEnergyStored();
            }

            @Override
            public BigInteger getMaxEnergyStored() {
                return baseStorage.getMaxEnergyStored();
            }
        };
    }
}
