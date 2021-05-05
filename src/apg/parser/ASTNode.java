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

import java.util.List;
import java.util.stream.Collectors;

import static gblibx.Util.repeat;

public abstract class ASTNode {
    protected ASTNode(Token start) {
        _start = start;
    }

    protected ASTNode() {
        this(null);
    }

    protected String getLocAndName(ASTNode node) {
        if (1 > __OFFSET) {
            __OFFSET = this.getClass().getPackage().getName().length() + 1;
        }
        return String.format("%s: %s",
                _start.loc.toString(),
                node.getClass().getCanonicalName().substring(__OFFSET)
        );
    }

    public abstract String toString();

    /**
     * Method for degenerate case of single token (is start).
     *
     * @param node subclass with single token.
     * @return parsed token detail.
     */
    protected String toString(ASTNode node) {
        return String.format("%s: %s",
                getLocAndName(node),
                _start.text
        );
    }

    protected String toString(ASTNode node, ASTNode other) {
        return String.format("%s: %s",
                getLocAndName(node),
                other.toString()
        );
    }

    protected String toString(List<?> eles, int indent) {
        final String spacing = repeat(" ", indent) + "\n";
        return toString(eles, spacing);
    }

    protected String toString(List<?> eles, String spacing) {
        return eles.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(spacing));
    }

    protected String toString(List<?> eles) {
        return toString(eles, 1);
    }

    private static int __OFFSET = 0;
    protected final Token _start;
}
