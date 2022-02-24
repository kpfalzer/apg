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

import static gblibx.Util.expectNever;

public class Repeated implements Acceptor {
    public enum ERep {eOptional, eZeroOrMore, eOneOrMore}

    ;

    public Repeated(Acceptor item, ERep rep) {
        __acceptor = item;
        __rep = rep;
    }

    @Override
    public boolean accept(State state) {
        boolean match = true;
        switch (__rep) {
            case eOptional: {
                // A failed accept will ALWAYS restore state on fail
                boolean ignore = __acceptor.accept(state);
            }
            break;
            case eZeroOrMore: {
                int count = 0;
                for (; __acceptor.accept(state); ++count) ;
            }
            break;
            case eOneOrMore: {
                final State.Snapshot snap = state.save();
                int count = 0;
                for (; __acceptor.accept(state); ++count) ;
                if (1 > count) {
                    match = false;
                    state.restore(snap);
                }
            }
            break;
            default:
                expectNever();
        }
        return match;
    }

    private final Acceptor __acceptor;
    private final ERep __rep;
}
