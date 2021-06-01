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
import apg.ast.PTokens;

import java.util.Collections;
import java.util.Set;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.toSet;
import static java.util.Objects.isNull;

// pred: ('&' | '!') '(' expression ')'
public class Predicate extends TokenConsumer {
    public static Node parse(PTokens tokens) {
        return new Predicate(tokens).parse();
    }

    private Predicate(PTokens tokens) {
        super(tokens, new Predicate.XNode());
    }

    public Node parse() {
        final Token op = pop();
        Token tok = peek();
        if (TokenCode.eLeftParen != tok.type) {
            invalidToken(tok, TokenCode.eLeftParen.getDetail());
        }
        if (!Expression.getFirstSet().contains(popAndPeek().type)) {
            invalidToken(peek());
        }
        addNode(op, Expression.parse(_tokens));
        if (TokenCode.eRightParen != (tok = pop()).type) {
            invalidToken(tok, TokenCode.eRightParen.getDetail());
        }
        return getNode();
    }

    public static class XNode extends NonTerminalNode {
        public String toString() {
            return String.format("%s(%s)", getNodes().getFirst().toString(), getNodes().getLast().toString());
        }
    }

    public static Set<TokenCode> getFirstSet() {
        if (isNull(__FIRST)) __FIRST = Collections.unmodifiableSet(
                toSet(
                        TokenCode.eAnd,
                        TokenCode.eNot
                )
        );
        return __FIRST;
    }

    private static Set<TokenCode> __FIRST = null;

}
