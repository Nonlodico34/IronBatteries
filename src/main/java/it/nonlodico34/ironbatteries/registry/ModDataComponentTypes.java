package it.nonlodico34.ironbatteries.registry;

import com.mojang.serialization.Codec;
import it.nonlodico34.ironbatteries.IronBatteries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponentTypes {

    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, IronBatteries.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
            DATA_COMPONENT_TYPES.registerComponentType("energy",
                    builder -> builder
                            .persistent(Codec.INT)
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> BIG_ENERGY =
            DATA_COMPONENT_TYPES.registerComponentType("big_energy",
                    builder -> builder
                            .persistent(Codec.STRING)
            );
}
