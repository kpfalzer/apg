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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static gblibx.Util.invariant;

public class Lexer {
    public Lexer(CharBuffer src) {
        __src = src;
    }

    public int tokenize() {
        while (isNotEof()) {
            switch (la()) {
                case ' ':
                case '\t':
                case '\n':
                    accept(); //skip whitespace
                    break;
                case '/':
                    if (isBlockComment() || isLineComment())
                        ;//do nothing
                    else
                        accept(mark(), Character.toString(accept()), Token.EType.eNonWS);
                    break;
                case '\\':
                    escaped();
                    break;
                case '\'':
                    quoted();
                    break;
                default:
                    if (__typeByChar.containsKey(__la)) {
                        accept(__typeByChar.get(__la));
                    } else if (isIdentStart()) {
                        ident();
                    } else {
                        invariant(__NONWS.matcher(Character.toString(__la)).matches());
                        accept(Token.EType.eNonWS);
                    }
            }
        }
        accept(Token.EType.eEOF);
        return __tokens.size();
    }

    private static final Pattern __NONWS = Pattern.compile("\\S");

    private void quoted() {
        final CharBuffer.Mark start = mark();
        StringBuilder text = new StringBuilder();
        text.append(accept());
        boolean done = false;
        while (!done && isNotEof()) {
            text.append(accept());
            if ('\\' == __la) text.append(accept());
            else done = '\'' == __la;
        }
        invariant(done);//todo: no closure on quoted
        accept(start, text.toString(), Token.EType.eQuoted);
    }

    private void escaped() {
        final CharBuffer.Mark start = mark();
        char la1 = la(1);
        invariant(la1 != CharBuffer.EOF);
        accept(start, String.format("\\%c", la1), Token.EType.eNonWS);
        accept(2);
    }

    private boolean isBlockComment() {
        if ('*' != la(1)) return false;
        accept(2);
        boolean done = false;
        while (!done && isNotEof()) {
            if ('*' != la()) accept();
            else {
                if ('/' == la(1)) {
                    done = true;
                }
                accept(2);
            }
        }
        invariant(done);//TODO: block comment not terminated
        return true;
    }

    private boolean isLineComment() {
        if ('/' != la(1)) return false;
        while (isNotEof()) {
            if ('\n' == accept()) break;
        }
        return true;
    }

    private void ident() {
        final CharBuffer.Mark start = mark();
        StringBuilder text = new StringBuilder();
        text.append(accept());
        while (isNotEof()) {
            la();
            if (isIdent()) {
                text.append(accept());
            } else {
                break;//while
            }
        }
        accept(start, text.toString(), Token.EType.eIdent);
    }

    private CharBuffer.Mark mark() {
        return __src.mark();
    }

    private boolean isNotEof() {
        return !__src.isEOF();
    }

    private boolean isIdentStart() {
        // [a-zA-Z_]
        return (('a' <= __la) && ('z' >= __la))
                || (('A' <= __la) && ('Z' >= __la))
                || ('_' == __la);
    }

    private boolean isIdent() {
        return isIdentStart()
                || (('0' <= __la) && ('9' >= __la));
    }

    private char la() {
        return (__la = __src.la());
    }

    private char la(int n) {
        return __src.la(n);
    }

    private char accept() {
        return (__la = __src.accept());
    }

    private void accept(int n) {
        __src.accept(n);
    }

    private void accept(Token.EType type) {
        accept(mark(), Character.toString(accept()), type);
    }

    private void accept(CharBuffer.Mark mark, String text, Token.EType type) {
        __tokens.add(new Token(mark, text, type));
    }

    private static final Map<Character, Token.EType> __typeByChar = new HashMap<>();

    static {
        __typeByChar.put(':', Token.EType.eColon);
        __typeByChar.put(';', Token.EType.eSemi);
        __typeByChar.put('[', Token.EType.eLeftBrack);
        __typeByChar.put(']', Token.EType.eRightBrack);
        __typeByChar.put('(', Token.EType.eLeftParen);
        __typeByChar.put(')', Token.EType.eRightParen);
        __typeByChar.put('-', Token.EType.eMinus);
        __typeByChar.put('+', Token.EType.ePlus);
        __typeByChar.put('*', Token.EType.eStar);
        __typeByChar.put('?', Token.EType.eQmark);
        __typeByChar.put('&', Token.EType.eAnd);
        __typeByChar.put('|', Token.EType.eOr);
        __typeByChar.put('!', Token.EType.eNot);
        __typeByChar.put('^', Token.EType.eCaret);
    }

    static {
        invariant('a' < 'z' && 'A' < 'Z' && '0' < '9');
    }

    private char __la;
    private final CharBuffer __src;
    private final Tokens __tokens = new Tokens();
}
