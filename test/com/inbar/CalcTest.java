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
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testRegExpOneLine() {
        RegExpCalc calc = new RegExpCalc();
        calc.parseLine("i = 5");
        int i = 5;
        assertEquals(i, calc.symbols.get("i").intValue());
        i = 5 * 2;
        calc.parseLine("i = 5 * 2");
        assertEquals(i, calc.symbols.get("i").intValue());
        i = 5 + 2 * 9;
        calc.parseLine("i = 5 + 2 * 9");
        assertEquals(i, calc.symbols.get("i").intValue());
        i = 105 - 13 * 80;
        calc.parseLine("i = 105 - 13 * 80");
        assertEquals(i, calc.symbols.get("i").intValue());
    }

    @Test
    public void testExpTreeOneLine() {
        ExpressionTreeCalc calc = new ExpressionTreeCalc();
        calc.parseLine("i = 5");
        int i = 5;
        assertEquals(i, calc.symbols.get("i").intValue());
        i = 5 * 2;
        calc.parseLine("i = 5 * 2");
        assertEquals(i, calc.symbols.get("i").intValue());
        i = 5 + 2 * 9;
        calc.parseLine("i = 5 + 2 * 9");
        assertEquals(i, calc.symbols.get("i").intValue());
        i = 105 - 13 * 80;
        calc.parseLine("i = 105 - 13 * 80");
        assertEquals(i, calc.symbols.get("i").intValue());

    }

    @Test
    public void testRegExpVarNoDependency() {
        RegExpCalc calc = new RegExpCalc();
        int i = 5 * 2;
        int j = 5 + 2 * 5 - 9;
        calc.parseLine("j = 5 + 2 * 5 - 9");
        calc.parseLine("i = 5 * 2");
        assertEquals(i, calc.symbols.get("i").intValue());
        assertEquals(j, calc.symbols.get("j").intValue());
    }

    @Test
    public void testExpTreeVarNoDependency() {
        ExpressionTreeCalc calc = new ExpressionTreeCalc();
        int i = 5 * 2;
        int j = 5 + 2 * 5 - 9;
        calc.parseLine("j = 5 + 2 * 5 - 9");
        calc.parseLine("i = 5 * 2");
        assertEquals(i, calc.symbols.get("i").intValue());
        assertEquals(j, calc.symbols.get("j").intValue());
    }

    @Test
    public void testRegExpVarWithDependency() {
        RegExpCalc calc = new RegExpCalc();
        int i = 5 * 4 - 10;
        int j = 5 + i / 5 - 9;
        calc.parseLine("i = 5 * 4 - 10");
        calc.parseLine("j = 5 + i / 5 - 9");
        assertEquals(i, calc.symbols.get("i").intValue());
        assertEquals(j, calc.symbols.get("j").intValue());
    }

    @Test
    public void testExpTreeVarWithDependency() {
        ExpressionTreeCalc calc = new ExpressionTreeCalc();
        int i = 5 * 4 - 10;
        int j = 5 + i / 5 - 9;
        calc.parseLine("i = 5 * 4 - 10");
        calc.parseLine("j = 5 + i / 5 - 9");
        assertEquals(i, calc.symbols.get("i").intValue());
        assertEquals(j, calc.symbols.get("j").intValue());
    }

    @Test
    public void testRegExpVarWithAssignment() {
        RegExpCalc calc = new RegExpCalc();
        int i = 5;
        i += 5;
        int j = 7;
        j *= i;
        int k = 20;
        k -= i;
        int x = 10;
        x /= i;

        calc.parseLine("i = 5");
        calc.parseLine("i += 5");
        calc.parseLine("j = 7");
        calc.parseLine("j *= i");
        calc.parseLine("k = 20");
        calc.parseLine("k -= i");
        calc.parseLine("x = 10");
        calc.parseLine("x /= i");

        assertEquals(i, calc.symbols.get("i").intValue(), "Failed to compute i += 5");
        assertEquals(j, calc.symbols.get("j").intValue(), "Failed to compute j *= i");
        assertEquals(k, calc.symbols.get("k").intValue(), "Failed to compute k -= i");
        assertEquals(x, calc.symbols.get("x").intValue(), "Failed to compute x /= i");
    }

    @Test
    public void testExpTreeVarWithAssignment() {
        ExpressionTreeCalc calc = new ExpressionTreeCalc();
        int i = 5;
        i += 5;
        int j = 7;
        j *= i;
        int k = 20;
        k -= i;
        int x = 10;
        x /= i;

        calc.parseLine("i = 5");
        calc.parseLine("i += 5");
        calc.parseLine("j = 7");
        calc.parseLine("j *= i");
        calc.parseLine("k = 20");
        calc.parseLine("k -= i");
        calc.parseLine("x = 10");
        calc.parseLine("x /= i");

        assertEquals(i, calc.symbols.get("i").intValue(), "Failed to compute i += 5");
        assertEquals(j, calc.symbols.get("j").intValue(), "Failed to compute j *= i");
        assertEquals(k, calc.symbols.get("k").intValue(), "Failed to compute k -= i");
        assertEquals(x, calc.symbols.get("x").intValue(), "Failed to compute x /= i");
    }

    @Test
    public void testRegExpVarWithUnary() {
        RegExpCalc calc = new RegExpCalc();
        int i = 20 - 10 / 2;
        int x = 6;
        int m = 4;
        int j = i++ / 3; // 5
        int k = ++j / 3; // 2
        int y = x + x++ / 3; // 8
        int z = m + ++m * 2; // 14

        calc.parseLine("i = 20 - 10 / 2");
        calc.parseLine("x = 6");
        calc.parseLine("m = 4");
        calc.parseLine("j = i++ / 3");
        calc.parseLine("k = ++j / 3");
        calc.parseLine("y = x + x++ / 3");
        calc.parseLine("z = m + ++m * 2");
        assertEquals(i, calc.symbols.get("i").intValue());
        assertEquals(j, calc.symbols.get("j").intValue());
        assertEquals(k, calc.symbols.get("k").intValue());
        assertEquals(x, calc.symbols.get("x").intValue());
        assertEquals(m, calc.symbols.get("m").intValue());
        assertEquals(y, calc.symbols.get("y").intValue());
        assertEquals(z, calc.symbols.get("z").intValue());
    }

    @Test
    public void testExpTreeVarWithUnary() {
        ExpressionTreeCalc calc = new ExpressionTreeCalc();
        int i = 20 - 10 / 2;
        int x = 6;
        int m = 4;
        int j = i++ / 3; // 5
        int k = ++j / 3; // 2
        int y = x + x++ / 3; // 8
        int z = m + ++m * 2; // 14

        calc.parseLine("i = 20 - 10 / 2");
        calc.parseLine("x = 6");
        calc.parseLine("m = 4");
        calc.parseLine("j = i++ / 3");
        calc.parseLine("k = ++j / 3");
        calc.parseLine("y = x + x++ / 3");
        calc.parseLine("z = m + ++m * 2");
        assertEquals(i, calc.symbols.get("i").intValue());
        assertEquals(j, calc.symbols.get("j").intValue());
        assertEquals(k, calc.symbols.get("k").intValue());
        assertEquals(x, calc.symbols.get("x").intValue());
        assertEquals(m, calc.symbols.get("m").intValue());
        assertEquals(y, calc.symbols.get("y").intValue());
        assertEquals(z, calc.symbols.get("z").intValue());
    }

    @Test
    public void testRegExpTaboolaExample() {
        RegExpCalc calc = new RegExpCalc();
        int i = 0;
        int j = ++i;
        int x = i++ + 5;
        int y = 5 + 3 * 10;
        i += y;

        calc.parseLine("i = 0");
        calc.parseLine("j = ++i");
        calc.parseLine("x = i++ + 5");
        calc.parseLine("y = 5 + 3 * 10");
        calc.parseLine("i += y");
        assertEquals("(i=37,j=1,x=6,y=35)", calc.formatResults());
        assertEquals(i, calc.symbols.get("i").intValue());
        assertEquals(j, calc.symbols.get("j").intValue());
        assertEquals(x, calc.symbols.get("x").intValue());
        assertEquals(y, calc.symbols.get("y").intValue());
    }

    @Test
    public void testExpTreeTaboolaExample() {
        ExpressionTreeCalc calc = new ExpressionTreeCalc();
        int i = 0;
        int j = ++i;
        int x = i++ + 5;
        int y = 5 + 3 * 10;
        i += y;

        calc.parseLine("i = 0");
        calc.parseLine("j = ++i");
        calc.parseLine("x = i++ + 5");
        calc.parseLine("y = 5 + 3 * 10");
        calc.parseLine("i += y");
        assertEquals("(i=37,j=1,x=6,y=35)", calc.formatResults());
        assertEquals(i, calc.symbols.get("i").intValue());
        assertEquals(j, calc.symbols.get("j").intValue());
        assertEquals(x, calc.symbols.get("x").intValue());
        assertEquals(y, calc.symbols.get("y").intValue());
    }
}