package com.google.common.base;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A validator for matching parentheses in a string.
 * <p>
 * Validates the following parentheses:
 * {} - curly;
 * [] - square;
 * () - round.
 * <p>
 * Rules:
 * - Each open parenthesis must have a closing;
 * - Brackets that are closer to the end of the line must be closed earlier.
 * <p>
 * Examples:
 * - "[c] [/ c] d" - correct use;
 * - "a {b [c] [/ c] d} e" - correct use;
 * - "a {b (c] d} e" - misuse, ']' does not match '(';
 * - "a [b {c} d] e}" - incorrect use, there is no pair for the closing parenthesis '}';
 * - "a {b (c)" - incorrect use, for an open parenthesis '{' no pair.
 *
 * @see <a href="https://javadevblog.com/primer-ispol-zovaniya-steka-na-java.html">Validator</a>
 */
public class PairedBracketChecker {
    private static final Logger log = Logger.getLogger(PairedBracketChecker.class.getName());
    private static final String MESSAGE_ERROR_POSITION_PATTERN = "Warning! '%s' bracket is in %s position.";

    private final String input;
    private final int lengthInput;
    private final Stack stack;

    // Setup params
    public PairedBracketChecker(String input) {
        this.input = input;
        this.lengthInput = input.length();
        stack = new Stack(lengthInput);
    }

    public boolean makeCheck() {
        // Start to read String
        for (int i = 0; i < lengthInput; i++) {
            // Read char to char
            char ch = input.charAt(i);

            switch (ch) {
                case '{':
                case '[':
                case '(':
                    stack.addElement(ch);
                    break;
                case '}':
                case ']':
                case ')':
                    String messageErrorPosition = String.format(MESSAGE_ERROR_POSITION_PATTERN, ch, i);
                    // If stack is not empty
                    if (!stack.isEmpty()) {
                        // Delete and check
                        char chClosed = stack.deleteElement();

                        if ((ch == '}' && chClosed != '{')
                                || (ch == ']' && chClosed != '[')
                                || (ch == ')' && chClosed != '('))
                            log.log(Level.CONFIG, messageErrorPosition);
                    } else
                        // Lack of items in the stack
                        log.log(Level.CONFIG, messageErrorPosition);
                    break;

                default:
                    // Skip for another symbols
                    break;
            }
        }

        if (!stack.isEmpty()) {
            log.log(Level.CONFIG, "Warning! Closed comma is missed.");
            return false;
        }
        return true;
    }
}