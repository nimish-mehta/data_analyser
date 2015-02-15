package parse;

import parse.condition.Condition;

/**
 * Created by Nimish on 15/02/15.
 *
 * Contains the token extracted from query.
 * Uses Simple Descent Parsing to find all valid tokens.
 *
 * Can be improved by using a better tokenizer.
 */
public class TokenContainer<T> {
        private final Token tokenType;
        private Condition<T> conditionalLiteral;
        private final String value;


        public TokenContainer(Token tokenType, String value) {
            this.tokenType = tokenType;
            this.value = value;
            this.conditionalLiteral = null;
        }

        public TokenContainer(Token tokenType) {
            this.tokenType = tokenType;
            this.value = null;
            this.conditionalLiteral = null;
        }

        public TokenContainer(Token tokenType, Condition<T> conditionalLiteral) {
            this.conditionalLiteral = conditionalLiteral;
            this.tokenType = tokenType;
            this.value = null;
        }

        public Token getTokenType() {
            return tokenType;
        }

        public String getValue() {
            return value;
        }

        public Condition<T> getConditionalLiteral() {
            return conditionalLiteral;
        }

    @Override
        public String toString() {
            return String.format("%s", tokenType);
        }

        public static class TokenContainerBuilder {
            private boolean tokenComplete = false;
            private boolean multiToken = false;
            private String token;
            private Token tokenType;
            private String value;
            private String remaining;

            public TokenContainerBuilder(String token) {
                this.token = token;
            }

            public void parse() {
                parseIfOperator();
                if (!this.isTokenComplete()) {
                    parsePartial();
                    if (!this.isMultiToken()) {
                        tokenType = Token.LITERAL;
                        value = this.token;
                        this.tokenComplete = true;
                    }
                }
            }

            private void parseIfOperator() {
                if (token.equals("and")) {
                    tokenType = Token.AND;
                    tokenComplete = true;
                } else if (token.equals("or")) {
                    tokenType = Token.OR;
                    tokenComplete = true;
                } else if (token.equals("not")) {
                    tokenComplete = true;
                    tokenType = Token.NOT;
                } else if (token.equals("=")) {
                    tokenComplete = true;
                    tokenType = Token.COMPARISON;
                } else if (token.equals("(")) {
                    tokenComplete = true;
                    tokenType = Token.BRACKET_OPEN;
                } else if (token.equals(")")) {
                    tokenComplete = true;
                    tokenType = Token.BRACKET_CLOSE;
                } else if (token.equals("'")) {
                    tokenComplete = true;
                    tokenType = Token.QUOTE;
                }
            }

            private void parsePartial() {
                if (token.startsWith("(")) {
                    tokenType = Token.BRACKET_OPEN;
                    multiToken = true;
                    remaining = token.substring(1);
                } else if (token.startsWith("=")) {
                    tokenType = Token.COMPARISON;
                    multiToken = true;
                    remaining = token.substring(1);
                } else if (token.startsWith("'")) {
                    tokenType = Token.QUOTE;
                    multiToken = true;
                    remaining = token.substring(1);
                } else if (token.startsWith(")")) {
                    tokenType = Token.BRACKET_CLOSE;
                    multiToken = true;
                    remaining = token.substring(1);
                } else {
                    int quote_pos = Integer.MAX_VALUE, brack_pos = Integer.MAX_VALUE, eq_pos = Integer.MAX_VALUE, min_pos = 0;

                    if (token.contains("'")) {
                        quote_pos = token.indexOf("'");
                    }
                    if (token.contains((")"))) {
                        brack_pos = token.indexOf(")");
                    }
                    if (token.contains("=")) {
                        eq_pos = token.indexOf("=");
                    }

                    if (!(quote_pos == brack_pos && brack_pos == eq_pos && eq_pos == Integer.MAX_VALUE)) {
                        if (quote_pos < brack_pos && quote_pos < eq_pos) {
                            min_pos = quote_pos;
                        } else if (brack_pos < quote_pos && brack_pos < eq_pos) {
                            min_pos = brack_pos;
                        } else {
                            min_pos = eq_pos;
                        }
                        tokenType = Token.LITERAL;
                        multiToken = true;
                        value = token.substring(0, min_pos);
                        remaining = token.substring(min_pos, token.length());

                    }

                }
            }

            public boolean isTokenComplete() {
                return tokenComplete;
            }

            public boolean isMultiToken() {
                return multiToken;
            }

            public String getRemaining() {
                return remaining;
            }

            public TokenContainer getToken() {
                if (tokenType != Token.LITERAL) {
                    return new TokenContainer(tokenType);
                } else {
                    return new TokenContainer(tokenType, value);
                }
            }
        }
}
