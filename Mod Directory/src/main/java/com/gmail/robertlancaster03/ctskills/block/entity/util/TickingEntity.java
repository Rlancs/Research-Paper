package com.gmail.robertlancaster03.ctskills.block.entity.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public interface TickingEntity
{
    void tick(Level pLevel, BlockPos pPos, BlockState pState);

    public static<T extends BlockEntity>BlockEntityTicker<T> getTickerHelper(Level pLevel)
    {
        return pLevel.isClientSide() ? null : (level0, pos0, state0, blockEntity) -> ((TickingEntity)blockEntity).tick(level0, pos0, state0);
    }

}
