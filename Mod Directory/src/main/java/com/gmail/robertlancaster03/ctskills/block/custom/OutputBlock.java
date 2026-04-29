package com.gmail.robertlancaster03.ctskills.block.custom;

import com.gmail.robertlancaster03.ctskills.block.ModBlockEntities;
import com.gmail.robertlancaster03.ctskills.block.entity.OutputBlockEntity;
import com.gmail.robertlancaster03.ctskills.block.entity.util.TickingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;

public class OutputBlock extends SequenceBlock implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public OutputBlock(Properties pProperties)
    {
        super(pProperties);
        registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return ModBlockEntities.OUTPUT_BLOCK_ENTITY.get().create(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POWERED);
    }

    /// Changes the start timing/measured clock generator timing on crouch interaction, and the current sequence to measure on regular interaction
    @Override
    @ParametersAreNonnullByDefault
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        if (pPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND)
        {
            OutputBlockEntity outputBlockEntity = (OutputBlockEntity)pLevel.getBlockEntity(pPos); assert outputBlockEntity != null;

            if (pPlayer.isCrouching())
            {
                if (outputBlockEntity.inputType == 3)
                {
                    // Alter the measured timing of a clock generator
                    outputBlockEntity.clockTicks = Math.floorMod(outputBlockEntity.clockTicks * 2, 240);
                    pPlayer.displayClientMessage(Component.literal("Clock ticks = " + outputBlockEntity.clockTicks), true);
                }
                else
                {
                    // Alter the start timing by 5 ticks
                    outputBlockEntity.startingTicks = Math.floorMod(outputBlockEntity.startingTicks + 5, 25);
                    pPlayer.displayClientMessage(Component.literal("Starting ticks = " + outputBlockEntity.startingTicks), true);
                }
            }
            else
            {
                // Change the sequence to be inputted
                outputBlockEntity.SetSequence(pState, pLevel, pPos, pPlayer, null);
            }
            pLevel.playSound(pPlayer, pPos, SoundEvents.NOTE_BLOCK_CHIME.get(), SoundSource.BLOCKS, 1f, 1f);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    @ParametersAreNonnullByDefault
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide)
    {
        OutputBlockEntity outputBlockEntity = (OutputBlockEntity)pBlockAccess.getBlockEntity(pPos); assert outputBlockEntity != null;
        return outputBlockEntity.emitSignal ? 15 : 0;
    }


    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston)
    {
        OutputBlockEntity outputBlockEntity = (OutputBlockEntity)pLevel.getBlockEntity(pPos); assert outputBlockEntity != null;

        if (outputBlockEntity.active && outputBlockEntity.inputType == 3)
        {
            outputBlockEntity.IncrementClockSequence(pState, pLevel, pPos, this);
        }

        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType)
    {
        return TickingEntity.getTickerHelper(pLevel);
    }

    @Override
    @ParametersAreNullableByDefault
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction)
    {
        return true;
    }

    @Override
    @ParametersAreNullableByDefault
    public boolean isSignalSource(BlockState pState)
    {
        return true;
    }

    public BooleanProperty getActiveProperty()
    {
        return POWERED;
    }
}

