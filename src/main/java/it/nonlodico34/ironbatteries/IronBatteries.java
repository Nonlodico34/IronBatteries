package it.nonlodico34.ironbatteries;

import com.mojang.logging.LogUtils;
import it.nonlodico34.ironbatteries.registry.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.math.BigInteger;

@Mod(IronBatteries.MODID)
public class IronBatteries {

    public static final String MODID = "ironbatteries";
    private static final Logger LOGGER = LogUtils.getLogger();


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            CREATIVE_MODE_TABS.register("main_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.ironbatteries"))
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(() -> ModItems.IRON_BATTERY.get().getDefaultInstance())
                            .displayItems((params, output) -> {

                                output.accept(ModBlocks.BATTERY_SLOT.get());
                                output.accept(ModBlocks.CONTROLLER.get());
                                output.accept(ModBlocks.PORT.get());
                                output.accept(ModBlocks.MONITOR.get());
                                output.accept(ModBlocks.CASING.get());
                                output.accept(ModBlocks.REDSTONE_LINK.get());
                                output.accept(ModBlocks.ATMOSPHERIC_CONDENSER.get());


                                output.accept(ModItems.IRON_BATTERY.get());
                                output.accept(ModItems.GOLDEN_BATTERY.get());
                                output.accept(ModItems.DIAMOND_BATTERY.get());
                                output.accept(ModItems.EMERALD_BATTERY.get());
                                output.accept(ModItems.NETHERITE_BATTERY.get());
                                output.accept(ModItems.INFINITE_BATTERY.get());


                                output.accept(createChargedStack(ModItems.IRON_BATTERY.get(), Integer.MAX_VALUE));
                                output.accept(createChargedStack(ModItems.GOLDEN_BATTERY.get(), Integer.MAX_VALUE));
                                output.accept(createChargedStack(ModItems.DIAMOND_BATTERY.get(), Integer.MAX_VALUE));
                                output.accept(createChargedStack(ModItems.EMERALD_BATTERY.get(), Integer.MAX_VALUE));
                                output.accept(createChargedStack(ModItems.NETHERITE_BATTERY.get(), Integer.MAX_VALUE));


                                output.accept(ModItems.BATTERY_COMPONENT.get());
                                output.accept(ModItems.PRISMARINE_RING.get());
                            })
                            .build()
            );

    public IronBatteries(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        modEventBus.addListener(this::commonSetup);

        CREATIVE_MODE_TABS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModDataComponentTypes.DATA_COMPONENT_TYPES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);

        modEventBus.addListener(ModCapabilities::registerCapabilities);

        if (dist.isClient()) {
            modEventBus.register(ClientEvents.class);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("IronBatteries loaded and ready to power up!");
    }

    private static ItemStack createChargedStack(Item item, int energy) {
        ItemStack stack = new ItemStack(item);
        stack.set(ModDataComponentTypes.ENERGY.get(), energy);
        return stack;
    }
}