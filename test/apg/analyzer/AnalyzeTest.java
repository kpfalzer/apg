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

package apg.analyzer;

import apg.ast.Node;
import apg.parser.ApgFile;
import apg.parser.Lexer;
import apg.parser.Tokens;
import gblibx.CharBuffer;
import gblibx.FileCharBuffer;
import org.junit.Test;

import java.io.IOException;

public class AnalyzeTest {

    @Test
    public void analyze() {
        if (true) {
            Tokens tokens = null;
            try {
                tokens = new Lexer(new FileCharBuffer(
                        "grammar.txt"
                        //"sv2012.peg"
                )).tokenize();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Node ast = ApgFile.parse(tokens);
            Analyze anz = Analyze.analyze(ast);
            boolean debug = true;
        }
        if (true) {
            final String S1 = "bad_dlr : &(bad_dlr) _x1 _x2;\n"
                    + "bad2: bad2+ _dog;\n"
                    + "bad3: ((_x bad3)? bad2) _x3;\n"
                    + "ilr1: p1 _a _b;\n"
                    + "p1: p2;\n"
                    + "p2: ilr1 _c;\n"
                    ;
            Tokens tokens = new Lexer(new CharBuffer(S1)).tokenize();
            Node ast = ApgFile.parse(tokens);
            Analyze anz = Analyze.analyze(ast);
        }

    }
}