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
import gblibx.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static apg.parser.Util.invalidToken;

// IDENT ':' expression* ';'
public class NonTerminal extends TokenConsumer {
    public static ASTNode parse(PTokens tokens) {
        return new NonTerminal(tokens).parse();
    }

    private NonTerminal(PTokens tokens) {
        super(tokens);
    }

    private ASTNode parse() {
        Token tok = pop();
        if (!_FIRST.contains(tok.type)) {
            invalidToken(tok);
        }
        __node = new Node(tok);
        if ((tok = pop()).type != TokenCode.eColon) {
            apg.parser.Util.invalidToken(tok, ":");
        }
        expressions();
        if ((tok = pop()).type != TokenCode.eSemi) {
            apg.parser.Util.invalidToken(tok, ";");
        }
        return __node;
    }

    private void expressions() {
        Token tok;
        while ((tok = peek()).type != TokenCode.eSemi) {
            if (!Expression._FIRST.contains(tok.type)) {
                invalidToken(tok);
            }
            __node.exprs.add(Expression.parse(_tokens));
        }
    }

    public static class Node extends ASTNode {
        private Node(Token ident) {
            super(ident);
        }

        public String toString() {
            return String.format("%s: %s",
                    getLocAndName(this),
                    toString(exprs, 4)
            );
        }

        public final List<ASTNode> exprs = new LinkedList<>();
    }

    private Node __node;
    /*package*/ static final Set<TokenCode> _FIRST = Util.toSet(TokenCode.eIdent);

}
