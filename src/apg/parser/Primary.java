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

/**
 * primary: IDENT
 * | terminal
 */
public class Primary extends TokenConsumer {
    public static Node parse(PTokens tokens) {
        return new Primary(tokens).parse();
    }

    private Primary(PTokens tokens) {
        super(tokens, new Primary.XNode());
    }

    public Node parse() {
        Token tok = peek();
        if (tok.isIdent() && !tok.identIsEOF()) {
            addNode(pop());
        } else if (Terminal.getFirstSet().contains(tok.type)) {
            addNode(Terminal.parse(_tokens));
        } else {
            invalidToken(tok);
        }
        return getNode();
    }

    public static class XNode extends NonTerminalNode implements Expression.Tree.XNode{
        @Override
        public boolean detectDLR(String productionName) {
            return getNodes().peek().detectDLR(productionName);
        }

        @Override
        public String getFirstNonTerminalName() {
            return getNodes().peek().getFirstNonTerminalName();
        }
    }

    public static Set<TokenCode> getFirstSet() {
        if (isNull(__FIRST)) __FIRST = Collections.unmodifiableSet(
                toSet(
                        TokenCode.eIdent,
                        Terminal.getFirstSet()
                )
        );
        return __FIRST;
    }

    private static Set<TokenCode> __FIRST = null;

}
