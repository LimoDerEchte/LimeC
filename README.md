***Please note, that LimeC is wip without an eta so stay tuned. It will get releases and better tutorials later too.***

# LimeC
LimeC is a compiled programming language used primarily to program Minecraft Redstone Computers.

## Compilers
These are all the compilers that were currently made / are being made.
| Name | Description | Progress | Authors |
| --- | --- | --- | --- |
| Runtime | Runs programs instead of compiling them | 100% | LimoDerEchte |

## Examples
This is an example for using the Runtime Compiler
```s
# including the runtime library (libs/runtime is a path to libs/runtime.lc)
include "libs/runtime"

# Printing to Console (print is contained within the runtime library)
print "Hello Son"

# Using variables (define, use, dispose)
var name = "John"
print "Hello, %name%!"
dis name

# Using Java code (useASM starts an assembly code block & the runtime compiler interprets this as java code)
useASM
javax.swing.JFrame frame = new javax.swing.JFrame("Lol");
frame.setSize(300, 300);
frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
frame.add(new javax.swing.JLabel("Hello"));
frame.setVisible(true);
endASM
```
