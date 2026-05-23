package it.nonlodico34.ironbatteries.block.batteryslot;

import it.nonlodico34.ironbatteries.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BatterySlotMenu extends AbstractContainerMenu {
    private final BatterySlotBlockEntity blockEntity;

    public BatterySlotMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, (BatterySlotBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BatterySlotMenu(int containerId, Inventory inv, BatterySlotBlockEntity entity) {
        super(ModMenuTypes.BATTERY_SLOT_MENU.get(), containerId);
        this.blockEntity = entity;
        IItemHandler handler = entity.getItemHandler();

        this.addSlot(new SlotItemHandler(handler, 0, 62, 35));
        this.addSlot(new SlotItemHandler(handler, 1, 98, 35));

        layoutPlayerInventory(inv, 8, 84);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, 2, false)) return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private void layoutPlayerInventory(Inventory playerInventory, int x, int y) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, x + i * 18, y + 58));
        }
    }
}
