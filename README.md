# LimeC
LimeC is a compiled programming language used primarily to program Minecraft Redstone Computers.

## Compilers
These are all the compilers that were currently made / are being made.
| Name | Information | Progress | Link |
| --- | --- | --- | --- |
| Runtime | Runs programs instead of compiling them | 100% | src/main/java/com/limo/limec/compiler/Runtime |

## Examples
This is an example for using the Runtime Compiler
```s
# including the runtime library
include "libs/runtime"

# Printing to Console
print "Hello Son"

# Using variables
var test = "Hello"
dis test

# Using Java code
useASM
javax.swing.JFrame frame = new javax.swing.JFrame("Lol");
frame.setSize(300, 300);
frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
frame.add(new javax.swing.JLabel("Hello"));
frame.setVisible(true);
endASM
```
