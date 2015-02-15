package parse;

import exception.SyntaxErrorException;
import parse.condition.*;

import java.util.*;

/**
 * Created by Nimish on 15/02/15.
 *
 * Takes a string and parses it to a conditional instruction. Uses infix to postfix processing
 * to create the final query. Use builder to create.
 */
public class Query<T> {

    private final Condition<T> condition;
    private final List<String> keys;

    private Query(Condition<T> condition, List<String> keys) {
        this.condition = condition;
        this.keys = keys;
    }

    public Condition<T> getCondition() {
        return condition;
    }

    public List<String> getKeys() {
        return keys;
    }

    public List<T> filter(List<T> data) {
        return condition.filter(data);
    }

    public static class QueryBuilder<T> {
        private String query;
        private Condition<T> condition;
        private List<String> keys;

        public QueryBuilder(String query) throws SyntaxErrorException {
            this.query = query;
            this.keys = new ArrayList<String>();
            this.condition = this.reduceTokenToCondition();
        }

        // Takes the initial string query and converts it into tokens the query processor can understand
        private List<TokenContainer<T>> parseQueryToTokens() {
            StringTokenizer tokenizer = new StringTokenizer(query, " ");
            List<TokenContainer<T>> tokens = new ArrayList<TokenContainer<T>>();
            Boolean remaining = false;
            String remaining_token = null;
            String token;
            while (remaining || tokenizer.hasMoreTokens()) {
                if (!remaining) {
                    token = tokenizer.nextToken();
                } else {
                    token = remaining_token;
                }
                TokenContainer.TokenContainerBuilder builder = new TokenContainer.TokenContainerBuilder(token);
                builder.parse();
                if (builder.isTokenComplete()) {
                    tokens.add(builder.getToken());
                    remaining = false;
                } else if (builder.isMultiToken()) {
                    tokens.add(builder.getToken());
                    remaining_token = builder.getRemaining();
                    remaining = true;
                }
            }
            return tokens;
        }

        // Convert parsed tokens into a Condition Class
        // Steps:
        // Step 1: Remove quotes by combining literals
        // Step 2: Combine equality literals to make initial base conditionals
        // Step 3: Convert to postfix to handle brackets
        // Step 4: Solve in postfix
        private Condition<T> reduceTokenToCondition() throws SyntaxErrorException {
            Stack<TokenContainer<T>> operandStack = new Stack<TokenContainer<T>>();
            List<TokenContainer<T>> tokens = this.parseQueryToTokens();
            tokens = this.resolveQuotes(tokens);
            tokens = this.resolveLiteral(tokens);
            tokens = this.toPostFix(tokens);
            Iterator<TokenContainer<T>> tokenContainerIterator = tokens.iterator();
            while (tokenContainerIterator.hasNext()) {
                TokenContainer<T> token = tokenContainerIterator.next();
                switch (token.getTokenType()) {
                    case EQUALITY_CONDITIONAL:
                        operandStack.push(token);
                        break;
                    case NOT:
                        operandStack.push(this.not(operandStack));
                        break;
                    case AND:
                        operandStack.push(this.and(operandStack));
                        break;
                    case OR:
                        operandStack.push(this.or(operandStack));
                        break;
                }
            }
            TokenContainer<T> result = operandStack.pop();
            if (operandStack.isEmpty()) {
                return result.getConditionalLiteral();
            } else {
                throw new SyntaxErrorException();
            }
        }

        // Check whether it is a valid conditional token
        private boolean validConditional(Token token) {
            return (token == Token.OR_CONDITIONAL || token == Token.AND_CONDITIONAL ||
                    token == Token.NOT_CONDITIONAL || token == Token.EQUALITY_CONDITIONAL);
        }

        // Simple infix to postfix converter
        private List<TokenContainer<T>> toPostFix(List<TokenContainer<T>> tokens) throws SyntaxErrorException {
            Stack<TokenContainer<T>> operatorStack = new Stack<TokenContainer<T>>();
            List<TokenContainer<T>> postfixList = new ArrayList<TokenContainer<T>>();
            TokenContainer<T> top = null;
            for (TokenContainer<T> token : tokens) {
                switch (token.getTokenType()) {
                    case BRACKET_OPEN:
                        operatorStack.push(token);
                        break;
                    case BRACKET_CLOSE:
                        top = operatorStack.peek();
                        do {
                            if (top.getTokenType() == Token.BRACKET_OPEN) {
                                operatorStack.pop();
                                break;
                            } else {
                                TokenContainer<T> op = operatorStack.pop();
                                postfixList.add(op);
                                if (operatorStack.empty()) {
                                    throw new SyntaxErrorException();
                                }
                                top = operatorStack.peek();
                            }
                        } while (true);
                        break;
                    case OR:
                        do {
                            if (!operatorStack.empty()) {
                                top = operatorStack.peek();
                            }
                            if (operatorStack.empty() || top.getTokenType() == Token.OR ||
                                    top.getTokenType() == Token.BRACKET_OPEN || top.getTokenType() == Token.NOT) {
                                operatorStack.push(token);
                                break;
                            } else {
                                TokenContainer<T> op = operatorStack.pop();
                                postfixList.add(op);
                            }
                        } while (true);
                        break;
                    case NOT:
                        do {
                            if (!operatorStack.empty()) {
                                top = operatorStack.peek();
                            }
                            if (operatorStack.empty() || top.getTokenType() == Token.BRACKET_OPEN
                                    || top.getTokenType() == Token.NOT) {
                                operatorStack.push(token);
                                break;
                            } else {
                                TokenContainer<T> op = operatorStack.pop();
                                postfixList.add(op);
                            }
                        } while (true);
                        break;
                    case AND:
                        do {
                            if (!operatorStack.empty()) {
                                top = operatorStack.peek();
                            }
                            if (operatorStack.empty() || top.getTokenType() == Token.OR ||
                                    top.getTokenType() == Token.BRACKET_OPEN || top.getTokenType() == Token.AND) {
                                operatorStack.push(token);
                                break;
                            } else {
                                TokenContainer<T> op = operatorStack.pop();
                                postfixList.add(op);
                            }
                        } while (true);
                        break;
                    case EQUALITY_CONDITIONAL:
                        postfixList.add(token);
                        break;
                    default:
                        throw new SyntaxErrorException();

                }
            }
            while (!operatorStack.empty()) {
                postfixList.add(operatorStack.pop());
            }
            return postfixList;
        }

        // process NOT Token
        private TokenContainer<T> not(Stack<TokenContainer<T>> operandStack)  throws SyntaxErrorException {
            TokenContainer<T> op1;
            Token t_type1;
            if (!operandStack.empty()) {
                op1 = operandStack.pop();
                t_type1 = op1.getTokenType();
                if (this.validConditional(t_type1)) {
                    return new TokenContainer<T>(Token.NOT_CONDITIONAL,
                            new NotCondition<T>(op1.getConditionalLiteral()));
                } else {
                    throw new SyntaxErrorException();
                }
            } else {
                throw new SyntaxErrorException();
            }
        }

        // process AND Token
        private TokenContainer<T> and(Stack<TokenContainer<T>> operandStack) throws SyntaxErrorException {
            TokenContainer<T> op1, op2;
            Token t_type1, t_type2;
            if (!operandStack.empty()) {
                op1 = operandStack.pop();
                t_type1 = op1.getTokenType();
            } else {
                throw new SyntaxErrorException();
            }
            if (!operandStack.empty()) {
                op2 = operandStack.pop();
                t_type2 = op2.getTokenType();
            } else {
                throw new SyntaxErrorException();
            }
            if (this.validConditional(t_type1) && this.validConditional(t_type2)) {
                return (new TokenContainer<T>(Token.AND_CONDITIONAL,
                        new AndCondition<T>(op1.getConditionalLiteral(), op2.getConditionalLiteral())));
            } else {
                throw new SyntaxErrorException();
            }
        }

        // process OR Token
        private TokenContainer<T> or(Stack<TokenContainer<T>> operandStack) throws SyntaxErrorException {
            TokenContainer<T> op1, op2;
            Token t_type1, t_type2;
            if (!operandStack.empty()) {
                op1 = operandStack.pop();
                t_type1 = op1.getTokenType();
            } else {
                throw new SyntaxErrorException();
            }
            if (!operandStack.empty()) {
                op2 = operandStack.pop();
                t_type2 = op2.getTokenType();
            } else {
                throw new SyntaxErrorException();
            }
            if (this.validConditional(t_type1) && this.validConditional(t_type2)) {
                return (new TokenContainer<T>(Token.OR_CONDITIONAL,
                        new OrCondition<T>(op1.getConditionalLiteral(), op2.getConditionalLiteral())));
            } else {
                throw new SyntaxErrorException();
            }
        }

        // a = b converted to equality literal
        private List<TokenContainer<T>> resolveLiteral(List<TokenContainer<T>> tokens) throws SyntaxErrorException {
            List<TokenContainer<T>> tokenContainers = new ArrayList<TokenContainer<T>>();
            Iterator<TokenContainer<T>> iterator = tokens.iterator();
            while (iterator.hasNext()) {
                TokenContainer<T> token = iterator.next();
                if (token.getTokenType() == Token.LITERAL) {
                    TokenContainer<T> comp;
                    TokenContainer<T> comparee;
                    if (iterator.hasNext()) {
                        comp = iterator.next();
                        if (comp.getTokenType() != Token.COMPARISON) {
                            // Equal sign not provided
                            throw new SyntaxErrorException();
                        }
                    } else {
                        throw new SyntaxErrorException();
                    }
                    if (iterator.hasNext()) {
                        comparee = iterator.next();
                        if (comparee.getTokenType() != Token.LITERAL) {
                            // No value to compare to provided.
                            throw new SyntaxErrorException();
                        }
                    } else {
                        throw new SyntaxErrorException();
                    }
                    // store key for future use
                    this.keys.add(token.getValue());
                    TokenContainer<T> comparisonLiteral = new TokenContainer<T>(Token.EQUALITY_CONDITIONAL,
                            new EqualityCondition<T>(token.getValue(), comparee.getValue()));
                    tokenContainers.add(comparisonLiteral);
                } else {
                    tokenContainers.add(token);
                }
            }
            return tokenContainers;
        }

        // quote a b quote converted to 'a b'. Could be sidestepped by using a better tokenizer
        private List<TokenContainer<T>> resolveQuotes(List<TokenContainer<T>> tokens) throws SyntaxErrorException {
            List<TokenContainer<T>> tokenContainers = new ArrayList<TokenContainer<T>>();
            Iterator<TokenContainer<T>> iter = tokens.iterator();
            boolean quote = false;
            while (iter.hasNext()) {
                TokenContainer<T> token = iter.next();
                if (token.getTokenType() == Token.QUOTE && !quote) {
                    // quote opened
                    quote = true;
                    StringBuilder val = new StringBuilder();
                    int ctr = 0;
                    while (iter.hasNext() && quote) {
                        TokenContainer<T> subToken = iter.next();
                        if (subToken.getTokenType() != Token.LITERAL && subToken.getTokenType() != Token.QUOTE) {
                            throw new SyntaxErrorException();
                        } else {
                            if (subToken.getTokenType() == Token.LITERAL) {
                                if (ctr != 0) val.append(" ");
                                val.append(subToken.getValue());
                                ctr++;
                            } else {
                                // quote closed
                                tokenContainers.add(new TokenContainer<T>(Token.LITERAL, val.toString()));
                                quote = false;
                            }
                        }
                    }
                    if (quote) {
                        // if quote remained open not valid
                        throw  new SyntaxErrorException();
                    }
                } else {
                    tokenContainers.add(token);
                }
            }
            return tokenContainers;
        }

        // Build and return query object
        public Query<T> build() {
            return new Query<T>(condition, keys);
        }
    }
}
