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
import apg.parser.TokenCode;
import gblibx.Util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static apg.analyzer.Util.error;
import static apg.analyzer.Util.warn;
import static java.util.Objects.isNull;

public class Analyze {
    public static Analyze analyze(Node ast) {
        return new Analyze(ast).analyze();
    }

    public Analyze(Node ast) {
        __ast = ast;
    }

    private Analyze analyze() {
        Util.<NonTerminalNode, Node>downcast(__ast).getNodes().stream()
                .map(Production::new)
                .forEach(p -> {
                    final String name = p.getName();
                    if (name.startsWith("_")) {
                        lerror("%s: '%s': production definition ignored since name starts with '_'",
                                p.getLineLocation(), name);
                    } else {
                        __firstProduction = (isNull(__firstProduction)) ? p : __firstProduction;
                        if (__productionByName.containsKey(name)) {
                            redefinedError(p);
                        } else {
                            __productionByName.put(name, p);
                        }
                    }
                });
        checkDefined()
                .checkNotUsed();
        if (0 < __errorCnt) {
            error(true, "%d error(s) precludes further processing", __errorCnt);
        }
        return this;
    }

    /**
     * Check that all reference production(s) are defined.
     *
     * @return this object.
     */
    private Analyze checkDefined() {
        return checkDefined(null, __firstProduction.getName());
    }

    private Analyze checkDefined(Production parent, String name) {
        if (__usedProductions.contains(name)) {
            return this;
        }
        __usedProductions.add(name);
        if (!__productionByName.containsKey(name)) {
            if (!name.startsWith("_") && !name.equals("EOF")) {
                lerror("%s: production '%s' references undefined '%s'",
                        parent.getLineLocation(), parent.getName(), name);
            }
        } else {
            final Production prod = __productionByName.get(name);
            for (Alternate alt : prod.getAlternates()) {
                for (Node anode : alt.getNodes()) {
                    anode.flatten().stream()
                            .filter(tk -> tk.type == TokenCode.eIdent)
                            .forEach(tk -> {
                                checkDefined(prod, tk.text);
                            });
                }
            }
        }
        return this;
    }

    private Analyze checkNotUsed() {
        __productionByName.keySet().stream().filter(k -> !__usedProductions.contains(k)).forEach(k -> {
            warn("%s: '%s' production is not used", __productionByName.get(k).getLineLocation(), k);
        });
        return this;
    }

    private void redefinedError(Production here) {
        final Production previous = __productionByName.get(here.getName());
        lerror("%s: redefines '%s': previously defined (%s)",
                here.getLineLocation(), here.getName(), previous.getLineLocation());
    }

    private void lerror(String format, Object... args) {
        error(format, args);
        __errorCnt++;
    }

    private final Node __ast;
    private final Map<String, Production> __productionByName = new HashMap<>();
    private int __errorCnt = 0;
    private Production __firstProduction = null;
    private Set<String> __usedProductions = new HashSet<>();
}
