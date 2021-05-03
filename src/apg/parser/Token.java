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

import gblibx.CharBuffer;

public class Token {
    public enum EType {
        eIdent,
        eColon,
        eSemi,
        eLeftBrack, eRightBrack,
        eLeftParen, eRightParen,
        eMinus,
        ePlus,
        eQmark,
        eStar,
        eEOF,
        eAnd,
        eOr,
        eNot,
        eQuoted,
        eCaret,
        eNonWS  //single non-whitespace char (not above)
    }

    public boolean isEOF() {
        return type == EType.eEOF;
    }

    public Token(CharBuffer.Mark loc, String text, EType type) {
        this.loc = loc;
        this.text = text;
        this.type = type;
    }

    public final CharBuffer.Mark loc;
    public final String text;
    public final EType type;
}
