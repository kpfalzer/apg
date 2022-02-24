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

package apg.ast;

import java.util.List;

import static gblibx.Util.downcast;

public interface Node {
    public boolean isTerminal();

    default public boolean isNonTerminal() {
        return !isTerminal();
    }

    default public PToken toToken() {
        return toTerminalNode().getToken();
    }

    default public TerminalNode toTerminalNode() {
        return downcast(this);
    }

    default public NonTerminalNode toNonTerminalNode() {
        return downcast(this);
    }

    /**
     * Flatten node to its tokens.
     * @return list of tokens.
     */
    public List<PToken> flatten();

    /**
     * Check for direct left recursion in this node.
     * @param productionName detect this production name.
     * @return true if direct left recursion detected for productionName.
     */
    public boolean detectDLR(String productionName);

    /**
     * To check for indirect left recursion for this node, return
     * the first nonterminal name found.
     * @return first nonterminal name or null (if first is not nonterminal).
     */
    public String getFirstNonTerminalName();

    /**
     * In cases of DLR detected in Predicate or Repeat
     */
    public static class InvalidDLR extends RuntimeException {
    }
}
