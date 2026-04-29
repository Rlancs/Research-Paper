package com.gmail.robertlancaster03.ctskills.block.entity;

import com.gmail.robertlancaster03.ctskills.CompThinkingMod;
import com.gmail.robertlancaster03.ctskills.block.ModBlockEntities;
import com.gmail.robertlancaster03.ctskills.block.custom.InputBlock;
import com.gmail.robertlancaster03.ctskills.block.custom.SequenceBlock;
import com.gmail.robertlancaster03.ctskills.block.entity.util.TickingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class InputBlockEntity extends SequenceEntity implements TickingEntity {

    public static final String[] inputTypes =
            {"AND Gate - Sequence 1", "AND Gate - Sequence 2", "NOT Gate", "RS Latch - Sequence 1", "RS Latch - Sequence 2", "Clock Generator"};
    private int[] inputSequence = ANDSequenceOne;

    private final int[][] inputSequences = {ANDSequenceOne, ANDSequenceTwo, NOTSequence, LatchSequenceOne, LatchSequenceTwo, ClockSequence};
    private static final int[] ANDSequenceOne = {0, 0, 1, 1};
    private static final int[] ANDSequenceTwo = {0, 1, 0, 1};
    private static final int[] NOTSequence = {0, 1};
    private static final int[] LatchSequenceOne = {1, 0, 0, 0};
    private static final int[] LatchSequenceTwo = {0, 0, 1, 0};
    private static final int[] ClockSequence = {1};

    public InputBlockEntity(BlockPos bPos, BlockState bState)
    {
        super(ModBlockEntities.INPUT_BLOCK_ENTITY.get(), bPos, bState);
    }

    /// Whilst active, will output the next signal in the sequence every second
    @Override
    @ParametersAreNonnullByDefault
    public void tick(Level pLevel, BlockPos pPos, BlockState pState)
    {
        if (this.level == null || pLevel.isClientSide()) { return; }

        InputBlock inputBlock = (InputBlock)pState.getBlock();

        if (active)
        {
            // Increment recorded time
            ticks++;

            // After a second passes, check for sequence progression
            if (SecondHasPassed(ticks))
            {
                // If sequence has finished, deactivate the block
                if (!ProgressSequence(pLevel, pPos, pState, inputSequence.length))
                {
                    // Otherwise, progress the sequence
                    IncrementSequence(pLevel, pPos, inputSequence, inputBlock);
                }

                pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_BIT.get() , SoundSource.BLOCKS, 0.5f, 1f);

                ticks = 0;
                UpdateBlock(pLevel, pPos, pState);
            }
        }
    }

    @Override
    public void SetSequence(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, @Nullable String[] sequences)
    {
        super.SetSequence(pState, pLevel, pPos, pPlayer, inputTypes);

        inputSequence = inputSequences[inputType];
    }


    /// Increments the sequence, emitting a signal dependent on the current sequence value
    private void IncrementSequence(Level pLevel, BlockPos pPos, int[] sequence, InputBlock inputBlock)
    {
        emitSignal = sequence[sequenceNumber] == 1;
        pLevel.updateNeighborsAt(pPos, inputBlock);

        sequenceNumber++;
    }

    @Override
    public void CheckForStart(BlockState pState, Level pLevel, BlockPos pPos, SequenceBlock sequenceBlock, boolean signal)
    {
        super.CheckForStart(pState, pLevel, pPos, sequenceBlock, pLevel.getSignal(pPos.above(), Direction.UP) > 0);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        CompoundTag blockData = nbt.getCompound(CompThinkingMod.MODID);
        inputType = blockData.getInt("input_block.inputType");
        startingTicks = blockData.getInt("input_block.startingTicks");
        inputSequence = blockData.getIntArray("input_block.inputSequence");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);

        CompoundTag blockData = new CompoundTag();
        blockData.putInt("input_block.inputType", inputType);
        blockData.putInt("input_block.startingTicks", startingTicks);
        blockData.putIntArray("input_block.inputSequence", inputSequence);
        nbt.put(CompThinkingMod.MODID, blockData);
    }
}
