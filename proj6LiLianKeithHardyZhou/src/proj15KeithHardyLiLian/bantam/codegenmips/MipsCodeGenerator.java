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

import proj15KeithHardyLiLian.bantam.ast.*;
import proj15KeithHardyLiLian.bantam.semant.ClassNameVisitor;
import proj15KeithHardyLiLian.bantam.semant.StringConstantsVisitor;
import proj15KeithHardyLiLian.bantam.util.*;
import proj15KeithHardyLiLian.bantam.util.Error;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

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

    private int classMapSize;

    private Hashtable<String,ClassTreeNode> classMap;

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
    public void generate(ClassTreeNode root, String outFile, Program rootAST) {
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
        this.classMap = root.getClassMap();

        this.out.println("#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian");
        this.out.println("#Date: " + LocalDate.now());

        Class_ mainNode = this.classMap.get("Main").getASTNode();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] filename = mainNode.getFilename().split(pattern);

        this.out.println("#Compiled From Source: " + filename[filename.length-1]);

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

        ClassNameVisitor classNameVisitor = new ClassNameVisitor();
        Map<String,String> classNames = classNameVisitor.getClassNames(rootAST);
        this.classMapSize = classNames.size();


        this.generateStringConstants(rootAST);

        this.generateClassNameTable(classNames);

        this.generateObjectTemplates(classNames);

        this.generateDispatchTables(classNames);
        System.out.println("after dispatch tables");

    }

    private void generateStringConstants(Program rootAST){
        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();
        Map<String,String> stringConstants = stringConstantsVisitor.getStringConstants(rootAST);

        for(Map.Entry<String,String> entry: stringConstants.entrySet()){
            this.out.println(entry.getValue() + ":");
            this.assemblySupport.genWord("1\t\t# String Identifier");
            int length = 17 + entry.getKey().length();
            double div4 = length/4.0;
            div4 = Math.ceil(div4);
            length = (int)div4 *4;
            this.assemblySupport.genWord(Integer.toString(length) +"\t\t# Size of Object in Bytes");
            this.assemblySupport.genWord("String_dispatch_table");
            this.assemblySupport.genWord(Integer.toString(entry.getKey().substring(1,entry.getKey().length()-1).length()));
            this.assemblySupport.genAscii(entry.getKey().substring(1,entry.getKey().length()-1));
        }
        this.out.println();

    }

    private void generateClassNameTable(Map<String,String> classNames){

//        int size = classNames.size();
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getValue() + ":");
            this.assemblySupport.genWord("1\t\t# String Identifier");

            int length = 17 + entry.getKey().length();
            double div4 = length/4.0;
            div4 = Math.ceil(div4);
            length = (int)div4 *4;
            this.assemblySupport.genWord(length+"\t\t# Size of Object in Bytes");

            this.assemblySupport.genWord("String_dispatch_table");
            this.assemblySupport.genWord(Integer.toString(entry.getKey().length()));
            this.assemblySupport.genAscii(entry.getKey());
        }

        this.out.println();

//        this.out.println("Class"+Integer.toString(size));
//        this.assemblySupport.genWord("1");
//        this.assemblySupport.genWord("24");
//        this.assemblySupport.genWord("String_dispatch_table");
//        this.assemblySupport.genWord("6");
//        this.assemblySupport.genAscii("String");
//        this.out.println();
//
//        this.out.println("Class"+Integer.toString(size+1));
//        this.assemblySupport.genWord("1");
//        this.assemblySupport.genWord("24");
//        this.assemblySupport.genWord("String_dispatch_table");
//        this.assemblySupport.genWord("6");
//        this.assemblySupport.genAscii("TextIO");
//        this.out.println();
//
//        this.out.println("Class"+Integer.toString(size+2));
//        this.assemblySupport.genWord("1");
//        this.assemblySupport.genWord("24");
//        this.assemblySupport.genWord("String_dispatch_table");
//        this.assemblySupport.genWord("6");
//        this.assemblySupport.genAscii("Object");
//        this.out.println();
//
//        this.out.println("Class"+Integer.toString(size+3));
//        this.assemblySupport.genWord("1");
//        this.assemblySupport.genWord("20");
//        this.assemblySupport.genWord("String_dispatch_table");
//        this.assemblySupport.genWord("3");
//        this.assemblySupport.genAscii("Sys");
//        this.out.println();

        this.out.println("class_name_table:");
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genWord(entry.getValue());
//            this.assemblySupport.genWord("Class"+Integer.toString(size));
//            this.assemblySupport.genWord("Class"+Integer.toString(size+1));
//            this.assemblySupport.genWord("Class"+Integer.toString(size+2));
//            this.assemblySupport.genWord("Class"+Integer.toString(size+3));
        }
        this.out.println();
    }

    private void generateObjectTemplates(Map<String,String> classNames){

//        this.assemblySupport.genGlobal("String_template");
//        this.assemblySupport.genGlobal("TextIO_template");
//        this.assemblySupport.genGlobal("Object_template");
//        this.assemblySupport.genGlobal("Sys_template");
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genGlobal(entry.getKey() + "_template");
        }
        this.out.println();

//        Hashtable<String,ClassTreeNode> classMap = rootNode.getClassMap();
        int i = 0;
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getKey()+"_template:");
            this.assemblySupport.genWord(Integer.toString(i));
            SymbolTable fields = this.classMap.get(entry.getKey()).getVarSymbolTable();
            int size = fields.getSize();
            this.assemblySupport.genWord(Integer.toString(12 + size*4));
            this.assemblySupport.genWord(entry.getKey()+"_dispatch_table");
            for(int j = 0; j< size; j++){
                this.assemblySupport.genWord("0");
            }

            i++;
            this.out.println();
        }
//        this.out.println("String_template:");
//        this.assemblySupport.genWord(Integer.toString(this.classMapSize));
//        this.assemblySupport.genWord("16");
//        this.assemblySupport.genWord("String_dispatch_table");
//        this.assemblySupport.genWord("0");
//        this.out.println();
//
//        this.out.println("TextIO_template:");
//        this.assemblySupport.genWord(Integer.toString(this.classMapSize+1));
//        this.assemblySupport.genWord("20");
//        this.assemblySupport.genWord("TextIO_dispatch_table");
//        this.assemblySupport.genWord("0");
//        this.assemblySupport.genWord("0");
//        this.out.println();
//
//        this.out.println("Object_template:");
//        this.assemblySupport.genWord(Integer.toString(this.classMapSize+2));
//        this.assemblySupport.genWord("12");
//        this.assemblySupport.genWord("Object_dispatch_table");
//        this.out.println();
//
//        this.out.println("Sys_template:");
//        this.assemblySupport.genWord(Integer.toString(this.classMapSize+3));
//        this.assemblySupport.genWord("12");
//        this.assemblySupport.genWord("Sys_dispatch_table");
//        this.out.println();
    }

    private void generateDispatchTables(Map<String,String> classNames){

//        this.assemblySupport.genGlobal("String_dispatch_table");
//        this.assemblySupport.genGlobal("TextIO_dispatch_table");
//        this.assemblySupport.genGlobal("Object_dispatch_table");
//        this.assemblySupport.genGlobal("Sys_dispatch_table");
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genGlobal(entry.getKey() + "_dispatch_table");
        }

//        this.out.println("String_dispatch_table:");
//        this.assemblySupport.genWord("Object.clone");
//        this.assemblySupport.genWord("String.equals");
//        this.assemblySupport.genWord("String.toString");
//        this.assemblySupport.genWord("String.length");
//        this.assemblySupport.genWord("String.substring");
//        this.assemblySupport.genWord("String.concat");
//
//        this.out.println("Object_dispatch_table:");
//        this.assemblySupport.genWord("Object.clone");
//        this.assemblySupport.genWord("Object.equals");
//        this.assemblySupport.genWord("Object.toString");
//
//        this.out.println("Sys_dispatch_table:");
//        this.assemblySupport.genWord("Object.clone");
//        this.assemblySupport.genWord("Object.equals");
//        this.assemblySupport.genWord("Object.toString");
//        this.assemblySupport.genWord("Sys.exit");
//        this.assemblySupport.genWord("Sys.time");
//        this.assemblySupport.genWord("Sys.random");
//
//        this.out.println("TextIO_dispatch_table:");
//        this.assemblySupport.genWord("Object.clone");
//        this.assemblySupport.genWord("Object.equals");
//        this.assemblySupport.genWord("Object.toString");
//        this.assemblySupport.genWord("TextIO.readStdin");
//        this.assemblySupport.genWord("TextIO.readFile");
//        this.assemblySupport.genWord("TextIO.writeStdout");
//        this.assemblySupport.genWord("TextIO.writeStderr");
//        this.assemblySupport.genWord("TextIO.writeFile");
//        this.assemblySupport.genWord("TextIO.getString");
//        this.assemblySupport.genWord("TextIO.getInt");
//        this.assemblySupport.genWord("TextIO.putString");
//        this.assemblySupport.genWord("TextIO.putInt");

        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getKey()+"_dispatch_table:");
            ClassTreeNode tempNode = classMap.get(entry.getKey());
            MemberList tempMemberList = tempNode.getASTNode().getMemberList();
            ArrayList<String> methodNames = new ArrayList<>();
            for(ASTNode m: tempMemberList){
                if(m instanceof Method){
                    methodNames.add(tempNode.getName()+"."+((Method) m).getName());
                }
            }
            while(tempNode.getParent()!=null){
                tempNode=tempNode.getParent();
                tempMemberList=tempNode.getASTNode().getMemberList();
                for(ASTNode m: tempMemberList){
                    if(m instanceof Method){
                        methodNames.add(tempNode.getName()+"."+((Method) m).getName());
                    }
                }
            }

            for(String method: methodNames){
                this.assemblySupport.genWord(method);
            }
//            this.assemblySupport.genWord("Object.clone");
//            this.assemblySupport.genWord("Object.equals");
//            this.assemblySupport.genWord("Object.toString");

        }

    }

    public static void main(String[] args) {
        // ... add testing code here ...
    }
}
