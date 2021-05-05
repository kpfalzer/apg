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

// pred: ('&' | '!') expression
public class Predicate extends TokenConsumer {
    public static ASTNode parse(Tokens tokens) {
        return new Predicate(tokens).parse();
    }

    private Predicate(Tokens tokens) {
        super(tokens);
    }

    private ASTNode parse() {
        Token op = pop();
        if (!Expression._FIRST.contains(peek().type)) {
            invalidToken(peek());
        }
        return new Node(op, Expression.parse(_tokens));
    }

    public static class Node extends ASTNode {
        private Node(Token start, ASTNode expr) {
            super(start);
            expression = expr;
        }

        @Override
        public String toString() {
            return toString(this, expression);
        }

        public final ASTNode expression;
    }

    /*package*/ static final Set<Token.EType> _FIRST = toSet(Token.EType.eAnd, Token.EType.eNot);

}
