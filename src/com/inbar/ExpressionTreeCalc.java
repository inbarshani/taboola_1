package com.inbar;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionTreeCalc {

    Map<String, Integer> symbols = new LinkedHashMap<>();

    public static void main(String[] args) {
        ExpressionTreeCalc calc = new ExpressionTreeCalc();
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

    public ExpressionTreeCalc() {

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
        Scanner scanner = new Scanner(inputLine);
        ExpressionTree tree = new ExpressionTree();
        while(scanner.hasNext()){
            String token = scanner.next();
            tree.addToken(token);
        }
        //System.out.println("Tree:");
        //System.out.println(tree.print());
        tree.compute(symbols);
    }

    private class ExpressionTree {
        private Node root;

        public ExpressionTree() {
        }

        public void addToken(String token) {
            // if token is a unary operator (inc/dec, prefix/postfix) break it down into 2 additions
            // the first addition is added at root to do the inc/dec at the end of the expression eval
            // the second addition is the evaluation within the tree, which is either the var or the the var inc/dec
            if (token.length() >= 3) {
                String prefix = token.substring(0,2);
                if (prefix.equals("++") || prefix.equals("--")) {
                    token = token.substring(2);
                    addToken(prefix);
                    addToken(token);
                    // add an explicit inc/dec expression
                    addToken(token);
                    addToken("p"+prefix.substring(1));
                    addToken("1");
                    return;
                }
                else {
                    String postfix = token.substring(token.length()-2);
                    if (postfix.equals("++") || postfix.equals("--")) {
                        token = token.substring(0, token.length()-2);
                        addToken(postfix);
                        addToken(token);
                        // add the eval of the variable
                        addToken(token);
                        return;
                    }
                }
            }
            // if new token priority is less than current root priority, add the new token as parent
            //  else add it as child
            Node newNode = new Node();
            newNode.data = token;
            newNode.priority = parsePriority(token);

            if (root == null)
                root = newNode;
            else
                root = insertChild(root, newNode);
        }

        public void compute(Map<String, Integer> symbols) {
            computeNode(root, symbols);
        }

        public String print() {
            StringBuilder output = new StringBuilder();
            printNode(output, root);
            return output.toString();
        }

        private void printNode(StringBuilder output, Node current) {
            // unary ops printed as a prefix to the output
            if (current.priority == 0) {
                output.append(current.data);
                printNode(output, current.leftChild);
                output.append(" ");
                printNode(output, current.rightChild);
            }
            else {
                if (current.leftChild != null)
                    printNode(output, current.leftChild);
                output.append(current.data);
                if (current.rightChild != null)
                    printNode(output, current.rightChild);
            }
        }

        private int computeNode(Node current, Map<String, Integer> symbols) {
            if (current.priority == 10) {
                // var or number
                return eval(current.data, symbols);
            }
            else {
                if (current.priority == 0) { // inc/dec
                    // first compute tree
                    computeNode(current.rightChild, symbols);
                    // only at the end process unary inc/dec
                    return evalAssignment(current.leftChild.data, current.data, 1, symbols);
                }
                else if (current.priority == 1) { // assignment
                    if (current.rightChild == null)
                        throw new RuntimeException("Invalid expression, was expecting a value for assignment: "+current.data);
                    if (current.leftChild == null)
                        throw new RuntimeException("Invalid expression, was expecting a variable for assignment: "+current.data);
                    int right_val = computeNode(current.rightChild, symbols);
                    String symbol = current.leftChild.data;
                    // not checking if symbol is valid
                    return evalAssignment(symbol, current.data, right_val, symbols);
                }
                else {
                    int left_val = computeNode(current.leftChild, symbols);
                    int right_val = computeNode(current.rightChild, symbols);
                    return evalOp(left_val, current.data, right_val);
                }
            }
        }

        private int eval(String expression, Map<String, Integer> symbols) {
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
                case "p+":
                case "+":
                    return (left_val + right_val);
                case "p-":
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

        private int evalAssignment(String symbol, String op, int val, Map<String, Integer> symbols) throws RuntimeException {
            int new_val;
            switch(op) {
                case "=":
                    new_val = val;
                    break;
                case "++":
                case "+=":
                    if (!symbols.containsKey(symbol))
                        throw new RuntimeException("Use of an undefined symbol: "+symbol);
                    new_val = symbols.get(symbol) + val;
                    break;
                case "--":
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

        private Node insertChild(Node current, Node newNode) {
            if (newNode.priority == 0) {
                newNode.rightChild = current;
                return newNode;
            }
            else if (newNode.priority <= current.priority) {
                newNode.leftChild = current;
                return newNode;
            }
            else {
                if (current.leftChild == null) {
                    current.leftChild = newNode;
                }
                else if (current.rightChild == null) {
                    current.rightChild = newNode;
                }
                else {
                    current.rightChild = insertChild(current.rightChild, newNode);
                }
                return current;
            }
        }


        private int parsePriority(String operator) {
            switch(operator) {
                case "++":
                case "--":
                    return 0;
                case "+=":
                case "-=":
                case "*=":
                case "/=":
                case "=":
                    return 1;
                case "+":
                case "-":
                    return 2;
                case "*":
                case "/":
                    return 3;
                case "p+": // postfix unary inc/dec replacement
                case "p-":
                    return 4;
                default: // not an operator
                    return 10;
            }
        }

        private class Node {
            String data;
            int priority;
            Node leftChild, rightChild;
        }
    }
}
