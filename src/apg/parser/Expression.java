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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static apg.parser.Util.error;
import static apg.parser.Util.invalidToken;
import static gblibx.Util.isNonNull;
import static gblibx.Util.toSet;

/**
 * expression: expression '|' expression    //V1
 * | '(' expression ')' rep?                //V2
 * | (pred? expression)+                    //V3
 * | primary rep?                           //V4
 */
public class Expression extends TokenConsumer {
    public static ASTNode parse(Tokens tokens) {
        return new Expression(tokens).parse();
    }

    private Expression(Tokens tokens) {
        super(tokens);
    }

    private void add(ASTNode node) {
        __node.items.add(node);
    }

    private ASTNode parse() {
        Token tok = peek();
        __node = new Node(tok);
        assert _FIRST.contains(tok.type);   //check is always done before before calling parse
        if (Token.EType.eLeftParen == tok.type) {
            add(v2(pop()));
        } else if (__V3_FIRST.contains(tok.type)) {
            add(v3(tok));
        } else if (Primary._FIRST.contains(tok.type)) {
            add(v4(tok));
        }
        tok = peek();
        if (Token.EType.eOr == tok.type) {
            tok = popAndPeek();
            if (!_FIRST.contains(tok.type)) {
                invalidToken(tok);
            }
            ASTNode expr2 = Expression.parse(_tokens);
            add(expr2);
        }
        return __node;
    }

    private V2 v2(Token lparen) {
        return new V2(lparen).parse();
    }

    private V3 v3(Token la0) {
        return new V3(la0).parse();
    }

    private V4 v4(Token la0) {
        return new V4(la0).parse();
    }

    // '(' . expression ')' rep?
    private class V2 extends ASTNode {
        private V2 parse() {
            Token tok = peek();
            if (!Expression._FIRST.contains(tok.type)) {
                invalidToken(tok);
            }
            expression = Expression.parse(_tokens);
            tok = pop();
            if (Token.EType.eRightParen != tok.type) {
                error(tok, ")");
            }
            tok = peek();
            if (Repeat._FIRST.contains(tok.type)) {
                repeat = Repeat.parse(_tokens);
            }
            return this;
        }

        private V2(Token start) {
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

    private static final Set<Token.EType> __V3_FIRST = toSet(Predicate._FIRST, Expression._FIRST);

    // . (pred? expression)+
    private class V3 extends ASTNode {
        private V3 parse() {
            while (!isEOF()) {
                Token tok = _start;
                ASTNode pred = null, expr = null;
                if (Predicate._FIRST.contains(tok.type)) {
                    pred = Predicate.parse(_tokens);
                    tok = peek();
                }
                if (!Expression._FIRST.contains(tok.type)) {
                    invalidToken(tok);
                }
                expr = Expression.parse(_tokens);
                items.add(new Item(pred, expr));
                tok = peek();
                if (!__V3_FIRST.contains(tok.type))
                    break; //while
            }
            return this;
        }

        private V3(Token start) {
            super(start);
        }

        @Override
        public String toString() {
            return String.format("%s: %s",
                    getLocAndName(this),
                    toString(items));
        }

        public class Item extends gblibx.Util.Pair<ASTNode, ASTNode> {
            private Item(ASTNode pred, ASTNode expr) {
                super(pred, expr);
            }

            private Item(ASTNode expr) {
                this(null, expr);
            }

            public ASTNode predicate() {
                return super.v1;
            }

            public ASTNode expression() {
                return super.v2;
            }

            public boolean hasPredicate() {
                return isNonNull(predicate());
            }

            public String toString() {
                return String.format("%s %s",
                        (isNonNull(predicate()) ? predicate().toString() : ""),
                        expression().toString()
                );
            }
        }

        public final List<Item> items = new LinkedList<>();
    }

    // . primary rep?
    private class V4 extends ASTNode {
        private V4(Token start) {
            super(start);
        }

        private V4 parse() {
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
                    toString(items, "\n  | ")
            );
        }

        public final List<ASTNode> items = new LinkedList<>();
    }

    private Node __node;
    /*package*/ static final Set<Token.EType> _FIRST = toSet(
            Token.EType.eLeftParen, Predicate._FIRST, Primary._FIRST
    );
}
