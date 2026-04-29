package com.gmail.robertlancaster03.ctskills.block.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SequenceEntityTest {

    @Test
    void VerifySecondHasPassed()
    {
        int halfSecond = 10;
        int nearlySecond = 19;
        int second = 20;
        Assertions.assertAll(() -> Assertions.assertFalse(SequenceEntity.SecondHasPassed(halfSecond)),
                             () -> Assertions.assertFalse(SequenceEntity.SecondHasPassed(nearlySecond)),
                             () -> Assertions.assertTrue(SequenceEntity.SecondHasPassed(second)));

    }

    @Test
    void VerifyPrimedSequence()
    {
        boolean primedTrue = true;
        boolean primedFalse = false;
        Assertions.assertAll(() -> Assertions.assertFalse(SequenceEntity.PrimeSequence(primedTrue, false)),
                             () -> Assertions.assertEquals(primedTrue, SequenceEntity.PrimeSequence(primedTrue, true)),
                             () -> Assertions.assertTrue(SequenceEntity.PrimeSequence(primedFalse, true)),
                             () -> Assertions.assertEquals(primedFalse, SequenceEntity.PrimeSequence(primedFalse, false)));
    }

    @Test
    void VerifySequenceFinished()
    {
        Assertions.assertAll(() -> Assertions.assertTrue(SequenceEntity.SequenceFinished(3, 3)),
                             () -> Assertions.assertFalse(SequenceEntity.SequenceFinished(2, 4)));

    }

    @Test
    void VerifyChangeSequence()
    {
        int sequenceType = 0;
        int sequenceTypesLength = 3;
        Assertions.assertAll(() -> Assertions.assertEquals(sequenceType + 1, SequenceEntity.ChangeSequence(sequenceType, sequenceTypesLength)),
                             () -> Assertions.assertEquals(sequenceType + 2, SequenceEntity.ChangeSequence(sequenceType + 1, sequenceTypesLength)),
                             () -> Assertions.assertEquals(sequenceType, SequenceEntity.ChangeSequence(sequenceType + 2, sequenceTypesLength)));

    }
}
