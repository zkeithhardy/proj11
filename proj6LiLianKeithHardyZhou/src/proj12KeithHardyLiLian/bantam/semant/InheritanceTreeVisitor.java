/*
 * TypeCheckerVisitor.java
 * Zeb Keith-Hardy, Michael Li, Iris Lian
 * Class: CS 461
 * Project 12
 * Date: February 25, 2019
 */


package proj12KeithHardyLiLian.bantam.semant;

import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj12KeithHardyLiLian.bantam.util.Error;
import proj12KeithHardyLiLian.bantam.util.ErrorHandler;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.*;

public class InheritanceTreeVisitor extends Visitor {
    private Hashtable<String, ClassTreeNode> classMap;
    //<child, parent>
    private ErrorHandler errorHandler;

    //tracks whether a Main class with a main method has been found yet

    /**
     * return the built class map with all nodes and parent structure
     * @param classMap class map class that is about to be build
     * @return the built class map
     */
    public Hashtable<String, ClassTreeNode> buildClassMap(Program ast, Hashtable<String, ClassTreeNode> classMap,
                                                          ErrorHandler errorHandler){
        this.classMap = classMap;
        this.errorHandler = errorHandler;
        ast.accept(this);
        return this.classMap;
    }

    /**
     * Set the parent for all classTreeNodes in the classMap
     * @param node the class List node
     * @return
     */
    public Object visit(ClassList node) {
        super.visit(node);
        // set parents
        Iterator classListIterator= node.iterator();
        while(classListIterator.hasNext()){
            Class_ tempClass = (Class_) classListIterator.next();
            String tempClassName = tempClass.getName();
            String tempClassParentName;
            if(tempClass.getParent().equals("")){
                tempClassParentName = "Object";
            }
            else{
                tempClassParentName = tempClass.getParent();
            }
            classMap.get(tempClassName).setParent(classMap.get(tempClassParentName));
        }

        List<String> buildIns = Arrays.asList("Object", "TextIO", "String","Sys");
        //check for cyclic inheritance
        classMap.forEach((name, tempTreeNode)->{
            if (!buildIns.contains(tempTreeNode.getName())&& tempTreeNode.getParent().getParent()==tempTreeNode){
                //get rid of any cycles you find in the inheritance tree by taking any node in the cycle and
                // (a) making that node a child of the Object class instead of its current parent and
                // (b) setting its parent to the Object class.
                ClassTreeNode objectNode=classMap.get("Object");
                ClassTreeNode tempParent=tempTreeNode.getParent();

                objectNode.addChild(tempTreeNode);
                objectNode.addChild(tempParent);

                tempTreeNode.removeChild(tempParent);
                tempTreeNode.setParent(objectNode);

                tempParent.removeChild(tempTreeNode);
                tempParent.setParent(objectNode);

                errorHandler.register(Error.Kind.SEMANT_ERROR, tempParent.getASTNode().getFilename(),
                        tempParent.getASTNode().getLineNum(),"Cyclic inheritance found in class "+
                                tempTreeNode.getName()+" and class " + tempParent.getName());
            }
        });
        return null;
    }

    /**
     * Visit the Class_ node and put it into the class map as well as parent map
     * @param node the class node
     * @return
     */
    public Object visit(Class_ node){
        ClassTreeNode tempTreeNode = new ClassTreeNode(node,false, true, this.classMap );
        this.classMap.put(tempTreeNode.getName(),tempTreeNode);
        return null;
    }
}
