/*
 *
 *  * The MIT License
 *  *
 *  * Copyright 2022 kpfalzer.
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

package apg.runtime;

import gblibx.CharBuffer;
import junit.framework.TestCase;

public class RangeTest extends TestCase {
    private static final Range R1 = new Range("a-zA-Z");
    private static final Repeated SPACING = new Repeated(new Range(" - \t-\t"), Repeated.ERep.eOneOrMore);
    private static final Repeated R2 = new Repeated(R1, Repeated.ERep.eOneOrMore);

    public void testAccept() {
        {
            final CharBuffer b1 = new CharBuffer("d");
            final State s1 = new State(b1);
            boolean match = R1.accept(s1);
            assertTrue(match);
        }
        {
            final CharBuffer b1 = new CharBuffer("foobar \tdog");
            final State s1 = new State(b1);
            boolean match = R2.accept(s1);
            assertTrue(match);
            match = SPACING.accept(s1);
            assertTrue(match);
            match = R2.accept(s1);
            assertTrue(match);
        }
    }
}