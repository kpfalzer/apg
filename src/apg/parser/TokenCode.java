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

import apg.ast.IToken;

public enum TokenCode implements IToken {
    eIdent("<ident>"),
    eColon(':'),
    eSemi(';'),
    eLeftBrack('['), eRightBrack(']'),
    eLeftParen('('), eRightParen(')'),
    eMinus('-'),
    ePlus('+'),
    eQmark('?'),
    eStar('*'),
    eEOF("<EOF>"),
    eAnd('&'),
    eOr('|'),
    eNot('!'),
    eQuoted("'<quoted>'"),
    eCaret('^'),
    eNonWS("\\S+");

    private TokenCode(String detail) {
        __detail = detail;
    }

    private TokenCode(char detail) {
        __detail = Character.toString(detail);
    }

    private TokenCode() {
        __detail = null;
    }

    @Override
    public String getDetail() {
        return __detail;
    }

    private final String __detail;
}
