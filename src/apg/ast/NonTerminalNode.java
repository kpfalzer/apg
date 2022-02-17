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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class NonTerminalNode implements Node {
    @Override
    public boolean isTerminal() {
        return false;
    }

    protected NonTerminalNode() {
    }

    protected NonTerminalNode(Node node) {
        add(node);
    }

    protected NonTerminalNode add(Node... nodes) {
        if (isNull(__nodes)) __nodes = new LinkedList<>();
        if (1 == nodes.length) __nodes.add(nodes[0]);
        else __nodes.addAll(Arrays.asList(nodes));
        return this;
    }

    protected NonTerminalNode add(Collection<Node> nodes) {
        if (isNull(__nodes)) __nodes = new LinkedList<>();
        __nodes.addAll(nodes);
        return this;
    }

    public String toString() {
        return getNodes().stream()
                .map(n -> n.toString())
                .collect(Collectors.joining(" "));
    }

    @Override
    public List<PToken> flatten() {
        List<PToken> toks = new LinkedList<>();
        getNodes().stream().map(node -> node.flatten()).forEach(f -> toks.addAll(f));
        return toks;
    }

    public LinkedList<Node> getNodes() {
        return isNull(__nodes) ? __EMPTY_LIST : __nodes;
    }

    protected LinkedList<Node> __nodes = null;

    private static final LinkedList<Node> __EMPTY_LIST = new LinkedList<>();
}
