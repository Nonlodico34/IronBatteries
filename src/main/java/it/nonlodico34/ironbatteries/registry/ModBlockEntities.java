package it.nonlodico34.ironbatteries.registry;

import it.nonlodico34.ironbatteries.IronBatteries;
import it.nonlodico34.ironbatteries.block.batteryslot.BatterySlotBlockEntity;
import it.nonlodico34.ironbatteries.block.controller.ControllerBlockEntity;
import it.nonlodico34.ironbatteries.block.monitor.MonitorBlockEntity;
import it.nonlodico34.ironbatteries.block.port.PortBlockEntity;
import it.nonlodico34.ironbatteries.block.casing.CasingBlockEntity;
import it.nonlodico34.ironbatteries.block.redstonelink.RedstoneLinkBlockEntity;
import it.nonlodico34.ironbatteries.block.atmosphericcondenser.AtmosphericCondenserBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE,
                    IronBatteries.MODID
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BatterySlotBlockEntity>> BATTERY_SLOT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "battery_slot_block_entity",
                    () -> BlockEntityType.Builder.of(
                            BatterySlotBlockEntity::new,
                            ModBlocks.BATTERY_SLOT.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ControllerBlockEntity>> CONTROLLER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "controller_block_entity",
                    () -> BlockEntityType.Builder.of(
                            ControllerBlockEntity::new,
                            ModBlocks.CONTROLLER.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PortBlockEntity>> PORT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "port_block_entity",
                    () -> BlockEntityType.Builder.of(
                            PortBlockEntity::new,
                            ModBlocks.PORT.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MonitorBlockEntity>> MONITOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "monitor_block_entity",
                    () -> BlockEntityType.Builder.of(
                            MonitorBlockEntity::new,
                            ModBlocks.MONITOR.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CasingBlockEntity>> CASING_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "casing_block_entity",
                    () -> BlockEntityType.Builder.of(
                            CasingBlockEntity::new,
                            ModBlocks.CASING.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneLinkBlockEntity>> REDSTONE_LINK_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "redstone_link_block_entity",
                    () -> BlockEntityType.Builder.of(
                            RedstoneLinkBlockEntity::new,
                            ModBlocks.REDSTONE_LINK.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AtmosphericCondenserBlockEntity>> ATMOSPHERIC_CONDENSER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "atmospheric_condenser_block_entity",
                    () -> BlockEntityType.Builder.of(
                            AtmosphericCondenserBlockEntity::new,
                            ModBlocks.ATMOSPHERIC_CONDENSER.get()
                    ).build(null)
            );

    public static void register(net.neoforged.bus.api.IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}