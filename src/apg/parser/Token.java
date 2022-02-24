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

import apg.ast.PToken;
import gblibx.CharBuffer;

public class Token extends PToken<TokenCode> {
    public boolean isEOF() {
        return type == TokenCode.eEOF;
    }

    public boolean isIdent() {
        return TokenCode.eIdent == type;
    }

    public boolean identIsEOF() {
        return isIdent() && text.equals("EOF");
    }

    public Token(CharBuffer.Mark loc, String text, TokenCode type) {
        super(loc, text, type);
    }

    @Override
    public String toString() {
        return identIsEOF() ? "<EOF>" : text;
    }

    @Override
    public boolean detectDLR(String productionName) {
        return (isIdent() && text.equals(productionName));
    }

    @Override
    public String getFirstNonTerminalName() {
        return isIdent()?text:null;
    }
}
