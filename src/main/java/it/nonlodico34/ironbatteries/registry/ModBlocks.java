package it.nonlodico34.ironbatteries.registry;

import it.nonlodico34.ironbatteries.IronBatteries;
import it.nonlodico34.ironbatteries.block.batteryslot.BatterySlotBlock;
import it.nonlodico34.ironbatteries.block.controller.ControllerBlock;
import it.nonlodico34.ironbatteries.block.casing.CasingBlock;
import it.nonlodico34.ironbatteries.block.monitor.MonitorBlock;
import it.nonlodico34.ironbatteries.block.port.PortBlock;
import it.nonlodico34.ironbatteries.block.redstonelink.RedstoneLinkBlock;
import it.nonlodico34.ironbatteries.block.atmosphericcondenser.AtmosphericCondenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(IronBatteries.MODID);

    public static final DeferredBlock<BatterySlotBlock> BATTERY_SLOT =
            BLOCKS.register("battery_slot",
                    registryName -> new BatterySlotBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0f)
                                    .noOcclusion()
                                    .requiresCorrectToolForDrops()
                    ));

    public static final DeferredBlock<ControllerBlock> CONTROLLER =
            BLOCKS.register("controller",
                    registryName -> new ControllerBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0f)
                                    .requiresCorrectToolForDrops()
                    ));

    public static final DeferredBlock<PortBlock> PORT =
            BLOCKS.register("port",
                    registryName -> new PortBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0f)
                                    .requiresCorrectToolForDrops()
                    ));

    public static final DeferredBlock<MonitorBlock> MONITOR =
            BLOCKS.register("monitor",
                    registryName -> new MonitorBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0f)
                                    .lightLevel(state -> 5)
                                    .requiresCorrectToolForDrops()
                    ));

    public static final DeferredBlock<CasingBlock> CASING =
            BLOCKS.register("casing",
                    registryName -> new CasingBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0f)
                                    .requiresCorrectToolForDrops()
                    ));

    public static final DeferredBlock<RedstoneLinkBlock> REDSTONE_LINK =
            BLOCKS.register("redstone_link",
                    registryName -> new RedstoneLinkBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0f)
                                    .requiresCorrectToolForDrops()
                    ));

    public static final DeferredBlock<AtmosphericCondenserBlock> ATMOSPHERIC_CONDENSER =
            BLOCKS.register("atmospheric_condenser",
                    registryName -> new AtmosphericCondenserBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.0f)
                                    .requiresCorrectToolForDrops()
                    ));
}