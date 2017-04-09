package com.inbar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

/**
 * Created by 2 on 09/04/2017.
 */
class MainTest {
    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        Main.initOperators();
    }

    @AfterEach
    void tearDown() {
        Main.symbols = new LinkedHashMap<>();
    }

    @Test
    public void testOneLine() {
        Main.parseLine("i = 5");
        int i = 5;
        assertEquals(i, Main.symbols.get("i").intValue());
        i = 5 * 2;
        Main.parseLine("i = 5 * 2");
        assertEquals(i, Main.symbols.get("i").intValue());
        i = 5 + 2 * 9;
        Main.parseLine("i = 5 + 2 * 9");
        assertEquals(i, Main.symbols.get("i").intValue());
        i = 105 - 13 * 80;
        Main.parseLine("i = 105 - 13 * 80");
        assertEquals(i, Main.symbols.get("i").intValue());
    }

    @Test
    public void testVarNoDependency() {
        int i = 5 * 2;
        int j = 5 + 2 * 5 - 9;
        Main.parseLine("j = 5 + 2 * 5 - 9");
        Main.parseLine("i = 5 * 2");
        assertEquals(i, Main.symbols.get("i").intValue());
        assertEquals(j, Main.symbols.get("j").intValue());
    }

    @Test
    public void testVarWithDependency() {
        int i = 5 * 4 - 10;
        int j = 5 + i / 5 - 9;
        Main.parseLine("i = 5 * 4 - 10");
        Main.parseLine("j = 5 + i / 5 - 9");
        assertEquals(i, Main.symbols.get("i").intValue());
        assertEquals(j, Main.symbols.get("j").intValue());
    }

    @Test
    public void testVarWithAssignment() {
        int i = 5;
        i += 5;
        int j = 7;
        j *= i;
        int k = 20;
        k -= i;
        int x = 10;
        x /= i;

        Main.parseLine("i = 5");
        Main.parseLine("i += 5");
        Main.parseLine("j = 7");
        Main.parseLine("j *= i");
        Main.parseLine("k = 20");
        Main.parseLine("k -= i");
        Main.parseLine("x = 10");
        Main.parseLine("x /= i");

        assertEquals(i, Main.symbols.get("i").intValue(), "Failed to compute i += 5");
        assertEquals(j, Main.symbols.get("j").intValue(), "Failed to compute j *= i");
        assertEquals(k, Main.symbols.get("k").intValue(), "Failed to compute k -= i");
        assertEquals(x, Main.symbols.get("x").intValue(), "Failed to compute x /= i");
    }

    @Test
    public void testVarWithUnary() {
        int i = 20 - 10 / 2;
        int j = i++ / 3;

        Main.parseLine("i = 20 - 10 / 2");
        Main.parseLine("j = i++ / 3");
        assertEquals(i, Main.symbols.get("i").intValue());
        assertEquals(j, Main.symbols.get("j").intValue());
    }

    @Test
    public void testTaboolaExample() {
        int i = 0;
        int j = ++i;
        int x = i++ + 5;
        int y = 5 + 3 * 10;
        i += y;

        Main.parseLine("i = 0");
        Main.parseLine("j = ++i");
        Main.parseLine("x = i++ + 5");
        Main.parseLine("y = 5 + 3 * 10");
        Main.parseLine("i += y");
        assertEquals("(i=37,j=1,x=6,y=35)", Main.formatResults());
        assertEquals(i, Main.symbols.get("i").intValue());
        assertEquals(j, Main.symbols.get("j").intValue());
        assertEquals(x, Main.symbols.get("x").intValue());
        assertEquals(y, Main.symbols.get("y").intValue());
    }
}