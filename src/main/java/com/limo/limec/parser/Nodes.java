package com.limo.limec.parser;

import java.util.*;

public class Nodes {

    public interface Node { }

    public record PureASM(String[] lines) implements Node { }
    public record StringNode(String name, Node[] value) implements Node { }
    public record ByteNode(String name, int value) implements Node { }
    public record CharNode(String name, char value) implements Node { }
    public record VariableUseNode(String name) implements Node { }
    public record DisposeNode(String name) implements Node { }
    public record CompilerNode(String name, Node[] args) implements Node { }
    public static class LoopNode implements Node {
        public int amount = 0;
        public Node[] nodes;
        boolean hasEnded = false;
        public List<Node> nodeBuild = new ArrayList<>();
        public LoopNode(int amount) {
            this.amount = amount;
        }
        public void build() {
            nodes = new Node[nodeBuild.size()];
            nodeBuild.toArray(nodes);
            hasEnded = true;
        }
        public void addNode(Node node) {
            if(nodeBuild.size() == 0) {
                nodeBuild.add(node);
                return;
            }
            Node n = nodeBuild.get(nodeBuild.size() - 1);
            if(n instanceof LoopNode && !((LoopNode)n).hasEnded)
                ((LoopNode)n).addNode(node);
            else if(node instanceof EndLoopNode)
                build();
            else
                nodeBuild.add(node);
        }
    }
    public record EndLoopNode() implements Node { }
    public record EndNode() implements Node { }
}
