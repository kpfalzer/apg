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

package apg.analyzer;

import apg.ast.Node;
import apg.ast.NonTerminalNode;
import apg.ast.PToken;
import apg.parser.Expression;
import apg.parser.NonTerminal;
import apg.parser.Token;
import apg.parser.TokenCode;

import java.util.LinkedList;
import java.util.List;

import static gblibx.Util.downcast;
import static gblibx.Util.invariant;
import static gblibx.Util.isNonNull;
import static java.util.Objects.isNull;

public class Production {
    public Production(Node node) {
        initialize(downcast(node));
    }

    private void initialize(NonTerminal.XNode node) {
        final List<Node> nodes = node.getNodes();
        invariant(!nodes.isEmpty());
        __name = nodes.get(0).toToken();
        invariant(2 >= nodes.size()); //do NOT expect more than 1 Expression (even tho grammar has expression*)
        if (1 < nodes.size()) __alts = flatten(nodes.get(1));
    }

    private static Alternates flatten(Node expr) {
        return flatten(expr, new State());
    }

    /**
     * Flatten expressions into alternates.
     */
    private static Alternates flatten(Node expr, State state) {
        if (isNull(expr)) return state.alts;
        if (expr.isTerminal()) return state.add(expr).alts;
        final LinkedList<Node> nodes = expr.toNonTerminalNode().getNodes();
        while (!nodes.isEmpty()) {
            expr = nodes.remove();
            invariant(isNonNull(expr));
            if (expr.isTerminal()) {
                Token tok = downcast(expr.toToken());
                switch (tok.type) {
                    case eLeftParen:
                        state.add(tok);
                        expr = nodes.pop();
                        Alternates parend = flatten(expr);
                        //expect the recursion to have added ')' to end of its Alternates.
                        //want to move it up to this level.
                        expr = parend.toNonTerminalNode().getNodes().peekLast() //last Alternate
                                .toNonTerminalNode().getNodes().removeLast(); //the ')' token
                        invariant(TokenCode.eRightParen == expr.toToken().type);
                        state.add(parend).add(expr);
                        break;
                    default:
                        state.add(tok);
                }
            } else {
                if (expr instanceof Expression.XNode.AltNode) {
                    final LinkedList<Node> xnodes = expr.toNonTerminalNode().getNodes();
                    invariant(TokenCode.eOr == xnodes.peekFirst().toToken().type);
                    if (1 < xnodes.size()) {
                        invariant(2 == xnodes.size());
                        state.alt = null; //forces next add to start new alt
                        flatten(xnodes.removeLast(), state);
                    } else {
                        //empty alternate
                        state.alts = Alternates.add(state.alts, new Alternate());
                    }
                } else {
                    // keep at same level ?!
                    flatten(expr, state);
                }
            }
        }
        return state.alts;
    }

    public static class Alternate extends NonTerminalNode {
        private Alternate() {
        }

        private Alternate(Node node) {
            add(node);
        }

        private Alternate add(Node node) {
            super.add(node);
            return this;
        }

        private static Alternate add(Alternate alt, Node node) {
            return isNull(alt) ? new Alternate(node) : alt.add(node);
        }
    }

    public static class Alternates extends NonTerminalNode {
        private static Alternates add(Alternates alts, Alternate alt) {
            if (isNull(alts)) alts = new Alternates();
            alts.add(alt);
            return alts;
        }
    }

    private static class State {
        public Alternate alt = null;
        public Alternates alts = null;

        private State add(Node node) {
            boolean addToAlts = isNull(alt);
            alt = Alternate.add(alt, node);
            if (addToAlts) alts = Alternates.add(alts, alt);
            return this;
        }
    }

    public String getName() {
        return __name.text;
    }

    public String getLocation() {
        return __name.loc.toString();
    }

    private PToken __name;
    private Alternates __alts = null;
}
