package com.gmail.robertlancaster03.ctskills.block;

import com.gmail.robertlancaster03.ctskills.CompThinkingMod;
import com.gmail.robertlancaster03.ctskills.block.entity.ClockGeneratorEntity;
import com.gmail.robertlancaster03.ctskills.block.entity.InputBlockEntity;
import com.gmail.robertlancaster03.ctskills.block.entity.OutputBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// Registers all entity blocks
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BlOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CompThinkingMod.MODID);

    public static final RegistryObject<BlockEntityType<InputBlockEntity>> INPUT_BLOCK_ENTITY = BlOCK_ENTITIES.register("input_block_entity",
                            () -> BlockEntityType.Builder.of(InputBlockEntity::new, ModBlocks.INPUT_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<OutputBlockEntity>> OUTPUT_BLOCK_ENTITY = BlOCK_ENTITIES.register("output_block_entity",
            () -> BlockEntityType.Builder.of(OutputBlockEntity::new, ModBlocks.OUTPUT_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<ClockGeneratorEntity>> CLOCK_GENERATOR_ENTITY = BlOCK_ENTITIES.register("clock_generator_entity",
            () -> BlockEntityType.Builder.of(ClockGeneratorEntity::new, ModBlocks.CLOCK_GENERATOR.get()).build(null));


    public static void register(IEventBus eventBus)
    {
        BlOCK_ENTITIES.register(eventBus);
    }
}
