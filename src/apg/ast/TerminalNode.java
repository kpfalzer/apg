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

import apg.parser.TokenCode;

import java.util.List;

import static gblibx.Util.invariant;
import static gblibx.Util.toList;
import static java.util.Objects.isNull;

public abstract class TerminalNode implements Node {
    @Override
    public boolean isTerminal() {
        return true;
    }

    protected TerminalNode() {
    }

    public abstract PToken getToken();

    @Override
    public List<PToken> flatten() {
        return toList(getToken());
    }

    public String toString() {
        return getToken().toString();
    }

    @Override
    public boolean detectDLR(String productionName) {
        final PToken tok = getToken();
        return (TokenCode.eIdent == tok.type && tok.text.equals(productionName));
    }

    @Override
    public String getFirstNonTerminalName() {
        final PToken tok = getToken();
        return (TokenCode.eIdent == tok.type)? tok.text:null;
    }

    /**
     * Convenience class for implementing wrapped TerminalNode.
     */
    public static class WNode extends TerminalNode {
        protected WNode(PToken tok) {
            _tok = tok;
        }

        protected WNode() {
            this(null);
        }

        @Override
        public PToken getToken() {
            return _tok;
        }

        public void setNode(PToken tok) {
            invariant(isNull(_tok));
            _tok = tok;
        }

        protected PToken _tok;
    }
}
