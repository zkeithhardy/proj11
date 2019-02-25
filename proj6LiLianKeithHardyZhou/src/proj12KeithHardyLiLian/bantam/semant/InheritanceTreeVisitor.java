package proj12KeithHardyLiLian.bantam.semant;

import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj12KeithHardyLiLian.bantam.util.Error;
import proj12KeithHardyLiLian.bantam.util.ErrorHandler;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.*;

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

        System.out.println(classMap.size()+" items in class map \n");

        //check for cyclic inheritance
        classMap.forEach((name, tempTreeNode)->{
            List<String> buildIns = Arrays.asList("Object", "TextIO", "String","Sys");

            if (!buildIns.contains(tempTreeNode.getName())&& tempTreeNode.getParent().getParent()==tempTreeNode){
                System.out.println("");
                ClassTreeNode objectNode=classMap.get("Object");
                ClassTreeNode tempParent=tempTreeNode.getParent();

                objectNode.addChild(tempTreeNode);
                objectNode.addChild(tempParent);

                tempTreeNode.removeChild(tempTreeNode.getParent());
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


    public Object visit(Class_ node){
        ClassTreeNode tempTreeNode = new ClassTreeNode(node,false, true, this.classMap );
        this.classMap.put(tempTreeNode.getName(),tempTreeNode);
        if(node.getParent().equals("")){
            this.parentMap.put(node.getName(), "Object");
        }
        else {
            this.parentMap.put(node.getName(), node.getParent());
        }
        return null;
    }


}
