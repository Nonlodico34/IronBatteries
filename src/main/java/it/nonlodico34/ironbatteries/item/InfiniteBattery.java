package it.nonlodico34.ironbatteries.item;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import it.nonlodico34.ironbatteries.bigleagues.BigIntHelper;

public class InfiniteBattery extends BaseEnergyItem {

    public InfiniteBattery(Properties properties) {
        super(properties);
    }

    public static BigInteger getEnergy(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (tag.contains("InfiniteEnergy")) {
            return new BigInteger(tag.getString("InfiniteEnergy"));
        }
        return BigInteger.ZERO;
    }

    public static BigInteger getPeakEnergy(ItemStack stack) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        if (tag.contains("PeakEnergy")) {
            return new BigInteger(tag.getString("PeakEnergy"));
        }
        return BigInteger.ZERO;
    }

    public static void setEnergy(ItemStack stack, BigInteger amount) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        tag.putString("InfiniteEnergy", amount.toString());

        BigInteger currentPeak = getPeakEnergy(stack);
        if (amount.compareTo(currentPeak) > 0) {
            tag.putString("PeakEnergy", amount.toString());
        }

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    protected float getEnergyRatio(@NotNull ItemStack stack) {
        BigInteger stored = getEnergy(stack);
        BigInteger peak = getPeakEnergy(stack);

        if (peak.equals(BigInteger.ZERO)) return 0.0F;

        BigDecimal storedBD = new BigDecimal(stored);
        BigDecimal peakBD = new BigDecimal(peak);

        return storedBD.divide(peakBD, 4, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    protected BigInteger getStoredEnergy(@NotNull ItemStack stack) {
        return getEnergy(stack);
    }

    @Override
    protected String getMaxEnergyDisplay(@NotNull ItemStack stack, boolean shiftDown) {
        return "∞";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        BigInteger peak = getPeakEnergy(stack);
        boolean shiftDown = Screen.hasShiftDown();

        String formattedPeak = shiftDown
                ? String.format("%,d", peak)
                : BigIntHelper.formatBigInteger(peak, 2, true);

        tooltip.add(Component.literal(String.format("§7Record: %s FE", formattedPeak)));
    }

    public static IEnergyStorage createEnergyStorage(ItemStack stack) {
        return new IEnergyStorage() {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (maxReceive <= 0) return 0;

                if (!simulate) {
                    BigInteger current = getEnergy(stack);
                    setEnergy(stack, current.add(BigInteger.valueOf(maxReceive)));
                }
                return maxReceive;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                if (maxExtract <= 0) return 0;

                BigInteger current = getEnergy(stack);
                BigInteger toExtract = current.min(BigInteger.valueOf(maxExtract));
                int extracted = toExtract.intValue();

                if (!simulate && extracted > 0) {
                    setEnergy(stack, current.subtract(toExtract));
                }
                return extracted;
            }

            @Override
            public int getEnergyStored() {
                BigInteger current = getEnergy(stack);
                if (current.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                    return Integer.MAX_VALUE;
                }
                return current.intValue();
            }

            @Override
            public int getMaxEnergyStored() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean canExtract() {
                return true;
            }

            @Override
            public boolean canReceive() {
                return true;
            }
        };
    }
}
