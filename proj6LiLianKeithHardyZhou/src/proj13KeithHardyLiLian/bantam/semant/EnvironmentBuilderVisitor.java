/*
 * EnvironmentBuilderVisitor.java
 * Zeb Keith-Hardy, Michael Li, Iris Lian
 * Class: CS 461
 * Project 12
 * Date: February 25, 2019
 */


package proj13KeithHardyLiLian.bantam.semant;

import proj13KeithHardyLiLian.bantam.ast.*;
import proj13KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj13KeithHardyLiLian.bantam.util.Error;
import proj13KeithHardyLiLian.bantam.util.ErrorHandler;
import proj13KeithHardyLiLian.bantam.util.SymbolTable;
import proj13KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Hashtable;

public class EnvironmentBuilderVisitor extends Visitor {
    private Hashtable<String, ClassTreeNode> classMap;
    private SymbolTable varSymbolTable;
    private SymbolTable methodSymbolTable;
    private ErrorHandler errorHandler;
    private String curClassName;
    private ClassTreeNode curClassTreeNode;

    /**
     * return the built class map with all nodes and parent structure
     * @param classMap class map class that is about to be build
     * @return the built class map
     */
    public Hashtable<String, ClassTreeNode> buildEnvironment(Program ast, Hashtable<String, ClassTreeNode> classMap,
                                                          ErrorHandler errorHandler){
        this.classMap = classMap;
        this.varSymbolTable = new SymbolTable();
        this.methodSymbolTable = new SymbolTable();
        this.errorHandler = errorHandler;
        this.classMap.get("Object").getVarSymbolTable().enterScope(); // temp solution
        this.classMap.get("Object").getMethodSymbolTable().enterScope();
        ast.accept(this);
        return this.classMap;
    }

    /**
     * visit class_ node and initialize the var symbol table and method symbol table by
     * initializing their scope
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node){
        // assign the ctn's symbol tables to the local symbol tables
        curClassTreeNode = this.classMap.get(node.getName());

        this.varSymbolTable = curClassTreeNode.getVarSymbolTable();
        if(this.varSymbolTable.getSize() == 0){
            this.varSymbolTable.enterScope();
        }

        this.methodSymbolTable = curClassTreeNode.getMethodSymbolTable();
        if(this.methodSymbolTable.getSize()==0){
            this.methodSymbolTable.enterScope();
        }
        this.curClassName = node.getName();

        super.visit(node);

        return null;
    }

    /**
     * visit the field node so that it add new entries in variable symbol table
     * @param node the field node
     * @return null
     */
    public Object visit(Field node){
        if(this.varSymbolTable.getSize() != 0 && this.varSymbolTable.peek(node.getName()) != null) {
            errorHandler.register(Error.Kind.SEMANT_ERROR, curClassTreeNode.getASTNode().getFilename(),
                    node.getLineNum(),"Field duplication " + node.getName()+
                            " found in class "+ this.curClassName);
        }
        this.varSymbolTable.add(node.getName(), node.getType());
        return null;
    }

    /**
     * visit the method node and modify entries in the method symbol table
     * @param node the method node
     * @return null
     */
    public Object visit(Method node){
        // if there's no duplication in the current scope
        if(this.methodSymbolTable.getSize() == 0 || this.methodSymbolTable.peek(node.getName()) == null){
            this.methodSymbolTable.add(node.getName(), node);
        }
        else{
            errorHandler.register(Error.Kind.SEMANT_ERROR, curClassTreeNode.getASTNode().getFilename(),
                    node.getLineNum(),"Method name duplication " + node.getName()+
                    " found in class "+ this.curClassName);
        }
        return null;
    }
}
