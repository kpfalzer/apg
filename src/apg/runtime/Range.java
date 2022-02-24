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

import java.util.BitSet;

import static gblibx.Util.invariant;

public class Range implements Acceptor {
    /**
     * Create acceptor of char range.
     * @param notIn specify true to accept char(s) not in the specified ranges.
     * @param ranges series of 'x-y' expressing x through y, inclusive.
     */
    public Range(boolean notIn, String ranges) {
        __notIn=notIn;
        initialize(ranges);
    }

    /**
     * Create acceptor of char range.
     * @param ranges series of 'x-y' expressing x through y, inclusive.
     */
    public Range(String ranges) {
        this(false, ranges);
    }

    private void initialize(String ranges) {
        //expect sequence of x-y
        invariant(0 == ranges.length() % 3);
        for (int i = 0; i < ranges.length(); i += 3) {
            int c1 = ranges.charAt(i), c2 = ranges.charAt(i+2);
            invariant(c1<__range.size() && c2 < __range.size() && c1 <= c2);
            __range.set(c1, c2+1);
        }
    }

    @Override
    public boolean accept(State state) {
        final State.Snapshot snap = state.save();
        final int c = state.accept();
        invariant(0 <= c && c < __range.size());
        boolean match = (__notIn && c == CharBuffer.EOF)
                || (__notIn && !__range.get(c))
                || (!__notIn && __range.get(c));
        if (match) {
            state.add(new Matched(snap, (char)c));
        } else  {
            state.restore(snap);
        }
        return match;
    }

    public static class Matched extends apg.runtime.Matched {
        private Matched(State.Snapshot start, char c) {
            super(start);
            __char=c;
        }

        public String toString() {
            return __char.toString();
        }

        private final Character __char;
    }

    private final BitSet __range = new BitSet(256);
    private final boolean __notIn;
}
