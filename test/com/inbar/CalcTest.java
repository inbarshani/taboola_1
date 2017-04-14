package com.inbar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

/**
 * Created by 2 on 09/04/2017.
 */
class CalcTest {
    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        Calc.initOperators();
    }

    @AfterEach
    void tearDown() {
        Calc.symbols = new LinkedHashMap<>();
    }

    @Test
    public void testOneLine() {
        Calc.parseLine("i = 5");
        int i = 5;
        assertEquals(i, Calc.symbols.get("i").intValue());
        i = 5 * 2;
        Calc.parseLine("i = 5 * 2");
        assertEquals(i, Calc.symbols.get("i").intValue());
        i = 5 + 2 * 9;
        Calc.parseLine("i = 5 + 2 * 9");
        assertEquals(i, Calc.symbols.get("i").intValue());
        i = 105 - 13 * 80;
        Calc.parseLine("i = 105 - 13 * 80");
        assertEquals(i, Calc.symbols.get("i").intValue());
    }

    @Test
    public void testVarNoDependency() {
        int i = 5 * 2;
        int j = 5 + 2 * 5 - 9;
        Calc.parseLine("j = 5 + 2 * 5 - 9");
        Calc.parseLine("i = 5 * 2");
        assertEquals(i, Calc.symbols.get("i").intValue());
        assertEquals(j, Calc.symbols.get("j").intValue());
    }

    @Test
    public void testVarWithDependency() {
        int i = 5 * 4 - 10;
        int j = 5 + i / 5 - 9;
        Calc.parseLine("i = 5 * 4 - 10");
        Calc.parseLine("j = 5 + i / 5 - 9");
        assertEquals(i, Calc.symbols.get("i").intValue());
        assertEquals(j, Calc.symbols.get("j").intValue());
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

        Calc.parseLine("i = 5");
        Calc.parseLine("i += 5");
        Calc.parseLine("j = 7");
        Calc.parseLine("j *= i");
        Calc.parseLine("k = 20");
        Calc.parseLine("k -= i");
        Calc.parseLine("x = 10");
        Calc.parseLine("x /= i");

        assertEquals(i, Calc.symbols.get("i").intValue(), "Failed to compute i += 5");
        assertEquals(j, Calc.symbols.get("j").intValue(), "Failed to compute j *= i");
        assertEquals(k, Calc.symbols.get("k").intValue(), "Failed to compute k -= i");
        assertEquals(x, Calc.symbols.get("x").intValue(), "Failed to compute x /= i");
    }

    @Test
    public void testVarWithUnary() {
        int i = 20 - 10 / 2;
        int j = i++ / 3;

        Calc.parseLine("i = 20 - 10 / 2");
        Calc.parseLine("j = i++ / 3");
        assertEquals(i, Calc.symbols.get("i").intValue());
        assertEquals(j, Calc.symbols.get("j").intValue());
    }

    @Test
    public void testTaboolaExample() {
        int i = 0;
        int j = ++i;
        int x = i++ + 5;
        int y = 5 + 3 * 10;
        i += y;

        Calc.parseLine("i = 0");
        Calc.parseLine("j = ++i");
        Calc.parseLine("x = i++ + 5");
        Calc.parseLine("y = 5 + 3 * 10");
        Calc.parseLine("i += y");
        assertEquals("(i=37,j=1,x=6,y=35)", Calc.formatResults());
        assertEquals(i, Calc.symbols.get("i").intValue());
        assertEquals(j, Calc.symbols.get("j").intValue());
        assertEquals(x, Calc.symbols.get("x").intValue());
        assertEquals(y, Calc.symbols.get("y").intValue());
    }
}