package com.gmail.robertlancaster03.ctskills.block.entity;

import com.gmail.robertlancaster03.ctskills.CompThinkingMod;
import com.gmail.robertlancaster03.ctskills.block.ModBlockEntities;
import com.gmail.robertlancaster03.ctskills.block.custom.OutputBlock;
import com.gmail.robertlancaster03.ctskills.block.custom.SequenceBlock;
import com.gmail.robertlancaster03.ctskills.block.entity.util.TickingEntity;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

public class OutputBlockEntity extends SequenceEntity implements TickingEntity {

    public static final String[] componentTypes =
            {"AND Gate", "NOT Gate", "RS Latch", "Clock Generator"};

    private int[] inputSequenceOne = new int[4];
    private int[] inputSequenceTwo = new int[4];

    private final int[][] componentSequences = {ANDGate, NOTGate, RSLatchOne, ClockSequence};
    private static final int[] ANDGate = {0, 0, 0, 1};
    private static final int[] NOTGate = {1, 0};
    private static final int[] RSLatchOne = {1, 1, 0, 0};
    private static final int[] RSLatchTwo = {0, 0, 1, 1};
    private static final int[] ClockSequence =  {0, 1, 1};

    public int clockTicks = 2;

    private int seconds = 0;
    private int lastClockTime = 0;
    private int currentClockTime = 0;

    public OutputBlockEntity(BlockPos bPos, BlockState bState)
    {
        super(ModBlockEntities.OUTPUT_BLOCK_ENTITY.get(), bPos, bState);
    }

    /// Whilst active, checks for the required outputs of the assigned tested component
    @Override
    @ParametersAreNonnullByDefault
    public void tick(Level pLevel, BlockPos pPos, BlockState pState)
    {
        if (this.level == null || pLevel.isClientSide()) { return; }

        OutputBlock outputBlock = (OutputBlock)pState.getBlock();

        if (active) {

            // Increment recorded time
            ticks++;

            // If measuring a clock generator, increment clock time
            if (inputType == 3)
            {
                currentClockTime++;
            }

            // After a second passes, record the current output and check for sequence progression/completion
            if (SecondHasPassed(ticks))
            {
                String componentType = ComponentType(inputType);
                switch (componentType)
                {
                    case "AND Gate":
                        ProcessGate(ANDGate, pLevel, pPos, pState, outputBlock,
                                new ResourceLocation("ctskills:andgate"), "ANDGate", "AND Gate!");
                        break;
                    case "NOT Gate":
                        ProcessGate(NOTGate, pLevel, pPos, pState, outputBlock,
                                new ResourceLocation("ctskills:notgate"), "NOTGate", "NOT Gate!");
                        break;
                    case "RS Latch":
                        // Checks for two simultaneous input sequences. If both are finished, check for completion
                        if (FinishedSequence(RSLatchOne) && FinishedSequence(RSLatchTwo))
                        {
                            boolean correctSequence = (CorrectSequence(pLevel, pPos, pState, inputSequenceOne, RSLatchOne, outputBlock))
                                                   && (CorrectSequence(pLevel, pPos, pState, inputSequenceTwo, RSLatchTwo, outputBlock));

                            OutputResult(correctSequence, pLevel, pPos,
                                    new ResourceLocation("ctskills:rslatch"), "RSLatch", "RS Latch!");

                            Minecraft.getInstance().player.displayClientMessage(Component.literal("Finished!"), true);
                        }
                        // Otherwise, record the current inputs and increment the sequence
                        else
                        {
                            DirectionalSequence(pLevel, inputSequenceOne, RSLatchOne, Direction.WEST, pPos.west(), Direction.NORTH, pPos.north());
                            DirectionalSequence(pLevel, inputSequenceTwo, RSLatchTwo, Direction.EAST, pPos.east(), Direction.SOUTH, pPos.south());
                            sequenceNumber++;

                            Minecraft.getInstance().player.displayClientMessage(Component.literal("Testing..."), true);
                        }
                        break;
                    case "Clock Generator":
                        seconds++;

                        if (seconds > 30)
                        {
                            seconds = 0;
                            lastClockTime = 0;
                            currentClockTime = 0;

                            emitSignal = false;
                            pLevel.updateNeighborsAt(pPos, outputBlock);

                            active = false;
                            pLevel.setBlockAndUpdate(pPos, pState.setValue(outputBlock.getActiveProperty(), false));

                            Minecraft.getInstance().player.displayClientMessage(Component.literal("Finished!"), true);
                        }
                        else
                        {
                            Minecraft.getInstance().player.displayClientMessage(Component.literal("Testing..."), true);
                        }

                        break;
                }

                ticks = 0;
                UpdateBlock(pLevel, pPos, pState);
            }
        }
    }

    public static String ComponentType(int inputType)
    {
        return componentTypes[inputType];
    }

    /// If sequence is finished, checks for a correct sequence. Otherwise, record the success of the current inputs and increment the sequence.
    private void ProcessGate(int[] gateSequence, Level pLevel, BlockPos pPos, BlockState pState, OutputBlock outputBlock,
                             ResourceLocation advancementLocation, String advancementKey, String advancementOutput )
    {
        // If sequence is fully inputted, check if sequence is correct
        if (FinishedSequence(gateSequence))
        {
            boolean correctSequence = (CorrectSequence(pLevel, pPos, pState, inputSequenceOne, gateSequence, outputBlock));
            OutputResult(correctSequence, pLevel, pPos,
                    advancementLocation, advancementKey, advancementOutput);

            Minecraft.getInstance().player.displayClientMessage(Component.literal("Finished!"), true);
        }
        // If not finished, record the current inputs and increment the sequence
        else
        {
            DirectionalSequence(pLevel, inputSequenceOne, gateSequence, Direction.UP, pPos.above(), Direction.DOWN, pPos.below());
            sequenceNumber++;

            Minecraft.getInstance().player.displayClientMessage(Component.literal("Testing..."), true);
        }
    }

    @Override
    public void SetSequence(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, @Nullable String[] sequences)
    {
        super.SetSequence(pState, pLevel, pPos, pPlayer, componentTypes);

        int inputSequenceLength = componentSequences[inputType].length;
        inputSequenceOne = new int[inputSequenceLength];
        inputSequenceTwo = new int[inputSequenceLength];
    }

    /// Retrieves an input signal from a specified direction, comparing it with the intended input and outputting a 'Correct' or 'Incorrect'
    private void DirectionalSequence(Level pLevel, int[] inputSequence, int[] targetSequence,
                                     Direction directionOne, BlockPos directionOnePos, Direction directionTwo, BlockPos directionTwoPos)
    {
        inputSequence[sequenceNumber] = (pLevel.getSignal(directionOnePos, directionOne) > 0 || pLevel.getSignal(directionTwoPos, directionTwo) > 0) ? 1 : 0;

        Minecraft.getInstance().player.displayClientMessage(Component.literal
                ("Input #" + (sequenceNumber + 1) + " - " +
                        inputSequence[sequenceNumber] + " - " +
                        (inputSequence[sequenceNumber] == targetSequence[sequenceNumber] ? "Correct" : "Incorrect")), false);
    }

    private boolean FinishedSequence(int[] targetSequence)
    {
        return sequenceNumber == targetSequence.length;
    }

    /// If the input sequence and target sequence are equal, return true. Otherwise, return false.
    /// Regardless of the result, deactivate the block
    public boolean CorrectSequence(Level pLevel, BlockPos pPos, BlockState pState, int[] inputSequence, int[] targetSequence, OutputBlock outputBlock)
    {
        boolean correctSequence = ConfirmSequence(inputSequence, targetSequence);

        emitSignal = false;
        pLevel.updateNeighborsAt(pPos, outputBlock);

        active = false;
        pLevel.setBlockAndUpdate(pPos, pState.setValue(outputBlock.getActiveProperty(), false));

        Arrays.fill(inputSequence, 0);
        sequenceNumber = 0;

        return correctSequence;
    }

    public static boolean ConfirmSequence(int[] inputSequence, int[] targetSequence)
    {
        return Arrays.equals(inputSequence, targetSequence);
    }

    /// If sequence is correct, it is messaged to the player with an associated Advancement if not already attained
    /// The advancement awards the tool block associated with the implemented component
    /// If sequence is incorrect, message it to the player
    private void OutputResult(boolean correctSequence, Level pLevel, BlockPos pPos, ResourceLocation advancementLocation, String advancementKey, String advancementOutput)
    {
        if (correctSequence)
        {
            ServerPlayer serverPlayer = Minecraft.getInstance().getSingleplayerServer().getPlayerList().getPlayer(Minecraft.getInstance().player.getUUID());
            Advancement advancement = Minecraft.getInstance().getSingleplayerServer().getAdvancements().getAdvancement(advancementLocation);

            serverPlayer.getAdvancements().award(advancement, advancementKey);

            Minecraft.getInstance().player.displayClientMessage(Component.literal("Correct component! - " + advancementOutput), false);
            pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_CHIME.get(), SoundSource.BLOCKS, 1f, 1f);
        }
        else
        {
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Wrong component! Try something else."), false);
            pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_DIDGERIDOO.get(), SoundSource.BLOCKS, 1f, 1f);
        }
    }

    @Override
    public void CheckForStart(BlockState pState, Level pLevel, BlockPos pPos, SequenceBlock sequenceBlock, boolean signal) {
        boolean adjacentSignal = (pLevel.hasNeighborSignal(pPos) && inputType == 0)
                || (!pLevel.hasNeighborSignal(pPos) && inputType == 1)
                || (((pLevel.getSignal(pPos.west(), Direction.WEST) > 0 && pLevel.getSignal(pPos.east(), Direction.EAST) > 0)
                || (pLevel.getSignal(pPos.north(), Direction.NORTH) > 0 && pLevel.getSignal(pPos.south(), Direction.SOUTH) > 0)) && inputType == 2)
                || (pLevel.getSignal(pPos.above(), Direction.UP) > 0 && inputType == 3);

        super.CheckForStart(pState, pLevel, pPos, sequenceBlock, adjacentSignal);
    }

    public void IncrementClockSequence(BlockState pState, Level pLevel, BlockPos pPos, OutputBlock outputBlock)
    {
        boolean signal = pLevel.getSignal(pPos.above(), Direction.UP) > 0;

        if (!primed && signal)
        {
            primed = true;
        }
        else if (primed && !signal)
        {
            primed = false;

            if (FinishedSequence(ClockSequence))
            {
                boolean correctSequence = (CorrectSequence(pLevel, pPos, pState, inputSequenceOne, ClockSequence, outputBlock));

                if (correctSequence && lastClockTime != clockTicks)
                {
                    Minecraft.getInstance().player.displayClientMessage(Component.literal("Correct component, but wrong tick delay - Needs " + clockTicks + " ticks"), false);
                    pLevel.playSound(null, pPos, SoundEvents.NOTE_BLOCK_DIDGERIDOO.get(), SoundSource.BLOCKS, 1f, 1f);
                }
                else
                {
                    OutputResult(correctSequence, pLevel, pPos,
                            new ResourceLocation("ctskills:clockgenerator"), "ClockGenerator", "Clock Generator!");

                }

                sequenceNumber = 0;
                lastClockTime = 0;
                seconds = 0;

                Minecraft.getInstance().player.displayClientMessage(Component.literal("Finished!"), true);
            }
            else
            {
                inputSequenceOne[sequenceNumber] = currentClockTime == lastClockTime ? 1 : 0;
                lastClockTime = currentClockTime;

                Minecraft.getInstance().player.displayClientMessage(Component.literal
                        ("Input #" + (sequenceNumber + 1) + " - " +
                        inputSequenceOne[sequenceNumber] + " - " +
                        (inputSequenceOne[sequenceNumber] == ClockSequence[sequenceNumber] ? "Correct" : "Incorrect")), false);

                sequenceNumber++;
            }

            currentClockTime = 0;
        }

        UpdateBlock(pLevel, pPos, pState);
    }

    @Override
    public void StartSequence(BlockState pState, Level pLevel, BlockPos pPos, SequenceBlock sequenceBlock) {
        super.StartSequence(pState, pLevel, pPos, sequenceBlock);

        Minecraft.getInstance().player.displayClientMessage(Component.literal("Testing..."), true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        CompoundTag blockData = nbt.getCompound(CompThinkingMod.MODID);
        inputType = blockData.getInt("output_block.inputType");
        startingTicks = blockData.getInt("output_block.startingTicks");
        clockTicks = blockData.getInt("output_block.clockTicks");
        inputSequenceOne = blockData.getIntArray("output_block.inputSequenceOne");
        inputSequenceTwo = blockData.getIntArray("output_block.inputSequenceTwo");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);

        CompoundTag blockData = new CompoundTag();
        blockData.putInt("output_block.inputType", inputType);
        blockData.putInt("output_block.startingTicks", startingTicks);
        blockData.putInt("output_block.clockTicks", clockTicks);
        blockData.putIntArray("output_block.inputSequenceOne", inputSequenceOne);
        blockData.putIntArray("output_block.inputSequenceTwo", inputSequenceTwo);
        nbt.put(CompThinkingMod.MODID, blockData);
    }
}
