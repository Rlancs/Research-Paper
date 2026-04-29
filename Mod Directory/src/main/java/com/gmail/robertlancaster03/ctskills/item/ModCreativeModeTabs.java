package com.gmail.robertlancaster03.ctskills.item;

import com.gmail.robertlancaster03.ctskills.CompThinkingMod;
import com.gmail.robertlancaster03.ctskills.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.intellij.lang.annotations.Identifier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CompThinkingMod.MODID);

    public static final RegistryObject<CreativeModeTab> TOOLBOX_TAB = CREATIVE_MODE_TABS.register("toolbox_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TOOLBOX.get()))
                    .title(Component.translatable("creativetab.toolbox.tab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModBlocks.INPUT_BLOCK.get());
                        pOutput.accept(ModBlocks.OUTPUT_BLOCK.get());
                        pOutput.accept(ModBlocks.AND_GATE.get());
                        pOutput.accept(ModBlocks.NOT_GATE.get());
                        pOutput.accept(ModBlocks.RS_LATCH.get());
                        pOutput.accept(ModBlocks.CLOCK_GENERATOR.get());

                    })
                    .build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
