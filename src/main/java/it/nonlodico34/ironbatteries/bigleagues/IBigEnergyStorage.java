package it.nonlodico34.ironbatteries.bigleagues;

import net.neoforged.neoforge.energy.IEnergyStorage;

import java.math.BigInteger;

public interface IBigEnergyStorage {

    BigInteger receiveEnergy(BigInteger toReceive, boolean simulate);

    BigInteger extractEnergy(BigInteger toExtract, boolean simulate);

    BigInteger getEnergyStored();

    BigInteger getMaxEnergyStored();

    boolean canExtract();

    boolean canReceive();

    default IEnergyStorage toIEnergyStorage() {
        return new IEnergyStorage() {

            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                BigInteger result = IBigEnergyStorage.this.receiveEnergy(BigInteger.valueOf(toReceive), simulate);
                return BigIntHelper.bigIntegerToInt(result);
            }

            @Override
            public int extractEnergy(int toExtract, boolean simulate) {
                BigInteger result = IBigEnergyStorage.this.extractEnergy(BigInteger.valueOf(toExtract), simulate);
                return BigIntHelper.bigIntegerToInt(result);
            }

            @Override
            public int getEnergyStored() {
                return BigIntHelper.bigIntegerToInt(IBigEnergyStorage.this.getEnergyStored());
            }

            @Override
            public int getMaxEnergyStored() {
                return BigIntHelper.bigIntegerToInt(IBigEnergyStorage.this.getMaxEnergyStored());
            }

            @Override
            public boolean canExtract() {
                return IBigEnergyStorage.this.canExtract();
            }

            @Override
            public boolean canReceive() {
                return IBigEnergyStorage.this.canReceive();
            }
        };
    }
}