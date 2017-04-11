package com.inbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 2 on 10/04/2017.
 */
public class ThreadsMain {

    public static void main(String[] args) {
        System.out.println("Start main");
        List<StringsTransformer.StringFunction> funcs = new ArrayList<>();
        funcs.add(new StringFunc(1));
        funcs.add(new StringFunc(2));
        List<String> strings = new ArrayList();
        strings.add("Inbar");
        strings.add("Yoav");
        StringsTransformer transformer = new StringsTransformer(strings);
        try {
            transformer.transform3(funcs);
        }
        catch(InterruptedException iex) {
            System.out.println("Interrupted: "+iex);
        }

        System.out.println("End main, data:");
        transformer.printAll();
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
