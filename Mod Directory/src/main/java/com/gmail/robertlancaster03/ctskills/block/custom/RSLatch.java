package com.gmail.robertlancaster03.ctskills.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class RSLatch extends DiodeBlock {
    public RSLatch(Properties pProperties)
    {
        super(pProperties);
        registerDefaultState(this.defaultBlockState().setValue(POWERED, true).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected int getDelay(BlockState pState) {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POWERED, FACING);
    }

    /// Gets the signals on the left and right of the latch, returning a positive signal dependent on the input signal
    /// Also depends on the powered state of the latch, with unpowered/powered corresponding to reset/set states
    @Override
    @ParametersAreNonnullByDefault
    protected int getInputSignal(Level pLevel, BlockPos pPos, BlockState pState)
    {
        Direction counterClockwise = pState.getValue(FACING).getCounterClockWise();
        Direction clockwise = pState.getValue(FACING).getClockWise();

        // Gets the signals in the left and right directions
        int signalOne = InputSignal(pLevel, pPos, counterClockwise);
        int signalTwo = InputSignal(pLevel, pPos, clockwise);

        int wireSignalOne = WireSignal(pLevel, pPos.relative(counterClockwise));
        int wireSignalTwo = WireSignal(pLevel, pPos.relative(clockwise));

        // If unpowered, gets the biggest signal of signalOne (switches state to powered)
        if (!pState.getValue(POWERED))
        {
            return Math.max(Math.max(signalOne, wireSignalOne), 0);
        }
        else
        {
            // If powered and signalTwo has power, returns 0 (switches state to unpowered)
            // Otherwise, gets the biggest signal of signalOne
            if (Math.max(signalTwo, wireSignalTwo) > 0)
            {
                return 0;
            }
            else
            {
                return Math.max(Math.max(signalOne, wireSignalOne), 0);
            }
        }
    }

    private int InputSignal(Level pLevel, BlockPos pPos, Direction direction)
    {
        BlockPos dirOnePos = pPos.relative(direction);
        return pLevel.getSignal(dirOnePos, direction);
    }

    private int WireSignal(Level pLevel, BlockPos blockPos)
    {
        BlockState blockState = pLevel.getBlockState(blockPos);
        return blockState.is(Blocks.REDSTONE_WIRE) ? blockState.getValue(RedStoneWireBlock.POWER) : 0;
    }

    /// If powered, outputs a signal to one side
    /// If unpowered, outputs a signal to the other side
    @Override
    @ParametersAreNonnullByDefault
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if (pBlockState.getValue(POWERED))
        {
            return pBlockState.getValue(FACING).getClockWise() == pSide ? this.getOutputSignal(pBlockAccess, pPos, pBlockState) : 0;
        }
        else
        {
            return pBlockState.getValue(FACING).getCounterClockWise() == pSide ? this.getOutputSignal(pBlockAccess, pPos, pBlockState) : 0;
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!this.isLocked(pLevel, pPos, pState)) {
            boolean flag = pState.getValue(POWERED);
            boolean flag1 = this.shouldTurnOn(pLevel, pPos, pState);
            if (flag && !flag1)
            {
                pLevel.setBlock(pPos, pState.setValue(POWERED, false), 2);
            }
            else if (!flag && flag1)
            {
                pLevel.setBlock(pPos, pState.setValue(POWERED, true), 2);
            }
            pLevel.updateNeighborsAt(pPos, this);
        }
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.literal("Stores the last provided input signal"));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
}

