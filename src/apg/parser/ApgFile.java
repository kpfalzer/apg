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

import static apg.parser.Util.invalidToken;

public class ApgFile extends TokenConsumer {
    public static ASTNode parse(PTokens tokens) {
        return new ApgFile(tokens).parse();
    }

    private ApgFile(PTokens tokens) {
        super(tokens);
    }

    private ASTNode parse() {
        while (__FIRST.contains(peek().type)) {
            __node.items.add(Item.parse(_tokens));
        }
        final Token tok = pop();
        if (tok.type != TokenCode.eEOF) {
            invalidToken(tok);
        }
        return __node;
    }

    public static class Node extends ASTNode {
        public String toString() {
            return toString(items, "\n");
        }

        public final List<ASTNode> items = new LinkedList<>();
    }

    private static final Set<TokenCode> __FIRST = Item._FIRST;
    private final Node __node = new Node();
}
