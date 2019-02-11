package proj11KeithHardyZhangZhao.bantam.semant;

import proj11KeithHardyZhangZhao.bantam.ast.*;
import proj11KeithHardyZhangZhao.bantam.visitor.Visitor;

public class MainMainVisitor extends Visitor {
    private boolean hasMainClass;
    private boolean hasMainMethod;

    public boolean hasMain(Program ast){
        hasMainClass = false;
        hasMainMethod = false;
        ast.accept(this);
        return hasMainMethod;
    }

    public Object visit(Class_ node){
        if(node.getName().equals("Main")){
            hasMainClass = true;
            node.getMemberList().accept(this);
        }
        return null;
    }

    public Object visit(Field node){
        return null;
    }

    public Object visit(MemberList node){
        for (ASTNode child : node){
            child.accept(this);
            if(hasMainMethod){
                return null;
            }
        }
        return null;
    }

    public Object visit(Method node){
        if(hasMainClass){
            if(node.getName().equals("main") && node.getReturnType().equals("void")
                    && node.getFormalList().getSize() == 0){
                hasMainMethod = true;
            }
        }
        return null;
    }

}
