/* Bantam Java Compiler and Language Toolset.

   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and 
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.

   The Bantam Java toolset is distributed under the following 
   conditions:

     You may make copies of the toolset for your own use and 
     modify those copies.

     All copies of the toolset must retain the author names and 
     copyright notice.

     You may not sell the toolset or distribute it in 
     conjunction with a commerical product or service without 
     the expressed written consent of the authors.

   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS 
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE 
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
   PARTICULAR PURPOSE. 
*/

package proj15KeithHardyLiLian.bantam.codegenmips;

import proj15KeithHardyLiLian.bantam.ast.ASTNode;
import proj15KeithHardyLiLian.bantam.ast.Program;
import proj15KeithHardyLiLian.bantam.semant.ClassNameVisitor;
import proj15KeithHardyLiLian.bantam.semant.StringConstantsVisitor;
import proj15KeithHardyLiLian.bantam.util.*;
import proj15KeithHardyLiLian.bantam.util.Error;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Map;

/**
 * The <tt>MipsCodeGenerator</tt> class generates mips assembly code
 * targeted for the SPIM or Mars emulators.
 * <p/>
 * This class is incomplete and will need to be implemented by the student.
 */
public class MipsCodeGenerator
{
    /**
     * Root of the class hierarchy tree
     */
    private ClassTreeNode root;

    /**
     * Print stream for output assembly file
     */
    private PrintStream out;

    /**
     * Assembly support object (using Mips assembly support)
     */
    private MipsSupport assemblySupport;

    /**
     * Boolean indicating whether garbage collection is enabled
     */
    private boolean gc = false;

    /**
     * Boolean indicating whether optimization is enabled
     */
    private boolean opt = false;

    /**
     * Boolean indicating whether debugging is enabled
     */
    private boolean debug = false;

    /**
     * for recording any errors that occur.
     */
    private ErrorHandler errorHandler;

    /**
     * MipsCodeGenerator constructor
     *
     * @param errorHandler ErrorHandler to record all errors that occur
     * @param gc      boolean indicating whether garbage collection is enabled
     * @param opt     boolean indicating whether optimization is enabled
     */
    public MipsCodeGenerator(ErrorHandler errorHandler, boolean gc, boolean opt) {
        this.gc = gc;
        this.opt = opt;
        this.errorHandler = errorHandler;
    }

    /**
     * Generate assembly file
     * <p/>
     * In particular, you will need to do the following:
     * 1 - start the data section
     * 2 - generate data for the garbage collector
     * 3 - generate string constants
     * 4 - generate class name table
     * 5 - generate object templates
     * 6 - generate dispatch tables
     * 7 - start the text section
     * 8 - generate initialization subroutines
     * 9 - generate user-defined methods
     * See the lab manual for the details of each of these steps.
     *
     * @param root    root of the class hierarchy tree
     * @param outFile filename of the assembly output file
     */
    public void generate(ClassTreeNode root, String outFile) {
        this.root = root;

        // set up the PrintStream for writing the assembly file.
        try {
            this.out = new PrintStream(new FileOutputStream(outFile));
            this.assemblySupport = new MipsSupport(out);
        } catch (IOException e) {
            // if don't have permission to write to file then throw an exception
            errorHandler.register(Error.Kind.CODEGEN_ERROR, "IOException when writing " +
                    "to file: " + outFile);
            throw new CompilationException("Couldn't write to output file.");
        }

        // comment out
        //throw new RuntimeException("MIPS code generator unimplemented");

        // add code here...
        //Header for MIPS file
        this.out.println("#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian");
        this.out.println("#Date: " + LocalDate.now());
        this.out.println("#Compiled From Source: " + root.getASTNode().getFilename());

        //start data section
        this.assemblySupport.genDataStart();
        this.out.println("gc_flag:");
        int gc;
        if(this.gc){
            gc = -1;
        }else{
            gc = 0;
        }
        this.assemblySupport.genWord(Integer.toString(gc));
        this.out.println();

        this.generateStringConstants(root.getASTNode());

        this.generateClassNameTable(root.getASTNode());

        this.generateObjectTemplates(root.getASTNode(),root);

        this.generateDispatchTables(root.getASTNode(),root);

    }

    private void generateStringConstants(ASTNode root){
        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();
        Map<String,String> stringConstants = stringConstantsVisitor.getStringConstants((Program)root);

        for(Map.Entry<String,String> entry: stringConstants.entrySet()){
            this.out.println(entry.getValue() + ":");
            this.assemblySupport.genWord("1\t\t# String Identifier");
            this.assemblySupport.genWord("24\t\t# Size of Object in Bytes");
            this.assemblySupport.genWord("String_dispatch_table");
            this.assemblySupport.genWord(Integer.toString(entry.getKey().length()));
            this.assemblySupport.genAscii(entry.getKey());
        }
        this.out.println();

    }

    private void generateClassNameTable(ASTNode root){
        ClassNameVisitor classNameVisitor = new ClassNameVisitor();
        Map<String,String> classNames = classNameVisitor.getClassNames((Program)root);
        int size = classNames.size();
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getValue() + ":");
            this.assemblySupport.genWord("1\t\t# String Identifier");
            this.assemblySupport.genWord("24\t\t# Size of Object in Bytes");
            this.assemblySupport.genWord("String_dispatch_table");
            this.assemblySupport.genWord(Integer.toString(entry.getKey().length()));
            this.assemblySupport.genAscii(entry.getKey());
        }

        this.out.println();

        this.out.println("Class"+Integer.toString(size));
        this.assemblySupport.genWord("1");
        this.assemblySupport.genWord("24");
        this.assemblySupport.genWord("String_dispatch_table");
        this.assemblySupport.genWord("6");
        this.assemblySupport.genAscii("String");
        this.out.println();

        this.out.println("Class"+Integer.toString(size+1));
        this.assemblySupport.genWord("1");
        this.assemblySupport.genWord("24");
        this.assemblySupport.genWord("String_dispatch_table");
        this.assemblySupport.genWord("6");
        this.assemblySupport.genAscii("TextIO");
        this.out.println();

        this.out.println("Class"+Integer.toString(size+2));
        this.assemblySupport.genWord("1");
        this.assemblySupport.genWord("24");
        this.assemblySupport.genWord("String_dispatch_table");
        this.assemblySupport.genWord("6");
        this.assemblySupport.genAscii("Object");
        this.out.println();

        this.out.println("Class"+Integer.toString(size+3));
        this.assemblySupport.genWord("1");
        this.assemblySupport.genWord("20");
        this.assemblySupport.genWord("String_dispatch_table");
        this.assemblySupport.genWord("3");
        this.assemblySupport.genAscii("Sys");
        this.out.println();

        this.out.println("class_name_table:");
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genWord(entry.getValue());
        }
        this.out.println();
    }

    private void generateObjectTemplates(ASTNode root,ClassTreeNode rootNode){
        ClassNameVisitor classNameVisitor = new ClassNameVisitor();
        Map<String,String> classNames = classNameVisitor.getClassNames((Program)root);

        this.assemblySupport.genGlobal("String_template");
        this.assemblySupport.genGlobal("TextIO_template");
        this.assemblySupport.genGlobal("Object_template");
        this.assemblySupport.genGlobal("Sys_template");
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genGlobal(entry.getKey() + "_template");
        }
        this.out.println();

        this.out.println("String_template:");
        this.assemblySupport.genWord("1");
        this.assemblySupport.genWord("16");
        this.assemblySupport.genWord("String_dispatch_table");
        this.assemblySupport.genWord("0");
        this.out.println();

        this.out.println("Object_template:");
        this.assemblySupport.genWord("0");
        this.assemblySupport.genWord("12");
        this.assemblySupport.genWord("Object_dispatch_table");
        this.out.println();

        this.out.println("Sys_template:");
        this.assemblySupport.genWord("2");
        this.assemblySupport.genWord("12");
        this.assemblySupport.genWord("Sys_dispatch_table");
        this.out.println();

        this.out.println("TextIO_template:");
        this.assemblySupport.genWord("4");
        this.assemblySupport.genWord("20");
        this.assemblySupport.genWord("TextIO_dispatch_table");
        this.assemblySupport.genWord("0");
        this.assemblySupport.genWord("0");
        this.out.println();

        Hashtable<String,ClassTreeNode> classMap = rootNode.getClassMap();
        int i = 5;
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getKey()+"_template:");
            if(entry.getKey().equals("Main")){
                this.assemblySupport.genWord("3");
                i--;
            }else{
                this.assemblySupport.genWord(Integer.toString(i));
            }
            SymbolTable fields = classMap.get(entry.getKey()).getVarSymbolTable();
            int size = fields.getSize();
            this.assemblySupport.genWord(Integer.toString(12 + size*4));
            this.assemblySupport.genWord(entry.getKey()+"_dispatch_table");
            for(int j = 0; j< size; j++){
                this.assemblySupport.genWord("0");
            }

            i++;
            this.out.println();
        }
    }

    private void generateDispatchTables(ASTNode root, ClassTreeNode rootNode){
        ClassNameVisitor classNameVisitor = new ClassNameVisitor();
        Map<String,String> classNames = classNameVisitor.getClassNames((Program)root);

        this.assemblySupport.genGlobal("String_dispatch_table");
        this.assemblySupport.genGlobal("TextIO_dispatch_table");
        this.assemblySupport.genGlobal("Object_dispatch_table");
        this.assemblySupport.genGlobal("Sys_dispatch_table");
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genGlobal(entry.getKey() + "_dispatch_table");
        }

        this.out.println("String_dispatch_table:");
        this.assemblySupport.genWord("Object.clone");
        this.assemblySupport.genWord("String.equals");
        this.assemblySupport.genWord("String.toString");
        this.assemblySupport.genWord("String.length");
        this.assemblySupport.genWord("String.substring");
        this.assemblySupport.genWord("String.concat");

        this.out.println("Object_dispatch_table:");
        this.assemblySupport.genWord("Object.clone");
        this.assemblySupport.genWord("Object.equals");
        this.assemblySupport.genWord("Object.toString");

        this.out.println("Sys_dispatch_table:");
        this.assemblySupport.genWord("Object.clone");
        this.assemblySupport.genWord("Object.equals");
        this.assemblySupport.genWord("Object.toString");
        this.assemblySupport.genWord("Sys.exit");
        this.assemblySupport.genWord("Sys.time");
        this.assemblySupport.genWord("Sys.random");

        this.out.println("TextIO_dispatch_table:");
        this.assemblySupport.genWord("Object.clone");
        this.assemblySupport.genWord("Object.equals");
        this.assemblySupport.genWord("Object.toString");
        this.assemblySupport.genWord("TextIO.readStdin");
        this.assemblySupport.genWord("TextIO.readFile");
        this.assemblySupport.genWord("TextIO.writeStdout");
        this.assemblySupport.genWord("TextIO.writeStderr");
        this.assemblySupport.genWord("TextIO.writeFile");
        this.assemblySupport.genWord("TextIO.getString");
        this.assemblySupport.genWord("TextIO.getInt");
        this.assemblySupport.genWord("TextIO.putString");
        this.assemblySupport.genWord("TextIO.putInt");

        Hashtable<String,ClassTreeNode> classMap = rootNode.getClassMap();
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getKey()+"_dispatch_table:");
            this.assemblySupport.genWord("Object.clone");
            this.assemblySupport.genWord("Object.equals");
            this.assemblySupport.genWord("Object.toString");
            SymbolTable methods = classMap.get(entry.getKey()).getMethodSymbolTable();

        }

    }

    public static void main(String[] args) {
        // ... add testing code here ...
    }
}