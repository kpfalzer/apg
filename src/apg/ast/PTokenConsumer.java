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

package apg.ast;

import gblibx.Util;

import static apg.parser.Util.invalidToken;
import static gblibx.Util.castobj;
import static gblibx.Util.invariant;

public abstract class PTokenConsumer<T extends PToken> {
    protected PTokenConsumer(PTokens tokens, Node node) {
        _tokens = tokens;
        _node = node;
    }

    protected PTokenConsumer(PTokens tokens) {
        this(tokens, null);
    }

    public abstract Node parse();

    public Node getNode() {
        return _node;
    }

    public T pop() {
        return castobj(_tokens.pop());
    }

    public T peek() {
        return castobj(_tokens.peek());
    }

    public T popAndPeek() {
        pop();
        return peek();
    }

    public boolean isEOF() {
        return peek().isEOF();
    }

    public T popAndNotExpectEOF() {
        final T tok = pop();
        if (tok.isEOF()) invalidToken(tok);
        return tok;
    }

    public PTokenConsumer replace(Node node) {
        _node = node;
        return this;
    }

    protected PTokenConsumer addNode(Node... nodes) {
        Util.<NonTerminalNode>castobj(_node).add(nodes);
        return this;
    }

    protected PTokenConsumer setNode(PToken tok) {
        invariant(_node instanceof TerminalNode.WNode);
        Util.<TerminalNode.WNode>castobj(_node).setNode(tok);
        return this;
    }

    protected final PTokens _tokens;
    protected Node _node;
}
