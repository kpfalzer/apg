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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static apg.analyzer.Util.error;
import static apg.analyzer.Util.info;
import static apg.analyzer.Util.warn;
import static gblibx.Util.toList;
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
                .checkSimpleLeftRecursive()
                .checkIndirectLeftRecursive() //NOTE: always after simple check!
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

    /**
     * Check alternates if simple left-recursive: i.e,, a -> a ... (and nothing more complex).
     * @return this object.
     */
    private Analyze checkSimpleLeftRecursive() {
        __productionByName.values().stream().forEach(prod ->{
            final String name = prod.getName();
            for (Alternate alt : prod.getAlternates()) {
                try {
                    if (alt.setDLR(alt.detectDLR(name)).isDLR()) {
                        info("%s: detected simple direct left recursion on '%s'", alt.flatten().get(0).loc.toString(), name);
                    };
                } catch (Node.InvalidDLR ex) {
                    lerror("%s: complex direct left recursion on '%s' detected",
                            prod.getLineLocation(), name);
                }
            }
        });
        return this;
    }

    private Analyze checkIndirectLeftRecursive() {
        __productionByName.values().stream().forEach(prod ->{
            checkIndirectLeftRecursive(new LinkedList<>(), prod.getName());
        });
        return this;
    }

    private void checkIndirectLeftRecursive(LinkedList<String> xpath, String name) {
        LinkedList<String> path = toList(xpath);
        if (__productionByName.containsKey(name)) {
            path.add(name);
            Production prod = __productionByName.get(name);
            for (Alternate alt : prod.getAlternates()) {
                if (alt.isDLR()) continue;
                String first = alt.getFirstNonTerminalName();
                //skip direct left recursion
                if (isNull(first) || (1 == path.size() && path.get(0).equals(first))) continue;
                if (1 < path.size() && path.get(0).equals(first)) {
                    //ILR detected
                    String spath = String.format("%s -> %s",
                            path.stream().collect(Collectors.joining(" -> ")),
                            first);
                    lerror("Indirect left recursion detected: %s", spath);
                } else {
                    checkIndirectLeftRecursive(path, first);
                }
            }
        }
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
    /**
     * Production by name.
     * We use LinkedHashSet for key iterate order as defined.
     */
    private final Map<String, Production> __productionByName = new LinkedHashMap<>();
    private int __errorCnt = 0;
    private Production __firstProduction = null;
    private Set<String> __usedProductions = new HashSet<>();
}
