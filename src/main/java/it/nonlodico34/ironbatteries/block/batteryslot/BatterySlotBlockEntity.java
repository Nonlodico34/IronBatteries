package it.nonlodico34.ironbatteries.block.batteryslot;

import it.nonlodico34.ironbatteries.network.NetworkNodeBlockEntity;
import it.nonlodico34.ironbatteries.network.AggregateEnergyStorage;
import it.nonlodico34.ironbatteries.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import it.nonlodico34.ironbatteries.bigleagues.IBigEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BatterySlotBlockEntity extends NetworkNodeBlockEntity implements MenuProvider {

    public static final TagKey<Item> TAG_BATTERIES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("ironbatteries", "batteries"));

    private final ItemStackHandler batteries = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(TAG_BATTERIES);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    };

    public BatterySlotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATTERY_SLOT_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BatterySlotBlockEntity be) {
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", batteries.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) batteries.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    public IItemHandler getItemHandler() { return batteries; }

    @Override
    public Component getDisplayName() { return Component.translatable("container.ironbatteries.battery_slot"); }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new BatterySlotMenu(containerId, inventory, this);
    }

    @Override
    public IBigEnergyStorage getEnergyStorage() {
        return new AggregateEnergyStorage(getBatteries());
    }

    public List<ItemStack> getBatteries() {
        List<ItemStack> batteries = new ArrayList<>();
        IItemHandler itemHandler = getItemHandler();
        if (itemHandler != null) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    batteries.add(stack);
                }
            }
        }
        return batteries;
    }
}