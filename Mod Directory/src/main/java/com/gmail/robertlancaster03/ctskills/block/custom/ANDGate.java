package com.gmail.robertlancaster03.ctskills.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ANDGate extends DiodeBlock {
    public ANDGate(Properties pProperties)
    {
        super(pProperties);
        registerDefaultState(this.defaultBlockState().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected int getDelay( BlockState pState) {
        return 4;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder)
    {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POWERED, FACING);
    }

    /// Gets the signals on the left and right of the gate, returning a positive signal if both are active.
    @Override
    protected int getInputSignal(Level pLevel, BlockPos pPos, BlockState pState)
    {
        Direction dirOne = pState.getValue(FACING).getClockWise();
        Direction dirTwo = pState.getValue(FACING).getCounterClockWise();
        BlockPos dirOnePos = pPos.relative(dirOne);
        BlockPos dirTwoPos = pPos.relative(dirTwo);

        // Gets the signals in the left and right directions
        int signalOne = pLevel.getSignal(dirOnePos, dirOne);
        int signalTwo = pLevel.getSignal(dirTwoPos, dirTwo);

        // If both signals are at maximum value, return a maximum signal
        if (signalOne >= 15 && signalTwo >= 15)
        {
            return 15;
        }
        else
        {
            // Otherwise, return a value proportionate to the smallest of the two provided signals
            return Math.max(Math.min(signalOne, signalTwo),
                   Math.min(WireSignal(pLevel, dirOnePos), WireSignal(pLevel, dirTwoPos)));
        }
    }

    private int WireSignal(Level pLevel, BlockPos blockPos)
    {
        BlockState blockState = pLevel.getBlockState(blockPos);
        return blockState.is(Blocks.REDSTONE_WIRE) ? blockState.getValue(RedStoneWireBlock.POWER) : 0;
    }

    /// Outputs a signal forwards and backwards whilst powered
    /// Determined by the signal returned by getInputSignal
    @Override
    @ParametersAreNonnullByDefault
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if (pBlockState.getValue(POWERED))
        {
            return pBlockState.getValue(FACING) == pSide || pBlockState.getValue(FACING).getOpposite() == pSide ? this.getOutputSignal(pBlockAccess, pPos, pBlockState) : 0;
        }

        return 0;
    }

    @Override
    @ParametersAreNonnullByDefault
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
        pTooltip.add(Component.literal("Outputs a signal if provided two input signals"));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
}

