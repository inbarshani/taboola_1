package com.inbar;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calc {

    static Map<String, Integer> symbols = new LinkedHashMap<>();
    static Map<Integer, Pattern> operators = new HashMap();

    public static void main(String[] args) {
        initOperators();
	    // input loop to capture symbols and then output results
        System.out.println("Enter a series of symbols (enter a blank input to end the series and produce the results):");
        Scanner scanner =new Scanner(System.in);
        String inputLine = scanner.nextLine();
        while(inputLine.length() > 0) {
            parseLine(inputLine);
            inputLine = scanner.nextLine();
        }
        System.out.println("Results:");
        System.out.println(formatResults());
    }

    static void initOperators() {
        // init equal operators
        operators.put(0, Pattern.compile(".*([a-zA-Z]\\+\\+|[a-zA-Z]--).*"));
        operators.put(1, Pattern.compile("(.*?)(\\+=|-=|\\*=|/=|=)(.*)"));
        operators.put(2, Pattern.compile("(.*[a-zA-Z0-9])([\\+-]{1})([a-zA-Z0-9].*)"));
        operators.put(3, Pattern.compile("(.*)([\\*/])(.*)"));
        operators.put(4, Pattern.compile("\\+\\+[a-zA-Z]|--[a-zA-Z]"));
    }

    static String formatResults() {
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

    static void parseLine(String inputLine) {
        parseOperatorByPriority(inputLine.replace(" ",""),0);
    }

    private static int parseOperatorByPriority(String expression, int priority) {
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
                // unary prefix operator
                // update the symbol and return the new value
                String var = expression.replace("++", "").replace("--","");
                String unary_op = expression.replace(var, "");
                left_val = eval(var);
                right_val = 1;
                int val = evalOp(left_val, unary_op, right_val);
                symbols.put(var, val);
                return val;
            }
            else if (groups == 1) {
                // unary postfix operator
                // replace operator and symbol with current value, compute the expression then update the symbol
                String var = first_expression.replace("++", "").replace("--","");
                String unary_op = first_expression.replace(var, "");
                int current_symbol_val = eval(var);
                expression = expression.replaceFirst(first_expression.replace("+","\\+"), ""+current_symbol_val);
                int val = parseOperatorByPriority(expression, priority);
                int symbol_update_val = evalOp(current_symbol_val, unary_op, 1);
                symbols.put(var, symbol_update_val);
                return val;
            }
            else if (groups == 3) {
                if (priority == 1) {// assignment
                    right_val = parseOperatorByPriority(third_expression, priority + 1);
                    return evalAssignment(first_expression, second_expression, right_val);
                }
                else {
                    left_val = parseOperatorByPriority(first_expression, priority);
                    right_val = parseOperatorByPriority(third_expression, priority + 1);
                    return evalOp(left_val, second_expression, right_val);
                }
            }
            else
                throw new RuntimeException("Unsupported expression: "+expression);
        }
        else
            return parseOperatorByPriority(expression, priority + 1);
    }

    private static int eval(String expression) {
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

    private static int evalOp(int left_val, String op, int right_val) throws RuntimeException {
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

    private static int evalAssignment(String symbol, String op, int val) throws RuntimeException {
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
