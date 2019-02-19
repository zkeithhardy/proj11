/*
 * File: NumLocalVarsVisitor.java
 * Names: Zeb Keith-Hardy, Tia Zhang, Danqing Zhao
 * Class: CS 461
 * Project 11
 * Date: February 13, 2018
 */

package proj12KeithHardyLiLian.bantam.semant;

import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj12KeithHardyLiLian.bantam.util.Error;
import proj12KeithHardyLiLian.bantam.util.ErrorHandler;
import proj12KeithHardyLiLian.bantam.util.SymbolTable;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Hashtable;

public class MainMainVisitor extends Visitor {
    private boolean hasMainClassAndMethod; //tracks whether a Main class with a main method has been found yet
    private Hashtable<String, ClassTreeNode> classMap;
    /**
     * Takes in a Syntax Tree and searches the tree for a main class with a main method
     * @param ast AST to search for main method
     * @return boolean of whether the main method is there
     */
    public boolean hasMain(Program ast, Hashtable<String, ClassTreeNode> classMap, ErrorHandler errorHandler){
        hasMainClassAndMethod = false;
        this.classMap = classMap;
        ast.accept(this);

        if(!hasMainClassAndMethod)
            errorHandler.register(Error.Kind.SEMANT_ERROR, "No proper main method found\n");
        return hasMainClassAndMethod;
    }

    /**
     * visits class node and checks if it is main class
     * @param node the class node
     */
    public Object visit(Class_ node){
        if(hasMainClassAndMethod){
            return null;
        }

        if(node.getName().equals("Main")){
            ClassTreeNode tempClassTreeNode=this.classMap.get(node.getName());
            SymbolTable curMethodSymTbl= tempClassTreeNode.getMethodSymbolTable();
            if (curMethodSymTbl.lookup("main") != null){
                Method mainMethod = (Method)curMethodSymTbl.lookup("main");
                if(mainMethod.getFormalList().getSize() == 0 && mainMethod.getReturnType().equals("void")){
                    hasMainClassAndMethod = true;
                }
            }
        }
        return null;
    }

}
