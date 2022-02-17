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

import java.util.LinkedList;
import java.util.List;

import static gblibx.Util.downcast;
import static gblibx.Util.invariant;
import static gblibx.Util.isNonNull;
import static java.util.Objects.isNull;

public class Production {
    public Production(Node node) {
        __node = downcast(node);
        initialize();
    }

    private void initialize() {
        final List<Node> nodes = __node.getNodes();
        //do NOT expect more than 1 Expression (even tho grammar has expression*)
        invariant(!nodes.isEmpty() && (2 >= nodes.size()));
        __name = nodes.get(0).toToken();
        createAlternates();
    }

    private void createAlternates() {
        if (2 > __node.getNodes().size()) return;
        addNode(__node.getNodes().get(1));
    }

    private void addNode(Node root) {
        if (root instanceof Expression.XNode.AltNode) {
            final Expression.XNode.AltNode alt = downcast(root);
            addAlternate(alt.getLhs());
            addNode(alt.getRhs());
        } else if (root instanceof Expression.XNode) {
            LinkedList<Node> alt = root.toNonTerminalNode().getNodes();
            if ((1 == alt.size()) && (alt.getFirst() instanceof Expression.XNode.AltNode)) {
                addNode(alt.getFirst());
            } else {
                addAlternate(root);
            }
        }
    }

    private void addAlternate(Node xalt) {
        final Alternate alt = new Alternate(xalt);
        if (isNull(__alternates)) __alternates = new Alternates();
        __alternates.add(alt);
    }

    public String getName() {
        return __name.text;
    }

    public String getLocation() {
        return __name.loc.toString();
    }

    public String getLineLocation() {
        String loc = getLocation();
        return loc.substring(0, loc.lastIndexOf(':'));
    }

    public Alternates getAlternates() {
        return isNonNull(__alternates) ? __alternates : Alternates.EMPTY;
    }

    private PToken __name;
    private final NonTerminal.XNode __node;
    private Alternates __alternates = null;
}
