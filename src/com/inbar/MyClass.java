package com.inbar;

import java.util.*;

public class MyClass
{
    private Date m_time;
    private String m_name;
    private List<Long> m_numbers;
    private List<String> m_strings;
    public MyClass(Date time, String name, List<Long> numbers, List<String> strings) {
        m_time = time;
        m_name = name;
        // m_numbers = numbers;
        // m_strings = strings;
        // this will be a reference copy, and may violate the expectation of whoever initialized the object
        // without knowing for sure the type of the List, we can opt to keep our own data with ArrayList
        // this is still shallow copy, if we're going to mutate the contents (and not just the order), we should do a deep copy
        if (numbers != null)
            m_numbers = new ArrayList<>(numbers);
        if (strings != null)
            m_strings = new ArrayList<>(strings);
    }
    public boolean equals(Object obj) {
        if (obj instanceof MyClass) {
            // return m_name.equals(((MyClass)obj).m_name);
            // verify m_name was initialized, otherwise this will throw a NullPointerException
            if (m_name == null)
                return (((MyClass)obj).m_name == null);
            else
                return m_name.equals(((MyClass)obj).m_name);
        }
        return false;
    }
    public String toString() {
        // String out = m_name;
        // Should be used with StringBuilder, as we modify it within a loop
        StringBuilder out = new StringBuilder();
        if (m_name != null)
            out.append(m_name);
        for (long item : m_numbers) {
            out.append(" ");
            out.append(item);
        }
        return out.toString();
    }
    public void removeString(String str) {
        // for (int i = 0; i < m_strings.size(); i++) {
        //  if (m_strings.get(i).equals(str)) {
        //      m_strings.remove(i);
        //  }
        //}
        // Should it replace all occurrences?
        // initial implementation suggests we should, if not we can use List.indexOf
        // otherwise, to make sure the iteration order isn't jeopardized, move from end to front
        // use iterator instead of get(i) as the List may be a LinkedList or other data structure where get(i) is costly
        // but in short - removeAll can do it for us (or remove(object)) but I'm not sure of the cost, depends on how much checks to the Collection cost
        // removeIf is also an option, depends on JDK level

        // check for initialization of the member
        if (m_strings == null)
            return;

        ListIterator<String> items = m_strings.listIterator();
        while (items.hasNext()) {
            String item = items.next();
            if (item.equals(str)) {
                items.remove();
            }
        }
    }
    public boolean containsNumber(long number) {
        // check for initialization of the member
        if (m_numbers == null)
            return false;

        // we can use the List method indexOf instead, but I'm not convinced it is better
        for (long num : m_numbers) {
            if (num == number) {
                return true;
            }
        }
        return false;
    }
    public boolean isHistoric() {
        // check for initialization of the member
        if (m_time == null)
            return false; // or true, as you see fit
        return m_time.before(new Date());
    }

    // added for testing
    public boolean containsString(String str) {
        // check for initialization of the member
        if (m_strings == null)
            return false;

        return (m_strings.indexOf(str) >= 0);
    }

}
