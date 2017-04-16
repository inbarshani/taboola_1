package com.inbar;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpCalc {

    Map<String, Integer> symbols = new LinkedHashMap<>();
    Map<Integer, Pattern> operators = new HashMap();

    public static void main(String[] args) {
        RegExpCalc calc = new RegExpCalc();
	    // input loop to capture symbols and then output results
        System.out.println("Enter a series of symbols (enter a blank input to end the series and produce the results):");
        Scanner scanner =new Scanner(System.in);
        String inputLine = scanner.nextLine();
        while(inputLine.length() > 0) {
            calc.parseLine(inputLine);
            inputLine = scanner.nextLine();
        }
        System.out.println("Results:");
        System.out.println(calc.formatResults());
    }

    public RegExpCalc() {
        initOperators();
    }

    private void initOperators() {
        // init equal operators
        operators.put(0, Pattern.compile("(.*?)\\s(\\+=|-=|\\*=|/=|=)\\s(.*)"));
        operators.put(1, Pattern.compile("(.*)\\s([+-])\\s(.*)"));
        operators.put(2, Pattern.compile("(.*)\\s([*/])\\s(.*)"));
        operators.put(3, Pattern.compile(".*[a-zA-Z]\\+\\+|[a-zA-Z]--|\\+\\+[a-zA-Z]|--[a-zA-Z].*"));
    }

    public String formatResults() {
        StringBuilder results = new StringBuilder("(");
        Iterator it = symbols.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            results.append(pair.getKey() + "=" + pair.getValue());
            results.append(",");
        }
        // replace last , with )
        results.deleteCharAt(results.length()-1);
        results.append(")");
        return results.toString();
    }

    public void parseLine(String inputLine) {
        Map<String, List> unaryQueue = new LinkedHashMap();
        parseOperatorByPriority(inputLine,0, unaryQueue);
        // resolve unary ops
        for (Map.Entry<String, List> entry : unaryQueue.entrySet()) {
            String symbol = entry.getKey();
            int symbol_val = symbols.get(symbol);
            List<String> ops = entry.getValue();
            for (String op : ops) {
                switch(op) {
                    case "++":
                        symbol_val++;
                        break;
                    case "--":
                        symbol_val--;
                        break;
                }
            }
            symbols.put(symbol, symbol_val);
        }
    }

    private int parseOperatorByPriority(String expression, int priority, Map<String, List> unaryQueue) {
        //System.out.println("evaluating expression "+expression+" for operators of priority "+priority);
        Pattern ops = operators.get(priority);
        if (ops == null) // no more operators to scan
            return eval(expression);
        // find first operator of this priority
        Matcher matcher = ops.matcher(expression);
        if (matcher.find()) {
            int groups = matcher.groupCount();
            String first_expression="", second_expression="", third_expression="";
            switch(groups) {
                case 3:
                    third_expression = matcher.group(3);
                case 2:
                    second_expression = matcher.group(2);
                case 1:
                    first_expression = matcher.group(1);
                default:
                    break;
            }
            int left_val, right_val;
            if (groups == 0) {
                // unary operator
                // return the original value (postfix) or computed value (prefix) and queue the symbol for later update
                String symbol = expression.replace("++", "").replace("--","");
                String unary_op = expression.replace(symbol, "");
                // add to unaryQueue for processing after the computation is finalized
                List<String> unaryOps = null;
                if (unaryQueue.containsKey(symbol)) {
                    unaryOps = unaryQueue.get(symbol);
                }
                else {
                    unaryOps = new ArrayList<>();
                    unaryQueue.put(symbol, unaryOps);
                }
                unaryOps.add(unary_op);
                // return the right (post/prefix) value
                if (expression.startsWith(symbol)) {
                    // postfix - return the current value of the variable
                    return eval(symbol);
                } else {
                    left_val = eval(symbol);
                    right_val = 1;
                    //
                    return evalOp(left_val, unary_op, right_val);
                }
            }
            else if (groups == 3) {
                if (priority == 0) {// assignment
                    right_val = parseOperatorByPriority(third_expression, priority + 1,unaryQueue);
                    return evalAssignment(first_expression, second_expression, right_val);
                }
                else {
                    left_val = parseOperatorByPriority(first_expression, priority,unaryQueue);
                    right_val = parseOperatorByPriority(third_expression, priority + 1,unaryQueue);
                    return evalOp(left_val, second_expression, right_val);
                }
            }
            else
                throw new RuntimeException("Unsupported expression: "+expression);
        }
        else
            return parseOperatorByPriority(expression, priority + 1,unaryQueue);
    }

    private int eval(String expression) {
        int val;
        try {
            val = Integer.parseInt(expression);
        }
        catch(Exception ex) {
            // expression is not a parsable number, it should be a symbol
            if (!symbols.containsKey(expression))
                throw new RuntimeException("Use of an undefined symbol: "+expression);
            val = symbols.get(expression);
        }
        return val;
    }

    private int evalOp(int left_val, String op, int right_val) throws RuntimeException {
        switch(op) {
            case "++":
            case "+":
                return (left_val + right_val);
            case "--":
            case "-":
                return (left_val - right_val);
            case "*":
                return (left_val * right_val);
            case "/":
                return (left_val / right_val);
            default:
                throw new RuntimeException("Unsupported math operator: "+op);
        }
    }

    private int evalAssignment(String symbol, String op, int val) throws RuntimeException {
        int new_val;
        switch(op) {
            case "=":
                new_val = val;
                break;
            case "+=":
                if (!symbols.containsKey(symbol))
                    throw new RuntimeException("Use of an undefined symbol: "+symbol);
                new_val = symbols.get(symbol) + val;
                break;
            case "-=":
                if (!symbols.containsKey(symbol))
                    throw new RuntimeException("Use of an undefined symbol: "+symbol);
                new_val = symbols.get(symbol) - val;
                break;
            case "*=":
                if (!symbols.containsKey(symbol))
                    throw new RuntimeException("Use of an undefined symbol: "+symbol);
                new_val = symbols.get(symbol) * val;
                break;
            case "/=":
                if (!symbols.containsKey(symbol))
                    throw new RuntimeException("Use of an undefined symbol: "+symbol);
                new_val = symbols.get(symbol) / val;
                break;
            default:
                throw new RuntimeException("Unsupported math operator: "+op);
        }
        symbols.put(symbol, new_val);
        return new_val;
    }

}
