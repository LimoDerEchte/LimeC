package com.limo.limec.compiler;

import com.limo.limec.parser.Nodes;
import pl.joegreen.lambdaFromString.LambdaCreationException;
import pl.joegreen.lambdaFromString.LambdaFactory;
import pl.joegreen.lambdaFromString.TypeReference;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Runtime implements Compiler {
    public Map<String, Nodes.Node> vars = new HashMap<>();

    @Override
    public void compile(Nodes.Node[] nodes) {
        for (Nodes.Node node : nodes) {
            String nodeType = node.getClass().getSimpleName();
            switch (nodeType) {
                case "CompilerNode" -> {
                    Nodes.CompilerNode cn = (Nodes.CompilerNode) node;
                    if ("print".equals(cn.name())) {
                        System.out.println(cn.args()[0]);
                    } else {
                        System.out.println("Compiler Function doesn't exist: " + cn.name());
                    }
                }
                case "StringNode" -> {
                    Nodes.StringNode sn = (Nodes.StringNode) node;
                    vars.put(sn.name(), sn);
                }
                case "ByteNode" -> {
                    Nodes.ByteNode bn = (Nodes.ByteNode) node;
                    vars.put(bn.name(), bn);
                }
                case "DisposeNode" -> {
                    Nodes.DisposeNode dn = (Nodes.DisposeNode) node;
                    vars.remove(dn.name());
                }
                case "PureASM" -> {
                    Nodes.PureASM pa = (Nodes.PureASM) node;
                    runCode(String.join("\n", pa.lines()));
                }
                case "EndNode" -> System.exit(0);
                default -> System.out.println("Node type not available for runtime: " + nodeType);
            }
        }
    }

    private void runCode(String text) {
        try {
            text = "ign -> { " + text + " return 0; }";
            LambdaFactory lf = LambdaFactory.get();
            Function<Integer, Integer> func = lf.createLambda(text, new TypeReference<>(){});
            func.apply(0);
        } catch (LambdaCreationException e) {
            e.printStackTrace();
            System.out.println("There was a problem running a PureASM node");
        }
    }

    @Override
    public String getVersion() {
        return "Alpha 1";
    }
}