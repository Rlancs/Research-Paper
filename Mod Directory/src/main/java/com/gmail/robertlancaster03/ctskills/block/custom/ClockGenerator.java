package com.gmail.robertlancaster03.ctskills.block.custom;

import com.gmail.robertlancaster03.ctskills.block.ModBlockEntities;
import com.gmail.robertlancaster03.ctskills.block.entity.ClockGeneratorEntity;
import com.gmail.robertlancaster03.ctskills.block.entity.util.TickingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.List;

public class ClockGenerator extends SequenceBlock implements EntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public ClockGenerator(Properties pProperties)
    {
        super(pProperties);
        registerDefaultState(this.defaultBlockState().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    @ParametersAreNonnullByDefault
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState)
    {
        return ModBlockEntities.CLOCK_GENERATOR_ENTITY.get().create(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POWERED, FACING);
    }

    /// Changes the number of ticks between each pulse on crouch interaction, and the total number of outputs on regular interaction
    @Override
    @ParametersAreNonnullByDefault
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit)
    {
        if (pPlayer.getUsedItemHand() == InteractionHand.MAIN_HAND)
        {
            ClockGeneratorEntity clockGeneratorEntity = (ClockGeneratorEntity)pLevel.getBlockEntity(pPos); assert clockGeneratorEntity != null;

            if (pPlayer.isCrouching())
            {
                // Doubles the current clock ticks, with an upper maximum of 128 ticks
                clockGeneratorEntity.redstoneTicks = Math.floorMod(clockGeneratorEntity.redstoneTicks * 2, 240);
                pPlayer.displayClientMessage(Component.literal("Clock ticks = " + clockGeneratorEntity.redstoneTicks), true);
            }
            else
            {
                // Increases the total number of outputs in a loop by 1, with an upper maximum of 10
                clockGeneratorEntity.numberOfOutputs = Math.floorMod(clockGeneratorEntity.numberOfOutputs + 1, 11);
                pPlayer.displayClientMessage(Component.literal("Number of outputs = " + clockGeneratorEntity.numberOfOutputs), true);
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
        ClockGeneratorEntity clockGeneratorEntity = (ClockGeneratorEntity)pBlockAccess.getBlockEntity(pPos); assert clockGeneratorEntity != null;
        return clockGeneratorEntity.emitSignal ? 15 : 0;
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

    public BlockState getStateForPlacement(BlockPlaceContext pContext)
    {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.literal("Outputs an oscillating signal of consistent frequency"));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    public BooleanProperty getActiveProperty()
    {
        return POWERED;
    }
}

