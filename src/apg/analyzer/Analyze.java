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
import apg.ast.NonTerminalNode;
import gblibx.Util;

import java.util.HashMap;
import java.util.Map;

import static apg.analyzer.Util.error;

public class Analyze {
    public static Analyze analyze(Node ast) {
        return new Analyze(ast).analyze();
    }

    public Analyze(Node ast) {
        __ast = ast;
    }

    public Analyze analyze() {
        Util.<NonTerminalNode, Node>downcast(__ast).getNodes().stream()
                .map(n -> new Production(n))
                .forEach(p -> {
                    final String name = p.getName();
                    if (__productionByName.containsKey(name)) {
                        redefinedError(p);
                    } else {
                        __productionByName.put(name, p);
                    }
                });
        //todo
        if (0 < __errorCnt) {
            error(true, "%d error(s) precludes further processing", __errorCnt);
        }
        return this;
    }

    private void redefinedError(Production here) {
        final Production previous = __productionByName.get(here.getName());
        error("%s: redefines '%s': previously defined (%s)",
                here.getLocation(), here.getName(), previous.getLocation());
        __errorCnt++;
    }

    private final Node __ast;
    private final Map<String, Production> __productionByName = new HashMap<>();
    private int __errorCnt = 0;
}
