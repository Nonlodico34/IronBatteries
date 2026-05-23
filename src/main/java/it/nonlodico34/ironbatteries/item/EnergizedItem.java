package it.nonlodico34.ironbatteries.item;

import java.math.BigInteger;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import it.nonlodico34.ironbatteries.bigleagues.BigIntHelper;

public class EnergizedItem extends BaseEnergyItem {

    public EnergizedItem(Properties properties) {
        super(properties);
    }

    @Override
    protected float getEnergyRatio(@NotNull ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null || storage.getMaxEnergyStored() <= 0) return 0.0F;

        return (float) storage.getEnergyStored() / storage.getMaxEnergyStored();
    }

    @Override
    protected BigInteger getStoredEnergy(@NotNull ItemStack stack) {
        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null) return BigInteger.ZERO;

        return BigInteger.valueOf(storage.getEnergyStored());
    }

    @Override
    protected String getMaxEnergyDisplay(@NotNull ItemStack stack, boolean shiftDown) {
        IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null) return "0";

        BigInteger max = BigInteger.valueOf(storage.getMaxEnergyStored());

        return shiftDown
                ? String.format("%,d", max)
                : BigIntHelper.formatBigInteger(max, 2, true);
    }
}