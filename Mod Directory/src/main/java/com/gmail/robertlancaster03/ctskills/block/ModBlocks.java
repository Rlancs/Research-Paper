package com.gmail.robertlancaster03.ctskills.block;

import com.gmail.robertlancaster03.ctskills.CompThinkingMod;
import com.gmail.robertlancaster03.ctskills.block.custom.*;
import com.gmail.robertlancaster03.ctskills.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.time.Clock;
import java.util.function.Supplier;

// Registers all standard non-entity blocks
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CompThinkingMod.MODID);

    public static final RegistryObject<Block> AND_GATE = registerBlock("and_gate",
            () -> new ANDGate(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> NOT_GATE = registerBlock("not_gate",
            () -> new NOTGate(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> RS_LATCH = registerBlock("rs_latch",
            () -> new RSLatch(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> INPUT_BLOCK = registerBlock("input_block",
            () -> new InputBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> OUTPUT_BLOCK = registerBlock("output_block",
            () -> new OutputBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> CLOCK_GENERATOR = registerBlock("clock_generator",
            () -> new ClockGenerator(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
