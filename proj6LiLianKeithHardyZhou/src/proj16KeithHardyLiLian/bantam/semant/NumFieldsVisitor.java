package proj16KeithHardyLiLian.bantam.semant;

import proj16KeithHardyLiLian.bantam.ast.*;
import proj16KeithHardyLiLian.bantam.util.ClassTreeNode;
import proj16KeithHardyLiLian.bantam.util.Error;
import proj16KeithHardyLiLian.bantam.util.ErrorHandler;
import proj16KeithHardyLiLian.bantam.util.SymbolTable;
import proj16KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Hashtable;

public class NumFieldsVisitor extends Visitor{
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

    /**
     * visit class_ node and initialize the var symbol table and method symbol table by
     * initializing their scope
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node){
        // assign the ctn

        super.visit(node);

        return null;
    }

    /**
     * visit the field node so that it add new entries in variable symbol table
     * @param node the field node
     * @return null
     */
    public Object visit(Field node){
        if(this.varSymbolTable.getSize() != 0 && this.varSymbolTable.peek(node.getName()) != null) {
            errorHandler.register(Error.Kind.SEMANT_ERROR, curClassTreeNode.getASTNode().getFilename(),
                    node.getLineNum(),"Field duplication " + node.getName()+
                            " found in class "+ this.curClassName);
        }
        this.varSymbolTable.add(node.getName(), node.getType());
        return null;
    }

    /**
     * visit the method node and modify entries in the method symbol table
     * @param node the method node
     * @return null
     */
    public Object visit(Method node){
        // if there's no duplication in the current scope
        if(this.methodSymbolTable.getSize() == 0 || this.methodSymbolTable.peek(node.getName()) == null){
            this.methodSymbolTable.add(node.getName(), node);
        }
        else{
            errorHandler.register(Error.Kind.SEMANT_ERROR, curClassTreeNode.getASTNode().getFilename(),
                    node.getLineNum(),"Method name duplication " + node.getName()+
                            " found in class "+ this.curClassName);
        }
        return null;
    }
}
