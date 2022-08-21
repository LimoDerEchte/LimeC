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
            compileSingle(node);
        }
    }

    private void compileSingle(Nodes.Node node) {
        String nodeType = node.getClass().getSimpleName();
        switch (nodeType) {
            case "CompilerNode" -> {
                Nodes.CompilerNode cn = (Nodes.CompilerNode) node;
                if ("print".equals(cn.name())) {
                    System.out.println(buildStringNode((Nodes.StringNode) cn.args()[0]));
                } else {
                    System.out.println("Compiler Function doesn't exist: " + cn.name());
                }
            }
            case "StringNode" -> {
                Nodes.StringNode n = (Nodes.StringNode) node;
                vars.put(n.name(), n);
            }
            case "ByteNode" -> {
                Nodes.ByteNode n = (Nodes.ByteNode) node;
                vars.put(n.name(), n);
            }
            case "DisposeNode" -> {
                Nodes.DisposeNode n = (Nodes.DisposeNode) node;
                vars.remove(n.name());
            }
            case "PureASM" -> {
                Nodes.PureASM n = (Nodes.PureASM) node;
                runCode(String.join("\n", n.lines()));
            }
            case "LoopNode" -> {
                Nodes.LoopNode n = (Nodes.LoopNode) node;
                for (int i = 0; i < n.amount; i++)
                    for(Nodes.Node sn : n.nodes)
                        compileSingle(sn);
            }
            case "EndNode" -> System.exit(0);
            default -> System.out.println("Node type not available for runtime: " + nodeType);
        }
    }

    private String buildStringNode(Nodes.StringNode node) {
        String build = "";
        for(Nodes.Node n : node.value()) {
            if(n instanceof Nodes.CharNode) {
                build += ((Nodes.CharNode)n).value();
            } else if (n instanceof Nodes.VariableUseNode) {
                Nodes.Node vun = vars.get(((Nodes.VariableUseNode) n).name());
                if(vun instanceof Nodes.StringNode)
                    build += buildStringNode((Nodes.StringNode) vun);
                else if(vun instanceof Nodes.ByteNode)
                    build += String.valueOf(((Nodes.ByteNode)vun).value());
            }
        }
        return build;
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