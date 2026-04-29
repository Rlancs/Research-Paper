package com.gmail.robertlancaster03.ctskills.block.entity;

import com.gmail.robertlancaster03.ctskills.block.custom.SequenceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SequenceEntity extends BlockEntity
{
    public int inputType = 0;

    public int startingTicks = 5;
    public int ticks = startingTicks;
    public int sequenceNumber = 0;

    public boolean emitSignal = false;
    public boolean active = false;
    public boolean primed = false;

    public SequenceEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public static boolean SecondHasPassed(int ticks)
    {
        return Math.floorMod(ticks, 20) == 0;
    }

    protected void SetSequence(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, String[] inputTypes)
    {
        if (this.level == null || pLevel.isClientSide()) { return; }

        inputType = ChangeSequence(inputType, inputTypes.length);
        pPlayer.displayClientMessage(Component.literal(inputTypes[inputType]), true);

        UpdateBlock(pLevel, pPos, pState);
    }

    public static int ChangeSequence(int sequenceType, int sequenceTypesLength)
    {
        return sequenceType < sequenceTypesLength - 1 ? sequenceType + 1 : 0;
    }

    /// When first provided a signal, the sequence will 'prime' to start
    /// When the signal is released, the sequence begins
    public void CheckForStart(BlockState pState, Level pLevel, BlockPos pPos, SequenceBlock sequenceBlock, boolean signal)
    {
        if (this.level == null || pLevel.isClientSide()) { return; }

        boolean primedSequence = PrimeSequence(primed, signal);
        if (primed != primedSequence)
        {
            primed = primedSequence;
            if (!primed) StartSequence(pState, pLevel, pPos, sequenceBlock);
        }

        UpdateBlock(pLevel, pPos, pState);
    }

    static public boolean PrimeSequence(boolean primed, boolean signal)
    {
        if (!primed && signal) return true;
        else if (primed && !signal) return false;

        return primed;
    }

    /// Sets the starting ticks of the sequence and sets its state to active
    /// Also emits a signal to ensure other components know the sequence has started
    public void StartSequence(BlockState pState, Level pLevel, BlockPos pPos, SequenceBlock sequenceBlock)
    {
        ticks = startingTicks;

        active = true;
        pLevel.setBlockAndUpdate(pPos, pState.setValue(sequenceBlock.getActiveProperty(), true));

        emitSignal = true;
        pLevel.updateNeighborsAt(pPos, sequenceBlock);
    }

    /// Returns true if the sequence has finished, subsequently deactivating the block
    /// Returns false if the sequence isn't finished
    public boolean ProgressSequence(Level pLevel, BlockPos pPos, BlockState pState, int sequenceLength)
    {
        SequenceBlock sequenceBlock = (SequenceBlock)pState.getBlock();

        if (SequenceFinished(sequenceNumber, sequenceLength))
        {
            emitSignal = false;
            pLevel.updateNeighborsAt(pPos, sequenceBlock);

            active = false;
            pLevel.setBlockAndUpdate(pPos, pState.setValue(sequenceBlock.getActiveProperty(), false));

            sequenceNumber = 0;

            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean SequenceFinished(int sequenceIndex, int sequenceLength)
    {
        return sequenceIndex == sequenceLength;
    }

    public void UpdateBlock(Level pLevel, BlockPos pPos, BlockState pState)
    {
        setChanged();
        pLevel.sendBlockUpdated(pPos, pState, pState, Block.UPDATE_ALL);
    }
}
