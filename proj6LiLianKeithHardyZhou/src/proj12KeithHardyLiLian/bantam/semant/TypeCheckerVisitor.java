package proj12KeithHardyLiLian.bantam.semant;


import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.util.*;
import proj12KeithHardyLiLian.bantam.util.Error;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Iterator;
import java.util.Set;

public class TypeCheckerVisitor extends Visitor
{
    private ClassTreeNode currentClass;
    private SymbolTable currentSymbolTable;
    private ErrorHandler errorHandler;

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
     * Visit a field node
     *
     * @param node the field node
     * @return null
     */
    public Object visit(Field node) {
        Set<String> classNames = currentClass.getClassMap().keySet();
        String type = node.getType();
        // The fields should have already been added to the symbol table by the
        // SemanticAnalyzer so the only thing to check is the compatibility of the init
        // expr's type with the field's type.

        /*...node's type is not a defined type...*/
        if (!classNames.contains(type) && !type.equals("boolean") && !type.equals("int")) {
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
            if(!(type.equals(initType))) { //have to deal with subtyping. only dealing with current type right now.
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

        /*...the node's return type is not a defined type and not "void"...*/
        if (!classNames.contains(type) && !type.equals("boolean") && !type.equals("int") && !type.equals("void")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The return type " + node.getReturnType() + " of the method "
                            + node.getName() + " is undefined.");
        }

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
        Set<String> classNames = currentClass.getClassMap().keySet();
        String type = node.getType();
        if (!classNames.contains(type) && !type.equals("boolean") && !type.equals("int")) {
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
        currentSymbolTable.enterScope();
        node.getThenStmt().accept(this);
        currentSymbolTable.exitScope();
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
        if (node.getInitExpr() != null) {
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
            if(!node.getPredExpr().equals("boolean")){
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
            if(!node.getExpr().getExprType().equals(currentClass.getMethodSymbolTable())){
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                        currentClass.getASTNode().getFilename(), node.getLineNum(),
                        "The return value" + node.getExpr().getExprType() + " of the method "
                                + " is undefined.");
            }
        }
        return null;
    }


    //UNFINISHED
    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {
        node.getInit().accept(this);
        return null;
    }

    //UNFINISHED
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

    //UNFINISHED
    /**
     * Visit a new array expression node
     *
     * @param node the new array expression node
     * @return result of the visit
     */
    public Object visit(NewArrayExpr node) {
        node.getSize().accept(this);
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
        return null;
    }

    //UNFINISHED
    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    public Object visit(CastExpr node) {
        node.getExpr().accept(this);
        return null;
    }

    //UNFINISHED
    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        if(node.getRefName() != null){

        }
        node.getExpr().accept(this);
        return null;
    }

    //UNFINISHED
    /**
     * Visit an array assignment expression node
     *
     * @param node the array assignment expression node
     * @return result of the visit
     */
    public Object visit(ArrayAssignExpr node) {
        node.getIndex().accept(this);
        node.getExpr().accept(this);
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
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return null
     */
    public Object visit(BinaryCompEqExpr node) {
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals(type2)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being compared for equality are not compatible types.");
        }
        node.setExprType("boolean");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being compared for equality are not compatible types.");
        }
        node.setExprType("boolean");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being compared for equality are not compatible types.");
        }
        node.setExprType("boolean");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being compared for equality are not compatible types.");
        }
        node.setExprType("boolean");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being compared for equality are not compatible types.");
        }
        node.setExprType("boolean");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(type1.equals("boolean") && type2.equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two statements are not boolean.");
        }
        node.setExprType("boolean");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(type1.equals("boolean") && type2.equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two statements are not boolean.");
        }
        node.setExprType("boolean");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being added are not of type int.");
        }
        node.setExprType("int");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being subtracted are not of type int.");
        }
        node.setExprType("int");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being multiplied are not of type int.");
        }
        node.setExprType("int");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being divided are not of type int.");
        }
        node.setExprType("int");
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
        node.getRightExpr().accept(this);
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        if(!type1.equals("int") || !type2.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                    currentClass.getASTNode().getFilename(), node.getLineNum(),
                    "The two values being modulo-ed are not of type int.");
        }
        node.setExprType("int");
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

    //UNFINISHED
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
