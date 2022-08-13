package com.limo.limec.parser;

public class Nodes {

    public interface Node { }

    public record PureASM(String[] lines) implements Node { }
    public record StringNode(String name, Node[] value) implements Node { }
    public record ByteNode(String name, int value) implements Node { }
    public record CharNode(String name, char value) implements Node { }
    public record VariableUseNode(String name) implements Node { }
    public record DisposeNode(String name) implements Node { }
    public record CompilerNode(String name, Node[] args) implements Node { }
    public record EndNode() implements Node { }
}
