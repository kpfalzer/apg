/*
 *
 *  * The MIT License
 *  *
 *  * Copyright 2021 kpfalzer.
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 *
 */

package apg.parser;

import apg.ast.PTokens;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.isNonNull;
import static gblibx.Util.toSet;

/**
 * expression: expression '|' expression
 * | '(' expression ')' rep?
 * | (pred? expression)+
 * | primary rep?
 * <p>
 * refactor to remove left recursion.
 * Instead of using right recursion, we do tail X*
 */
/* Refresh remove left recursion:
 *
 *  E: '(' E ')' rep? EE*   //E1
 *   | pred E rep? EE*      //E2
 *   | primary rep? EE*     //E3
 *
 *  EE: '|'? E   //EE1
 */
public class Expression extends TokenConsumer {
    public static ASTNode parse(PTokens tokens) {
        return new Expression(tokens).parse();
    }

    private Expression(PTokens tokens) {
        super(tokens);
    }

    private ASTNode parse() {
        Token tok = peek();
        if (!_FIRST.contains(tok.type)) {
            invalidToken(tok);
        }
        __node = new Node(tok);
        if (tok.type == TokenCode.eLeftParen) {
            __node.add(e1(pop()));
        } else if (Predicate._FIRST.contains(tok.type)) {
            __node.add(e2(peek()));
        } else {
            __node.add(e3(peek()));
        }
        while (!isEOF()) {
            tok = peek();
            // EE*
            boolean isAlt = false;
            if (TokenCode.eOr == tok.type) {
                pop();
                isAlt = true;
            } else if (_FIRST.contains(tok.type)) {
                ;
            } else {
                break;//while
            }
            final ASTNode expression = Expression.parse(_tokens);
            __node.add(isAlt, expression);
        }
        return __node;
    }

    private E1 e1(Token lparen) {
        return new E1(lparen).parse();
    }

    private E2 e2(Token la0) {
        return new E2(la0).parse();
    }

    private E3 e3(Token la0) {
        return new E3(la0).parse();
    }

    // '(' . E ')' rep?
    private class E1 extends ASTNode {
        private E1 parse() {
            Token tok = peek();
            if (!Expression._FIRST.contains(tok.type))
                Util.invalidToken(tok);
            expression = Expression.parse(_tokens);
            tok = pop();
            if (TokenCode.eRightParen != tok.type) {
                Util.invalidToken(tok, ")");
            }
            if (Repeat._FIRST.contains(peek().type)) {
                repeat = Repeat.parse(_tokens);
            }
            return this;
        }

        private E1(Token start) {
            super(start);
        }

        @Override
        public String toString() {
            return String.format("%s: ( %s ) %s",
                    getLocAndName(this),
                    expression.toString(),
                    (isNonNull(repeat)) ? repeat.toString() : ""
            );
        }

        public ASTNode expression = null, repeat = null;
    }

    // . pred E rep?
    private class E2 extends ASTNode {
        private E2 parse() {
            Token tok = _start;
            predicate = Predicate.parse(_tokens);
            expression = Expression.parse(_tokens);
            if (Repeat._FIRST.contains(peek().type)) {
                repeat = Repeat.parse(_tokens);
            }
            return this;
        }

        private E2(Token start) {
            super(start);
        }

        @Override
        public String toString() {
            return String.format("%s: %s %s %s",
                    getLocAndName(this),
                    predicate.toString(),
                    expression.toString(),
                    (isNonNull(repeat)) ? repeat.toString() : ""
            );
        }

        public ASTNode predicate = null, expression = null, repeat = null;
    }

    // . primary rep?
    private class E3 extends ASTNode {
        private E3(Token start) {
            super(start);
        }

        private E3 parse() {
            primary = Primary.parse(_tokens);
            Token tok = peek();
            if (Repeat._FIRST.contains(tok.type)) {
                repeat = Repeat.parse(_tokens);
            }
            return this;
        }

        @Override
        public String toString() {
            return String.format("%s: %s %s",
                    getLocAndName(this),
                    primary.toString(),
                    (isNonNull(repeat)) ? repeat.toString() : ""
            );
        }

        public ASTNode primary = null, repeat = null;
    }

    public static class Node extends ASTNode {
        private Node(Token start) {
            super(start);
        }

        public String toString() {
            return String.format("%s: %s",
                    getLocAndName(this),
                    toString(items, "\n  ")
            );
        }

        public void add(boolean isAlt, ASTNode expression) {
            items.add(new Item(isAlt, expression));
        }

        public void add(ASTNode expression) {
            add(false, expression);
        }

        public static class Item extends gblibx.Util.Pair<Boolean, ASTNode> {
            public Item(boolean isAlt, ASTNode expression) {
                super(isAlt, expression);
            }

            public boolean isAlt() {
                return v1;
            }

            public ASTNode expression() {
                return v2;
            }

            public String toString() {
                return String.format("%s %s",
                        isAlt() ? "| " : "",
                        expression().toString()
                );
            }
        }

        public final List<Item> items = new LinkedList<>();
    }

    private Node __node;
    /*package*/ static final Set<TokenCode> _FIRST = toSet(
            TokenCode.eLeftParen, Predicate._FIRST, Primary._FIRST
    );
}
