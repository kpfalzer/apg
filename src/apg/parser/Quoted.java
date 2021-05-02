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

import java.util.Set;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.toSet;

// quoted: '\'' [^\']* '\''
public class Quoted {
    public static ASTNode parse(Tokens tokens) {
        return new Quoted(tokens).parse();
    }

    private Quoted(Tokens tokens) {
        __tokens = tokens;
    }

    private ASTNode parse() {
        Token tok = __tokens.pop();
        if (!_FIRST.contains(tok.type)) {
            invalidToken(tok);
        }
        return (__node = new Node(tok));
    }

    public static class Node implements ASTNode {
        private Node(Token tok) {
            __quoted = tok;
        }

        private final Token __quoted;
    }

    private final Tokens __tokens;
    private Node __node;
    /*protected*/ static final Set<Token.EType> _FIRST = toSet(Token.EType.eQuoted);

}
