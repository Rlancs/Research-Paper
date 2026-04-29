package com.gmail.robertlancaster03.ctskills.block.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OutputBlockEntityTest {

    @Test
    void VerifyComponentType()
    {
        Assertions.assertAll(() -> Assertions.assertEquals("AND Gate", OutputBlockEntity.ComponentType(0)),
                             () -> Assertions.assertEquals("NOT Gate", OutputBlockEntity.ComponentType(1)),
                             () -> Assertions.assertEquals("RS Latch", OutputBlockEntity.ComponentType(2)),
                             () -> Assertions.assertEquals("Clock Generator", OutputBlockEntity.ComponentType(3)));
    }

    @Test
    void VerifyConfirmedSequence()
    {
        int[] targetSequence = {0, 1, 0, 1, 1, 0, 0, 1};
        int[] wrongSequence = {0, 0, 1, 1, 0, 1, 0, 0};
        Assertions.assertAll(() -> Assertions.assertFalse(OutputBlockEntity.ConfirmSequence(wrongSequence, targetSequence)),
                             () -> Assertions.assertTrue(OutputBlockEntity.ConfirmSequence(targetSequence, targetSequence)));
    }
}