package com.limo.limec.compiler;

import com.limo.limec.parser.Nodes;

public interface Compiler {
    void compile(Nodes.Node[] nodes);
    String getVersion();
}
