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

import static gblibx.Util.*;
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
        return flatten(expr, null, null);
    }

    /**
     * Flatten expressions into alternates.
     */
    private static Alternates flatten(Node expr, Alternates alts, Alternate alt) {
        if (isNull(expr)) return alts;
        List<Node> nodes = expr.toNonTerminalNode().getNodes(true);
        for (boolean stay = true; stay; ) {
            invariant(isNonNull(expr));
            stay = expr.isNonTerminal();
            if (stay) {
                invariant(!nodes.isEmpty());  //?
                expr = nodes.remove(0);
                if (expr instanceof Expression.XNode.AltNode) {
                    boolean todo = true;
                } else {
                    alt = Alternate.add(alt, expr);
                }
            } else {
                Token tok = downcast(expr.toToken());
                switch (tok.type) {
                    case eLeftParen:
                        alt = Alternate.add(alt, expr);
                        expr = nodes.remove(0);
                        Alternate.add(alt, flatten(expr));
                        tok = downcast(nodes.remove(0).toToken());
                        invariant(TokenCode.eRightParen == tok.type);
                        alt.add(tok);
                        break;
                    case eRightParen:
                        alts = Alternates.add(alts, alt);
                        nodes.add(0, tok);
                        break;
                    default:
                        alt = Alternate.add(alt, expr);
                }
            }
        }
        return alts;
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

    public String getName() {
        return __name.text;
    }

    public String getLocation() {
        return __name.loc.toString();
    }

    private PToken __name;
    private Alternates __alts = null;
}
