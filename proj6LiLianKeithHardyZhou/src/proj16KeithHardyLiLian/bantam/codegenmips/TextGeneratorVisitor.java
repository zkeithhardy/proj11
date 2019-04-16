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
    private Stack<String> currentLoop;
    private int currentClassFieldLevel;
    private Map<String, String> stringNameMap;
    private Hashtable<String,ClassTreeNode> classMap;
    private String initOrGenMethods;
    private Map<String,Integer> numLocalVarsMap;
    private int methodLocalVars;
    private HashMap<String, ArrayList<String>> dispatchTableMap;
    private int currentParameterOffset = 0;
    private ArrayList<ClassTreeNode> idTable;

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

        classNode.getMemberList().accept(this);
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
            this.assemblySupport.genStoreWord("$v0", 4*fieldCount, "$a0");
        }
        Location fieldLocation= new Location("$v0", 4*fieldCount);
        classSymbolTables.get(currentClass).add(node.getName(), fieldLocation);
        fieldCount+=1;
        return null;

        /*if(node.getType().equals("int")){
            ConstIntExpr tempIntExpr= (ConstIntExpr)node.getInit();

            this.assemblySupport.genLoadImm("$v0", tempIntExpr.getIntConstant());
        }
        else if(node.getType().equals("String")){
            ConstStringExpr tempStringExpr= (ConstStringExpr)node.getInit();
            this.assemblySupport.genLoadWord("$v0",4*fieldCount,  stringNameMap.get(tempStringExpr.getConstant()));
        }
        else if(node.getType().equals("boolean")){
            ConstBooleanExpr tempBoolExpr= (ConstBooleanExpr)node.getInit();
            if (tempBoolExpr.getConstant().equals("true")){
                this.assemblySupport.genLoadImm("$v0", -1);
            }
            else if (tempBoolExpr.getConstant().equals("false")){
                this.assemblySupport.genLoadImm("$v0", 0);
            }
        }
        else{
            this.assemblySupport.genStoreWord("$a0", 0, "$fp");
            this.assemblySupport.genDirCall(node.getName()+ "_init");
            this.assemblySupport.genMove("$v0", "$a0");
            this.assemblySupport.genLoadWord("$a0", 0,"$fp");
        }*/


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
        this.generateMethodEpilogue();
        return null;
    }

    private void generateMethodPrologue(){
        this.assemblySupport.genComment("Start Prologue");
        this.assemblySupport.genSub("$sp","$sp", 4);
        this.assemblySupport.genStoreWord("$ra",0,"$sp");
        this.assemblySupport.genSub("$sp","$sp", 4);
        this.assemblySupport.genStoreWord("$fp",0,"$sp");
        this.assemblySupport.genSub("$sp", "$sp", 4);
        this.assemblySupport.genStoreWord("$a0",0,"$sp");

        this.assemblySupport.genSub("$fp","$sp", 4*methodLocalVars);
        this.assemblySupport.genMove("$sp","$fp");

        this.assemblySupport.genComment("End Prologue");
    }

    private void generateMethodEpilogue(){
        this.assemblySupport.genComment("Start Epilogue");
        this.assemblySupport.genAdd("$sp","$fp", 4*methodLocalVars);
        this.assemblySupport.genLoadWord("$a0",0,"$sp");
        this.assemblySupport.genAdd("$sp", "$sp", 4);
        this.assemblySupport.genLoadWord("$fp",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp", 4);
        this.assemblySupport.genLoadWord("$ra",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp", 4);
        this.assemblySupport.genMove("$sp","$fp");
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
        this.assemblySupport.genCondBeq("$v0","$zero",elseLabel);
        this.assemblySupport.genLabel(thenLabel);
        currentSymbolTable.enterScope();
        node.getThenStmt().accept(this);
        currentSymbolTable.exitScope();
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
        this.assemblySupport.genCondBeq("$v0","$zero",afterWhile);

        currentSymbolTable.enterScope();
        currentLoop.push(afterWhile);
        node.getBodyStmt().accept(this);

        currentSymbolTable.exitScope();
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
        this.assemblySupport.genCondBeq("$zero","$v0",afterFor);
        currentLoop.push(afterFor);
        node.getBodyStmt().accept(this);

        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }
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
        if (node.getRefExpr() == null) { //local var or field of "this"
            type = this.currentClass;
        }
        else if ((node.getRefExpr() instanceof VarExpr) &&
                ((VarExpr) node.getRefExpr()).getName().equals("this")) {
            type = this.currentClass;

        }
        else if ((node.getRefExpr() instanceof VarExpr) &&
                ((VarExpr) node.getRefExpr()).getName().equals("super")) {
            type = this.classMap.get(this.currentClass).getParent().getName();
        }else{
            node.getRefExpr().accept(this);
            type = node.getRefExpr().getExprType();
        }



        this.assemblySupport.genComment("move v0 to a0");
        this.assemblySupport.genMove("$a0","$v0");

        this.assemblySupport.genComment("access " + type +"_dispatch_table");
        this.assemblySupport.genLoadAddr("$v0",type + "_dispatch_table");
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
            this.assemblySupport.genSub("$sp","$sp",4);
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
        this.assemblySupport.genComment("save $a0 onto stack in case init creates a new object.");
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genStoreWord("$a0",0,"$sp");
        // load the address of the template to $a0
        this.assemblySupport.genLoadAddr("$a0", node.getType()+"_template");
        // load the address of the dispatch table to $v0
//        this.assemblySupport.genLoadAddr("$v0", node.getType()+"_dispatch_table");
        // get the address of the clone method, save it to $v0
        this.assemblySupport.genLoadWord("$v0", 0, "$v0" );
        // jump to that clone method
        this.assemblySupport.genInDirCall("$v0");

        this.assemblySupport.genDirCall(node.getType()+"_init");

        this.assemblySupport.genComment("restore $a0");
        this.assemblySupport.genLoadWord("$a0",0,"$sp");
        this.assemblySupport.genAdd("$sp","$sp",4);

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
        // Instanceof will always return true, otherwise the code wouldn't pass the type checker
        this.assemblySupport.genLoadImm("$v0", -1);

        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    public Object visit(CastExpr node) {
        node.getExpr().accept(this);
        if(!node.getUpCast()){
            return null;
        }
        String objectType = node.getExpr().getExprType();

        return null;
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        Location location = null;
        String varName = node.getName();
        String refName = node.getRefName();
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genStoreWord("$a0",0,"$sp");
        node.getExpr().accept(this);
        if (refName == null) { //local var or field of "this"
            location = (Location) currentSymbolTable.lookup(varName);
            this.assemblySupport.genStoreWord("$v0", location.getOffset(),location.getBaseReg());
        }
        else if (refName.equals("this")) {
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);
            this.assemblySupport.genStoreWord("$v0", location.getOffset(),location.getBaseReg());
        }
        else if (refName.equals("super")) {
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);
            this.assemblySupport.genStoreWord("$v0", location.getOffset(),location.getBaseReg());

        }
        else { // refName is not "this" or "super" or varName is not "length" for an array

            Location refVarLocation = (Location) currentSymbolTable.lookup(refName);
            SymbolTable currentSymbolTable = this.classMap.get(this.currentClass).getVarSymbolTable();
            String varType = (String) currentSymbolTable.lookup(refName);
            location = (Location) this.classSymbolTables.get(varType).lookup(varName);

            this.assemblySupport.genComment("storing a field from an object");
            this.assemblySupport.genLoadWord("$a0",refVarLocation.getOffset(),refVarLocation.getBaseReg());
            this.assemblySupport.genStoreWord("$v0",location.getOffset(),"$a0");
        }
        this.assemblySupport.genLoadWord("$a0",0,"$sp");
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v0");
        node.getRightExpr().accept(this);
        this.out.println("seq $v0 $v0 $v1");
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v0");
        node.getRightExpr().accept(this);
        this.out.println("sne $v0 $v0 $v1");
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v0");
        node.getRightExpr().accept(this);
        this.out.println("slt $v0 $v0 $v1");
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v0");
        node.getRightExpr().accept(this);
        this.out.println("sle $v0 $v0 $v1");
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v0");
        node.getRightExpr().accept(this);
        this.out.println("sgt $v0 $v0 $v1");
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v0");
        node.getRightExpr().accept(this);
        this.out.println("sge $v0 $v0 $v1");
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v1");
        node.getRightExpr().accept(this);
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v1");
        node.getRightExpr().accept(this);
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v1");
        node.getRightExpr().accept(this);
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v1");
        node.getRightExpr().accept(this);
        this.assemblySupport.genDiv("$v0","$v0","$v1");
        return null;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithModulusExpr node) {
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v1");
        node.getRightExpr().accept(this);
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
        node.getExpr().accept(this);
        this.assemblySupport.genComment("negation");
        this.assemblySupport.genSub("$v0","$zero","$v0");
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
        this.assemblySupport.genAdd("$v0","$v0",1);
        this.assemblySupport.genMul("$v0","$v0",-1);
        return null;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    public Object visit(UnaryIncrExpr node) {
        node.getExpr().accept(this);
        this.assemblySupport.genComment("increment");
        this.assemblySupport.genAdd("$v0","$v0",1);
        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return result of the visit
     */
    public Object visit(UnaryDecrExpr node) {
        node.getExpr().accept(this);
        this.assemblySupport.genComment("decrement");
        this.assemblySupport.genSub("$v0","$v0",1);
        return null;
    }

    /**
     * Visit a variable expression node
     *
     * @param node the variable expression node
     * @return result of the visit
     */
    public Object visit(VarExpr node) {
        Location location = null;
        String varName = node.getName();
        this.assemblySupport.genSub("$sp","$sp",4);
        this.assemblySupport.genStoreWord("$a0",0,"$sp");
        if (node.getRef() != null) { // expr = "null"
            node.getRef().accept(this);
        }
        if(node.getRef() == null){
            location = (Location) currentSymbolTable.lookup(varName);
            this.assemblySupport.genLoadWord("$v0",location.getOffset(),location.getBaseReg());
        }
        else if ((node.getRef() instanceof VarExpr) &&
                ((VarExpr) node.getRef()).getName().equals("this")) {
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);
            this.assemblySupport.genLoadWord("$v0", location.getOffset(),location.getBaseReg());

        }
        else if ((node.getRef() instanceof VarExpr) &&
                ((VarExpr) node.getRef()).getName().equals("super")) {
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);
            this.assemblySupport.genLoadWord("$v0", location.getOffset(),location.getBaseReg());

        }
        else { // ref is not null, "this", or "super"
            String refTypeName = node.getRef().getExprType();

            Location refVarLocation = (Location) currentSymbolTable.lookup(((VarExpr) node.getRef()).getName());
            SymbolTable currentSymbolTable = this.classMap.get(this.currentClass).getVarSymbolTable();
            String varType = (String) currentSymbolTable.lookup(((VarExpr) node.getRef()).getName());
            location = (Location) this.classSymbolTables.get(varType).lookup(varName);
            this.assemblySupport.genComment("loading a field from an object");
            this.assemblySupport.genLoadWord("$a0",refVarLocation.getOffset(),refVarLocation.getBaseReg());
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
        this.assemblySupport.genLoadAddr("$v0",stringNameMap.get(node.getConstant()));
        return null;
    }
}
