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
    private HashSet<String> memberNames;

    //tracks whether a Main class with a main method has been found yet

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
        this.methodSymbolTable = tempTreeNode.getMethodSymbolTable();
        this.curClassName = node.getName();
        this.memberNames = new HashSet<>();

        super.visit(node);

        return null;
    }

    public Object visit(Field node){
        if(this.varSymbolTable.lookup(node.getName()) == null) {
            this.varSymbolTable.add(node.getName(), node.getType());
            if(!this.memberNames.add(node.getName())){
                errorHandler.register(Error.Kind.SEMANT_ERROR, "Field and method name duplication "
                        + node.getName()+ " found in class "+ this.curClassName+"\n");
            }
        }
        else{
            errorHandler.register(Error.Kind.SEMANT_ERROR, "Field duplication " + node.getName()+
                    " found in class "+ this.curClassName+"\n");
        }
        return null;
    }

    public Object visit(Method node){
        if(this.methodSymbolTable.lookup(node.getName()) == null){
            this.methodSymbolTable.add(node.getName(), node);
            if(!this.memberNames.add(node.getName())){
                errorHandler.register(Error.Kind.SEMANT_ERROR, "Field and method name duplication "
                        + node.getName()+ " found in class "+ this.curClassName+"\n");
            }
        }
        else{
            errorHandler.register(Error.Kind.SEMANT_ERROR, "Method name duplication " + node.getName()+
                    " found in class "+ this.curClassName+"\n");
        }
        return null;
    }
}
