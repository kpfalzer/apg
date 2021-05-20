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

// IDENT ':' expression* ';'
public class NonTerminal extends TokenConsumer {
    public static Node parse(PTokens tokens) {
        return new NonTerminal(tokens).parse();
    }

    private NonTerminal(PTokens tokens) {
        super(tokens, new NonTerminal.XNode());
    }

    public static class XNode extends NonTerminalNode {
        public String toString() {
            return String.format("%s\n",super.toString());
        }
    }

    public Node parse() {
        Token tok = pop();
        if (!getFirstSet().contains(tok.type)) {
            invalidToken(tok);
        }
        addNode(tok);
        if ((tok = pop()).type != TokenCode.eColon) {
            apg.parser.Util.invalidToken(tok, ":");
        }
        expressions();
        if ((tok = pop()).type != TokenCode.eSemi) {
            apg.parser.Util.invalidToken(tok, ";");
        }
        return getNode();
    }

    private void expressions() {
        Token tok;
        while ((tok = peek()).type != TokenCode.eSemi) {
            if (!Expression.getFirstSet().contains(tok.type)) {
                invalidToken(tok);
            }
            addNode(Expression.parse(_tokens));
        }
    }

    public static Set<TokenCode> getFirstSet() {
        if (isNull(__FIRST)) __FIRST = Collections.unmodifiableSet(
                toSet(
                        TokenCode.eIdent
                )
        );
        return __FIRST;
    }

    private static Set<TokenCode> __FIRST = null;


}
