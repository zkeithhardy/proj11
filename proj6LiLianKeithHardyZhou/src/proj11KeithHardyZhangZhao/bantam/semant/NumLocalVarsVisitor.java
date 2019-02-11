package proj11KeithHardyZhangZhao.bantam.semant;

import proj11KeithHardyZhangZhao.bantam.ast.*;
import proj11KeithHardyZhangZhao.bantam.visitor.Visitor;

import java.util.HashMap;
import java.util.Map;

public class NumLocalVarsVisitor extends Visitor {
    private String className;
    private String methodName;
    private int numLocalVars = 0;
    private HashMap<String, Integer> varMap;

    public Map<String, Integer> getNumLocalVars(Program node){
        varMap = new HashMap<>();
        node.accept(this);
        return varMap;
    }

    public Object visit(Class_ node) {
        className = node.getName();
        super.visit(node);
        return null;
    }

    public Object visit(Field node){
        return null;
    }

    public Object visit(Method node){
        numLocalVars = 0;
        methodName = node.getName();
        super.visit(node);
        varMap.put(className + "." + methodName,numLocalVars + node.getFormalList().getSize());
        return null;

    }

    public Object visit(DeclStmt node){
        numLocalVars++;
        return null;
    }

    public Object visit(ReturnStmt node){
        return null;
    }

    public Object visit(DispatchExpr node){
        return null;
    }

    public Object visit(CastExpr node){
        return null;
    }
}
