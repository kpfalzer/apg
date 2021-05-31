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

import apg.ast.Node;
import gblibx.CharBuffer;
import gblibx.FileCharBuffer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ExpressionTest {

    @Test
    public void parse() {
        if (true) {
            final String[] T1 = new String[]{
                    "a b c d| e f g",
                    "expression '|' expression\n" +
                            "| expression rep? expression?\n" +
                            "| '(' expression ')'\n" +
                            "| pred expression rep? expression?\n" +
                            "| primary",
                    "yo (a|b)",
                    "x | z",
                    "w | x | y",
                    "e1 e2 | f1 (f2 | f3) | g1",
                    "a b",
                    "(  ( a )*  b )*",
                    "foo* yoyo | bar | (&(e) a b c | d e f) | (g|h) | i j k",
                    "foo* yoyo | bar | (&(e) a)"
            };
            for (String t1 : T1) {
                Tokens tokens = new Lexer(new CharBuffer(t1)).tokenize();
                Node ast = Expression.parse(tokens);
                System.out.println("=> " +ast.toString());
                assertTrue(tokens.isEOF());
            }
        }
        if (true) {
            Tokens tokens = null;
            try {
                tokens = new Lexer(new FileCharBuffer("grammar.txt")).tokenize();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Node ast = ApgFile.parse(tokens);
            System.out.println("\n\n" + ast.toString());
            assertTrue(tokens.isEmpty());
        }
        if (true) {
            Tokens tokens = null;
            try {
                tokens = new Lexer(new FileCharBuffer("sv2012.peg")).tokenize();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Node ast = ApgFile.parse(tokens);
            System.out.println("\n\n"+ast.toString());
            assertTrue(tokens.isEmpty());
        }
    }
}