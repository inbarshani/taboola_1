package com.inbar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by 2 on 14/04/2017.
 */
class MyClassTest {

    private List<Long> initNumbersList(Long limit) {
        List<Long> numbers = new ArrayList<>();
        for(Long i=new Long(1);i<=limit;i++)
            numbers.add(i);
        return numbers;
    }

    private List<String> initStringsList() {
        List<String> strings = new ArrayList<>();
        strings.add("a");
        strings.add("b");
        return strings;
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void testEquals() {
        List<Long> m_numbers = initNumbersList(new Long(3));
        List<String> m_strings = initStringsList();
        Date d = new Date();
        MyClass class1 = new MyClass(d, "class1", m_numbers, m_strings);
        MyClass class2 = new MyClass(d, "class2", m_numbers, m_strings);
        MyClass class3_eq_2 = new MyClass(d, "class2", m_numbers, m_strings);
        assertEquals(class1.equals(class2), false);
        assertEquals(class1.equals(class3_eq_2), false);
        assertEquals(class2.equals(class3_eq_2), true);
    }

    @Test
    void testToString() {
        List<Long> m_numbers1 = initNumbersList(new Long(5));
        List<Long> m_numbers2 = initNumbersList(new Long(3));
        List<String> m_strings = initStringsList();
        Date d = new Date();
        MyClass class1 = new MyClass(d, "class1", m_numbers1, m_strings);
        MyClass class2 = new MyClass(d, "class2", m_numbers2, m_strings);
        assertEquals(class1.toString(), "class1 1 2 3 4 5");
        assertEquals(class2.toString(), "class2 1 2 3");
    }

    @Test
    void testRemoveString() {
        List<Long> m_numbers = initNumbersList(new Long(3));
        List<String> m_strings = initStringsList();
        Date d = new Date();
        MyClass class1 = new MyClass(d, "class1", m_numbers, m_strings);
        assertEquals(class1.containsString("a"), true);
        assertEquals(class1.containsString("b"), true);
        class1.removeString("a");
        assertEquals(class1.containsString("a"), false);
        assertEquals(class1.containsString("b"), true);
        class1.removeString("c");
        assertEquals(class1.containsString("a"), false);
        assertEquals(class1.containsString("b"), true);
        class1.removeString("b");
        assertEquals(class1.containsString("a"), false);
        assertEquals(class1.containsString("b"), false);
    }

    @Test
    void testContainsNumber() {
        List<Long> m_numbers = initNumbersList(new Long(3));
        List<String> m_strings = initStringsList();
        Date d = new Date();
        MyClass class1 = new MyClass(d, "class1", m_numbers, m_strings);
        assertEquals(class1.containsNumber(new Long(1)), true);
        assertEquals(class1.containsNumber(new Long(0)), false);
        MyClass class2 = new MyClass(d, "class1", null, null);
        assertEquals(class2.containsNumber(new Long(1)), false);
    }

    @Test
    void testIsHistoric() {
        Date d = null;
        MyClass class1 = new MyClass(d, "class1", null, null);
        assertEquals(class1.isHistoric(), false);
        try {
            d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-05-15 00:00:00");
            class1 = new MyClass(d, "class1", null, null);
            assertEquals(class1.isHistoric(), true);
        }
        catch(ParseException pex) {
        }
        try {
            d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-05-15 00:00:00");
            class1 = new MyClass(d, "class1", null, null);
            assertEquals(class1.isHistoric(), false);
        }
        catch(ParseException pex) {
        }
    }

}