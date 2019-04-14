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

package proj16KeithHardyLiLian.bantam.codegenmips;

import proj16KeithHardyLiLian.bantam.ast.*;
import proj16KeithHardyLiLian.bantam.parser.Parser;
import proj16KeithHardyLiLian.bantam.semant.ClassNameVisitor;
import proj16KeithHardyLiLian.bantam.semant.SemanticAnalyzer;
import proj16KeithHardyLiLian.bantam.semant.StringConstantsVisitor;
import proj16KeithHardyLiLian.bantam.util.*;
import proj16KeithHardyLiLian.bantam.util.Error;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.*;
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

    private Hashtable<String,ClassTreeNode> classMap;

    private HashMap<String, ArrayList<String>> userDefinedMethodsMap;

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

        //Header for MIPS file
        this.classMap = root.getClassMap();

        this.out.println("#Authors: Zeb Keith-Hardy, Michael Li, Iris Lian");
        this.out.println("#Date: " + LocalDate.now());

        Class_ mainNode = this.classMap.get("Main").getASTNode();
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] filename = mainNode.getFilename().split(pattern);

        this.out.println("#Compiled From Source: " + filename[filename.length-1]);

        // Step 1-2
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

        // Step 3
        this.generateStringConstants(rootAST);
        // Step 4
        this.generateClassNameTable(classNames);
        // Step 5
        this.generateObjectTemplates(classNames);

        this.userDefinedMethodsMap = new HashMap<>();
        // Step 6
        this.generateDispatchTables(classNames);

        // Step 7-8
        //start text section
        this.assemblySupport.genTextStart();

        TextGeneratorVisitor textGeneratorVisitor = new TextGeneratorVisitor(this.out,this.assemblySupport);
        textGeneratorVisitor.generateTextSection(rootAST);

        for(Map.Entry<String, String> entry: classNames.entrySet()){
            this.assemblySupport.genLabel(entry.getKey()+"_init");

            //generate the field
            ClassTreeNode tempNode= classMap.get(entry.getKey());
            List<ClassTreeNode> parents= new LinkedList<>();
            while (tempNode.getParent()!=null){
                tempNode=tempNode.getParent();
                ((LinkedList<ClassTreeNode>) parents).addFirst(tempNode);
            }
            for(ClassTreeNode tempParent: parents){
                this.assemblySupport.genInDirCall(tempParent.getASTNode().getName()+ "_init");
            }
        }


    }


    /**
     * Generates the string constants
     * @param rootAST the ast root
     */
    private void generateStringConstants(Program rootAST){
        this.assemblySupport.genComment("String Constants:");
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
            this.assemblySupport.genWord(Integer.toString(entry.getKey()
                    .substring(1,entry.getKey().length()-1).length()));
            this.assemblySupport.genAscii(entry.getKey().substring(1,entry.getKey().length()-1));
        }
        this.out.println();

    }

    /**
     * Generates the class string templates and the class name table
     * @param classNames map of classes to their label generated by ClassName Visitor
     */
    private void generateClassNameTable(Map<String,String> classNames){
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

        this.out.println("class_name_table:");
        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genWord(entry.getValue());
        }
        this.out.println();
    }

    /**
     * Generates globals and class templates for objects
     * @param classNames the name of the classes
     */
    private void generateObjectTemplates(Map<String,String> classNames){
        this.assemblySupport.genComment("Object Templates:");

        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genGlobal(entry.getKey() + "_template");
        }
        this.out.println();

        int i = 0;

        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getKey()+"_template:");
            this.assemblySupport.genWord(Integer.toString(i));

            SymbolTable fields = this.classMap.get(entry.getKey()).getVarSymbolTable();

            int size = fields.getSize() - 2*fields.getCurrScopeLevel();
            this.assemblySupport.genWord(Integer.toString(12 + size*4));
            this.assemblySupport.genWord(entry.getKey()+"_dispatch_table");

            for(int j = 0; j< size; j++){
                this.assemblySupport.genWord("0");
            }

            i++;
            this.out.println();
        }
    }

    /**
     * generates globals and dispatch tables
     * @param classNames map of classes to their label generated by ClassName Visitor
     */
    private void generateDispatchTables(Map<String,String> classNames){
        this.assemblySupport.genComment("Dispatch Tables:");

        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.assemblySupport.genGlobal(entry.getKey() + "_dispatch_table");
        }

        String[] builtIns = {"Object", "String", "TextIO", "Sys"};

        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getKey()+"_dispatch_table:");
            ClassTreeNode tempNode = classMap.get(entry.getKey());
            LinkedHashMap<String,String> methodNames =
                    new LinkedHashMap<>(0,0.75f,false);
            this.addMethods(tempNode, methodNames);

            for(Map.Entry<String,String> methodEntry: methodNames.entrySet()){
                this.assemblySupport.genWord(methodEntry.getValue()+ "." + methodEntry.getKey());
            }

            //this part is to get all of the unique user defined methods for each class to create the inits.
            //easiest to do it here where we have access to the hashmap of method names.
            if(!Arrays.asList(builtIns).contains(entry.getKey())){
                ArrayList<String> userDefinedMethods = new ArrayList<>();
                for(Map.Entry<String, String> e: methodNames.entrySet()){
                    if(e.getValue().equals(entry.getKey()))
                        userDefinedMethods.add(e.getKey());
                }
                this.userDefinedMethodsMap.put(entry.getKey(), userDefinedMethods);
            }

        }

        this.out.println();

    }

    /**
     * adds methods to Hashmap from furthest parent down
     * @param tempNode current Class
     * @param methodNames HashMap to store all methods for each class
     */
    private void addMethods(ClassTreeNode tempNode, LinkedHashMap<String,String> methodNames){
        if(tempNode.getParent()!=null){
            this.addMethods(tempNode.getParent(),methodNames);
        }
        MemberList tempMemberList =tempNode.getASTNode().getMemberList();
        for(ASTNode m: tempMemberList){
            if(m instanceof Method){
                String methodName = ((Method) m).getName();

                methodNames.put(methodName,tempNode.getName());
            }
        }
    }


    public static void main(String[] args) {
        ErrorHandler errorHandler = new ErrorHandler();
        MipsCodeGenerator generator = new MipsCodeGenerator(errorHandler, true , true);
        Parser parser = new Parser(errorHandler);
        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);

        for (String inFile : args) {
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String[] filename = inFile.split(pattern);
            System.out.println("\n========== Results for " + filename[filename.length-1] + " =============");
            try {
                errorHandler.clear();
                Program program = parser.parse(inFile);
                ClassTreeNode root = analyzer.analyze(program);
                String outString = inFile.split("\\.")[0] + ".asm";
                generator.generate(root, outString, program);

                if(errorHandler.errorsFound()){
                    List<Error> errors = errorHandler.getErrorList();
                    for (Error error : errors) {
                        System.out.println("\t" + error.toString());
                    }
                }
                else{
                    System.out.println("\n========== Compilation Was Successful  =============");
                }
            } catch (CompilationException ex) {
                System.out.println(" Compilation is unsuccessful");
                if(errorHandler.errorsFound()){
                    List<Error> errors = errorHandler.getErrorList();
                    for (Error error : errors) {
                        System.out.println("\t" + error.toString());
                    }
                }
            }
        }
    }
}
