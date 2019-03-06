/*
 * File: SuperClassVisitor.java
 * CS461 Project 13
 * Names: Zeb Keith-Hardy, Michael Li, Iris Lian
 * Date: 3/5/19
 */

package proj13KeithHardyLiLian;

import proj13KeithHardyLiLian.bantam.ast.ASTNode;
import proj13KeithHardyLiLian.bantam.ast.ClassList;
import proj13KeithHardyLiLian.bantam.ast.Class_;
import proj13KeithHardyLiLian.bantam.ast.Program;
import proj13KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.HashMap;

public class SuperClassVisitor extends Visitor {

    private String className;
    private Integer lineNum;
    private HashMap<String, Integer> classLineNumMap;


    /**
     * Finds the line number of the super class of the given classname
     * @param ast Program to be searched
     * @param className class to find super class of
     * @return line number of super class
     */
    public Integer findSuperClass(Program ast, String className){
        this.className = className;
        this.classLineNumMap = new HashMap<>();
        ast.accept(this);
        return this.lineNum;
    }

    /**
     * Loops through classes twice to find class line num and then super class line num
     * @param node the class list node
     * @return null
     */
    @Override
    public Object visit(ClassList node) {
        for (ASTNode aNode:node){
            Class_ tempNode = (Class_)aNode;
            classLineNumMap.put(tempNode.getName(), tempNode.getLineNum());
        }
        for(ASTNode aNode:node){
            Class_ tempNode = (Class_)aNode;
            if(tempNode.getName().equals(this.className)) {
                aNode.accept(this);
            }
        }
        return null;
    }


    /**
     * gets the line number of the super class
     * @param node the class node
     * @return null
     */
    @Override
    public Object visit(Class_ node) {
        this.lineNum = classLineNumMap.get(node.getParent());
        return null;
    }
}
