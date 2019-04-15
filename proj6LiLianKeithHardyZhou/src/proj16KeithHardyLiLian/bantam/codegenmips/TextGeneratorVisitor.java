package proj16KeithHardyLiLian.bantam.codegenmips;

import proj16KeithHardyLiLian.bantam.ast.*;
import proj16KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj16KeithHardyLiLian.bantam.util.Location;
import proj16KeithHardyLiLian.bantam.util.SymbolTable;
import proj16KeithHardyLiLian.bantam.visitor.Visitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class TextGeneratorVisitor extends Visitor {
    private PrintStream out;
    private MipsSupport assemblySupport;
    private String currentClass;
    private SymbolTable currentSymbolTable;
    private HashMap<String, SymbolTable> classSymbolTables;
    private int fpOffset = 0;
    private Stack<Stmt> currentLoop;
    private int currentClassFieldLevel;

    public TextGeneratorVisitor(PrintStream out, MipsSupport assemblySupport){
        this.out = out;
        this.assemblySupport = assemblySupport;
    }

    public void generateTextSection(Program root){
        root.accept(this);
    }

    public void generateFieldInitialization(Class_ classNode){
        classSymbolTables.put(classNode.getName(), new SymbolTable());
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
        for (ASTNode child : node)
            child.accept(this);
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        this.currentSymbolTable.enterScope();
        fpOffset = 0;
        this.assemblySupport.genLabel(this.currentClass+"."+node.getName());
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);
        this.currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a list node of formals
     *
     * @param node the formal list node
     * @return result of the visit
     */
    public Object visit(FormalList node) {
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
        this.currentSymbolTable.add(node.getName(),new Location("$fp",fpOffset));
        fpOffset += 4;
        return null;
    }

    /**
     * Visit a list node of statements
     *
     * @param node the statement list node
     * @return result of the visit
     */
    public Object visit(StmtList node) {
        for (Iterator it = node.iterator(); it.hasNext(); )
            ((Stmt) it.next()).accept(this);
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
        fpOffset += 4;
        this.assemblySupport.genStoreWord("$v0",fpOffset,"$fp");
        this.currentSymbolTable.add(node.getName(),new Location("$fp",fpOffset));
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
        currentLoop.push(node);
        node.getBodyStmt().accept(this);
        currentLoop.pop();
        currentSymbolTable.exitScope();

        this.assemblySupport.genUncondBr(startWhile);
        this.assemblySupport.genLabel(afterWhile);
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
        }
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }
        currentSymbolTable.enterScope();
        currentLoop.push(node);
        node.getBodyStmt().accept(this);
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
        this.assemblySupport.genRetn();
        return null;
    }

    /**
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return result of the visit
     */
    public Object visit(DispatchExpr node) {
        if(node.getRefExpr() != null)
            node.getRefExpr().accept(this);
        node.getActualList().accept(this);
        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return result of the visit
     */
    public Object visit(NewExpr node) {
//        // load the address of the template to $a0
//        this.assemblySupport.genLoadAddr("$a0", node.getType()+"_template");
//        // load the address of the dispatch table to $v0
//        this.assemblySupport.genLoadAddr("$v0", node.getType()+"_dispatch_table");
//        // get the address of the clone method, save it to $v0
//        this.assemblySupport.genLoadWord("$v0", 0, "$v0" );
//        // jump to that clone method
//        this.assemblySupport.genInDirCall("$v0");

        this.assemblySupport.genDirCall(node.getType()+"_init");
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
        if (refName == null) { //local var or field of "this"
            location = (Location) currentSymbolTable.lookup(varName);
        }
        else if (refName.equals("this")) {
            location = (Location) currentSymbolTable.lookup(varName, currentClassFieldLevel);
        }
        else if (refName.equals("super")) {
            location = (Location) currentSymbolTable.lookup(varName,
                    currentClassFieldLevel - 1);
        }
        else { // refName is not "this" or "super" or varName is not "length" for an array
            Location refVarLocation = (Location) currentSymbolTable.lookup(refName);
            //HERE: NEED TO GET THE CLASS SUMBOL TABLE SO I CAN GET THE LOCATION OBJECT OF THE FIELD.


            //SymbolTable refTable = refVarType.getVarSymbolTable();
            //location = (String) refTable.lookup(varName);
        }
        node.getExpr().accept(this);
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
        node.getLeftExpr().accept(this);
        this.assemblySupport.genMove("$v1","$v0");
        node.getRightExpr().accept(this);
        //now compare two values
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
        this.assemblySupport.genMove("$v1","$v0");
        node.getRightExpr().accept(this);
        //now compare two values
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
        if (node.getRef() != null) {
            node.getRef().accept(this);
        }
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
        return null;
    }
}
