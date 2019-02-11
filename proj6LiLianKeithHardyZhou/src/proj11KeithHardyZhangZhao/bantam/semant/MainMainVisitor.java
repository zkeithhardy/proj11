/*
 * File: NumLocalVarsVisitor.java
 * Names: Zeb Keith-Hardy, Tia Zhang, Danqing Zhao
 * Class: CS 461
 * Project 11
 * Date: February 13, 2018
 */

package proj11KeithHardyZhangZhao.bantam.semant;

import proj11KeithHardyZhangZhao.bantam.ast.*;
import proj11KeithHardyZhangZhao.bantam.visitor.Visitor;

public class MainMainVisitor extends Visitor {
    private boolean hasMainClass;
    private boolean hasMainMethod;

    /**
     * Takes in a Syntax Tree and searches the tree for a main class with a main method
     * @param ast AST to search for main method
     * @return boolean of whether the main method is there
     */
    public boolean hasMain(Program ast){
        hasMainClass = false;
        hasMainMethod = false;
        ast.accept(this);
        return hasMainMethod;
    }

    /**
     * visits class node and checks if it is main class
     * @param node the class node
     */
    public Object visit(Class_ node){
        if(node.getName().equals("Main")){
            hasMainClass = true;
            node.getMemberList().accept(this);
        }
        return null;
    }

    /**
     * bypass fields
     * @param node the field node
     */
    public Object visit(Field node){
        return null;
    }

    /**
     * updated visitor so that if main method is found in main class, stops searching through
     * the member list
     * @param node the member list node
     * @return
     */
    public Object visit(MemberList node){
        for (ASTNode child : node){
            child.accept(this);
            if(hasMainMethod){
                return null;
            }
        }
        return null;
    }

    /**
     * if in main class, searches for main method with correct format.
     * @param node the method node
     */
    public Object visit(Method node){
        if(hasMainClass){

            //must be called main, have void return, and no parameters
            if(node.getName().equals("main") && node.getReturnType().equals("void")
                    && node.getFormalList().getSize() == 0){
                hasMainMethod = true;
            }
        }
        return null;
    }

}
