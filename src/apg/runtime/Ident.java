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

import static java.util.Objects.isNull;

public class Ident {
    private static Acceptor initialize() {
        return new Acceptor() {
            @Override
            public boolean accept(State state) {
                char ch = state.la();
                // _a-zA-Z
                if (!(('_' == ch)
                        || ('a' <= ch && 'z' >= ch)
                        || ('A' <= ch && 'Z' >= ch))) {
                    return false;
                }
                final State.Snapshot snap = state.save();
                final StringBuilder sb = new StringBuilder();
                sb.append(state.accept());
                while (true) {
                    ch = state.la();
                    if (!(('_' == ch)
                            || ('a' <= ch && 'z' >= ch)
                            || ('A' <= ch && 'Z' >= ch)
                            || ('0' <= ch && '9' >= ch))) {
                        break;
                    }
                    sb.append(state.accept());
                }
                state.add(new Matched(snap, sb.toString()));
                return true;
            }
        };
    }

    private static Acceptor __acceptor = null;

    public static boolean accept(State state) {
        return (__acceptor = (isNull(__acceptor) ? initialize() : __acceptor)).accept(state);
    }

    public static class Matched extends apg.runtime.Matched {
        private Matched(State.Snapshot start, String ident) {
            super(start);
            __ident = ident;
        }

        private final String __ident;

        public String toString() {
            return __ident;
        }
    }
}
