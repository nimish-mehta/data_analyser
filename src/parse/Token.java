package parse;

/**
 * Created by Nimish on 15/02/15.
 *
 * Enum of tokens supported by the system
 */
public enum Token {
    COMPARISON, // Operators
    AND,
    NOT,
    OR,
    BRACKET_OPEN,
    BRACKET_CLOSE,
    QUOTE,
    LITERAL,
    AND_CONDITIONAL, // Query Conditional Constructs
    OR_CONDITIONAL,
    NOT_CONDITIONAL,
    EQUALITY_CONDITIONAL,
}
