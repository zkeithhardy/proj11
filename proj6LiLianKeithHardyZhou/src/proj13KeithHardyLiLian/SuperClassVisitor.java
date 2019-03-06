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


    public Integer findSuperClass(Program ast, String className){
        this.className = className;
        this.classLineNumMap = new HashMap<>();
        ast.accept(this);
        return this.lineNum;
    }

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

    @Override
    public Object visit(Class_ node) {
        this.lineNum = classLineNumMap.get(node.getParent());
        return null;
    }
}
