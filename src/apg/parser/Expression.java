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

import apg.ast.Node;
import apg.ast.NonTerminalNode;
import apg.ast.PTokenConsumer;
import apg.ast.PTokens;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.invariant;
import static gblibx.Util.toSet;
import static java.util.Objects.isNull;

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
    public static Node parse(PTokens tokens) {
        return new Expression(tokens).parse();
    }

    private Expression(PTokens tokens) {
        super(tokens, new Expression.XNode());
    }

    public static class XNode extends NonTerminalNode {
        private XNode(Collection<Node> nodes) {
            super.add(nodes);
        }

        private XNode() {
        }

        protected XNode add(Collection<Node> nodes) {
            super.add(nodes);
            return this;
        }

        public static class AltNode extends NonTerminalNode {
            private AltNode(Node lhs, Node rhs) {
                super.add(lhs, rhs);
            }

            public String toString() {
                return getNodes().stream()
                        .map(n -> n.toString())
                        .collect(Collectors.joining("\n  |  "));
            }

            public Node getLhs() {
                return getNodes().getFirst();
            }

            public Node getRhs() {
                return getNodes().getLast();
            }
        }
    }

    public Node parse() {
        Token tok = peek();
        if (!getFirstSet().contains(tok.type)) {
            invalidToken(tok);
        }
        if (tok.type == TokenCode.eLeftParen) {
            e1(pop());
        } else if (Predicate.getFirstSet().contains(tok.type)) {
            e2(peek());
        } else {
            e3(peek());
        }
        while (!isEOF()) {
            tok = peek();
            // EE*
            Node altNode = null;
            if (TokenCode.eOr == tok.type) {
                altNode = pop();
            } else if (getFirstSet().contains(tok.type)) {
                ;
            } else {
                break;//while
            }
            final Node expr = Expression.parse(_tokens);
            if (isNull(altNode))
                addNode(expr); //todo: flatten, so 'e1 e2' stay at same level
            else
                addAlt(expr);
        }
        return getNode();
    }

    protected PTokenConsumer addNode(Node... nodes) {
        if ((1 == nodes.length) && (nodes[0] instanceof XNode.AltNode)) {
            //push existing to front of lhs
            LinkedList<Node> lhs = nodes[0].toNonTerminalNode().getNodes().getFirst().toNonTerminalNode().getNodes();
            LinkedList<Node> infront = super.getNode().toNonTerminalNode().getNodes();
            while (!infront.isEmpty())
                lhs.offerFirst(infront.removeLast());
        }
        super.addNode(nodes);
        return this;
    }

    private void addAlt(Node rhs) {
        invariant(super.getNode().isNonTerminal());
        final Expression.XNode lhs = new XNode(super.getNode().toNonTerminalNode().getNodes());
        super.replace(new XNode.AltNode(lhs, rhs));
    }

    // '(' . E ')' rep?
    private void e1(Token lparen) {
        Token tok = peek();
        if (!Expression.getFirstSet().contains(tok.type))
            Util.invalidToken(tok);
        Node expression = Expression.parse(_tokens);
        tok = pop();
        if (TokenCode.eRightParen != tok.type) {
            Util.invalidToken(tok, ")");
        }
        addNode(lparen, expression, tok);
        if (Repeat.getFirstSet().contains(peek().type)) {
            addNode(Repeat.parse(_tokens));
        }
    }

    // . pred E rep?
    private void e2(Token la0) {
        addNode(Predicate.parse(_tokens), Expression.parse(_tokens));
        if (Repeat.getFirstSet().contains(peek().type)) {
            addNode(Repeat.parse(_tokens));
        }
    }

    // . primary rep?
    private void e3(Token la0) {
        addNode(Primary.parse(_tokens));
        Token tok = peek();
        if (Repeat.getFirstSet().contains(tok.type)) {
            addNode(Repeat.parse(_tokens));
        }
    }

    public static Set<TokenCode> getFirstSet() {
        if (isNull(__FIRST)) __FIRST = Collections.unmodifiableSet(
                toSet(
                        TokenCode.eLeftParen,
                        Predicate.getFirstSet(),
                        Primary.getFirstSet()
                )
        );
        return __FIRST;
    }

    private static Set<TokenCode> __FIRST = null;

}
