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

   Completed by Dale Skrien
   January 2019
*/

package proj15KeithHardyLiLian.bantam.semant;

import proj15KeithHardyLiLian.bantam.ast.*;
import proj15KeithHardyLiLian.bantam.parser.Parser;
import proj15KeithHardyLiLian.bantam.util.*;
import proj15KeithHardyLiLian.bantam.util.Error;

import java.util.*;

/**
 * The <tt>SemanticAnalyzer</tt> class performs semantic analysis.
 * In particular this class is able to perform (via the <tt>analyze()</tt>
 * method) the following tests and analyses: (1) legal inheritence
 * hierarchy (all classes have existing parent, no cycles), (2)
 * legal class member declaration, (3) there is a correct Main class
 * and main() method, and (4) each class member is correctly typed.
 * <p/>
 */
public class SemanticAnalyzer
{

    /**
     * reserved words that are tokens of type ID, but cannot be declared as the
     * names of (a) classes, (b) methods, (c) fields, (d) variables.
     * These words are:  null, this, super, void, int, boolean.
     * However, class names can be used as variable names.
     */
    public static final Set<String> reservedIdentifiers = new HashSet<>(Arrays.asList(
            "null", "this", "super", "void", "int", "boolean"));

    /**
     * Root of the AST
     */
    private Program program;

    /**
     * Root of the class hierarchy tree
     */
    private ClassTreeNode root;

    /**
     * Maps class names to ClassTreeNode objects describing the class
     */
    private Hashtable<String, ClassTreeNode> classMap = new Hashtable<String,
            ClassTreeNode>();

    /**
     * Object for error handling
     */
    private ErrorHandler errorHandler;

    /**
     * Maximum number of inherited and non-inherited fields that can be defined for any
     * one class
     */
    private final int MAX_NUM_FIELDS = 1500;

    /**
     * SemanticAnalyzer constructor
     *
     * @param errorHandler the ErrorHandler to use for reporting errors
     */
    public SemanticAnalyzer(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Analyze the AST checking for semantic errors and annotating the tree
     * Also builds an auxiliary class hierarchy tree
     *
     * @param program root of the AST
     * @return root of the class hierarchy tree (needed for code generation)
     * <p>
     * Must add code to do the following:
     * 1 - add built-in classes in classMap (already done)
     * 2 - add user-defined classes and build the inheritance tree of ClassTreeNodes
     * 3 - build the environment for each class (add class members only) and check
     * that members are declared properly
     * 4 - check that the Main class and main method are declared properly
     * 5 - type check everything
     * See the lab manual for more details on each of these steps.
     */
    public ClassTreeNode analyze(Program program) {
        this.program = program;
        this.classMap.clear();

        // step 1:  add built-in classes to classMap
        addBuiltins();

        //step 2:  add user-defined classes and build the inheritance tree of
        // ClassTreeNodes
        buildInheritanceTree();

        //step 3: build the field and method symbol tables for each ClassTreeNode
        //        Just add the class's fields & methods and not the
        //        inherited ones since the SymbolTable's lookup method checks
        //        the superclasses for you.
        buildFieldAndMethodTables();

        //step 4: check whether there is a Main class with a main method.
        checkForMainClassWithMainMethod();
        System.out.println("found main");
        //step 5:  do type-checking for all expressions.  This includes checking for:
        //         1. two local variables of the same name with overlapping scopes
        //         2. break statements not in loops
        //         3. calling a non-existent method
        //         4. method calls with the wrong number or types of arguments
        //         5. use of an undeclared variable
        doTypeChecking();
        // if errors were found, throw an exception, indicating failure
        if (errorHandler.errorsFound()) {
            throw new CompilationException("Checker errors found.");
        }

        return root;
    }

    public ErrorHandler getErrorHandler() { return errorHandler; }

    private void doTypeChecking() {
        TypeCheckerVisitor visitor = new TypeCheckerVisitor(errorHandler, root);
        visitor.visit(program);
    }

    private void checkForMainClassWithMainMethod() {
        ClassTreeNode mainNode = classMap.get("Main");
        if (mainNode == null) {
            errorHandler.register(Error.Kind.SEMANT_ERROR, "There is no Main class " +
                    "(with a main() method).");
        }
        else {
            Method mainMethod = (Method) mainNode.getMethodSymbolTable().lookup("main");
            if (mainMethod == null || ! mainMethod.getReturnType().equals("void")
                                   || mainMethod.getFormalList().getSize() > 0) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        "There is no main() method in the Main class with " +
                                "no parameters and void return type.");
            }
        }
    }

    private void buildFieldAndMethodTables() {
        /* NOTE:  This should have been implemented as a visitor,
                  such as part of the ClassMapBuilderVisitor
        */
        for (ClassTreeNode treeNode : classMap.values()) {
            SymbolTable fields = treeNode.getVarSymbolTable();
            SymbolTable methods = treeNode.getMethodSymbolTable();
            fields.enterScope();
            fields.add("this", treeNode.getName());
            fields.add("super", (treeNode.getParent() == null ? "" :
                    treeNode.getParent().getName()));
            methods.enterScope();
            MemberList list = treeNode.getASTNode().getMemberList();
            for (ASTNode member : list) {
                if (member instanceof Field) {
                    if (SemanticAnalyzer.reservedIdentifiers.contains(((Field) member).getName())) {
                        errorHandler.register(Error.Kind.SEMANT_ERROR,
                                treeNode.getASTNode().getFilename(),
                                member.getLineNum(), "Class " + treeNode.getName() + " "
                                        + "has a field " + "named: " + ((Field) member).getName()
                                        + ", which is illegal.");
                    }
                    else if (fields.peek(((Field) member).getName()) != null) {
                        errorHandler.register(Error.Kind.SEMANT_ERROR,
                                treeNode.getASTNode().getFilename(), member.getLineNum(),
                                "Class " + treeNode.getName()
                                        + " has two fields of the same name: "
                                        + ((Field) member).getName() + ".");
                    }
                    else {
                        fields.add(((Field) member).getName(), ((Field) member).getType());
                    }
                }
                else { // if(member instanceof Method)
                    if (SemanticAnalyzer.reservedIdentifiers.contains(((Method) member).getName())) {
                        errorHandler.register(Error.Kind.SEMANT_ERROR,
                                treeNode.getASTNode().getFilename(),
                                member.getLineNum(), "Class " + treeNode.getName() + " "
                                        + "has a method named: "
                                        + ((Method) member).getName() + ", which is illegal.");
                    }
                    else if (methods.peek(((Method) member).getName()) != null) {
                        errorHandler.register(Error.Kind.SEMANT_ERROR,
                                treeNode.getASTNode().getFilename(), member.getLineNum(),
                                "Class " + treeNode.getName()
                                        + " has two methods of the same name: "
                                        + ((Method) member).getName() + ".");
                    }
                    else {
                        methods.add(((Method) member).getName(), member);
                    }
                }
            }
        }
    }

    private void buildInheritanceTree() {
        //step 1: add all user-defined classes to classMap
        ClassMapBuilderVisitor visitor = new ClassMapBuilderVisitor(classMap,
                errorHandler);
        visitor.visit(program);

        //step 2: fix parent pointers in all ClassTreeNodes in classMap
        for (ClassTreeNode treeNode : classMap.values()) {
            Class_ astNode = treeNode.getASTNode();
            if (astNode.getName().equals("Object")) {
                continue; //no parent
            }

            ClassTreeNode parentNode = classMap.get(astNode.getParent());
            if (parentNode == null) {
                errorHandler.register(Error.Kind.SEMANT_ERROR, astNode.getFilename(),
                        astNode.getLineNum(), "Superclass " + astNode.getParent() + " " +
                                "of class " + astNode.getName() + " does not exist.");
                treeNode.setParent(classMap.get("Object")); //to allow checking to
                // continue
            }
            else if (astNode.getParent().equals("Sys") || astNode.getParent().equals(
                    "String") || astNode.getParent().equals("TextIO")) {
                errorHandler.register(Error.Kind.SEMANT_ERROR, astNode.getFilename(),
                        astNode.getLineNum(), "Superclass " + astNode.getParent() + " " +
                                "of class " + astNode.getName() + " is not allowed to " +
                                "have" + "subclasses (it is final).");
            }
            else {
                treeNode.setParent(parentNode);
            }
        }

        //step 3: check for cycles in inheritance digraph
        for (ClassTreeNode treeNode : classMap.values()) {
            // use a new HashSet for each node and its ancestors
            HashSet<ClassTreeNode> marked = new HashSet<>();
            while (treeNode != null) {
                if (marked.contains(treeNode)) {
                    errorHandler.register(Error.Kind.SEMANT_ERROR,
                            treeNode.getASTNode().getFilename(),
                            treeNode.getASTNode().getLineNum(),
                            "Class " + treeNode.getName() + " is part of a cycle " + " " +
                                    "of inheritances.");
                    treeNode.getParent().removeChild(treeNode);
                    treeNode.setParent(classMap.get("Object"));
                    classMap.get("Object").addChild(treeNode);
                    break;
                }
                else {
                    marked.add(treeNode);
                    treeNode = treeNode.getParent();
                }
            }
        }
    }

    /**
     * Add built in classes to the class tree
     */
    private void addBuiltins() {
        // create AST node for object
        Class_ astNode = new Class_(-1, "<built-in class>", "Object", null,
                (MemberList) (new MemberList(-1)).addElement(new Method(-1, "Object",
                        "clone", new FormalList(-1),
                        (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                new VarExpr(-1, null, "null"))))).addElement(new Method(-1, "boolean", "equals", (FormalList) (new FormalList(-1)).addElement(new Formal(-1, "Object", "o")), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new ConstBooleanExpr(-1, "false"))))).addElement(new Method(-1, "String", "toString", new FormalList(-1), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new VarExpr(-1, null, "null"))))));
        // create a class tree node for object, save in variable root
        root = new ClassTreeNode(astNode, /*built-in?*/true, /*extendable?*/true,
                classMap);
        // add object class tree node to the mapping
        classMap.put("Object", root);

        // note: String, TextIO, and Sys all have fields that are not shown below.
        // Because these classes cannot be extended and fields are protected, they cannot be
        // accessed by other classes, so they do not have to be included in the AST.

        // create AST node for String
        astNode = new Class_(-1, "<built-in class>", "String", "Object",
                (MemberList) (new MemberList(-1)).addElement(new Field(-1, "int",
                        "length", /*0 by default*/null))
                /* note: str is the character sequence -- no applicable type for a
               character sequence so it is just made an int.  it's OK to
               do this since this field is only accessed (directly) within
               the runtime system */.addElement(new Method(-1, "int", "length",
                                new FormalList(-1),
                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new ConstIntExpr(-1, "0"))))).addElement(new Method(-1, "boolean", "equals", (FormalList) (new FormalList(-1)).addElement(new Formal(-1, "Object", "str")), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new ConstBooleanExpr(-1, "false"))))).addElement(new Method(-1, "String", "toString", new FormalList(-1), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new VarExpr(-1, null, "null"))))).addElement(new Method(-1, "String", "substring", (FormalList) (new FormalList(-1)).addElement(new Formal(-1, "int", "beginIndex")).addElement(new Formal(-1, "int", "endIndex")), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new VarExpr(-1, null, "null"))))).addElement(new Method(-1, "String", "concat", (FormalList) (new FormalList(-1)).addElement(new Formal(-1, "String", "str")), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new VarExpr(-1, null, "null"))))));
        // create class tree node for String, add it to the mapping
        classMap.put("String", new ClassTreeNode(astNode, /*built-in?*/true,
                /*extendable?*/false, classMap));

        // create AST node for TextIO
        astNode = new Class_(-1, "<built-in class>", "TextIO", "Object",
                (MemberList) (new MemberList(-1)).addElement(new Field(-1, "int",
                        "readFD", /*0 by default*/null)).addElement(new Field(-1, "int"
                        , "writeFD", new ConstIntExpr(-1, "1"))).addElement(new Method(-1, "void", "readStdin", new FormalList(-1), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, null)))).addElement(new Method(-1, "void", "readFile", (FormalList) (new FormalList(-1)).addElement(new Formal(-1, "String", "readFile")), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, null)))).addElement(new Method(-1, "void", "writeStdout", new FormalList(-1), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, null)))).addElement(new Method(-1, "void", "writeStderr", new FormalList(-1), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, null)))).addElement(new Method(-1, "void", "writeFile", (FormalList) (new FormalList(-1)).addElement(new Formal(-1, "String", "writeFile")), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, null)))).addElement(new Method(-1, "String", "getString", new FormalList(-1), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new VarExpr(-1, null, "null"))))).addElement(new Method(-1, "int", "getInt", new FormalList(-1), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new ConstIntExpr(-1, "0"))))).addElement(new Method(-1, "TextIO", "putString", (FormalList) (new FormalList(-1)).addElement(new Formal(-1, "String", "str")), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new VarExpr(-1, null, "null"))))).addElement(new Method(-1, "TextIO", "putInt", (FormalList) (new FormalList(-1)).addElement(new Formal(-1, "int", "n")), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new VarExpr(-1, null, "null"))))));
        // create class tree node for TextIO, add it to the mapping
        classMap.put("TextIO", new ClassTreeNode(astNode, /*built-in?*/true,
                /*extendable?*/false, classMap));

        // create AST node for Sys
        astNode = new Class_(-1, "<built-in class>", "Sys", "Object",
                (MemberList) (new MemberList(-1)).addElement(new Method(-1, "void",
                        "exit",
                        (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                "int", "status")),
                        (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                null))))
                /* MC: time() and random() requires modifying SPIM to add a time system
                 call
               (note: random() does not need its own system call although it uses the time
               system call).  We have a version of SPIM with this system call available,
               otherwise, just comment out. (For x86 and jvm there are no issues.)
               */.addElement(new Method(-1, "int", "time", new FormalList(-1),
                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new ConstIntExpr(-1, "0"))))).addElement(new Method(-1, "int", "random", new FormalList(-1), (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1, new ConstIntExpr(-1, "0"))))));
        // create class tree node for Sys, add it to the mapping
        classMap.put("Sys", new ClassTreeNode(astNode, /*built-in?*/true, /*extendable
        ?*/false, classMap));
    }


    public static void main(String[] args) {
        args = new String[]{"testsByDale/FindUsesTest.btm"};//Students/CheckerTestKeithHardyLiLian.btm"};
        ErrorHandler errorHandler = new ErrorHandler();
        Parser parser = new Parser(errorHandler);
        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);

        for (String inFile : args) {
            System.out.println("\n========== Results for " + inFile + " =============");
            try {
                errorHandler.clear();
                Program program = parser.parse(inFile);
                analyzer.analyze(program);
                System.out.println("  Checking was successful.");
            } catch (CompilationException ex) {
                System.out.println("  There were errors:");
                List<Error> errors = errorHandler.getErrorList();
                for (Error error : errors) {
                    System.out.println("\t" + error.toString());
                }
            }
        }

    }

}
