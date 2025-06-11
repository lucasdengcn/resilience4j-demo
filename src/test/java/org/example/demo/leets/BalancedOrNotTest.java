package org.example.demo.leets;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BalancedOrNotTest {

    @Test
    void testIsBalanced() {
        // Test cases for balanced brackets
        assertTrue(BalancedOrNot.isBalanced(""));          // Empty string
        assertTrue(BalancedOrNot.isBalanced("()"));        // Simple balanced
        assertTrue(BalancedOrNot.isBalanced("(())"));      // Nested balanced
        assertTrue(BalancedOrNot.isBalanced("()()"));      // Sequential balanced
        assertTrue(BalancedOrNot.isBalanced("(()())"));    // Complex balanced
        
        // Test cases for unbalanced brackets
        assertFalse(BalancedOrNot.isBalanced("("));        // Single open
        assertFalse(BalancedOrNot.isBalanced(")"));        // Single close
        assertFalse(BalancedOrNot.isBalanced("())"));      // Extra close
        assertFalse(BalancedOrNot.isBalanced("(()"));      // Extra open
        assertFalse(BalancedOrNot.isBalanced(")("));       // Wrong order
        assertFalse(BalancedOrNot.isBalanced("((())"));    // Unbalanced nested
        
        // Test cases with other characters (should be ignored)
        assertTrue(BalancedOrNot.isBalanced("(a)"));       // With letters
        assertTrue(BalancedOrNot.isBalanced("(1+2)"));      // With numbers and symbols
        assertFalse(BalancedOrNot.isBalanced("(a"));       // Unbalanced with letters
        
        // Edge cases
        assertTrue(BalancedOrNot.isBalanced(null));        // Null input
        assertTrue(BalancedOrNot.isBalanced("   "));       // Whitespace only
    }
}
