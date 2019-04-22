package proj16KeithHardyLiLian.bantam.codegenmips;

import proj16KeithHardyLiLian.bantam.ast.*;
import proj16KeithHardyLiLian.bantam.semant.NumLocalVarsVisitor;
import proj16KeithHardyLiLian.bantam.util.Location;
import proj16KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj16KeithHardyLiLian.bantam.util.SymbolTable;
import proj16KeithHardyLiLian.bantam.visitor.Visitor;

import java.io.PrintStream;
import java.util.*;

public class TextGeneratorVisitor extends Visitor {
    private PrintStream out;
    private MipsSupport assemblySupport;
    private String currentClass;
    private SymbolTable currentSymbolTable;
    private int fieldCount=3;
    private HashMap<String, SymbolTable> classSymbolTables = new HashMap<>();
    private int numLocalVars = 0;
    private Stack<String> currentLoop = new Stack<>();
    private int currentClassFieldLevel;
    private Map<String, String> stringNameMap;
    private Hashtable<String,ClassTreeNode> classMap;
    private String initOrGenMethods;
    private Map<String,Integer> numLocalVarsMap;
    private int methodLocalVars;
    private HashMap<String, ArrayList<String>> dispatchTableMap;
    private int currentParameterOffset = 0;
    private ArrayList<ClassTreeNode> idTable;

    //fixme: delete this after checking is done
    private final String[] registers =
            {"$zero", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
                    "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$t8", "$t9",
                    "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7",
                    "$k0", "$k1", "$gp", "$sp", "$fp", "$ra"};
    
    public TextGeneratorVisitor(PrintStream out, MipsSupport assemblySupport, Hashtable<String,ClassTreeNode> classMap,
                                HashMap<String, ArrayList<String>> dispatchTableMap, ArrayList<ClassTreeNode> idTable){
        this.out = out;
        this.assemblySupport = assemblySupport;
        this.classMap = classMap;
        this.dispatchTableMap = dispatchTableMap;
        this.idTable = idTable;
    }

    public void generateTextSection(Program root){
        this.initOrGenMethods = "genMethods";
        numLocalVars = 0;
        NumLocalVarsVisitor numLocalVarsVisitor = new NumLocalVarsVisitor();
        this.numLocalVarsMap = numLocalVarsVisitor.getNumLocalVars(root);
        root.accept(this);
    }

    public void generateFieldInitialization(Class_ classNode, Map<String, String> stringNameMap){
        classSymbolTables.put(classNode.getName(), new SymbolTable());
        classSymbolTables.get(classNode.getName()).enterScope();
        this.stringNameMap=stringNameMap;
        this.currentClass=classNode.getName();
        this.initOrGenMethods = "init";
        fieldCount = 3;

        classNode.accept(this);
    }


    /**
     * peek at sepcific register with different indicator
     * @param reg name of the register to look at
     *
     */
    //fixme: delete this after checking is done
    public void printRegister(String reg){
        this.assemblySupport.genComment("peeking register "+ reg);
        this.assemblySupport.genMove("$t7", "$a0");
        this.assemblySupport.genMove("$t6", "$v0");
        for (String register : registers) {
            if (reg.equals(register)) {
                this.assemblySupport.genMove("$a0", reg);
            }
        }
        this.out.println("\tli $v0 1");
        this.out.println("\tsyscall");
        //restore the a0 v0 register values
        this.assemblySupport.genMove("$a0", "$t7");
        this.assemblySupport.genMove("$v0", "$t6");
        this.assemblySupport.genComment("end peeking register " + reg);
    }

    /**
     * Visit a list node of classes
     *
     * @param node the class list node
     * @return result of the visit
     */
    public Object visit(ClassList node) {
        for (ASTNode aNode : node)
            aNode.accept(this);
        return null;
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return result of the visit
     */
    public Object visit(Class_ node) {
        this.currentClass = node.getName();
        this.currentSymbolTable = classSymbolTables.get(this.currentClass);

        if(initOrGenMethods.equals("init")) {
            this.currentSymbolTable.add("super", new Location("$a0", 0));

            this.currentSymbolTable.enterScope();

            this.currentSymbolTable.add("this", new Location("$a0", 0)); // template of this object
        }

        currentClassFieldLevel = currentSymbolTable.getCurrScopeLevel();
        node.getMemberList().accept(this);
        return null;
    }

    /**
     * Visit a list node of members
     *
     * @param node the member list node
     * @return result of the visit
     */
    public Object visit(MemberList node) {
        for (ASTNode child : node) {
            if (this.initOrGenMethods.equals("init")) {
                if(child instanceof Field){
                    child.accept(this);
                }
            }else{
                if(child instanceof Method){
                    child.accept(this);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param node the field node
     * @return
     */
    public Object visit(Field node){
        if(node.getInit() != null){
            node.getInit().accept(this);
            this.assemblySupport.genComment("store the field "+4*fieldCount+" away from $a0 to $v0");
            this.assemblySupport.genStoreWord("$v0", 4*fieldCount, "$a0");
        }
        Location fieldLocation= new Location("$a0", 4*fieldCount);
        classSymbolTables.get(currentClass).add(node.getName(), fieldLocation);
        fieldCount+=1;
        return null;

    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        numLocalVars = 0;
        this.currentSymbolTable.enterScope();
        this.methodLocalVars = this.numLocalVarsMap.get(this.currentClass + "." + node.getName());
        node.getFormalList().accept(this);
        this.assemblySupport.genLabel(this.currentClass+"."+node.getName());
        this.generateMethodPrologue();

        node.getStmtList().accept(this);
        this.currentSymbolTable.exitScope();
        this.generateMethodEpilogue(node.getFormalList().getSize());
        return null;
    }

    public void generateMethodPrologue(){
        this.assemblySupport.genComment("Start Prologue");

        this.assemblySupport.genComment("subtract 4 from $sp");
        this.assemblySupport.genSub("$sp","$sp", 4);

        this.assemblySupport.genComment("store $ra to $sp");
        this.assemblySupport.genStoreWord("$ra",0,"$sp");

        this.assemblySupport.genComment("subtract 4 from $sp");
        this.assemblySupport.genSub("$sp","$sp", 4);

        this.assemblySupport.genComment("store $fp to $sp");
        this.assemblySupport.genStoreWord("$fp",0,"$sp");

        this.assemblySupport.genComment("subtract 4 from $sp");
        this.assemblySupport.genSub("$sp", "$sp", 4);

        this.assemblySupport.genComment("store $a0 to $sp");
        this.assemblySupport.genStoreWord("$a0",0,"$sp");

        this.assemblySupport.genComment("subtract "+4*methodLocalVars+" from $sp and store the result to $fp");
        this.assemblySupport.genSub("$fp","$sp", 4*methodLocalVars);

        this.assemblySupport.genComment("move $fp to $sp");
        this.assemblySupport.genMove("$sp","$fp");

        this.assemblySupport.genComment("End Prologue");
    }

    public void generateMethodEpilogue(int numParams){
        this.assemblySupport.genComment("Start Epilogue");

        this.assemblySupport.genComment("add "+4*methodLocalVars+" to $fp and store the result to $sp");
        this.assemblySupport.genAdd("$sp","$fp", 4*methodLocalVars);

        this.assemblySupport.genComment("load $sp to $a0");
        this.assemblySupport.genLoadWord("$a0",0,"$sp");

        this.assemblySupport.genComment("add 4 to $sp");
        this.assemblySupport.genAdd("$sp", "$sp", 4);

        this.assemblySupport.genComment("load $sp to $fp");
        this.assemblySupport.genLoadWord("$fp",0,"$sp");

        this.assemblySupport.genComment("add 4 to $sp");
        this.assemblySupport.genAdd("$sp","$sp", 4);

        this.assemblySupport.genComment("load $sp to $ra");
        this.assemblySupport.genLoadWord("$ra",0,"$sp");

        this.assemblySupport.genComment("add 4 to $sp");
        this.assemblySupport.genAdd("$sp","$sp", 4);

//        this.assemblySupport.genComment("move $fp to $sp");
//        this.assemblySupport.genMove("$sp","$fp");

        this.assemblySupport.genComment("add "+4*numParams+" to $fp and store the result to $sp");
        this.assemblySupport.genAdd("$sp","$fp", 4*numParams);

        this.assemblySupport.genRetn();
        this.assemblySupport.genComment("End Epilogue");
    }

    /**
     * Visit a list node of formals
     *
     * @param node the formal list node
     * @return result of the visit
     */
    public Object visit(FormalList node) {
        this.currentParameterOffset = node.getSize()*4;
        for (Iterator it = node.iterator(); it.hasNext(); )
            ((Formal) it.next()).accept(this);
        return null;
    }

    /**
     * Visit a formal node
     *
     * @param node the formal node
     * @return result of the visit
     */
    public Object visit(Formal node) {
        this.currentSymbolTable.add(node.getName(),new Location("$fp",this.methodLocalVars * 4 +
                12 + this.currentParameterOffset));
        this.currentParameterOffset -= 4;
        numLocalVars += 1;
        return null;
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {
        node.getInit().accept(this);
        this.assemblySupport.genComment("store $v0 to ("+numLocalVars*4+")$fp");
        this.assemblySupport.genStoreWord("$v0",numLocalVars*4,"$fp");
        this.currentSymbolTable.add(node.getName(),new Location("$fp",numLocalVars*4));
        numLocalVars += 1;
        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        node.getPredExpr().accept(this);
        String thenLabel = this.assemblySupport.getLabel();
        String elseLabel = this.assemblySupport.getLabel();
        String afterLabel = this.assemblySupport.getLabel();
        this.assemblySupport.genComment("branch to "+elseLabel+" if $v0 is equal to 0");
        this.assemblySupport.genCondBeq("$v0","$zero",elseLabel);
        this.assemblySupport.genLabel(thenLabel);
        currentSymbolTable.enterScope();
        node.getThenStmt().accept(this);
        currentSymbolTable.exitScope();
        this.assemblySupport.genComment("unconditional branch to "+afterLabel);
        this.assemblySupport.genUncondBr(afterLabel);

        this.assemblySupport.genLabel(elseLabel);

        if (node.getElseStmt() != null) {
            currentSymbolTable.enterScope();
            node.getElseStmt().accept(this);
            currentSymbolTable.exitScope();
        }
        this.assemblySupport.genLabel(afterLabel);
        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        String startWhile = this.assemblySupport.getLabel();
        String afterWhile = this.assemblySupport.getLabel();

        this.assemblySupport.genLabel(startWhile);
        node.getPredExpr().accept(this);

        this.assemblySupport.genComment("branch to "+afterWhile+" if $v0 is equal to 0");
        this.assemblySupport.genCondBeq("$v0","$zero",afterWhile);

        currentSymbolTable.enterScope();
        currentLoop.push(afterWhile);
        node.getBodyStmt().accept(this);

        currentSymbolTable.exitScope();
        this.assemblySupport.genComment("unconditional branch to "+startWhile);
        this.assemblySupport.genUncondBr(startWhile);
        this.assemblySupport.genLabel(afterWhile);
        currentLoop.pop();
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
        currentSymbolTable.enterScope();
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        String beforeFor = this.assemblySupport.getLabel();
        String afterFor = this.assemblySupport.getLabel();
        this.assemblySupport.genLabel(beforeFor);
        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
        }
        this.assemblySupport.genComment("branch to "+afterFor+" if $v0 is equal to 0");
        this.assemblySupport.genCondBeq("$zero","$v0",afterFor);
        currentLoop.push(afterFor);
        node.getBodyStmt().accept(this);

        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }
        this.assemblySupport.genComment("unconditional branch to "+beforeFor);
        this.assemblySupport.genUncondBr(beforeFor);
        this.assemblySupport.genLabel(afterFor);
        currentLoop.pop();
        currentSymbolTable.exitScope();

        return null;
    }

    /**
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return result of the visit
     */
    public Object visit(BreakStmt node) {
        this.assemblySupport.genComment("breaking out of loop to label " + currentLoop.peek());
        this.assemblySupport.genUncondBr(currentLoop.peek());
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return result of the visit
     */
    public Object visit(BlockStmt node) {
        currentSymbolTable.enterScope();
        node.getStmtList().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return result of the visit
     */
    public Object visit(ReturnStmt node) {
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
        }
        return null;
    }

    /**
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return result of the visit
     */
    public Object visit(DispatchExpr node) {
        String type;

        if (node.getRefExpr() == null || ((node.getRefExpr() instanceof VarExpr) && //local var or field of "this"
                ((VarExpr) node.getRefExpr()).getName().equals("this"))) {
            type = this.currentClass;
            this.assemblySupport.genComment("access dispatch_table");
            this.assemblySupport.genLoadAddr("$v0",type + "_dispatch_table");
        }
        else if ((node.getRefExpr() instanceof VarExpr) &&
                ((VarExpr) node.getRefExpr()).getName().equals("super")) {
            type = this.classMap.get(this.currentClass).getParent().getName();

            this.assemblySupport.genComment("access dispatch_table");
            this.assemblySupport.genLoadAddr("$v0",type + "_dispatch_table");
        }else{
            node.getRefExpr().accept(this);
            type = node.getRefExpr().getExprType();
            this.assemblySupport.genLoadWord("$v0",8,"$v0");
        }

        ArrayList currentDispatchTable = this.dispatchTableMap.get(type);
        int idx = currentDispatchTable.indexOf(node.getMethodName());
        this.assemblySupport.genComment("load method address");
        this.assemblySupport.genLoadWord("$a1",idx*4, "$v0");
        node.getActualList().accept(this);

        this.assemblySupport.genInDirCall("$a1");

        return null;
    }

    /**
     * Visit a list node of expressions
     *
     * @param node the expression list node
     * @return result of the visit
     */
    public Object visit(ExprList node) {
        for (Iterator it = node.iterator(); it.hasNext(); ){
            ((Expr) it.next()).accept(this);
            this.assemblySupport.genComment("save parameters on stack");
            this.assemblySupport.genComment("subtract 4 from $sp");
            this.assemblySupport.genSub("$sp","$sp",4);
            this.assemblySupport.genComment("store $v0 to $sp");
            this.assemblySupport.genStoreWord("$v0",0,"$sp");
        }

        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return result of the visit
     */
    public Object visit(NewExpr node) {
//        this.assemblySupport.genComment("save $a0 onto stack in case init creates a new object.");
//        this.assemblySupport.genComment("subtract 4 from $sp");
//        this.assemblySupport.genSub("$sp","$sp",4);
//        this.assemblySupport.genComment("store $a0 to $sp");
//        this.assemblySupport.genStoreWord("$a0",0,"$sp");

        // load the address of the template to $a0
        this.assemblySupport.genComment("load the address of "+node.getType()+"_template to $a0");
        this.assemblySupport.genLoadAddr("$a0", node.getType()+"_template");
        // get the address of the clone method, save it to $v0
        this.assemblySupport.genComment("load (8)$a0 to $v0");
        //this.assemblySupport.genLoadWord("$v0", 8, "$a0" );
        // jump to that clone method
        this.assemblySupport.genComment("jump to $v0");
        //this.assemblySupport.genInDirCall("$v0");
        this.assemblySupport.genDirCall("Object.clone");
        this.assemblySupport.genMove("$a0","$v0");

        this.assemblySupport.genComment("jump to "+node.getType()+"_init");
        this.assemblySupport.genDirCall(node.getType()+"_init");

        this.assemblySupport.genLoadWord("$a0", 4*(methodLocalVars), "$fp");

//        this.assemblySupport.genComment("restore $a0");
//        this.assemblySupport.genComment("load $a0 to $sp");
//        this.assemblySupport.genLoadWord("$a0",0,"$sp");
//        this.assemblySupport.genComment("add 4 to $sp");
//        this.assemblySupport.genAdd("$sp","$sp",4);

        return null;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return result of the visit
     */
    public Object visit(InstanceofExpr node) {
        node.getExpr().accept(this);
        this.assemblySupport.genComment("handle instanceof expression");
        this.assemblySupport.genComment("load i into $v0");
        this.assemblySupport.genLoadWord("$v0", 0, "$v0"); // i is in v0 now

        int j = this.idTable.indexOf(this.classMap.get(node.getType())); // id in the table
        int k = this.classMap.get(node.getType()).getNumDescendants();

        this.assemblySupport.genComment("load j into $t0 and j+k into $t1");
        this.assemblySupport.genLoadImm("$t0", j);
        this.assemblySupport.genLoadImm("$t1", j+k);

        this.assemblySupport.genComment("compare j<=i and i<=j+k, if both true, return true");
        this.out.println("sle $t0 $t0 $v0"); // j <= i
        this.out.println("sle $v0 $v0 $t1"); // i <= j+k

        this.assemblySupport.genAnd("$v0", "$t0", "$v0");
        this.assemblySupport.genSub("$v0", "$zero", "$v0"); // b/c we're using -1 as true

        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    public Object visit(CastExpr node) {
        this.assemblySupport.genComment("handle Cast Expression");
        node.getExpr().accept(this);
        if(!node.getUpCast()){
            this.assemblySupport.genComment("case where the cast expression is down-casting");
            String objectType = node.getExpr().getExprType();
            this.assemblySupport.genComment("check if the expression is an instance of target class");
            InstanceofExpr instanceChecker= new InstanceofExpr(node.getLineNum(), node.getExpr(), objectType);
            instanceChecker.accept(this);

            String ifZero= this.assemblySupport.getLabel();
            String ifNotZero = this.assemblySupport.getLabel();
            this.assemblySupport.genCondBeq("$v0", "$zero", ifZero);
            this.assemblySupport.genUncondBr(ifNotZero);
            //false, handle error
            this.assemblySupport.genComment("case where the expression is not a proper subtype of target class, handle error");
            this.assemblySupport.genLabel(ifZero);
            this.assemblySupport.genDirCall("_class_cast_error");
            //true, bypass handle error
            this.assemblySupport.genComment("case where the expression is a proper subtype of target class");
            this.assemblySupport.genLabel(ifNotZero);

            return null;
        }

        return null;
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        Location location;
        String varName = node.getName();
        String refName = node.getRefName();
        this.assemblySupport.genComment("assign expr");
        this.assemblySupport.genComment("subtrack 4 from the the stack pointer");
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genComment("save $a0 to stack pointer with offset of 0");
        this.assemblySupport.genStoreWord("$a0",0,"$sp");
        node.getExpr().accept(this);

        if (refName == null) { //local var or field of "this"
            this.assemblySupport.genComment("case where the reference name is null");
            location = (Location) currentSymbolTable.lookup(varName);
            this.assemblySupport.genStoreWord("$v0", location.getOffset(),location.getBaseReg());
        }
        else if (refName.equals("this")) {
            this.assemblySupport.genComment("case where the reference name is /this/");
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);
            this.assemblySupport.genStoreWord("$v0", location.getOffset(),location.getBaseReg());
        }
        else if (refName.equals("super")) {
            this.assemblySupport.genComment("case where the reference name is /super/");
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);
            this.assemblySupport.genStoreWord("$v0", location.getOffset(),location.getBaseReg());
        }
        this.assemblySupport.genComment("save stack pointer result to $a0");
        this.assemblySupport.genLoadWord("$a0",0,"$sp");
        this.assemblySupport.genComment("add stack pointer with 4");
        this.assemblySupport.genAdd("$sp","$sp", 4);
        return null;
    }

    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompEqExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("move v0 to v1");
        this.assemblySupport.genMove("$v1","$v0");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("compare left and right sides of expression");
        this.out.println("\tseq $v0 $v0 $v1");
        this.assemblySupport.genSub("$v0","$zero","$v0");
        return null;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompNeExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("move v0 to v1");
        this.assemblySupport.genMove("$v1","$v0");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("compare left and right sides of expression");
        this.out.println("\tsne $v0 $v0 $v1");
        this.assemblySupport.genSub("$v0","$zero","$v0");
        return null;
    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLtExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("move v0 to v1");
        this.assemblySupport.genMove("$v1","$v0");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("compare left and right sides of expression");
        this.out.println("\tslt $v0 $v1 $v0");
        this.assemblySupport.genSub("$v0","$zero","$v0");
        return null;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLeqExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("move v0 to v1");
        this.assemblySupport.genMove("$v1","$v0");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("compare left and right sides of expression");
        this.out.println("\tsle $v0 $v1 $v0");
        this.assemblySupport.genSub("$v0","$zero","$v0");
        return null;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGtExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("move v0 to v1");
        this.assemblySupport.genMove("$v1","$v0");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("compare left and right sides of expression");
        this.out.println("\tsgt $v0 $v1 $v0");
        this.assemblySupport.genSub("$v0","$zero","$v0");
        return null;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGeqExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("move v0 to v1");
        this.assemblySupport.genMove("$v1","$v0");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("compare left and right sides of expression");
        this.out.println("\tsge $v0 $v0 $v1");
        this.assemblySupport.genSub("$v0","$zero","$v0");
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithPlusExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("store left expression on stack");
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genStoreWord("$v0",0,"$sp");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("load left expression on stack");
        this.assemblySupport.genLoadWord("$v1",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp",4);
        this.assemblySupport.genComment("add left and right sides of expression");
        this.assemblySupport.genAdd("$v0","$v0","$v1");
        return null;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithMinusExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("store left expression on stack");
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genStoreWord("$v0",0,"$sp");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("load left expression on stack");
        this.assemblySupport.genLoadWord("$v1",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp",4);
        this.assemblySupport.genComment("subtract left and right sides of expression");
        this.assemblySupport.genSub("$v0","$v0","$v1");
        return null;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithTimesExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("store left expression on stack");
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genStoreWord("$v0",0,"$sp");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("load left expression on stack");
        this.assemblySupport.genLoadWord("$v1",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp",4);
        this.assemblySupport.genComment("multiply left and right sides of expression");
        this.assemblySupport.genMul("$v0","$v0","$v1");
        return null;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithDivideExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("store left expression on stack");
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genStoreWord("$v0",0,"$sp");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("load left expression on stack");
        this.assemblySupport.genLoadWord("$v1",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp",4);

        String zeroError = this.assemblySupport.getLabel();
        String afterError = this.assemblySupport.getLabel();

        this.assemblySupport.genComment("check for divide by zero error");
        this.assemblySupport.genCondBeq("$zero", "$v1", zeroError);
        this.assemblySupport.genComment("divide left and right sides of expression");
        this.assemblySupport.genDiv("$v0","$v0","$v1");
        this.assemblySupport.genComment("branch to afterError");
        this.assemblySupport.genUncondBr(afterError);
        this.assemblySupport.genLabel(zeroError);
        this.assemblySupport.genDirCall("_divide_zero_error");
        this.assemblySupport.genLabel(afterError);
        return null;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithModulusExpr node) {
        this.assemblySupport.genComment("gen left side of expression");
        node.getLeftExpr().accept(this);
        this.assemblySupport.genComment("store left expression on stack");
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genStoreWord("$v0",0,"$sp");
        this.assemblySupport.genComment("gen right side of expression");
        node.getRightExpr().accept(this);
        this.assemblySupport.genComment("load left expression from stack");
        this.assemblySupport.genLoadWord("$v1",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp",4);
        this.out.println("mod left and right sides of expression");
        this.assemblySupport.genMod("$v0","$v0","$v1");
        return null;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicAndExpr node) {
        this.assemblySupport.genComment("and expression");
        node.getLeftExpr().accept(this);
        String afterAnd = this.assemblySupport.getLabel();
        this.assemblySupport.genCondBeq("$v0","$zero",afterAnd);
        node.getRightExpr().accept(this);

        this.assemblySupport.genLabel(afterAnd);
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicOrExpr node) {
        this.assemblySupport.genComment("or expression");
        node.getLeftExpr().accept(this);
        //lazy evaluation
        this.assemblySupport.genLoadImm("$v1",-1);
        String afterOr = this.assemblySupport.getLabel();
        this.assemblySupport.genCondBeq("$v0","$v1",afterOr);
        node.getRightExpr().accept(this);

        this.assemblySupport.genLabel(afterOr);
        return null;
    }



    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return result of the visit
     */
    public Object visit(UnaryNegExpr node) {
        this.assemblySupport.genComment("negation");
        node.getExpr().accept(this);
        Location location = null;
        String varName =((VarExpr) node.getExpr()).getName();

        if(((VarExpr) node.getExpr()).getRef() == null){
            this.assemblySupport.genComment("case where the reference object is null");
            location = (Location) currentSymbolTable.lookup(varName);
        }

        else if (((VarExpr)((VarExpr) node.getExpr()).getRef()).getName().equals("this")) {
            this.assemblySupport.genComment("case where the reference object is /this./");
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);

        }

        else if (((VarExpr)((VarExpr) node.getExpr()).getRef()).getName().equals("super")) {
            this.assemblySupport.genComment("case where the reference object is /.super/");
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);

        }
        this.assemblySupport.genSub("$v0","$zero","$v0");
        this.assemblySupport.genStoreWord("$v0",location.getOffset(),location.getBaseReg());
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return result of the visit
     */
    public Object visit(UnaryNotExpr node) {
        node.getExpr().accept(this);
        this.assemblySupport.genComment("unary not expression");
        Location location = null;
        String varName =((VarExpr) node.getExpr()).getName();

        if(((VarExpr) node.getExpr()).getRef() == null){
            this.assemblySupport.genComment("case where the reference object is null");
            location = (Location) currentSymbolTable.lookup(varName);
        }

        else if (((VarExpr)((VarExpr) node.getExpr()).getRef()).getName().equals("this")) {
            this.assemblySupport.genComment("case where the reference object is /this./");
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);

        }

        else if (((VarExpr)((VarExpr) node.getExpr()).getRef()).getName().equals("super")) {
            this.assemblySupport.genComment("case where the reference object is /.super/");
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);

        }
        this.assemblySupport.genAdd("$v0","$v0",1);
        this.assemblySupport.genMul("$v0","$v0",-1);
        this.assemblySupport.genStoreWord("$v0",location.getOffset(),location.getBaseReg());
        return null;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    public Object visit(UnaryIncrExpr node) {
        this.assemblySupport.genComment("increment");
        node.getExpr().accept(this);
        Location location = null;
        String varName =((VarExpr) node.getExpr()).getName();

        if(((VarExpr) node.getExpr()).getRef() == null){
            this.assemblySupport.genComment("case where the reference object is null");
            location = (Location) currentSymbolTable.lookup(varName);
        }

        else if (((VarExpr)((VarExpr) node.getExpr()).getRef()).getName().equals("this")) {
            this.assemblySupport.genComment("case where the reference object is /this./");
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);

        }

        else if (((VarExpr)((VarExpr) node.getExpr()).getRef()).getName().equals("super")) {
            this.assemblySupport.genComment("case where the reference object is /.super/");
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);

        }
        this.assemblySupport.genAdd("$v0","$v0",1);
        this.assemblySupport.genStoreWord("$v0",location.getOffset(),location.getBaseReg());
        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return result of the visit
     */
    public Object visit(UnaryDecrExpr node) {
        this.assemblySupport.genComment("decrement");
        node.getExpr().accept(this);
        Location location = null;
        String varName =((VarExpr) node.getExpr()).getName();

        if(((VarExpr) node.getExpr()).getRef() == null){
            this.assemblySupport.genComment("case where the reference object is null");
            location = (Location) currentSymbolTable.lookup(varName);
        }

        else if (((VarExpr)((VarExpr) node.getExpr()).getRef()).getName().equals("this")) {
            this.assemblySupport.genComment("case where the reference object is /this./");
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);

        }

        else if (((VarExpr)((VarExpr) node.getExpr()).getRef()).getName().equals("super")) {
            this.assemblySupport.genComment("case where the reference object is /.super/");
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);

        }
        this.assemblySupport.genSub("$v0","$v0",1);
        this.assemblySupport.genStoreWord("$v0",location.getOffset(),location.getBaseReg());
        return null;
    }

    /**
     * Visit a variable expression node
     *
     * @param node the variable expression node
     * @return result of the visit
     */
    public Object visit(VarExpr node) {
        this.assemblySupport.genComment("var expression");
        Location location = null;
        String varName = node.getName();
        this.assemblySupport.genComment("subtract stack pointer with 4");
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genComment("save value in $a0 to stack pointer with 0 offset");
        this.assemblySupport.genStoreWord("$a0",0,"$sp");

        this.assemblySupport.genComment("accept the reference object and save its location $v0");

        if(node.getRef() == null){
            this.assemblySupport.genComment("case where the reference object is null");
            location = (Location) currentSymbolTable.lookup(varName);
            this.assemblySupport.genComment("load ("+ location.getOffset()+")"+ location.getBaseReg() +" to $v0 ");
            this.assemblySupport.genLoadWord("$v0",location.getOffset(),location.getBaseReg());
        }

        else if ((node.getRef() instanceof VarExpr) &&
                ((VarExpr) node.getRef()).getName().equals("this")) {
            this.assemblySupport.genComment("case where the reference object is /this./");
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);
            this.assemblySupport.genComment("load ("+ location.getOffset()+")"+ location.getBaseReg() +" to $v0 ");
            this.assemblySupport.genLoadWord("$v0", location.getOffset(),location.getBaseReg());

        }

        else if ((node.getRef() instanceof VarExpr) &&
                ((VarExpr) node.getRef()).getName().equals("super")) {
            this.assemblySupport.genComment("case where the reference object is /.super/");
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);
            this.assemblySupport.genComment("load ("+ location.getOffset()+")"+ location.getBaseReg() +" to $v0 ");
            this.assemblySupport.genLoadWord("$v0", location.getOffset(),location.getBaseReg());

        }
        else { // ref is not null, "this", or "super"
            node.getRef().accept(this);
            this.assemblySupport.genComment("case where the reference object is user defined class");
            String refTypeName = node.getRef().getExprType();

            Location refVarLocation = (Location) currentSymbolTable.lookup(((VarExpr) node.getRef()).getName());
            SymbolTable currentSymbolTable = this.classMap.get(this.currentClass).getVarSymbolTable();
            String varType = (String) currentSymbolTable.lookup(((VarExpr) node.getRef()).getName());
            location = (Location) this.classSymbolTables.get(varType).lookup(varName);
            this.assemblySupport.genComment("load ("+ location.getOffset()+")"+ location.getBaseReg() +" to $v0 ");
            this.assemblySupport.genLoadWord("$v0",refVarLocation.getOffset(),refVarLocation.getBaseReg());
            //check for null pointer
            String nullError = this.assemblySupport.getLabel();
            String afterError = this.assemblySupport.getLabel();
            this.assemblySupport.genComment("check for null pointer errors");
            this.assemblySupport.genComment("if $v0 == 0, branch to nullError");
            this.assemblySupport.genCondBeq("$zero", "$v0", nullError);
            this.assemblySupport.genUncondBr(afterError);
            this.assemblySupport.genLabel(nullError);
            this.assemblySupport.genDirCall("_null_pointer_error");
            this.assemblySupport.genLabel(afterError);
            this.assemblySupport.genComment("move $v0 to $a0");
            this.assemblySupport.genMove("$a0", "$v0");
            this.assemblySupport.genComment("load ("+location.getOffset()+")$a0 to $v0");
            this.assemblySupport.genLoadWord("$v0",location.getOffset(),"$a0");
        }
        this.assemblySupport.genLoadWord("$a0",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp", 4);
        return null;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return result of the visit
     */
    public Object visit(ConstIntExpr node) {
        this.assemblySupport.genComment("constant int expression");
        this.assemblySupport.genLoadImm("$v0",node.getIntConstant());
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return result of the visit
     */
    public Object visit(ConstBooleanExpr node) {
        this.assemblySupport.genComment("constant boolean expression");
        if(node.getConstant().equals("true")){
            this.assemblySupport.genLoadImm("$v0",-1);
        }else{
            this.assemblySupport.genLoadImm("$v0",0);
        }
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return result of the visit
     */
    public Object visit(ConstStringExpr node) {
        this.assemblySupport.genComment("constant string expression");
        this.assemblySupport.genLoadAddr("$v0",stringNameMap.get(node.getConstant()));
        return null;
    }
}
