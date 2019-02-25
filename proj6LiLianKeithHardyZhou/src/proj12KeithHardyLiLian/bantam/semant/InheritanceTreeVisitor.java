package proj12KeithHardyLiLian.bantam.semant;

import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj12KeithHardyLiLian.bantam.util.Error;
import proj12KeithHardyLiLian.bantam.util.ErrorHandler;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class InheritanceTreeVisitor extends Visitor {
    private Hashtable<String, ClassTreeNode> classMap;
    private HashMap<String, String> parentMap;
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
        this.parentMap = new HashMap<>();
        this.errorHandler = errorHandler;
        this.parentMap=new HashMap<>();
        ast.accept(this);
        return this.classMap;
    }

    /**
     * Set the parent for all classTreeNodes in the classMap
     * @param node
     * @return
     */
    public Object visit(ClassList node) {
        super.visit(node);
        // detect cyclic extension
        for(Map.Entry<String, String> entry : parentMap.entrySet()){
            System.out.println(entry);
            String parent = entry.getValue();
            String child = entry.getKey();
//            System.out.println("parent: "+parent+" child: "+child+"\n");
            if(!parent.equals("Object") && parentMap.get(parent).equals(child)){
                errorHandler.register(Error.Kind.SEMANT_ERROR, "Cyclic Extension: "+parent+" and "
                +child+"\n");
            }
        }
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
        return null;
    }


    public Object visit(Class_ node){
        ClassTreeNode tempTreeNode = new ClassTreeNode(node,false, true, this.classMap );
        this.classMap.put(tempTreeNode.getName(),tempTreeNode);
//        System.out.println("in Class visit: class name: "+node.getName()+" parent: "+node.getParent()+"\n");
        if(node.getParent().equals("")){
            this.parentMap.put(node.getName(), "Object");
        }
        else {
            this.parentMap.put(node.getName(), node.getParent());
        }
        return null;
    }

}
