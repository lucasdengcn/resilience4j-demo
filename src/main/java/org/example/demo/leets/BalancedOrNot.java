package org.example.demo.leets;

import java.util.Stack;

public class BalancedOrNot {

    public static boolean isPair(char a, char b) {
        return (a == '(' && b == ')') || (a == '{' && b == '}') || (a == '[' && b == ']');
    }

    public static boolean isToken(char c) {
        return c == '(' || c == '{' || c == '[' || c == ')' || c == '}' || c == ']';
    }

    public static boolean isBalanced(String s) {
        if (s == null || s.trim().isEmpty()) {
            return true;
        }
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!isToken(ch)){
                continue;
            }
            if (stack.isEmpty()) {
                stack.push(String.valueOf(ch));
            } else {
                String top = stack.pop();
                if (isPair(top.charAt(0), ch)) {

                } else {
                    stack.push(top);
                    stack.push(String.valueOf(ch));
                }
            }
        }
        return stack.isEmpty();
    }

}
