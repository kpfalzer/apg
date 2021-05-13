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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.castobj;
import static gblibx.Util.toSet;

// range: '[' '^'? (.-. | .)+ ']'
public class Range extends TokenConsumer {
    public static ASTNode parse(PTokens tokens) {
        return new Range(tokens).parse();
    }

    private Range(PTokens tokens) {
        super(tokens);
    }

    private ASTNode parse() {
        final Token tok = pop();
        if (!_FIRST.contains(tok.type)) {
            invalidToken(tok);
        }
        items(tok);
        return __node;
    }

    private void items(Token start) {
        boolean hasNotPrefix = peek().type == TokenCode.eCaret;
        __node = new Node(start, hasNotPrefix);
        if (hasNotPrefix) pop();
        while (!isClose()) {
            final Token tok = popAndNotExpectEOF();
            if (peek().type == TokenCode.eMinus) {
                pop();// -
                final Token tok2 = popAndNotExpectEOF();
                __node.items.add(new Token[]{tok, tok2});
            } else {
                __node.items.add(tok);
            }
        }
        pop(); // ]
    }

    private boolean isClose() {
        return peek().type == TokenCode.eRightBrack;
    }

    public static class Node extends ASTNode {
        private Node(Token start, boolean hasNotPrefix) {
            super(start);
            this.hasNotPrefix = hasNotPrefix;
        }

        public String toString() {
            return String.format("%s: [%c%s]",
                    getLocAndName(this),
                    (hasNotPrefix) ? '^' : '\0',
                    items.stream().map(e -> toString(e)).collect(Collectors.joining())
            );
        }

        private String toString(Object e) {
            String s;
            if (e instanceof Token)
                s = ((Token) e).text;
            else {
                Token[] toks = castobj(e);
                s = Stream.of(toks).map(x -> x.text).collect(Collectors.joining("-"));
            }
            return s;
        }

        public final boolean hasNotPrefix;
        public final List<Object> items = new LinkedList<>();
    }

    private Node __node;
    /*protected*/ static final Set<TokenCode> _FIRST = toSet(TokenCode.eLeftBrack);
}
