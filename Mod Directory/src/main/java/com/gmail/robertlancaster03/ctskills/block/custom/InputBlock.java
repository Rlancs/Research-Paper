package com.gmail.robertlancaster03.ctskills.block.custom;

import com.gmail.robertlancaster03.ctskills.block.ModBlockEntities;
import com.gmail.robertlancaster03.ctskills.block.entity.InputBlockEntity;
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

public class InputBlock extends SequenceBlock implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public InputBlock(Properties pProperties)
    {
        super(pProperties);
        registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return ModBlockEntities.INPUT_BLOCK_ENTITY.get().create(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POWERED);
    }

    /// Changes the start timing on crouch interaction, and the current sequence to input on regular interaction
    @Override
    @ParametersAreNonnullByDefault
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        if (pPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND)
        {
            InputBlockEntity inputBlockEntity = (InputBlockEntity)pLevel.getBlockEntity(pPos); assert inputBlockEntity != null;

            if (pPlayer.isCrouching())
            {
                // Alter the start timing by 5 ticks
                inputBlockEntity.startingTicks = Math.floorMod(inputBlockEntity.startingTicks + 5, 25);
                pPlayer.displayClientMessage(Component.literal("Startingticks = " + inputBlockEntity.startingTicks), true);
            }
            else
            {
                // Change the sequence to be inputted
                inputBlockEntity.SetSequence(pState, pLevel, pPos, pPlayer, null);
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
        InputBlockEntity inputBlockEntity = (InputBlockEntity)pBlockAccess.getBlockEntity(pPos); assert inputBlockEntity != null;
        return inputBlockEntity.emitSignal ? 15 : 0;
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

