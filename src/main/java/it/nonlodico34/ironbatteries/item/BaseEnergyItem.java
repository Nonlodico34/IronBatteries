package it.nonlodico34.ironbatteries.item;

import java.math.BigInteger;
import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import it.nonlodico34.ironbatteries.bigleagues.BigIntHelper;

public abstract class BaseEnergyItem extends Item {

    public BaseEnergyItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    protected abstract float getEnergyRatio(@NotNull ItemStack stack);

    protected abstract BigInteger getStoredEnergy(@NotNull ItemStack stack);

    protected abstract String getMaxEnergyDisplay(@NotNull ItemStack stack, boolean shiftDown);

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        if (stack.getCount() != 1) return false;
        return getStoredEnergy(stack).compareTo(BigInteger.ZERO) > 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        float ratio = getEnergyRatio(stack);
        return Math.round(13.0F * ratio);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float ratio = getEnergyRatio(stack);
        ratio = Math.max(0.0F, Math.min(1.0F, ratio));

        if (ratio == 0.0F) {
            return 0xFF0000;
        }

        int r, g;

        if (ratio < 0.5F) {
            r = 255;
            g = (int) (ratio * 2.0F * 255);
        } else {
            r = (int) ((1.0F - (ratio - 0.5F) * 2.0F) * 255);
            g = 255;
        }

        return (r << 16) | (g << 8);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        BigInteger stored = getStoredEnergy(stack);
        boolean shiftDown = Screen.hasShiftDown();

        String formattedStored = shiftDown
                ? String.format("%,d", stored)
                : BigIntHelper.formatBigInteger(stored, 2, true);

        String formattedMax = getMaxEnergyDisplay(stack, shiftDown);

        tooltip.add(Component.literal(String.format("§6Energy: §e%s §7/ §e%s FE", formattedStored, formattedMax)));

        super.appendHoverText(stack, context, tooltip, flag);
    }
}
