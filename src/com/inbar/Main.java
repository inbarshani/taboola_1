package com.inbar;

import java.util.*;

public class Main {

    static Map<String, Integer> symbols = new HashMap<>();
    static Map<Integer, ArrayList> operators = new HashMap();

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
        ArrayList<String> ops_1 = new ArrayList<>();
        ops_1.add("+");
        ops_1.add("-");
        operators.put(0, ops_1);
        ArrayList<String> ops_2 = new ArrayList<>();
        ops_2.add("*");
        ops_2.add("/");
        operators.put(1, ops_2);
    }

    static String formatResults() {
        StringBuilder results = new StringBuilder("(");
        Iterator it = symbols.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            results.append(pair.getKey() + " = " + pair.getValue());
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
        scanner.next(); // skip equal sign
        String expression = scanner.nextLine();
        //System.out.println("Calculating symbol "+symbol+" with expression "+expression);
        int val = parseOperatorByPriority(expression,0);
        symbols.put(symbol, val);
    }

    private static int parseOperatorByPriority(String expression, int priority) {
        //System.out.println("evaluating expression "+expression+" for operators of priority "+priority);
        ArrayList ops = operators.get(priority);
        if (ops == null) // no more operators to scan
            return eval(expression);
        // find operators of this priority
        Scanner scanner = new Scanner(expression);
        StringBuilder left_expression = new StringBuilder(), right_expression = new StringBuilder();
        String op = scanner.next();
        while ((ops.indexOf(op) == -1) && scanner.hasNext()) {
            if (left_expression.length() > 0)
                left_expression.append(" ");
            left_expression.append(op);
            op = scanner.next();
        }
        if (scanner.hasNext()) {
            right_expression.append(scanner.nextLine());
            int left_val = parseOperatorByPriority(left_expression.toString(), priority + 1);
            int right_val = parseOperatorByPriority(right_expression.toString(), priority);
            return evalOp(left_val, op, right_val);
        }
        else {
            if (ops.indexOf(op) == -1) {
                if (left_expression.length() > 0)
                    left_expression.append(" ");
                left_expression.append(op);
            }
            return parseOperatorByPriority(left_expression.toString(), priority + 1);
        }
    }

    private static int eval(String expression) {
        //System.out.println("eval expression "+expression);
        int val;
        try {
            val = Integer.parseInt(expression);
        }
        catch(Exception ex) {
            // expression is not a parsable number, it should be a symbol
            //System.out.println("Lookup symbol "+expression);
            val = symbols.get(expression);
        }
        //System.out.println("eval expression "+expression+" to "+val);
        return val;
    }

    private static int evalOp(int left_val, String op, int right_val) throws RuntimeException {
        //System.out.println("eval operator "+op+" with values "+left_val+","+right_val);
        switch(op) {
            case "+":
                return (left_val + right_val);
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
}
