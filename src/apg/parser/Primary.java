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

import java.util.Set;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.isNonNull;
import static gblibx.Util.toSet;

/**
 * primary: IDENT
 * | terminal
 */
public class Primary extends TokenConsumer {
    public static ASTNode parse(PTokens tokens) {
        return new Primary(tokens).parse();
    }

    private Primary(PTokens tokens) {
        super(tokens);
    }

    private ASTNode parse() {
        Token tok = peek();
        if (tok.isIdent() && !tok.identIsEOF()) {
            __node = new Node(pop());
        } else if (Terminal._FIRST.contains(tok.type)) {
            __node = new Node(tok, Terminal.parse(_tokens));
        } else {
            invalidToken(tok);
        }
        return __node;
    }

    public static class Node extends ASTNode {
        private Node(Token start, ASTNode item) {
            super(start);
            this.item = item;
        }

        private Node(Token start) {
            this(start, null);
        }

        public String toString() {
            return (isNonNull(item))
                    ? toString(this, item)
                    : toString(this);
        }

        public final ASTNode item;
    }

    private Node __node;
    /*package*/ static final Set<TokenCode> _FIRST = toSet(TokenCode.eIdent, Terminal._FIRST);
}
