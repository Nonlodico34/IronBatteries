package it.nonlodico34.ironbatteries.registry;

import it.nonlodico34.ironbatteries.IronBatteries;
import it.nonlodico34.ironbatteries.block.batteryslot.BatterySlotMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, IronBatteries.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<BatterySlotMenu>> BATTERY_SLOT_MENU =
            MENUS.register("battery_slot_menu", () -> IMenuTypeExtension.create(BatterySlotMenu::new));
}