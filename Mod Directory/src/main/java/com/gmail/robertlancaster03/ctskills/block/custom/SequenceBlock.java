package com.gmail.robertlancaster03.ctskills.block.custom;

import com.gmail.robertlancaster03.ctskills.block.entity.SequenceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.ParametersAreNonnullByDefault;

public class SequenceBlock extends Block
{
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public SequenceBlock(BlockBehaviour.Properties pProperties)
    {
        super(pProperties);
        registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    /// If the sequence is inactive, check for start when a neighbouring block changes
    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston)
    {
        SequenceEntity sequenceEntity = (SequenceEntity)pLevel.getBlockEntity(pPos); assert sequenceEntity != null;
        if (!sequenceEntity.active)
        {
            sequenceEntity.CheckForStart(pState, pLevel, pPos, this, true);
        }
    }

    public BooleanProperty getActiveProperty()
    {
        return POWERED;
    }
}
