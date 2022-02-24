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

public class XString implements Acceptor {
    /**
     * Create acceptor of string.
     * @param string string value to accept.
     */
    public XString(String string) {
        __string = string;
    }

    @Override
    public boolean accept(State state) {
        final State.Snapshot snap = state.save();
        boolean match = true;
        for (int i = 0; match  && (i < __string.length()); ++i) {
            match = state.accept() == __string.charAt(i);
        }
        if (match) {
            state.add(new Matched(snap));
        } else  {
            state.restore(snap);
        }
        return match;
    }

    public class Matched extends apg.runtime.Matched {
        private Matched(State.Snapshot start) {
            super(start);
        }

        public String toString() {
            return __string;
        }
    }

    private final String __string;
}
