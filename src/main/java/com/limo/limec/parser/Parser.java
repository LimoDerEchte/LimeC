package com.limo.limec.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Parser {
    private List<Character> numberRegex = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private List<Character> hexNumberRegex = List.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f');
    private char[] chars;
    private int index;
    private boolean includeSection;
    private Map<String, String[]> compFuncs;
    private String libPath;
    private boolean asmArea;
    private List<String> asmLines = new ArrayList<>();
    public List<Nodes.Node> nodes;

    public void parse(List<String> input, String libPath) {
        this.libPath = libPath;
        asmArea = false;
        includeSection = true;
        nodes = new ArrayList<>();
        compFuncs = new HashMap<>();
        List<Exception.ParsingException> exceptions = new ArrayList<>();
        for (String line : input) {
            try {
                parseline(line);
            } catch (Exception.ParsingException e) {
                exceptions.add(e);
            }
        }
        if(exceptions.size() > 0) {
            System.out.println("Parsing failed with following problems:");
            for(Exception.ParsingException e : exceptions)
                System.out.println(e);
        }
    }

    public void includeParse(List<String> input) throws Exception.ParsingException {
        List<Exception.ParsingException> exceptions = new ArrayList<>();
        for (String line : input) {
            try {
                parseline(line);
            } catch (Exception.ParsingException e) {
                exceptions.add(e);
            }
        }
        includeSection = true;
        if(exceptions.size() > 0) {
            List<String> exStr = new ArrayList<>();
            exceptions.forEach(e -> exStr.add(e.toString()));
            throw new Exception.ParsingException(String.join("\n", exStr));
        }
    }

    public void parseline(String line) throws Exception.ParsingException {
        if(line.trim().isEmpty())
            return;
        index = 0;
        chars = line.toCharArray();
        String tmp = "";
        skipSpaces();
        if(asmArea) {
            String l = "";
            while (index < chars.length) {
                l += chars[index];
                index++;
            }
            if(l.startsWith("endASM")) {
                asmArea = false;
                String[] asm = new String[asmLines.size()];
                asmLines.toArray(asm);
                nodes.add(new Nodes.PureASM(asm));
            }else
                asmLines.add(l);
        }else {
            while (index < chars.length) {
                if (chars[index] == ' ') {
                    index++;
                    break;
                } else if (chars[index] == '#') {
                    return;
                } else
                    tmp += chars[index];
                index++;
            }
            boolean include = false;
            switch (tmp) {
                case "var":
                    parseVar();
                    break;
                case "dis":
                    parseDispose();
                    break;
                case "include":
                    if(includeSection) {
                        include = true;
                        parseInclude();
                    } else
                        throw new Exception.ParsingException("Includes have to be at the top of the file");
                    break;
                case "compFunc":
                    parseCompilerFunction();
                    break;
                case "end":
                    nodes.add(new Nodes.EndNode());
                    break;
                case "useASM":
                    asmArea = true;
                    break;
                case "endASM":
                    throw new Exception.ParsingException("You cant use 'endasm' outside an asm block");
                default:
                    if(compFuncs.containsKey(tmp)) {
                        parseCompFuncUse(tmp);
                    }else
                        throw new Exception.ParsingException("Unknown function: " + tmp);
            }
            includeSection = include;
        }
    }

    public void parseCompFuncUse(String tmp) throws Exception.ParsingException {
        List<Nodes.Node> objects = new ArrayList<>();
        for (String type : compFuncs.get(tmp)) {
            skipSpaces();
            if(type.equalsIgnoreCase("string"))
                if(chars[index] == '"')
                    objects.add(new Nodes.StringNode("", parseNodeString()));
                else
                    throw new Exception.ParsingException("A string has to start with a '\"'");
            else if(type.equalsIgnoreCase("byte")) {
                if(numberRegex.contains(chars[index]))
                    objects.add(new Nodes.ByteNode("", parseNumber()));
                else
                    throw new Exception.ParsingException("An argument of a function had a wrong argument type");
            }
            index++;
            if(index >= chars.length)
                break;
        }
        if(objects.size() != compFuncs.get(tmp).length)
            throw new Exception.ParsingException("Invalid arguments for compiler function '%s'".formatted(tmp));
        Nodes.Node[] objs = new Nodes.Node[objects.size()];
        objects.toArray(objs);
        nodes.add(new Nodes.CompilerNode(tmp, objs));
    }

    public void parseCompilerFunction() throws Exception.ParsingException {
        skipSpaces();
        if(chars[index] == '"') {
            String text = parseString();
            String name = "";
            List<String> argTypes = new ArrayList<>();
            for(String s : text.split(" ")) {
                if(name.isEmpty())
                    name = s;
                else {
                    if(s.equalsIgnoreCase("string") || s.equalsIgnoreCase("byte"))
                        argTypes.add(s.toLowerCase());
                }
            }
            if(name.isEmpty())
                throw new Exception.ParsingException("Compiler Functions have to have a name!");
            String[] argTypesArr = new String[argTypes.size()];
            argTypes.toArray(argTypesArr);
            compFuncs.put(name, argTypesArr);
        }else
            throw new Exception.ParsingException("Compiler Functions have to be used with a string");
    }

    public void parseInclude() throws Exception.ParsingException {
        skipSpaces();
        if(chars[index] == '"') {
            String path = parseString() + ".lc";
            File file = Path.of(libPath, path).toFile();
            if(file.exists()) {
                try {
                    includeParse(Files.readAllLines(file.toPath()));
                    System.out.println("Included file '%s'".formatted(path));
                } catch (IOException e) {
                    throw new Exception.ParsingException("The included file '%s' doesn't exist".formatted(path));
                }
            }else
                throw new Exception.ParsingException("The included file '%s' doesn't exist".formatted(path));
        }else
            throw new Exception.ParsingException("Includes have to be used with a string");
    }

    public void parseDispose() throws Exception.ParsingException {
        skipSpaces();
        String text = "";
        while (index < chars.length) {
            if(chars[index] == ' ')
                break;
            else
                text += chars[index];
            index++;
        }
        if(text.isEmpty())
            throw new Exception.ParsingException("Dispose has to be used with a variable name");
        nodes.add(new Nodes.DisposeNode(text));
    }

    public void parseVar() throws Exception.ParsingException {
        skipSpaces();
        String name = "";
        boolean wasEqualsChar = false;
        while (index < chars.length) {
            if(wasEqualsChar) {
                if(chars[index] != ' ') {
                    if(chars[index] == '"') {
                        nodes.add(new Nodes.StringNode(name, parseNodeString()));
                        return;
                    }else if(numberRegex.contains(chars[index])) {
                        int val = parseNumber();
                        if(val >= 0 && val < 256) {
                            nodes.add(new Nodes.ByteNode(name, val));
                            return;
                        }
                        throw new Exception.ParsingException("Bytes have to be a value between 0 and 255");
                    }else
                        throw new Exception.ParsingException("Invalid datatype for variable " + name);
                }
            }else if(chars[index] == '=') {
                wasEqualsChar = true;
            }else if(chars[index] != ' '){
                name += chars[index];
            }
            index++;
        }
        throw new Exception.ParsingException("A variable definition is invalid");
    }

    public Nodes.Node[] parseNodeString() throws Exception.ParsingException {
        List<Nodes.Node> nodes = new ArrayList<>();
        String text = parseString();
        boolean inVarParse = false;
        String varName = "";
        for(char c : text.toCharArray()) {
            if(!inVarParse && c == '%') {
                inVarParse = true;
                varName = "";
            }else if(inVarParse && c == '%') {
                inVarParse = false;
                nodes.add(new Nodes.VariableUseNode(varName));
            }else if(inVarParse)
                varName += c;
            else
                nodes.add(new Nodes.CharNode("", c));
        }
        if(inVarParse)
            throw new Exception.ParsingException("A variable use has to end with a '%'");
        Nodes.Node[] ret = new Nodes.Node[nodes.size()];
        nodes.toArray(ret);
        return ret;
    }

    public String parseString() throws Exception.ParsingException {
        index++;
        String ret = "";
        boolean lastWasBackslash = false;
        while (index < chars.length) {
            if(chars[index] == '"' && !lastWasBackslash)
                break;
            else if(chars[index] == '\\' && !lastWasBackslash)
                lastWasBackslash = true;
            else {
                ret += chars[index];
                lastWasBackslash = false;
            }
            index++;
        }
        if(index >= chars.length)
            throw new Exception.ParsingException("A string has to end with a '\"'");
        index++;
        return ret;
    }

    public int parseNumber() throws Exception.ParsingException {
        String text = "";
        int type = 0;
        while (index < chars.length) {
            if(chars[index] != '0')
                break;
            index++;
        }
        while (index < chars.length) {
            if(type == 0 && chars[index] == 'b')
                type = 1;
            else if(type == 0 && chars[index] == 'x')
                type = 2;
            else if(chars[index] == ' ')
                break;
            else if(type == 1) {
                if(chars[index] == '0' || chars[index] == '1')
                    text += chars[index];
                else
                    throw new Exception.ParsingException("Invalid regex for number");
            } else if(type == 2) {
                if(hexNumberRegex.contains(chars[index]))
                    text += chars[index];
                else
                    throw new Exception.ParsingException("Invalid regex for number");
            } else if(numberRegex.contains(chars[index]))
                text += chars[index];
            else
                throw new Exception.ParsingException("Invalid regex for number");
            index++;
        }
        if(text.isEmpty())
            return 0;
        if(type == 0)
            return Integer.parseInt(text);
        else if(type == 1)
            return Integer.parseInt(text, 2);
        else if (type == 2)
            return Integer.parseInt(text.toLowerCase(), 16);
        throw new Exception.ParsingException("Invalid regex for number");
    }

    private void skipSpaces() {
        while (index < chars.length) {
            if(chars[index] != ' ')
                break;
            index++;
        }
    }
}
