package it.nonlodico34.ironbatteries.network;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import it.nonlodico34.ironbatteries.bigleagues.IBigEnergyStorage;
import it.nonlodico34.ironbatteries.item.InfiniteBattery;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AggregateEnergyStorage implements IBigEnergyStorage {

    List<ItemStack> batteries;

    public AggregateEnergyStorage(List<ItemStack> batteries) {
        this.batteries = batteries;
    }

    private List<IEnergyStorage> getBatteryStorages() {
        if (batteries.isEmpty()) {
            return List.of();
        }

        List<IEnergyStorage> storages = new ArrayList<>(batteries.size());

        for (ItemStack battery : batteries) {
            if (battery.isEmpty()) {
                continue;
            }

            IEnergyStorage storage = battery.getCapability(Capabilities.EnergyStorage.ITEM);

            if (storage != null) {
                storages.add(storage);
            }
        }

        return storages;
    }

    @Override
    public BigInteger receiveEnergy(BigInteger maxReceive, boolean simulate) {
        if (maxReceive.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ZERO;
        }

        BigInteger received = BigInteger.ZERO;
        BigInteger remaining = maxReceive;

        for (IEnergyStorage storage : getBatteryStorages()) {
            if (remaining.compareTo(BigInteger.ZERO) <= 0) {
                break;
            }

            if (!storage.canReceive()) {
                continue;
            }

            int maxReceiveInt = remaining.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 ? Integer.MAX_VALUE : remaining.intValue();
            int amount = storage.receiveEnergy(maxReceiveInt, simulate);

            if (amount > 0) {
                received = received.add(BigInteger.valueOf(amount));
                remaining = remaining.subtract(BigInteger.valueOf(amount));
            }
        }

        return received;
    }

    @Override
    public BigInteger extractEnergy(BigInteger maxExtract, boolean simulate) {
        if (maxExtract.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ZERO;
        }

        BigInteger extracted = BigInteger.ZERO;
        BigInteger remaining = maxExtract;

        for (IEnergyStorage storage : getBatteryStorages()) {
            if (remaining.compareTo(BigInteger.ZERO) <= 0) {
                break;
            }

            if (!storage.canExtract()) {
                continue;
            }

            int maxExtractInt = remaining.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0 ? Integer.MAX_VALUE : remaining.intValue();
            int amount = storage.extractEnergy(maxExtractInt, simulate);

            if (amount > 0) {
                extracted = extracted.add(BigInteger.valueOf(amount));
                remaining = remaining.subtract(BigInteger.valueOf(amount));
            }
        }

        return extracted;
    }

    @Override
    public BigInteger getEnergyStored() {
        BigInteger total = BigInteger.ZERO;

        for (ItemStack battery : batteries) {
            if (battery.isEmpty()) continue;
            if (battery.getItem() instanceof InfiniteBattery) {
                total = total.add(InfiniteBattery.getEnergy(battery));
            } else {
                IEnergyStorage storage = battery.getCapability(Capabilities.EnergyStorage.ITEM);
                if (storage != null) {
                    total = total.add(BigInteger.valueOf(storage.getEnergyStored()));
                }
            }
        }

        return total;
    }

    @Override
    public BigInteger getMaxEnergyStored() {
        BigInteger total = BigInteger.ZERO;

        for (ItemStack battery : batteries) {
            if (battery.isEmpty()) continue;
            if (battery.getItem() instanceof InfiniteBattery) {
                return BigInteger.valueOf(Integer.MAX_VALUE);
            } else {
                IEnergyStorage storage = battery.getCapability(Capabilities.EnergyStorage.ITEM);
                if (storage != null) {
                    total = total.add(BigInteger.valueOf(storage.getMaxEnergyStored()));
                }
            }
        }

        return total;
    }

    @Override
    public boolean canExtract() {
        for (IEnergyStorage storage : getBatteryStorages()) {
            if (storage.canExtract()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canReceive() {
        for (IEnergyStorage storage : getBatteryStorages()) {
            if (storage.canReceive()) {
                return true;
            }
        }

        return false;
    }

    public IBigEnergyStorage restrict(boolean canInsert, boolean canExtract) {
        IBigEnergyStorage baseStorage = this;

        return new IBigEnergyStorage() {

            @Override
            public BigInteger receiveEnergy(BigInteger maxReceive, boolean simulate) {
                return canInsert
                        ? baseStorage.receiveEnergy(maxReceive, simulate)
                        : BigInteger.ZERO;
            }

            @Override
            public BigInteger extractEnergy(BigInteger maxExtract, boolean simulate) {
                return canExtract
                        ? baseStorage.extractEnergy(maxExtract, simulate)
                        : BigInteger.ZERO;
            }

            @Override
            public boolean canReceive() {
                return canInsert && baseStorage.canReceive();
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
