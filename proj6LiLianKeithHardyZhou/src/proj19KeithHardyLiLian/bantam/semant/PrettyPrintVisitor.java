package proj19KeithHardyLiLian.bantam.semant;

import proj19KeithHardyLiLian.bantam.ast.*;
import proj19KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PrettyPrintVisitor extends Visitor{
    //pretty printed source code
    private StringBuilder sourceCode;
    //level of indentation
    private int indentLevel;
    //boolean that indicates if the node is in condition braces
    private boolean inConditionBraces;
    //map of comment string with their line number
    private HashMap<Integer,String> commentMap;

    /**
     * pretty print the original code from the ASTroot node and
     * return the resulted code as a long string
     * @param node the root node
     * @param commentMap map of comments passed from the scanner
     * @return source code
     */
    public String sourceCode(ASTNode node,HashMap<Integer,String> commentMap){
        this.sourceCode = new StringBuilder();
        this.indentLevel =0;
        this.inConditionBraces = false;
        this.commentMap = commentMap;
        node.accept(this);
        return this.sourceCode.toString();

    }

    /**
     * print the indent level, if the node is in condition braces,
     * we shall not print the scope indent
     */
    private void printScopeIndent(){
        if (!this.inConditionBraces) {
            for (int i = 0; i < indentLevel; i++) {
                this.sourceCode.append("\t");
            }
        }
    }
    /**
     * Visit a class node
     *
     * @param node the class node
     * @return result of the visit
     */
    public Object visit(Class_ node) {
        this.checkForComment(node.getLineNum());
        this.indentLevel += 1;
        if (node.getParent().equals("") ){
            this.sourceCode.append("class ");
            this.sourceCode.append(node.getName());
            this.sourceCode.append("{\n");
        }
        else{
            this.sourceCode.append("class ");
            this.sourceCode.append(node.getName());
            this.sourceCode.append(" extends ");
            this.sourceCode.append(node.getParent());
            this.sourceCode.append("{\n");
        }

        node.getMemberList().accept(this);
        this.indentLevel -= 1;
        this.printScopeIndent();
        this.sourceCode.append("}\n");

        return null;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return result of the visit
     */
    public Object visit(Field node) {
        String type = node.getType();
        String identifier = node.getName();
        this.checkForComment(node.getLineNum());
        this.sourceCode.append("\n");
        this.printScopeIndent();
        this.sourceCode.append(type);
        this.sourceCode.append(" ");
        this.sourceCode.append(identifier);
        if(node.getInit()!= null){
            this.sourceCode.append(" = ");
            node.getInit().accept(this);
        }
        this.sourceCode.append(";\n");
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        String returnType = node.getReturnType();
        String identifier = node.getName();
        this.checkForComment(node.getLineNum());
        this.sourceCode.append("\n");
        printScopeIndent();
        this.sourceCode.append(returnType);
        this.sourceCode.append(" ");
        this.sourceCode.append(identifier);
        node.getFormalList().accept(this);
        this.sourceCode.append("{\n");
        this.indentLevel += 1;
        node.getStmtList().accept(this);
        this.indentLevel -= 1;
        this.printScopeIndent();
        this.sourceCode.append("}\n");

        return null;
    }

    /**
     * Visit a formal node
     *
     * @param node the formal node
     * @return result of the visit
     */
    public Object visit(Formal node) {
        this.sourceCode.append(node.getType());
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getName());
        return null;
    }


    /**
     * Visit a list node of formals
     *
     * @param node the formal list node
     * @return result of the visit
     */
    public Object visit(FormalList node) {
        this.sourceCode.append("(");
        for (Iterator it = node.iterator(); it.hasNext(); ) {
            ((Formal) it.next()).accept(this);
            this.sourceCode.append(", ");
        }
        if(this.sourceCode.toString().endsWith(", ")) {
            String s = this.sourceCode.toString().substring(0, this.sourceCode.toString().length() - 2);
            this.sourceCode.delete(0,this.sourceCode.toString().length());
            this.sourceCode.append(s);
        }
        this.sourceCode.append(")");
        return null;
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {
        String name = node.getName();
        this.checkForComment(node.getLineNum());
        printScopeIndent();
        this.sourceCode.append("var ");
        this.sourceCode.append(name);
        this.sourceCode.append(" = ");
        node.getInit().accept(this);
        this.sourceCode.append(";\n");
        return null;
    }

    /**
     * Visit an expression statement node
     *
     * @param node the expression statement node
     * @return result of the visit
     */
    public Object visit(ExprStmt node) {
        this.checkForComment(node.getLineNum());
        this.printScopeIndent();
        System.out.println(node.getLineNum());
        node.getExpr().accept(this);
        this.sourceCode.append(";\n");
        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        this.checkForComment(node.getLineNum());
        printScopeIndent();
        this.sourceCode.append("if( ");
        this.inConditionBraces = true;
        node.getPredExpr().accept(this);
        this.inConditionBraces = false;
        this.sourceCode.append(" ){\n");
        this.indentLevel += 1;
        node.getThenStmt().accept(this);
        this.indentLevel -= 1;
        this.printScopeIndent();
        this.sourceCode.append("}\n");
        if (node.getElseStmt() != null) {
            this.checkForComment(node.getElseStmt().getLineNum());
            this.printScopeIndent();
            this.sourceCode.append("else {\n");
            this.indentLevel += 1;
            node.getElseStmt().accept(this);
            this.indentLevel -= 1;
            this.printScopeIndent();
            this.sourceCode.append("}\n");
        }
        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        this.checkForComment(node.getLineNum());
        this.printScopeIndent();
        this.sourceCode.append("while( ");
        this.inConditionBraces = true;
        node.getPredExpr().accept(this);
        this.inConditionBraces = false;
        this.sourceCode.append(" ){\n");
        this.indentLevel += 1;
        node.getBodyStmt().accept(this);
        this.indentLevel -= 1;
        this.printScopeIndent();
        this.sourceCode.append("}\n");
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
        this.checkForComment(node.getLineNum());
        this.printScopeIndent();
        this.sourceCode.append("for( ");
        this.inConditionBraces = true;
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        this.sourceCode.append("; ");
        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
        }
        this.sourceCode.append("; ");
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }
        this.inConditionBraces = false;
        this.sourceCode.append(" ){\n");
        this.indentLevel += 1;
        node.getBodyStmt().accept(this);
        this.indentLevel -= 1;
        this.printScopeIndent();
        this.sourceCode.append("}\n");
        return null;
    }

    /**
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return result of the visit
     */
    public Object visit(BreakStmt node) {
        this.checkForComment(node.getLineNum());
        printScopeIndent();
        this.sourceCode.append("break;\n");
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return result of the visit
     */
    public Object visit(BlockStmt node) {
        this.checkForComment(node.getLineNum());
        node.getStmtList().accept(this);
        return null;
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return result of the visit
     */
    public Object visit(ReturnStmt node) {
        this.checkForComment(node.getLineNum());
        printScopeIndent();
        this.sourceCode.append("return");
        if (node.getExpr() != null) {
            this.sourceCode.append(" ");
            node.getExpr().accept(this);
        }
        this.sourceCode.append(";\n");
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
            this.sourceCode.append(", ");
        }
        if(this.sourceCode.toString().endsWith(", ")) {
            String s = this.sourceCode.substring(0, this.sourceCode.length() - 2);
            this.sourceCode.delete(0,this.sourceCode.length());
            this.sourceCode.append(s);
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
        if(node.getRefExpr() != null){
            if(!((node.getRefExpr() instanceof VarExpr) || (node.getRefExpr() instanceof DispatchExpr)
                    || (node.getRefExpr() instanceof ArrayExpr)))
                this.sourceCode.append("( ");
            node.getRefExpr().accept(this);
            if(!((node.getRefExpr() instanceof VarExpr) || (node.getRefExpr() instanceof DispatchExpr)
                    || (node.getRefExpr() instanceof ArrayExpr)))
                this.sourceCode.append(" )");
            this.sourceCode.append(".");
        }
        this.sourceCode.append(node.getMethodName());
        this.sourceCode.append("(");
        node.getActualList().accept(this);
        this.sourceCode.append(")");
        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return result of the visit
     */
    public Object visit(NewExpr node) {
        this.sourceCode.append("new ");
        this.sourceCode.append(node.getType());
        this.sourceCode.append("()");
        return null;
    }

    /**
     * Visit a new array expression node
     *
     * @param node the new array expression node
     * @return result of the visit
     */
    public Object visit(NewArrayExpr node) {
        this.sourceCode.append("new ");
        this.sourceCode.append(node.getType());
        this.sourceCode.append("[");
        node.getSize().accept(this);
        this.sourceCode.append("]");
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
        this.sourceCode.append(" instanceof ");
        this.sourceCode.append(node.getType());
        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    public Object visit(CastExpr node) {
        this.sourceCode.append("cast(");
        this.sourceCode.append(node.getType());
        this.sourceCode.append(",");
        node.getExpr().accept(this);
        this.sourceCode.append(")");
        return null;
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        if(node.getRefName()!=null){
            this.sourceCode.append(node.getRefName());
            this.sourceCode.append(".");
        }
        this.sourceCode.append(node.getName());
        this.sourceCode.append(" = ");
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit an array assignment expression node
     *
     * @param node the array assignment expression node
     * @return result of the visit
     */
    public Object visit(ArrayAssignExpr node) {
        if(node.getRefName()!=null){
            this.sourceCode.append(node.getRefName());
            this.sourceCode.append(".");
        }
        this.sourceCode.append(node.getName());
        this.sourceCode.append("[");
        node.getIndex().accept(this);
        this.sourceCode.append("] = ");
        node.getExpr().accept(this);
        return null;
    }


    /**
     * Visit a binary comparison equal to expression node
     *
     * @param node the binary comparison equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompEqExpr node) {
        node.getLeftExpr().accept(this);
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison not equal to expression node
     *
     * @param node the binary comparison not equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompNeExpr node) {
        node.getLeftExpr().accept(this);
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less to or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLeqExpr node) {
        node.getLeftExpr().accept(this);
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        node.getRightExpr().accept(this);
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
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        node.getRightExpr().accept(this);
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
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        node.getRightExpr().accept(this);
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
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithPlusExpr node) {
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getLeftExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        if(node.getRightExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getRightExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithMinusExpr node) {
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getLeftExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        if(node.getRightExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getRightExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithTimesExpr node) {
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getLeftExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        if(node.getRightExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getRightExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithDivideExpr node) {
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getLeftExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        if(node.getRightExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getRightExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        return null;
    }


    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithModulusExpr node) {
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getLeftExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        if(node.getRightExpr() instanceof BinaryArithExpr){
            this.sourceCode.append("( ");
        }
        node.getRightExpr().accept(this);
        if(node.getLeftExpr() instanceof BinaryArithExpr){
            this.sourceCode.append(" )");
        }
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
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        node.getRightExpr().accept(this);
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicOrExpr node) {
        node.getLeftExpr().accept(this);
        this.sourceCode.append(" ");
        this.sourceCode.append(node.getOpName());
        this.sourceCode.append(" ");
        node.getRightExpr().accept(this);
        return null;
    }


    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return result of the visit
     */
    public Object visit(UnaryNegExpr node) {
        this.sourceCode.append(node.getOpName());
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return result of the visit
     */
    public Object visit(UnaryNotExpr node) {
        this.sourceCode.append(node.getOpName());
        node.getExpr().accept(this);
        return null;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    public Object visit(UnaryIncrExpr node) {
        if(!node.isPostfix()){
            this.sourceCode.append(node.getOpName());
        }
        node.getExpr().accept(this);
        if(node.isPostfix()){
            this.sourceCode.append(node.getOpName());
        }
        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return result of the visit
     */
    public Object visit(UnaryDecrExpr node) {
        if(!node.isPostfix()){
            this.sourceCode.append(node.getOpName());
        }
        node.getExpr().accept(this);
        if(node.isPostfix()){
            this.sourceCode.append(node.getOpName());
        }
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
            if(!((node.getRef() instanceof VarExpr) || (node.getRef() instanceof DispatchExpr)
                    || (node.getRef() instanceof ArrayExpr)))
                this.sourceCode.append("( ");
            node.getRef().accept(this);
            if(!((node.getRef() instanceof VarExpr) || (node.getRef() instanceof DispatchExpr)
                    || (node.getRef() instanceof ArrayExpr)))
                this.sourceCode.append(" )");
            this.sourceCode.append(".");
        }
        this.sourceCode.append(node.getName());
        return null;
    }

    /**
     * Visit an array expression node
     *
     * @param node the array expression node
     * @return result of the visit
     */
    public Object visit(ArrayExpr node) {
        if (node.getRef() != null) {
            node.getRef().accept(this);
            this.sourceCode.append(".");
        }
        System.out.println(node.getName());
        if (node.getName()!=null){
            this.sourceCode.append(node.getName());
        }
        this.sourceCode.append("[");
        node.getIndex().accept(this);
        this.sourceCode.append("]");
        return null;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return result of the visit
     */
    public Object visit(ConstIntExpr node) {
        this.sourceCode.append(node.getIntConstant()) ;
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return result of the visit
     */
    public Object visit(ConstBooleanExpr node) {
        this.sourceCode.append(node.getConstant()) ;
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return result of the visit
     */
    public Object visit(ConstStringExpr node) {
        this.sourceCode.append("\"");
        this.sourceCode.append(node.getConstant());
        this.sourceCode.append("\"");
        return null;
    }

    /**
     * Iterate over the commentMap and insert comments in a pretty printed version
     * @param linenum the line of code we are interested in
     */
    public void checkForComment(int linenum){
        Iterator keyItr = this.commentMap.keySet().iterator();
        ArrayList<Integer> usedComments = new ArrayList<>();
        while(keyItr.hasNext()){
            Integer commentLine = (Integer) keyItr.next();
            if(commentLine < linenum){
                this.sourceCode.append("\n");
                this.printScopeIndent();
                this.sourceCode.append(this.commentMap.get(commentLine));
                this.sourceCode.append("\n");
                usedComments.add(commentLine);
            }
        }
        for(int i = 0; i < usedComments.size(); i++){
            this.commentMap.remove(usedComments.get(i));
        }

    }
}
