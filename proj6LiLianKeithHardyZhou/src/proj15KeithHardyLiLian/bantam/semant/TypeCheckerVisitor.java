/*
 * TypeCheckerVisitor.java
 * Zeb Keith-Hardy, Michael Li, Iris Lian
 * Class: CS 461
 * Project 12
 * Date: February 25, 2019
 */
package proj15KeithHardyLiLian.bantam.semant;


import proj15KeithHardyLiLian.bantam.ast.*;
import proj15KeithHardyLiLian.bantam.util.*;
import proj15KeithHardyLiLian.bantam.util.Error;
import proj15KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class TypeCheckerVisitor extends Visitor
{
    private ClassTreeNode currentClass;
    private SymbolTable currentSymbolTable;
    private ErrorHandler errorHandler;
    private String currentMethodType;

    /**
     * type check the given program with its SymbolTable and errorHandler
     * @param ast the prgram to be checked
     * @param currentClass the current class tree node
     * @param currentSymbolTable the current symbol table
     * @param errorHandler the error handler
     * @return boolean to indicate it has been successfully checked or not
     */
    public boolean typeCheck(Program ast,ClassTreeNode currentClass,
                             SymbolTable currentSymbolTable,ErrorHandler errorHandler){
        boolean success = true;
        this.currentClass = currentClass;
        this.currentSymbolTable = currentSymbolTable;
        this.errorHandler = errorHandler;

        ast.accept(this);

        if(this.errorHandler.errorsFound()){
            success = false;
        }

        return success;
    }

    /**
     * visit the class node
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node){
        ClassTreeNode newClass = this.currentClass.getClassMap().get(node.getName());
        currentClass = newClass;
        currentSymbolTable = newClass.getVarSymbolTable();
        super.visit(node);
        return null;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return null
     */
    public Object visit(Field node) {
        Set<String> classNames = currentClass.getClassMap().keySet();
        String type = node.getType();

        //check for arrays as well
        ArrayList<String> classNamesArray = new ArrayList<>();
        classNamesArray.addAll(classNames);
        for(int i = 0; i < classNamesArray.size(); i++){
            classNamesArray.set(i,classNamesArray.get(i) + "[]");
        }
        // The fields should have already been added to the symbol table by the
        // SemanticAnalyzer so the only thing to check is the compatibility of the init
        // expr's type with the field's type.

        /*...node's type is not a defined type...*/
        if (!classNames.contains(type) && !classNamesArray.contains(type) && !type.equals("boolean")
                && !type.equals("int") && !type.equals("int[]") && !type.equals("boolean[]")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The declared type " + node.getType() + " of the field "
                            + node.getName() + " is undefined.");
        }
        Expr initExpr = node.getInit();
        if (initExpr != null) {
            initExpr.accept(this);
            String initType = initExpr.getExprType();
            /*...the initExpr's type is not a subtype of the node's type...*/
            if(!(type.equals(initType))) {
                if(classNames.contains(type)){
                    ClassTreeNode classNode = currentClass.getClassMap().get(type);
                    Iterator<ClassTreeNode> subclasses = classNode.getChildrenList();

                    //loop through children of class
                    //if initType is in children, return out (this is an allowed Type)
                    //otherwise register the error to the error handler
                    while(subclasses.hasNext()){

                        ClassTreeNode subclassNode = subclasses.next();
                        if(subclassNode.getName().equals(initType)){
                            return null;
                        }
                    }
                }
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The type of the initializer is " + initExpr.getExprType()
                                + " which is not compatible with the " + node.getName() +
                                " field's type " + node.getType());
            }
        }
        //Note: if there is no initExpr, then leave it to the Code Generator to
        //      initialize it to the default value since it is irrelevant to the
        //      SemanticAnalyzer.
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the Method node to visit
     * @return null
     */
    public Object visit(Method node) {
        Set<String> classNames = currentClass.getClassMap().keySet();
        String type = node.getReturnType();

        //check for arrays as well
        ArrayList<String> classNamesArray = new ArrayList<>();
        classNamesArray.addAll(classNames);
        for(int i = 0; i < classNamesArray.size(); i++){
            classNamesArray.set(i,classNamesArray.get(i) + "[]");
        }

        /*...the node's return type is not a defined type and not "void"...*/
        if (!classNames.contains(type) && !classNamesArray.contains(type) &&
                !type.equals("boolean") && !type.equals("int") && !type.equals("int[]")
                && !type.equals("boolean[]") && !type.equals("void")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The return type " + node.getReturnType() + " of the method "
                            + node.getName() + " is undefined.");
        }
        this.currentMethodType = type;

        //create a new scope for the method body
        currentSymbolTable.enterScope();
        node.getFormalList().accept(this);
        node.getStmtList().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a formal parameter node
     *
     * @param node the Formal node
     * @return null
     */
    public Object visit(Formal node) {
        //grab all possible types for the object
        Set<String> classNames = currentClass.getClassMap().keySet();
        String type = node.getType();

        //check for arrays as well
        ArrayList<String> classNamesArray = new ArrayList<>();
        classNamesArray.addAll(classNames);
        for(int i = 0; i < classNamesArray.size(); i++){
            classNamesArray.set(i,classNamesArray.get(i) + "[]");
        }

        //not a valid type
        if (!classNames.contains(type) && !classNamesArray.contains(type) && !type.equals("boolean")
                && !type.equals("int") && !type.equals("int[]") && !type.equals("boolean[]")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The declared type " + node.getType() + " of the formal" +
                            " parameter " + node.getName() + " is undefined.");
        }

        // add it to the current scope
        currentSymbolTable.add(node.getName(), node.getType());

        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return null
     */
    public Object visit(WhileStmt node) {
        node.getPredExpr().accept(this);

        if(!node.getPredExpr().getExprType().equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type of the predicate is " + node.getPredExpr().getExprType()
                            + " which is not boolean.");
        }

        //enter scope for while loop
        currentSymbolTable.enterScope();
        node.getBodyStmt().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a if statement node
     *
     * @param node the if statement node
     * @return null
     */
    public Object visit(IfStmt node) {
        node.getPredExpr().accept(this);

        if(!node.getPredExpr().getExprType().equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type of the predicate is " + node.getPredExpr().getExprType()
                            + " which is not boolean.");
        }

        //enter scope for body
        currentSymbolTable.enterScope();
        node.getThenStmt().accept(this);
        currentSymbolTable.exitScope();

        //enter scope for else
        if (node.getElseStmt() != null) {
            currentSymbolTable.enterScope();
            node.getElseStmt().accept(this);
            currentSymbolTable.exitScope();
        }
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {

        //check init type
        if (node.getInitExpr() != null) {
            AssignExpr init = (AssignExpr) node.getInitExpr();
            currentSymbolTable.add(init.getName(),"int");
            node.getInitExpr().accept(this);
            if(!node.getInitExpr().getExprType().equals("int")) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The type of the Init is " + node.getInitExpr().getExprType()
                                + " which is not int.");
            }
        }

        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
            if(!node.getPredExpr().getExprType().equals("boolean")){
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The type of the predicate is " + node.getPredExpr().getExprType()
                                + " which is not boolean.");
            }
        }

        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
            if(!node.getUpdateExpr().getExprType().equals("int")) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The type of the update is " + node.getUpdateExpr().getExprType()
                                + " which is not int.");
            }
        }
        currentSymbolTable.enterScope();
        node.getBodyStmt().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return null
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

            if(!node.getExpr().getExprType().equals(this.currentMethodType)){
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The return value " + node.getExpr().getExprType() + " of the method"
                                + " is of the wrong type.");
            }
        }
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
        node.setType(node.getInit().getExprType());

        //make sure that variable is not being declared null: not legal in Bantam
        if( node.getInit().getExprType().equals("null")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Cannot declare a variable as null.");
        }

        //make sure variable name is not a reserved keyword
        if(SemanticAnalyzer.reservedIdentifiers.contains(node.getName())){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Cannot use the reserved keyword " + node.getName() + " to declare a variable.");
        }

        //add var to symbol table if not already there, register an error otherwise
        if(currentSymbolTable.peek(node.getName()) == null){
            currentSymbolTable.add(node.getName(),node.getType());
        }else{
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The variable " + node.getName() + " has already been declared in this scope.");
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
            node.getRefExpr().accept(this);
        }

        //find corresponding method
        Method method = (Method) currentClass.getMethodSymbolTable().lookup(node.getMethodName());
        if(method == null){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Method " + node.getMethodName() + " is not declared in this class.");
                    node.setExprType("Object");
                    return null;
        }

        String methodType = method.getReturnType();
        node.setExprType(methodType);
        node.getActualList().accept(this);

        //now have to check Actual List against Formal List for the Method.
        Iterator<ASTNode> formalIterator = method.getFormalList().iterator();
        Iterator<ASTNode> actualIterator = node.getActualList().iterator();

        while (formalIterator.hasNext() && actualIterator.hasNext()){
             Formal formal = (Formal) formalIterator.next();
             Expr actual = (Expr) actualIterator.next();
             String formalType = formal.getType();
             String actualType = actual.getExprType();

             //parameter of incorrect type passed in
            if(!formalType.equals(actualType)) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "Cannot pass value of type " + actualType + " into method " + method.getName() +
                        " where it requires an argument of type " + formalType + "." );
            }

        }

        //incorrect number of arguments passed into method call
        if(formalIterator.hasNext() || actualIterator.hasNext()){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Incorrect number of arguments passed into method " + method.getName() + "." );
        }

        node.setExprType(method.getReturnType());
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

        if(node.getName().equals("this")){
            node.setExprType(currentClass.getName());
            return null;
        }
        if(node.getName().equals("super")){
            node.setExprType(currentClass.getParent().getName());
            return null;
        }

        Object type = currentSymbolTable.lookup(node.getName());

        //undeclared variable
        if(type == null){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Variable " + node.getName()  + " has not been declared in this scope");
                    node.setExprType("Object");//to allow it to keep analyzing
        }else{
            node.setExprType((String)type);
        }

        if(node.getName().equals("null")){
            node.setExprType("null");
        }
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

        if(node.getExpr().getExprType().equals("int") || node.getExpr().getExprType().equals("boolean")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Cannot use instanceof on a variable of primitive type");
        }

        String superType = node.getType();
        node.setUpCheck(true);

        ClassTreeNode exprClass = currentClass.lookupClass(superType);

        //invalid instance class
        if(exprClass == null){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Class " + node.getType() + " does not exist");
        }

        node.setExprType("boolean");
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

        String currentType = node.getExpr().getExprType();
        String castType = node.getType();

        if(node.getExpr().getExprType().equals("int") || node.getExpr().getExprType().equals("boolean")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Cannot use cast on a variable of primitive type " + node.getExpr().getExprType());
            node.setExprType(castType);
            return null;
        }

        //gather all of the possible parents to be cast to for an upcast
        ClassTreeNode exprClass = currentClass.lookupClass(currentType);
        ArrayList<String> parents = new ArrayList<>();
        ClassTreeNode parent = exprClass.getParent();
        while(parent != null){
            parents.add(parent.getName());
            parent = parent.getParent();
        }

        //look for upcasts
        if(parents.contains(castType)){
            node.setUpCast(true);
            node.setExprType(castType);
            return null;
        }

        //gather all possible downcasts and look for one
        Iterator<ClassTreeNode> children = exprClass.getChildrenList();
        while(children.hasNext()){
            ClassTreeNode child = children.next();
            if(child.getName().equals(castType)){
                node.setUpCast(false);
                node.setExprType(castType);
                return null;
            }
        }

        //cast was not of a valid type
        errorHandler.register(Error.Kind.SEMANT_ERROR,
                currentClass.getASTNode().getFilename(), node.getLineNum(),
                "Class " + node.getType() + " is not a super class or subclass of class" +
                node.getExpr().getExprType());
        return null;
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        Object leftType;
        if(node.getRefName() != null){
            switch (node.getRefName()){
                case "this":
                    leftType = currentSymbolTable.peek(node.getName(),0);
                    break;

                case "super":
                    leftType = currentClass.getParent().getVarSymbolTable().lookup(node.getName());
                    break;

                //look for type in available classes
                default:
                    leftType = currentSymbolTable.lookup(node.getRefName());
                    if(leftType.equals("int") || leftType.equals("boolean")){
                        errorHandler.register(Error.Kind.SEMANT_ERROR,
                                currentClass.getASTNode().getFilename(), node.getLineNum(),
                                "Invalid var expression, primitives do not have fields");
                    }else{
                        leftType = currentClass.getClassMap().get(leftType).
                                getVarSymbolTable().lookup(node.getName());
                        if(leftType == null){
                            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                                    "The variable " + node.getName() + " has not been declared");
                        }
                    }
                    break;
            }
        }else{
            //no ref in this case
            leftType = currentSymbolTable.lookup(node.getName());
        }

        //could not be found
        if(leftType==null){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The variable " + node.getName() + " has not been declared");
                    leftType = "Object";
        }

        //check type of expression
        node.getExpr().accept(this);
        String rightType = node.getExpr().getExprType();
        if(!leftType.equals(rightType)){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Cannot assign expression of type " + node.getExpr().getExprType()
                            + " to variable of type " + leftType);
        }
        node.setExprType(rightType);
        return null;
    }

    /**
     * Visit an array assignment expression node
     *
     * @param node the array assignment expression node
     * @return result of the visit
     */
    public Object visit(ArrayAssignExpr node) {
        node.getIndex().accept(this);
        if (!node.getIndex().getExprType().equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Index of Array is not of type int");
        }

        Object leftType;
        if(node.getRefName() != null){
            switch (node.getRefName()){
                case "this":
                    leftType = currentSymbolTable.lookup(node.getName(),1);
                    break;

                case "super":
                    leftType = currentSymbolTable.lookup(node.getName(),0);
                    break;

                //look for ref type
                default:
                    leftType = currentSymbolTable.lookup(node.getRefName());
                    if(leftType.equals("int") || leftType.equals("boolean")){
                        errorHandler.register(Error.Kind.SEMANT_ERROR,
                                currentClass.getASTNode().getFilename(), node.getLineNum(),
                                "Invalid var expression, primitives do not have fields");
                    }else{
                        leftType = currentClass.getClassMap().get(leftType).
                                getVarSymbolTable().lookup(node.getName());
                        if(leftType == null){
                            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                                    "The variable " + node.getName() + " has not been declared");
                        }
                    }
                    break;
            }
        }else{
            //no ref
            leftType = currentSymbolTable.lookup(node.getName());
        }

        //could not find var
        if(leftType==null){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The variable " + node.getName() + " has not been declared");
                    leftType = "Object[]";
        }

        //type check both side make sure they match
        node.getExpr().accept(this);
        String rightType = node.getExpr().getExprType();
        String left = (String) leftType;
        if(!left.substring(0,left.length()-2).equals(rightType)){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Cannot assign expression of type " + node.getExpr().getExprType() + " to variable" +
                            " of type " + leftType);
        }
        node.setExprType(rightType);
        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return null
     */
    public Object visit(NewExpr node) {
        Set<String> classNames = currentClass.getClassMap().keySet();
        String type = node.getType();
        /*...the node's type is not a defined class type...*/
        if(!classNames.contains(type) && !type.equals("int") && !type.equals("boolean") ) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type " + node.getType() + " does not exist.");
            node.setExprType("Object"); // to allow analysis to continue
        }
        else {
            node.setExprType(node.getType());
        }
        return null;
    }

    /**
     * Visit a new array expression node
     *
     * @param node the new array expression node
     * @return result of the visit
     */
    public Object visit(NewArrayExpr node) {
        node.getSize().accept(this);
        if(!node.getSize().getExprType().equals("int")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Size of New Array is not of type int");
        }

        Set<String> classNames = currentClass.getClassMap().keySet();
        String type = node.getType();
        /*...the node's type is not a defined class type...*/
        if(!classNames.contains(type) && !type.equals("int") && !type.equals("boolean") ) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The type " + node.getType() + " does not exist.");
            node.setExprType("Object"); // to allow analysis to continue
        }
        else {
            node.setExprType(node.getType() + "[]");
        }
        return null;
    }

    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return null
     */
    public Object visit(BinaryCompEqExpr node) {
        this.checkEquality(node);
        return null;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompNeExpr node) {
        this.checkEquality(node);
        return null;
    }

    /**
     * Type checks generic Binary Comp Expressions
     * @param node node to be type checked
     */
    private void checkEquality(BinaryCompExpr node){
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();

        if(!type1.equals(type2)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being compared for equality are not compatible types.");
        }
        node.setExprType("boolean");
    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLtExpr node) {
        this.checkBinaryCompInt(node);
        return null;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLeqExpr node) {
        this.checkBinaryCompInt(node);
        return null;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGtExpr node) {
        this.checkBinaryCompInt(node);
        return null;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGeqExpr node) {
        this.checkBinaryCompInt(node);
        return null;
    }

    /**
     * Type checks BinaryCompExpr with integer operands
     * @param node node to be type checked
     */
    private void checkBinaryCompInt(BinaryCompExpr node){
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();

        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being compared for equality are not compatible types.");
        }
        node.setExprType("boolean");
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicAndExpr node) {
        this.checkBinaryLogicExpr(node);
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicOrExpr node) {
        this.checkBinaryLogicExpr(node);
        return null;
    }

    /**
     * Type checks Binary Logic Expressions
     * @param node Binary Logic node to type check
     */
    private void checkBinaryLogicExpr(BinaryLogicExpr node){
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();

        if(type1.equals("boolean") && type2.equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two statements are not boolean.");
        }
        node.setExprType("boolean");
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithPlusExpr node) {
        this.checkBinaryArithNode(node,"The two values being added are not of type int.");
        return null;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithMinusExpr node) {
        this.checkBinaryArithNode(node,"The two values being subtracted are not of type int.");
        return null;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithTimesExpr node) {
        this.checkBinaryArithNode(node,"The two values being multiplied are not of type int.");
        return null;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithDivideExpr node) {
        this.checkBinaryArithNode(node,"The two values being divided are not of type int.");
        return null;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithModulusExpr node) {
        this.checkBinaryArithNode(node,"The two values being modulo-ed are not of type int.");
        return null;
    }

    /**
     * Type checks all BinaryArithExpr nodes to make sure operands are of type int
     * @param node node to be type checked
     * @param errMsg error message registered with the error handler
     */
    private void checkBinaryArithNode(BinaryArithExpr node, String errMsg){
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();

        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(), errMsg);
        }
        node.setExprType("int");
    }

    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return result of the visit
     */
    public Object visit(UnaryNegExpr node) {
        node.getExpr().accept(this);
        String type = node.getExpr().getExprType();

        if(!type.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The neg (-) operator applies only to int expressions," +
                            " not " + type + " expressions.");
        }
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return null
     */
    public Object visit(UnaryNotExpr node) {
        node.getExpr().accept(this);
        String type = node.getExpr().getExprType();

        if(!type.equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The not (!) operator applies only to boolean expressions," +
                            " not " + type + " expressions.");
        }
        node.setExprType("boolean");
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
        String type = node.getExpr().getExprType();

        if(!type.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The inc (++) operator applies only to int expressions," +
                            " not " + type + " expressions.");
        }
        node.setExprType("int");
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
        String type = node.getExpr().getExprType();

        if(!type.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The decr (--) operator applies only to int expressions," +
                            " not " + type + " expressions.");
        }
        node.setExprType("int");
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
        }
        node.getIndex().accept(this);

        if(!node.getIndex().getExprType().equals("int")){
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Index of Array is not of type int");
        }

        Object type = currentSymbolTable.lookup(node.getName());

        String typeString = "";
        if (type != null) {
            typeString = (String) type;
        }

        if(type == null || !typeString.endsWith("[]")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "Array " + node.getName() + " has not been declared in this scope");
        }

        node.setExprType(typeString);
        return null;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return null
     */
    public Object visit(ConstIntExpr node) {
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return null
     */
    public Object visit(ConstBooleanExpr node) {
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return null
     */
    public Object visit(ConstStringExpr node) {
        node.setExprType("String");
        return null;
    }

}
