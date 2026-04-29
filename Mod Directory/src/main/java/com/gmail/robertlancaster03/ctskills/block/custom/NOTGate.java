package com.gmail.robertlancaster03.ctskills.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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

public class NOTGate extends DiodeBlock {
    public NOTGate(Properties pProperties)
    {
        super(pProperties);
        registerDefaultState(this.defaultBlockState().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    @ParametersAreNonnullByDefault
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

    /// Gets the signals on the left, right and backward directions of the gate, returning a positive signal any are active.
    @Override
    protected int getInputSignal(Level pLevel, BlockPos pPos, BlockState pState)
    {
        Direction dirOne = pState.getValue(FACING).getClockWise();
        Direction dirTwo = pState.getValue(FACING).getCounterClockWise();
        Direction dirThree = pState.getValue(FACING);

        BlockPos dirOnePos = pPos.relative(dirOne);
        BlockPos dirTwoPos = pPos.relative(dirTwo);
        BlockPos dirThreePos = pPos.relative(dirThree);

        // Gets the signals in the left, right and backward directions
        int signalOne = pLevel.getSignal(dirOnePos, dirOne);
        int signalTwo = pLevel.getSignal(dirTwoPos, dirTwo);
        int signalThree = pLevel.getSignal(dirThreePos, dirThree);

        // If any signals are at maximum value, return a maximum signal
        if (signalOne >= 15 || signalTwo >= 15 || signalThree >= 15)
        {
            return 15;
        }
        else
        {
            int retrievedSignal = Math.max(signalOne, Math.max(signalTwo, signalThree));

            // Otherwise, return a value proportionate to the biggest of the provided signals
            return Math.max(retrievedSignal, Math.max(WireSignal(pLevel, dirOnePos),
                   Math.max(WireSignal(pLevel, dirTwoPos), WireSignal(pLevel, dirThreePos))));
        }
    }

    private int WireSignal(Level pLevel, BlockPos blockPos)
    {
        BlockState blockState = pLevel.getBlockState(blockPos);
        return blockState.is(Blocks.REDSTONE_WIRE) ? blockState.getValue(RedStoneWireBlock.POWER) : 0;
    }

    /// Outputs a signal forward whilst not powered, and no signal whilst powered
    /// Determined by the signal returned by getInputSignal
    @Override
    @ParametersAreNonnullByDefault
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        if (pBlockState.getValue(POWERED))
        {
            return 0;
        }
        else
        {
            return pBlockState.getValue(FACING) == pSide ? this.getOutputSignal(pBlockAccess, pPos, pBlockState) : 0;
        }
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
        pTooltip.add(Component.literal("Outputs the opposite of any provided input signal"));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }
}

