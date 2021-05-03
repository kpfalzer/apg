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

import static apg.parser.Util.invalidToken;
import static gblibx.Util.toSet;

// range: '[' '^'? (.-. | .)+ ']'
public class Range {
    public static ASTNode parse(Tokens tokens) {
        return new Range(tokens).parse();
    }

    private Range(Tokens tokens) {
        __tokens = tokens;
    }

    private ASTNode parse() {
        final Token tok = __tokens.pop();
        if (!_FIRST.contains(tok.type)) {
            invalidToken(tok);
        }
        items();
        return __node;
    }

    private void items() {
        boolean hasNotPrefix = __tokens.peek().type == Token.EType.eCaret;
        __node = new Node(hasNotPrefix);
        if (hasNotPrefix) __tokens.pop();
        while (!isClose()) {
            final Token tok = __tokens.popAndNotExpectEOF();
            if (__tokens.peek().type == Token.EType.eMinus) {
                __tokens.pop();// -
                final Token tok2 = __tokens.popAndNotExpectEOF();
                __node.elements.add(new Token[]{tok, tok2});
            } else {
                __node.elements.add(tok);
            }
        }
        __tokens.pop(); // ]
    }

    private boolean isClose() {
        return __tokens.peek().type == Token.EType.eRightBrack;
    }

    public static class Node implements ASTNode {
        private Node(boolean hasNotPrefix) {
            this.hasNotPrefix = hasNotPrefix;
        }

        public final boolean hasNotPrefix;
        public List<Object> elements = new LinkedList<>();
    }

    private Node __node;
    private final Tokens __tokens;
    /*protected*/ static final Set<Token.EType> _FIRST = toSet(Token.EType.eLeftBrack);
}
