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
import proj15KeithHardyLiLian.bantam.semant.StringConstantsVisitor;
import proj15KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj15KeithHardyLiLian.bantam.util.CompilationException;
import proj15KeithHardyLiLian.bantam.util.Error;
import proj15KeithHardyLiLian.bantam.util.ErrorHandler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.HashMap;
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
        this.out.println("\t.word\t" + gc);
        this.out.println();

        this.generateStringConstants(root.getASTNode());
    }

    public void generateStringConstants(ASTNode root){
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

    public static void main(String[] args) {
        // ... add testing code here ...
    }
}
