package com.limo.limec.parser;

public class Nodes {

    public interface Node { }

    public record PureASM(String[] lines) implements Node { }
    public record StringNode(String name, String value) implements Node { }
    public record ByteNode(String name, int value) implements Node { }
    public record CharNode(Character name, int value) implements Node { }
    public record DisposeNode(String name) implements Node { }
    public record CompilerNode(String name, Object[] args) implements Node { }
    public record EndNode() implements Node { }
}
