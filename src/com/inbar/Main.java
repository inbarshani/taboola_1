package com.inbar;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static Map<String, Integer> symbols = new LinkedHashMap<>();
    static Map<Integer, Pattern> operators = new HashMap();
    static Pattern assignment_operators = Pattern.compile("\\+=|-=|\\*=|/=");

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
        operators.put(-1,assignment_operators);
        operators.put(0, Pattern.compile("[\\+-]"));
        operators.put(1, Pattern.compile("[\\*/]"));
        operators.put(2, Pattern.compile("\\+\\+[a-zA-Z]|--[a-zA-Z]|[a-zA-Z]\\+\\+|[a-zA-Z]--"));
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
        Scanner scanner = new Scanner(inputLine);
        String symbol = scanner.next();
        String equalOp = scanner.next();
        // check if it's a simple equation, or an operator
        int val = 0;
        if (assignment_operators.matcher(equalOp).matches()) {
            if (symbols.get(symbol) == null)
                throw new RuntimeException("Use of symbol that wasn't defined: "+symbol);
            val = parseOperatorByPriority(inputLine,-1);
        }
        else {
            String expression = scanner.nextLine();
            val = parseOperatorByPriority(expression, 0);
        }
        symbols.put(symbol, val);
    }

    private static int parseOperatorByPriority(String expression, int priority) {
        //System.out.println("evaluating expression "+expression+" for operators of priority "+priority);
        Pattern ops = operators.get(priority);
        if (ops == null) // no more operators to scan
            return eval(expression);
        // find operators of this priority
        Scanner scanner = new Scanner(expression);
        StringBuilder left_expression = new StringBuilder(), right_expression = new StringBuilder();
        String op = scanner.next();
        Matcher matcher = ops.matcher(op);
        while ((!matcher.matches()) && scanner.hasNext()) {
            if (left_expression.length() > 0)
                left_expression.append(" ");
            left_expression.append(op);
            op = scanner.next();
            matcher = ops.matcher(op);
        }
        if (scanner.hasNext()) {
            right_expression.append(scanner.nextLine());
            int left_val = parseOperatorByPriority(left_expression.toString(), priority + 1);
            int right_val = parseOperatorByPriority(right_expression.toString(), priority);
            return evalOp(left_val, op, right_val);
        }
        else {
            if (!matcher.matches()) {
                if (left_expression.length() > 0)
                    left_expression.append(" ");
                left_expression.append(op);
                return parseOperatorByPriority(left_expression.toString(), priority + 1);
            }
            else {
                // should only be a unary operator, need to break it down
                if (left_expression.length() > 0)
                    throw new RuntimeException("Unrecognized expression syntax: "+expression);
                // extract the variable

                String var = op.replace("++", "").replace("--","");
                String unary_op = op.replace(var, "");
                int left_val = eval(var);
                int right_val = 1;
                int val = evalOp(left_val, unary_op, right_val);
                symbols.put(var, val);
                return val;
            }
        }
    }

    private static int eval(String expression) {
        int val;
        try {
            val = Integer.parseInt(expression);
        }
        catch(Exception ex) {
            // expression is not a parsable number, it should be a symbol
            val = symbols.get(expression);
        }
        return val;
    }

    private static int evalOp(int left_val, String op, int right_val) throws RuntimeException {
        switch(op) {
            case "++":
            case "+=":
            case "+":
                return (left_val + right_val);
            case "--":
            case "-=":
            case "-":
                return (left_val - right_val);
            case "*=":
            case "*":
                return (left_val * right_val);
            case "/=":
            case "/":
                return (left_val / right_val);
            default:
                throw new RuntimeException("Unsupported math operator: "+op);
        }
    }
}
