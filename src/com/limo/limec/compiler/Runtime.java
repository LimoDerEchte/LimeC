package com.limo.limec.compiler;

import com.limo.limec.parser.Nodes;

import java.util.HashMap;
import java.util.Map;

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
                case "EndNode" -> System.exit(0);
                default -> System.out.println("Node type not available for runtime: " + nodeType);
            }
        }
    }

    @Override
    public String getVersion() {
        return "Alpha 1";
    }
}