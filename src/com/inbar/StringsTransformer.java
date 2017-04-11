package com.inbar;

// first issue is that the threads are not being started - not sure if this is part of the question or not
// We can start the threads in the threads loop with subsequent join - in this case however the next thread will not be
//  executed until the current transformation completes (as the main thread is blocked)
// While this guarantees the order of the execution of the functions, it doesn't utilize multithreading
// Similarly, we can guarantee the order by passing the 'previous' function thread to a new thread and have it 'join' on the
//  previous thread, so it will only start transforming after the previous transformations are ended
//
// I assume for this task that:
//  a. transformations must be executed in the predefined sequential order
//  b. none of the transformations change the order or the number of elements in the strings list (as evident in the existing code)
//  c. We do want to use multithreading to utilize System resources - if not, we can just apply the transformation sequentially
//
// Given that, I can suggest the following options:
//  a. Create a thread that will apply the transformations sequentially on a single string in the list
//     In this solution, a thread will be created for each string item, and will run the functions one by one on that item, updating the existing list
//       with the final result with List.set (has to be atomic as it may affect the data structure beyond the element)
//     As threads will not affect one another beyond the final replacement, the main thread can start them all then join on all
//      if there is a limitation on the number of threads in a process / machine, we can start a number of threads to that limit, join on them
//      then start the next bunch (or use a more elaborated mechanism to track each individual thread completion and start a new one so we keep the
//      overall number of threads at maximum)
//  b. Somewhat similarly, we loop in the main thread on the transformations, and for each transformation, create threads for each string item
//     then start all these threads and join on them all - that way, when the main thread continues, the transformation completion is guaranteed, and it
//     potentially was executed in parallel
//     To merge the resulting strings, we can again use atomic replace on the shared strings List, or access a return values from all the thread instances
//     and merge them into the main thread into a new strings List, then replace our member list with the new one.


import java.util.*;
import java.util.function.Consumer;

public class StringsTransformer {
    private List<String> data = new ArrayList<String>();
    public StringsTransformer(List<String> startingData) {
        this.data = startingData;
    }
    private void forEach(StringFunction function) {
        List<String> newData = new ArrayList<String>();
        for (String str : data) {
            newData.add(function.transform(str));
        }
        data = newData;
    }
    public List<String> transform(List<StringFunction> functions) throws
            InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();
        for (final StringFunction f : functions) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    forEach(f);
                    System.out.println("Thread "+this.hashCode()+" is running");
                }
            }));
        }
        // ArrayList guarantees order, so there is no issue with the order of the applied functions
        for (Thread t : threads) {
            // start and join in the iteration will guarantee completion of thread X before starting of thread X+1
            t.start();
            t.join();
        }
        return data;
    }

    public List<String> transform2(final List<StringFunction> functions) throws
            InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();
        for (int i=0;i<data.size();i++) {
            final int index = i;
            final String initial_value = data.get(index);
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Thread "+this.hashCode()+" is running");
                    String result = initial_value;
                    for (StringFunction f : functions) {
                        result = f.transform(result);
                    }
                    // insert the resulting string back to the list at the same index
                    // this should be an atomic action
                    data.set(index, result);
                }
            }));
        }
        // first start all threads
        for (Thread t : threads) {
            t.start();
        }
        // now join all threads
        for (Thread t : threads) {
            t.join();
        }
        return data;
    }

    public List<String> transform3(final List<StringFunction> functions) throws
            InterruptedException {
        for (final StringFunction f : functions) {
            List<Thread> threads = new ArrayList<Thread>();
            for (int i=0;i<data.size();i++) {
                final int index = i;
                final String initial_value = data.get(index);
                threads.add(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Thread " + this.hashCode() + " is running");
                        String result = f.transform(initial_value);
                        // insert the resulting string back to the list at the same index
                        // this should be an atomic action
                        data.set(index, result);
                    }
                }));
            }
            for (Thread t : threads) {
                t.start();
            }
            for (Thread t : threads) {
                t.join();
            }
        }
        return data;
    }

    public void printAll() {
        data.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });
    }

    public static interface StringFunction {
        public String transform(String str);
    }
}