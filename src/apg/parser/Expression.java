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
import apg.ast.PToken;
import apg.ast.PTokenConsumer;
import apg.ast.PTokens;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.expectNever;
import static gblibx.Util.invariant;
import static gblibx.Util.toList;
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

    private static Node xparse(PTokens tokens) {
        return new Expression(tokens).parse(false);
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

    }

    public static class Tree {
        /**
         * Flatten to XNode.
         * @param root root node.
         * @return flattened to XNode.
         */
        public static LinkedList<Node> flatten(Node root) {
            return new Tree(root).__flat;
        }

        private static Node create(Node root) {
            final Tree tree = new Tree(root);
            return tree.treeify();
        }

        private Tree(Node root) {
            add(root);
        }

        private Node treeify() {
            List<Node> lhs = new LinkedList<>();
            while (!__flat.isEmpty()) {
                final Node n = __flat.removeFirst();
                if (n.isTerminal() && n.toToken().type == TokenCode.eOr) {
                    invariant(!lhs.isEmpty());
                    final Node lhsExpr = new Expression.XNode(lhs);
                    final Node rhsExpr = treeify();
                    lhs = toList(new Alternate(lhsExpr, rhsExpr));
                } else {
                    lhs.add(n);
                }
            }
            final Node tree = (1 == lhs.size() && (lhs.get(0) instanceof Expression.XNode))
                    ? lhs.get(0) : new Expression.XNode(lhs);
            return tree;
        }

        void add(Node node) {
            if (node.isTerminal() || node instanceof Tree.XNode) {
                __flat.add(node);
            } else {
                add(node.toNonTerminalNode());
            }
        }

        void add(NonTerminalNode node) {
            node.getNodes().stream().forEach(xnode -> add(xnode));
        }

        private LinkedList<Node> __flat = new LinkedList<>();

        /**
         * Tag tree nodes here.
         */
        public interface XNode {
        }
    }

    public Node parse() {
        return parse(true);
    }

    private Node parse(boolean isFirst) {
        Token tok = peek();
        if (!getFirstSet().contains(tok.type)) {
            invalidToken(tok);
        }
        if (tok.type == TokenCode.eLeftParen) {
            addNode(e1(pop()));
        } else if (Predicate.getFirstSet().contains(tok.type)) {
            addNode(e2(peek()));
        } else {
            addNode(e3(peek()));
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
            final Node expr = Expression.xparse(_tokens);
            if (isNull(altNode))
                addNode(expr);
            else
                //just add '| expr' since we'll flatten later
                addNode(altNode, expr);
        }
        final Node expr = (isFirst) ? Tree.create(getNode()) : getNode();
        return expr;
    }

    protected PTokenConsumer addNode(Node... nodes) {
        super.addNode(nodes);
        return this;
    }

    public static class Parenthesized extends NonTerminalNode implements Expression.Tree.XNode {
        private Parenthesized(Token lparen, Node expr, Token rparen) {
            super.add(lparen, expr, rparen);
        }

        @Override
        /**
         * Flatten to tokens: but skip the parens '(' ')'
         */
        public List<PToken> flatten() {
            invariant(3 == getNodes().size());
            return getNodes().get(1).flatten();
        }

        @Override
        public boolean detectDLR(String productionName) {
            return getNodes().get(1).detectDLR(productionName);
        }

        @Override
        public String getFirstNonTerminalName() {
            return getNodes().get(1).getFirstNonTerminalName();
        }
    }

    public static class Repeated extends NonTerminalNode implements Expression.Tree.XNode {
        private Repeated(Node e, Node op) {
            super.add(Expression.Tree.create(e), op);
        }

        @Override
        public boolean detectDLR(String productionName) {
            boolean dlr = getNodes().getFirst().detectDLR(productionName);
            if (dlr) {
                throw new Node.InvalidDLR();
            }
            return false;
        }

        @Override
        public String getFirstNonTerminalName() {
            return getNodes().getFirst().getFirstNonTerminalName();
        }
    }

    // '(' . E ')' rep?
    private Node e1(Token lparen) {
        Token tok = peek();
        if (!Expression.getFirstSet().contains(tok.type))
            Util.invalidToken(tok);
        Node expression = Expression.parse(_tokens);
        tok = pop();
        if (TokenCode.eRightParen != tok.type) {
            Util.invalidToken(tok, ")");
        }
        Node e1 = new Parenthesized(lparen, expression, tok);
        if (Repeat.getFirstSet().contains(peek().type)) {
            e1 = new Repeated(e1, Repeat.parse(_tokens));
        }
        return e1;
    }

    public static class Predicated extends NonTerminalNode implements Expression.Tree.XNode {
        private Predicated(Node pred, Node expr) {
            super.add(pred, expr);
        }

        @Override
        public boolean detectDLR(String productionName) {
            return getNodes().getFirst().detectDLR(productionName);
        }

        @Override
        public String getFirstNonTerminalName() {
            return getNodes().getFirst().getFirstNonTerminalName();
        }
    }

    // . pred E rep?
    private Node e2(Token la0) {
        Node e2 = new Predicated(Predicate.parse(_tokens), Expression.parse(_tokens));
        if (Repeat.getFirstSet().contains(peek().type)) {
            e2 = new Repeated(e2, Repeat.parse(_tokens));
        }
        return e2;
    }

    // . primary rep?
    private Node e3(Token la0) {
        Node e3 = Primary.parse(_tokens);
        Token tok = peek();
        if (Repeat.getFirstSet().contains(tok.type)) {
            e3 = new Repeated(e3, Repeat.parse(_tokens));
        }
        return e3;
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

    public static class Alternate extends NonTerminalNode {
        private Alternate(Node lhs, Node rhs) {
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

        @Override
        public boolean detectDLR(String productionName) {
            expectNever();
            return false;
        }
    }
}
