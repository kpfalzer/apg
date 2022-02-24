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

import java.util.LinkedList;

import static java.util.Objects.isNull;

/**
 * The parsing automaton is stateless (static class).
 * We maintain exploration state here.
 */
public class State {
    public State(CharBuffer buffer) {
        __buffer = buffer;
    }

    public CharBuffer getBuf() {
        return __buffer;
    }

    public Snapshot save() {
        return new Snapshot();
    }

    public State restore(Snapshot snap) {
        snap.restore(snap);
        return this;
    }

    public char accept() {
        return __buffer.accept();
    }

    public char la() {
        return __buffer.la();
    }

    public State add(Matched matched) {
        __matches.add(matched);
        return this;
    }

    public Matched removeLast() {
        return __matches.removeLast();
    }
    private final Matches __matches = new Matches();

    /**
     * Encapsulate state we save/restore.
     */
    public class Snapshot {
        public void restore(Snapshot snap) {
            __buffer.reset(snap.__mark);
            __matches.__matchesIx = snap.__matchesIx;

        }
        public Location getLocation() {
            return new Location(__buffer.getFilename(), __mark.lineno, __mark.col);
        }
        private Snapshot() {
            __mark = __buffer.mark();
            __matchesIx = __matches.__matchesIx;
        }
        private final CharBuffer.Mark __mark;
        private final int __matchesIx;
    }

    public static class Matches {
        private Matches() {}
        private void add(Matched matched) {
            if (isNull(__matches)) {
                __matches = new LinkedList<>();
            }
            __matches.add(__matchesIx++, matched);
        }
        private Matched removeLast() {
            --__matchesIx;
            return __matches.removeLast();
        }
        private LinkedList<Matched> __matches = null;
        private int __matchesIx = 0;
    }

    private final CharBuffer __buffer;
}
