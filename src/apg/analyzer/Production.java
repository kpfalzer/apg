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
import apg.ast.PToken;
import apg.parser.Expression;
import apg.parser.NonTerminal;

import java.util.List;

import static gblibx.Util.downcast;
import static gblibx.Util.invariant;

public class Production {
    public Production(Node node) {
        __node = downcast(node);
        initialize();
    }

    private void initialize() {
        final List<Node> nodes = __node.getNodes();
        invariant(!nodes.isEmpty());
        __name = nodes.get(0).toToken();
        if (1 < nodes.size()) __expression = downcast(nodes.get(1));
    }

    public String getName() {
        return __name.text;
    }

    public String getLocation() {
        return __name.loc.toString();
    }

    private final NonTerminal.XNode __node;
    private PToken __name;
    private Expression.XNode __expression = null;
}
