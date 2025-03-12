package com.tales.koreanvp.model;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void testToString() {
        assertEquals(new Player("test", 100, 5, 3).toString(),
                "Player{username='test', credits=100, wins=5, losses=3}");
    }
}
