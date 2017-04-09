package com.inbar;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Created by 2 on 09/04/2017.
 */
class MainTest {
    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        Main.initOperators();
    }

    @Test
    public void testOneLine() {
        Main.parseLine("i = 5");
        assertEquals(5, Main.symbols.get("i").intValue());
        Main.parseLine("i = 5 * 2");
        assertEquals(10, Main.symbols.get("i").intValue());
        Main.parseLine("i = 5 + 2 * 9");
        assertEquals(23, Main.symbols.get("i").intValue());
        Main.parseLine("i = 105 - 13 * 80");
        assertEquals(-935, Main.symbols.get("i").intValue());
    }

    @Test
    public void testVarNoDependency() {
        Main.parseLine("i = 5 * 2");
        Main.parseLine("j = 5 + 2 * 5 - 9");
        assertEquals(10, Main.symbols.get("i").intValue());
        assertEquals(6, Main.symbols.get("j").intValue());
    }

    @Test
    public void testVarWithDependency() {
        Main.parseLine("i = 5 * 4 - 10");
        Main.parseLine("j = 5 + i * 5 - 9");
        assertEquals(10, Main.symbols.get("i").intValue());
        assertEquals(46, Main.symbols.get("j").intValue());
    }

    @Test
    public void testVarWithDependency() {
        Main.parseLine("i = 5 * 4 - 10");
        Main.parseLine("j = 5 + i * 5 - 9");
        assertEquals(10, Main.symbols.get("i").intValue());
        assertEquals(46, Main.symbols.get("j").intValue());
    }
}