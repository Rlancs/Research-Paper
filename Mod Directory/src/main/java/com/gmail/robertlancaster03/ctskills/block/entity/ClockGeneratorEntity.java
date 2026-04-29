package com.gmail.robertlancaster03.ctskills.block.entity;

import com.gmail.robertlancaster03.ctskills.CompThinkingMod;
import com.gmail.robertlancaster03.ctskills.block.ModBlockEntities;
import com.gmail.robertlancaster03.ctskills.block.custom.ClockGenerator;
import com.gmail.robertlancaster03.ctskills.block.custom.SequenceBlock;
import com.gmail.robertlancaster03.ctskills.block.entity.util.TickingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

public class ClockGeneratorEntity extends SequenceEntity implements TickingEntity {

    public int redstoneTicks = 64;
    public int numberOfOutputs = 5;

    public ClockGeneratorEntity(BlockPos bPos, BlockState bState)
    {
        super(ModBlockEntities.CLOCK_GENERATOR_ENTITY.get(), bPos, bState);
    }

    /// Whilst active, repeatedly outputs a signal with specified frequency and number of outputs
    @Override
    @ParametersAreNonnullByDefault
    public void tick(Level pLevel, BlockPos pPos, BlockState pState)
    {
        if (this.level == null || pLevel.isClientSide()) { return; }

        ClockGenerator clockGenerator = (ClockGenerator)pState.getBlock();

        if (active) {

            // Increment recorded time
            ticks++;

            // If signal has been active for a second, disable it
            if (SecondHasPassed(ticks) && emitSignal)
            {
                emitSignal = false;
                pLevel.updateNeighborsAt(pPos, clockGenerator);
            }

            // After the specified number of game ticks, check for signal output
            if (Math.floorMod(ticks, redstoneTicks * 2) == 0)
            {
                // If loop has already reached its specified number of outputs, deactivate the block
                if (!ProgressSequence(pLevel, pPos, pState, numberOfOutputs))
                {
                    // Otherwise, output a signal
                    emitSignal = true;
                    pLevel.updateNeighborsAt(pPos, clockGenerator);

                    sequenceNumber++;
                }

                // Reset recorded time after the specified number of ticks has occured
                ticks = 0;
                UpdateBlock(pLevel, pPos, pState);
            }
        }
    }

    @Override
    public void CheckForStart(BlockState pState, Level pLevel, BlockPos pPos, SequenceBlock sequenceBlock, boolean signal)
    {
        if (!emitSignal)
        {
            super.CheckForStart(pState, pLevel, pPos, sequenceBlock, pLevel.hasNeighborSignal(pPos));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        CompoundTag blockData = nbt.getCompound(CompThinkingMod.MODID);
        redstoneTicks = blockData.getInt("output_block.redstoneTicks");
        startingTicks = blockData.getInt("input_block.startingTicks");
        numberOfOutputs = blockData.getInt("output_block.numberOfOutputs");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);

        CompoundTag blockData = new CompoundTag();
        blockData.putInt("output_block.redstoneTicks", redstoneTicks);
        blockData.putInt("input_block.startingTicks", startingTicks);
        blockData.putInt("output_block.numberOfOutputs", numberOfOutputs);
        nbt.put(CompThinkingMod.MODID, blockData);
    }
}
