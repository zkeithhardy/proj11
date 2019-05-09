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

package proj18KeithHardyLiLian.bantam.codegenmips;

import proj18KeithHardyLiLian.bantam.ast.*;
import proj18KeithHardyLiLian.bantam.parser.Parser;
import proj18KeithHardyLiLian.bantam.semant.ClassNameVisitor;
import proj18KeithHardyLiLian.bantam.semant.SemanticAnalyzer;
import proj18KeithHardyLiLian.bantam.semant.StringConstantsVisitor;
import proj18KeithHardyLiLian.bantam.util.*;
import proj18KeithHardyLiLian.bantam.util.Error;

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

    private HashMap<String, ArrayList<String>> dispatchTableMap = new HashMap<>();

    private ArrayList<String> idTable = new ArrayList<>();


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

        //create id table from inheritance tree
        this.addChildren(this.classMap.get("Object"));

        this.addArrayChildren(this.classMap.get("Object[]"));

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

        Map<String, String> stringNameMap=this.generateStringConstants(rootAST,classNames);

        // Step 4
        this.generateClassNameTable(classNames);
        // Step 5
        this.generateObjectTemplates(classNames);
        // Step 6
        this.generateDispatchTables(classNames);

        // Step 7-8
        //start text section
        this.assemblySupport.genTextStart();

        TextGeneratorVisitor textGeneratorVisitor = new TextGeneratorVisitor(this.out,this.assemblySupport,
                classMap, dispatchTableMap,idTable);

        // create the inits for default classes
        for(int i = 0; i < idTable.size();i++){
            if(idTable.get(i).endsWith("[]") && !idTable.get(i).equals("Object[]")){
                continue;
            }
            this.assemblySupport.genLabel(idTable.get(i) + "_init");
            textGeneratorVisitor.generateMethodPrologue();
            if(idTable.get(i).equals("Object")||idTable.get(i).equals("String")||idTable.get(i).equals("TextIO")
                    ||idTable.get(i).equals("Sys")) {
                if(idTable.get(i).equals("String") || idTable.get(i).equals("Integer") || idTable.get(i).equals("Boolean")){
                    this.assemblySupport.genLoadImm("$v0", 0);
                    this.assemblySupport.genStoreWord("$v0", 12, "$a0");
                }
                else if(idTable.get(i).equals("TextIO")){
                    this.assemblySupport.genLoadImm("$v0", 0);
                    this.assemblySupport.genStoreWord("$v0", 12, "$a0");
                    this.assemblySupport.genLoadImm("$v0", 1);
                    this.assemblySupport.genStoreWord("$v0", 16, "$a0");
                }
            }else{
                //generate the fields
                ClassTreeNode tempNode= this.classMap.get(idTable.get(i));
                LinkedList<ClassTreeNode> parents= new LinkedList<>();
                while (tempNode.getParent()!=null){
                    tempNode=tempNode.getParent();
                    parents.addFirst(tempNode);
                }
                for(ClassTreeNode tempParent: parents){
                    this.assemblySupport.genDirCall(tempParent.getASTNode().getName()+ "_init");
                }
                textGeneratorVisitor.generateFieldInitialization(this.classMap.get(idTable.get(i)).getASTNode(),stringNameMap);
            }
            this.assemblySupport.genMove("$v0","$a0");
            textGeneratorVisitor.generateMethodEpilogue(0);

        }
        //Step 9: generate User Defined Methods
        textGeneratorVisitor.generateTextSection(rootAST);

    }

    /**
     * a recursive helper function to build the class id table
     * adds all the class to the idtable in the order that parent classes are followed by child classes
     * @param root the root
     */
    private void addChildren(ClassTreeNode root){
        this.idTable.add(root.getName());
        for(Iterator<ClassTreeNode> children = root.getChildrenList();children.hasNext();){
            ClassTreeNode child = children.next();
            this.addChildren(child);
        }

    }

    private void addArrayChildren(ClassTreeNode root){
        int index = idTable.indexOf(root.getName()) + 1;
        ArrayList<String> temp = (ArrayList<String>) idTable.clone();
        for(String id : temp){
            if(id.equals("Object") || id.equals("Object[]") || id.equals("TextIO") || id.equals("Sys")){
                continue;
            }
            idTable.add(index, id + "[]");
            index ++;
        }

    }


    /**
     * Generates the string constants
     * @param rootAST the ast root
     */
    private Map<String, String> generateStringConstants(Program rootAST,Map<String,String> classNames){
        this.assemblySupport.genComment("String Constants:");

        for(Map.Entry<String,String> entry: classNames.entrySet()){

            this.out.println(entry.getValue() + ":");
            this.assemblySupport.genWord("1\t\t# String Identifier");

            int length = 17 + entry.getKey().length();
            length = length + (4-length%4)%4;
            this.assemblySupport.genWord(length+"\t\t# Size of Object in Bytes");

            this.assemblySupport.genWord("String_dispatch_table");
            this.assemblySupport.genWord(Integer.toString(entry.getKey().length()));
            this.assemblySupport.genAscii(entry.getKey());
        }

        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();
        Map<String,String> stringConstants = stringConstantsVisitor.getStringConstants(rootAST);

        for(Map.Entry<String,String> entry: stringConstants.entrySet()){
            if(classNames.containsKey(entry.getKey())){
                stringConstants.put(entry.getKey(),classNames.get(entry.getKey()));
                continue;
            }
            this.out.println(entry.getValue() + ":");
            this.assemblySupport.genWord("1\t\t# String Identifier");

            int length = 17 + entry.getKey().length();
            length = length + (4-length%4)%4;
            this.assemblySupport.genWord(Integer.toString(length) +"\t\t# Size of Object in Bytes");
            this.assemblySupport.genWord("String_dispatch_table");
            this.assemblySupport.genWord(Integer.toString(entry.getKey().length()));
            this.assemblySupport.genAscii(entry.getKey());
        }
        this.out.println();
        return stringConstants;
    }

    /**
     * Generates the class string templates and the class name table
     * @param classNames map of classes to their label generated by ClassName Visitor
     */
    private void generateClassNameTable(Map<String,String> classNames){
        this.out.println();

        this.out.println("class_name_table:");
        for(int i = 0; i < this.idTable.size();i++){
            this.assemblySupport.genWord(classNames.get(this.idTable.get(i)));
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

        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getKey()+"_template:");
            this.assemblySupport.genWord(Integer.toString(this.idTable.indexOf(this.classMap.get(entry.getKey())))+"\t\t# Class ID");

            SymbolTable fields = this.classMap.get(entry.getKey()).getVarSymbolTable();

            //subtract 2*fields.getCurrScopeLevel() because symbol table includes
            //this and super which we do not need to count here.
            int size = fields.getSize() - 2*fields.getCurrScopeLevel();
            this.assemblySupport.genWord(Integer.toString(12 + size*4)+"\t\t# Size of Object in Bytes");
            this.assemblySupport.genWord(entry.getKey()+"_dispatch_table");

            for(int j = 0; j< size; j++){
                this.assemblySupport.genWord("0");
            }
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

        for(Map.Entry<String,String> entry: classNames.entrySet()){
            this.out.println(entry.getKey()+"_dispatch_table:");
            ClassTreeNode tempNode = classMap.get(entry.getKey());
            LinkedHashMap<String,String> methodNames =
                    new LinkedHashMap<>(0,0.75f,false);
            this.addMethods(tempNode, methodNames);
            this.dispatchTableMap.put(entry.getKey(),new ArrayList<>(methodNames.keySet()));
            for(Map.Entry<String,String> methodEntry: methodNames.entrySet()){
                this.assemblySupport.genWord(methodEntry.getValue()+ "." + methodEntry.getKey());
            }
        }

        this.out.println();

    }

    /**
     * adds methods to Hashmap from furthest parent down
     * @param tempNode current Class
     * @param methodNames HashMap to store all methods for each class
     *                    Keys are names of methods, values are class they come from.
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
