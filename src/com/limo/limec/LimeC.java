package com.limo.limec;

import com.limo.limec.compiler.Runtime;
import com.limo.limec.parser.Nodes;
import com.limo.limec.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class LimeC {

    public static void main(String[] args) {
        try {
            System.out.println(" --- Parsing --- ");
            Parser p = new Parser();
            File target = new File(args[0]);
            String path = target.getAbsolutePath();
            p.parse(Files.readAllLines(target.toPath()), path.substring(0, path.length() - target.getName().length()));

            System.out.println("\nUsing Runtime Compiler Version: " + new Runtime().getVersion());
            System.out.println(" --- Compiling --- ");
            if(p.nodes.size() > 0) {
                Nodes.Node[] nodes = new Nodes.Node[p.nodes.size()];
                p.nodes.toArray(nodes);
                new Runtime().compile(nodes);
            }
            /*for(Nodes.Node node : p.nodes) {
                if(node instanceof Nodes.ByteNode)
                    System.out.println(((Nodes.ByteNode) node).name() + " :: " + ((Nodes.ByteNode) node).value());
                if(node instanceof Nodes.StringNode)
                    System.out.println(((Nodes.StringNode) node).name() + " :: " + ((Nodes.StringNode) node).value());
                if(node instanceof Nodes.DisposeNode)
                    System.out.println("Dispose :: " + ((Nodes.DisposeNode) node).name());
                if(node instanceof Nodes.CompilerNode)
                    System.out.println("Compiler :: %s :: %s".formatted(((Nodes.CompilerNode) node).name(), ((Nodes.CompilerNode) node).args().length));
                if(node instanceof Nodes.PureASM)
                    Arrays.stream(((Nodes.PureASM) node).lines()).forEach(e -> System.out.println("ASM :: " + e));
            }*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
