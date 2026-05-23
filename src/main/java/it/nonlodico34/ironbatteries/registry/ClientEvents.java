package it.nonlodico34.ironbatteries.registry;

import it.nonlodico34.ironbatteries.block.batteryslot.BatterySlotRenderer;
import it.nonlodico34.ironbatteries.block.batteryslot.BatterySlotScreen;
import it.nonlodico34.ironbatteries.block.monitor.MonitorRenderer;
import it.nonlodico34.ironbatteries.block.atmosphericcondenser.AtmosphericCondenserRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.ModelResourceLocation;
import it.nonlodico34.ironbatteries.IronBatteries;

public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.BATTERY_SLOT.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.ATMOSPHERIC_CONDENSER.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.BATTERY_SLOT_BLOCK_ENTITY.get(),
                BatterySlotRenderer::new
        );

        event.registerBlockEntityRenderer(
                ModBlockEntities.MONITOR_BLOCK_ENTITY.get(),
                MonitorRenderer::new
        );

        event.registerBlockEntityRenderer(
                ModBlockEntities.ATMOSPHERIC_CONDENSER_BLOCK_ENTITY.get(),
                AtmosphericCondenserRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.BATTERY_SLOT_MENU.get(), BatterySlotScreen::new);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(IronBatteries.MODID, "block/ring"), "standalone"));
    }
}