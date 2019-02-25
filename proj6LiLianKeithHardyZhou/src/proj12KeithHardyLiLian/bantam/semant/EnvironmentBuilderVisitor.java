package proj12KeithHardyLiLian.bantam.semant;

import proj12KeithHardyLiLian.bantam.ast.*;
import proj12KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj12KeithHardyLiLian.bantam.util.Error;
import proj12KeithHardyLiLian.bantam.util.ErrorHandler;
import proj12KeithHardyLiLian.bantam.util.SymbolTable;
import proj12KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.HashSet;
import java.util.Hashtable;

public class EnvironmentBuilderVisitor extends Visitor {
    private Hashtable<String, ClassTreeNode> classMap;
    private SymbolTable varSymbolTable;
    private SymbolTable methodSymbolTable;
    private ErrorHandler errorHandler;
    private String curClassName;

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
        ast.accept(this);
        return this.classMap;
    }

    public Object visit(Class_ node){
        // assign the ctn's symbol tables to the local symbol tables
        ClassTreeNode tempTreeNode = this.classMap.get(node.getName());
        this.varSymbolTable = tempTreeNode.getVarSymbolTable();

        if(this.varSymbolTable.getSize() == 0){
            this.varSymbolTable.enterScope();
            ClassTreeNode tempParent = tempTreeNode.getParent();
            while(tempParent != null){
                System.out.println(tempParent.getName());
                SymbolTable parentVarST = tempParent.getVarSymbolTable();
                if(parentVarST.getSize() == 0){
                    parentVarST.enterScope();
                    System.out.println(tempParent.getName()+" entered scope" +" size: "+parentVarST.getCurrScopeSize() +"\n");
                }
                tempParent=tempParent.getParent();
            }

        }

        this.methodSymbolTable = tempTreeNode.getMethodSymbolTable();
        if(this.methodSymbolTable.getSize()==0){
            this.methodSymbolTable.enterScope();
            SymbolTable parentMethodST = tempTreeNode.getParent().getMethodSymbolTable();
            if(parentMethodST.getSize() == 0){
                parentMethodST.enterScope();
            }
        }
        this.curClassName = node.getName();

        super.visit(node);

        return null;
    }

    public Object visit(Field node){
//        System.out.println(node.getName()+" "+this.varSymbolTable.getCurrScopeSize());
        if(this.varSymbolTable.getSize() == 0 || this.varSymbolTable.peek(node.getName()) == null) {
//            System.out.println("Type of "+node.getName()+": "+node.getType());
            this.varSymbolTable.add(node.getName(), node.getType());
        }
        else{
            errorHandler.register(Error.Kind.SEMANT_ERROR, "Field duplication " + node.getName()+
                    " found in class "+ this.curClassName);
        }
        System.out.println("Field Dumping");
        this.varSymbolTable.dump();
        return null;
    }

    public Object visit(Method node){
        if(this.methodSymbolTable.getSize() == 0 || this.methodSymbolTable.peek(node.getName()) == null){
            this.methodSymbolTable.add(node.getName(), node);
        }
        else{
            errorHandler.register(Error.Kind.SEMANT_ERROR, "Method name duplication " + node.getName()+
                    " found in class "+ this.curClassName);
        }
        System.out.println("Method Dumping");
        this.methodSymbolTable.dump();
        return null;
    }
}
