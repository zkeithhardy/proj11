package proj12KeithHardyLiLian.bantam.semant;

import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class InheritanceTreeVisitor extends Visitor {
    private Hashtable<String, ClassTreeNode> classMap;
    private HashMap<String, String> parentMap;
    //<child, parent>

    //tracks whether a Main class with a main method has been found yet

    /**
     * return the built class map with all nodes and parent structure
     * @param classMap class map class that is about to be build
     * @return the built class map
     */
    public Hashtable<String, ClassTreeNode> buildClassMap(Program ast, Hashtable<String, ClassTreeNode> classMap){
        this.classMap=classMap;
        ast.accept(this);
        return classMap;
    }

    /**
     * Set the parent for all classTreeNodes in the classMap
     * @param node
     * @return
     */
    public Object visit(ClassList node) {
        super.visit(node);
        Iterator classListIterator= node.iterator();
        while(classListIterator.hasNext()){
            Class_ tempClass=(Class_) classListIterator.next();
            String tempClassName=tempClass.getName();
            String tempClassParentName=tempClass.getParent();
            classMap.get(tempClassName).setParent(classMap.get(tempClassParentName));
        }
        return null;
    }


    public Object visit(Class_ node){
        ClassTreeNode tempTreeNode = new ClassTreeNode(node,false, true, this.classMap );
        this.classMap.put(tempTreeNode.getName(),tempTreeNode);
        this.parentMap.put(node.getName(), node.getParent());
        return null;
    }

    /**
     * bypass fields
     * @param node the field node
     */
    public Object visit(Field node){
        return null;
    }





}
