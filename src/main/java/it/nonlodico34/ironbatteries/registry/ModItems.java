package it.nonlodico34.ironbatteries.registry;

import it.nonlodico34.ironbatteries.IronBatteries;
import it.nonlodico34.ironbatteries.item.EnergizedItem;
import it.nonlodico34.ironbatteries.item.TooltipBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(IronBatteries.MODID);

    public static final DeferredItem<BlockItem> BATTERY_SLOT_ITEM =
            ITEMS.register("battery_slot",
                    () -> new TooltipBlockItem(
                            ModBlocks.BATTERY_SLOT.get(),
                            new Item.Properties(),
                            "tooltip.ironbatteries.block.battery_slot"
                    ));

    public static final DeferredItem<BlockItem> CONTROLLER_ITEM =
            ITEMS.register("controller",
                    () -> new TooltipBlockItem(
                            ModBlocks.CONTROLLER.get(),
                            new Item.Properties(),
                            "tooltip.ironbatteries.block.controller"
                    ));

    public static final DeferredItem<BlockItem> PORT_ITEM =
            ITEMS.register("port",
                    () -> new TooltipBlockItem(
                            ModBlocks.PORT.get(),
                            new Item.Properties(),
                            "tooltip.ironbatteries.block.port"
                    ));

    public static final DeferredItem<BlockItem> MONITOR_ITEM =
            ITEMS.register("monitor",
                    () -> new TooltipBlockItem(
                            ModBlocks.MONITOR.get(),
                            new Item.Properties(),
                            "tooltip.ironbatteries.block.monitor"
                    ));

    public static final DeferredItem<BlockItem> CASING_ITEM =
            ITEMS.register("casing",
                    () -> new TooltipBlockItem(
                            ModBlocks.CASING.get(),
                            new Item.Properties(),
                            "tooltip.ironbatteries.block.casing"
                    ));

    public static final DeferredItem<BlockItem> REDSTONE_LINK_ITEM =
            ITEMS.register("redstone_link",
                    () -> new TooltipBlockItem(
                            ModBlocks.REDSTONE_LINK.get(),
                            new Item.Properties(),
                            "tooltip.ironbatteries.block.redstone_link"
                    ));

    public static final DeferredItem<BlockItem> ATMOSPHERIC_CONDENSER_ITEM =
            ITEMS.register("atmospheric_condenser",
                    () -> new TooltipBlockItem(
                            ModBlocks.ATMOSPHERIC_CONDENSER.get(),
                            new Item.Properties(),
                            "tooltip.ironbatteries.block.atmospheric_condenser"
                    ));

    public static final DeferredItem<EnergizedItem> IRON_BATTERY =
            ITEMS.register("iron_battery",
                    () -> new EnergizedItem(
                            new Item.Properties()
                    ));

    public static final DeferredItem<EnergizedItem> GOLDEN_BATTERY =
            ITEMS.register("golden_battery",
                    () -> new EnergizedItem(
                            new Item.Properties()
                    ));

    public static final DeferredItem<EnergizedItem> DIAMOND_BATTERY =
            ITEMS.register("diamond_battery",
                    () -> new EnergizedItem(
                            new Item.Properties()
                    ));

    public static final DeferredItem<EnergizedItem> NETHERITE_BATTERY =
            ITEMS.register("netherite_battery",
                    () -> new EnergizedItem(
                            new Item.Properties()
                    ));

    public static final DeferredItem<EnergizedItem> EMERALD_BATTERY =
            ITEMS.register("emerald_battery",
                    () -> new EnergizedItem(
                            new Item.Properties()
                    ));

    public static final DeferredItem<it.nonlodico34.ironbatteries.item.InfiniteBattery> INFINITE_BATTERY =
            ITEMS.register("infinite_battery",
                    () -> new it.nonlodico34.ironbatteries.item.InfiniteBattery(
                            new Item.Properties()
                    ));

    public static final DeferredItem<Item> BATTERY_COMPONENT =
            ITEMS.register("battery_component",
                    () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PRISMARINE_RING =
            ITEMS.register("prismarine_ring",
                    () -> new Item(new Item.Properties()));
}
