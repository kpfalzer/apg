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
import apg.ast.PTokens;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.toSet;
import static java.util.Objects.isNull;

// range: '[' '^'? (.-. | .)+ ']'
public class Range extends TokenConsumer {
    public static Node parse(PTokens tokens) {
        return new Range(tokens).parse();
    }

    private Range(PTokens tokens) {
        super(tokens, new Range.XNode());
    }

    public Node parse() {
        final Token tok = pop();
        if (!getFirstSet().contains(tok.type)) {
            invalidToken(tok);
        }
        return parse(tok);
    }

    public Node parse(Token start) {
        if (peek().type == TokenCode.eCaret) addNode(pop());
        Token[] toks = new Token[3];
        while (!isClose()) {
            toks[0] = popAndNotExpectEOF();
            if (peek().type == TokenCode.eMinus) {
                toks[1] = pop();// -
                toks[2] = popAndNotExpectEOF();
                addNode(new XNode.Span(toks));
            } else {
                addNode(toks[0]);
            }
        }
        pop(); //]
        return getNode();
    }

    private boolean isClose() {
        return peek().type == TokenCode.eRightBrack;
    }

    public static class XNode extends NonTerminalNode {
        public static class Span extends NonTerminalNode {
            private Span(Token[] eles) {
                add(eles);
            }

            public String toString() {
                return getNodes().stream().map(n -> n.toString()).collect(Collectors.joining());
            }
        }

        public String toString() {
            LinkedList<Node> tmp = new LinkedList(getNodes());
            char not = '\0';
            if (tmp.peek().isTerminal() && (TokenCode.eCaret == tmp.peek().toToken().type)) {
                not = '^';
                tmp.removeFirst();
            }
            return String.format("[%c%s]",
                    not,
                    tmp.stream().map(n -> n.toString()).collect(Collectors.joining()));
        }

        @Override
        /**
         * Skip over the entirety of a range.
         */
        public List<PToken> flatten() {
            return Collections.emptyList();
        }
    }

    public static Set<TokenCode> getFirstSet() {
        if (isNull(__FIRST)) __FIRST = Collections.unmodifiableSet(toSet(TokenCode.eLeftBrack));
        return __FIRST;
    }

    private static Set<TokenCode> __FIRST = null;
}
