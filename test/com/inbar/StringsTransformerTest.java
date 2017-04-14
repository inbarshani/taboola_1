package com.inbar;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by 2 on 14/04/2017.
 */
class StringsTransformerTest {

    @Test
    void testTransformOriginal() {
        List<StringsTransformer.StringFunction> funcs = new ArrayList<>();
        StringBuilder resultInbar = new StringBuilder("Inbar");
        StringBuilder resultYoav = new StringBuilder("Yoav");
        for(int i=1;i<=100;i++) {
            funcs.add(new StringsTransformerTest.StringFunc(i));
            resultInbar.append(i);
            resultYoav.append(i);
        }
        List<String> strings = new ArrayList();
        strings.add("Inbar");
        strings.add("Yoav");
        StringsTransformer transformer = new StringsTransformer(strings);
        try {
            transformer.transform(funcs);
        }
        catch(InterruptedException iex) {
            fail("Threads interrupted");
        }
        String result =transformer.printAll();
        assertEquals(result ,resultInbar.append("\n").append(resultYoav).append("\n").toString());
    }


    @Test
    void testTransformOnItem() {
        List<StringsTransformer.StringFunction> funcs = new ArrayList<>();
        StringBuilder resultInbar = new StringBuilder("Inbar");
        StringBuilder resultYoav = new StringBuilder("Yoav");
        for(int i=1;i<=100;i++) {
            funcs.add(new StringsTransformerTest.StringFunc(i));
            resultInbar.append(i);
            resultYoav.append(i);
        }
        List<String> strings = new ArrayList();
        strings.add("Inbar");
        strings.add("Yoav");
        StringsTransformer transformer = new StringsTransformer(strings);
        try {
            transformer.transformOnItem(funcs);
        }
        catch(InterruptedException iex) {
            fail("Threads interrupted");
        }
        String result =transformer.printAll();
        assertEquals(result ,resultInbar.append("\n").append(resultYoav).append("\n").toString());
    }

    @Test
    void testTransformOnFunc() {
        List<StringsTransformer.StringFunction> funcs = new ArrayList<>();
        StringBuilder resultInbar = new StringBuilder("Inbar");
        StringBuilder resultYoav = new StringBuilder("Yoav");
        for(int i=1;i<=100;i++) {
            funcs.add(new StringsTransformerTest.StringFunc(i));
            resultInbar.append(i);
            resultYoav.append(i);
        }
        List<String> strings = new ArrayList();
        strings.add("Inbar");
        strings.add("Yoav");
        StringsTransformer transformer = new StringsTransformer(strings);
        try {
            transformer.transformOnFunc(funcs);
        }
        catch(InterruptedException iex) {
            fail("Threads interrupted");
        }
        String result =transformer.printAll();
        assertEquals(result ,resultInbar.append("\n").append(resultYoav).append("\n").toString());
    }

    public static class StringFunc implements StringsTransformer.StringFunction {

        private int m_additive;

        public StringFunc(int additive) {
            m_additive = additive;
        }

        @Override
        public String transform(String str) {
            return str + m_additive;
        }
    }
}
