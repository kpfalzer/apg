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

package apg.test;

import apg.runtime.Acceptor;
import apg.runtime.Range;
import apg.runtime.State;

import static gblibx.Util.downcast;
import static java.util.Objects.isNull;

public class Repeated {
    private static Acceptor initialize() {
        return new Range("?-?+-+*-*");
    }

    private static Acceptor __acceptor;

    public static boolean accept(State state) {
        final boolean match = (__acceptor = (isNull(__acceptor) ? initialize() : __acceptor)).accept(state);
        if (match) {
            state.add(new Matched(state.removeLast()));
        }
        return match;
    }

    public static class Matched extends apg.runtime.Matched {
        private Matched(apg.runtime.Matched matched) {
            super(matched);
            final Range.Matched rmatch = downcast(matched);
            __char = matched.toString().charAt(0);
            __rep = ('*' == __char)
                    ? apg.runtime.Repeated.ERep.eZeroOrMore
                    : ('+' == __char)
                    ? apg.runtime.Repeated.ERep.eOneOrMore
                    : apg.runtime.Repeated.ERep.eOptional;
        }

        public String toString() {
            return __char.toString();
        }

        private final Character __char;
        private final apg.runtime.Repeated.ERep __rep;
    }
}
