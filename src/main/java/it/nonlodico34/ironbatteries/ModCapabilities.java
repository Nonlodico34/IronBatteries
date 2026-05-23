package it.nonlodico34.ironbatteries;

import it.nonlodico34.ironbatteries.registry.ModBlockEntities;
import it.nonlodico34.ironbatteries.registry.ModDataComponentTypes;
import it.nonlodico34.ironbatteries.registry.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LightningRodBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.BATTERY_SLOT_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage().toIEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.CONTROLLER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getVirtualEnergyStorage().restrict(false, false).toIEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.PORT_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage().toIEnergyStorage()
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.REDSTONE_LINK_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage() != null ? blockEntity.getEnergyStorage().toIEnergyStorage() : null
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ATMOSPHERIC_CONDENSER_BLOCK_ENTITY.get(),
                (blockEntity, side) -> {
                    if (blockEntity.getBlockState() == null || !blockEntity.getBlockState().hasProperty(LightningRodBlock.FACING)) {
                        return null;
                    }

                    Direction facing = blockEntity.getBlockState().getValue(LightningRodBlock.FACING);
                    Direction baseSide = facing.getOpposite();

                    if (side == null || side == baseSide) {
                        return blockEntity.getExposedEnergy();
                    }
                    return null;
                }
        );

        int capacity = 10_000;
        int growthFactor = 20;
        registerBattery(event, ModItems.IRON_BATTERY.get(), capacity, capacity/10);
        capacity *= growthFactor;
        registerBattery(event, ModItems.GOLDEN_BATTERY.get(), capacity, capacity/10);
        capacity *= growthFactor;
        registerBattery(event, ModItems.DIAMOND_BATTERY.get(), capacity, capacity/10);
        capacity *= growthFactor;
        registerBattery(event, ModItems.EMERALD_BATTERY.get(), capacity, capacity/10);
        capacity *= growthFactor;
        registerBattery(event, ModItems.NETHERITE_BATTERY.get(), capacity, capacity/10);

        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> it.nonlodico34.ironbatteries.item.InfiniteBattery.createEnergyStorage(stack),
                ModItems.INFINITE_BATTERY.get()
        );
    }

    private static void registerBattery(RegisterCapabilitiesEvent event, Item item, int capacity, int transfer) {
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, ctx) -> new ComponentEnergyStorage(
                        stack,
                        ModDataComponentTypes.ENERGY.get(),
                        capacity,
                        transfer,
                        transfer
                ),
                item
        );
    }
}