package proj12KeithHardyLiLian.bantam.semant;

import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj12KeithHardyLiLian.bantam.util.Error;
import proj12KeithHardyLiLian.bantam.util.ErrorHandler;
import proj12KeithHardyLiLian.bantam.util.SymbolTable;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

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


    public Object visit(Field node){
        if(this.varSymbolTable.getSize() != 0 && this.varSymbolTable.peek(node.getName()) != null) {
            errorHandler.register(Error.Kind.SEMANT_ERROR, curClassTreeNode.getASTNode().getFilename(),
                    node.getLineNum(),"Field duplication " + node.getName()+
                            " found in class "+ this.curClassName);
        }
        this.varSymbolTable.add(node.getName(), node.getType());
        return null;
    }

    public Object visit(Method node){
//        System.out.println("Current class: "+curClassName + " Parent: "+this.classMap.get(this.curClassName).getParent().getName());
        // if there's no duplication in the current scope
        if(this.methodSymbolTable.getSize() == 0 || this.methodSymbolTable.peek(node.getName()) == null){
            // if the current class has a parent and that parent has a method with the same name
            // or if the current class has any number of children and any of the children has a method with the same name
//            if((curClassTreeNode.getParent() != null &&
//                    curClassTreeNode.getParent().getMethodSymbolTable().getCurrScopeSize()!=0 &&
//                    curClassTreeNode.getParent().getParent().getMethodSymbolTable().peek(node.getName())!=null)
//                    || (curClassTreeNode.getNumChildren() != 0 &&
//                    childHasDuplicate(curClassTreeNode.getChildrenList(), node.getName()))) {
//                errorHandler.register(Error.Kind.SEMANT_ERROR, curClassTreeNode.getASTNode().getFilename(),
//                        node.getLineNum(),"Method overriding " + node.getName()+
//                        " found in class "+ this.curClassName);
//            }
            this.methodSymbolTable.add(node.getName(), node);
        }
        else{
            errorHandler.register(Error.Kind.SEMANT_ERROR, curClassTreeNode.getASTNode().getFilename(),
                    node.getLineNum(),"Method name duplication " + node.getName()+
                    " found in class "+ this.curClassName);
        }
        return null;
    }

    /**
     *
     * @param children the list of children
     * @param methodName the target method name
     * @return whether any of the children has the target method name
     */
    private boolean childHasDuplicate(Iterator<ClassTreeNode> children, String methodName){
        while(children.hasNext()){
            // if the child has a scope and a method with the same name
            ClassTreeNode curChild = children.next();
            if(curChild.getMethodSymbolTable().getCurrScopeSize() != 0 &&
                    curChild.getMethodSymbolTable().peek(methodName) != null)
                return true;
        }
        return false;
    }
}
