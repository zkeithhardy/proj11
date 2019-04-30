package proj17KeithHardyLiLian.bantam.builder;

import proj17KeithHardyLiLian.bantam.ast.*;

public class ASTNodeBuilder {


    public ArrayAssignExpr buildArrayAssignExpr(int position, String refName, String name, Expr index, Expr expr){
        return new ArrayAssignExpr(position, refName, name, index, expr);
    }


    public ArrayExpr buildArrayExpr(int position, Expr expr, String name, Expr idx){
        return new ArrayExpr(position,expr,name,idx);
    }

    public AssignExpr buildAssignExpr(int position, String refName, String name, Expr expr){
        return new AssignExpr(position, refName, name, expr);
    }

    public BinaryArithDivideExpr buildBinaryArithDivideExpr(int position, Expr left, Expr right){
        return new BinaryArithDivideExpr(position,left,right);
    }

    public BinaryArithMinusExpr buildBinaryArithMinusExpr(int position, Expr left, Expr right){
        return new BinaryArithMinusExpr(position,left,right);
    }

    public BinaryArithModulusExpr buildBinaryArithModulusExpr(int position, Expr left, Expr right){
        return new BinaryArithModulusExpr(position,left,right);
    }

    public BinaryArithPlusExpr buildBinaryArithPlusExpr(int position, Expr left, Expr right){
        return new BinaryArithPlusExpr(position,left,right);
    }

    public BinaryArithTimesExpr buildBinaryArithTimesExpr(int position, Expr left, Expr right){
        return new BinaryArithTimesExpr(position,left,right);
    }

    public BinaryCompEqExpr buildBinaryCompEqExpr(int position, Expr left, Expr right){
        return new BinaryCompEqExpr(position,left,right);
    }

    public BinaryCompGeqExpr buildBinaryCompGeqExpr(int position, Expr left, Expr right){
        return new BinaryCompGeqExpr(position,left,right);
    }

    public BinaryCompGtExpr buildBinaryCompGtExpr(int position, Expr left, Expr right){
        return new BinaryCompGtExpr(position,left,right);
    }

    public BinaryCompLeqExpr buildBinaryCompLeqExpr(int position, Expr left, Expr right){
        return new BinaryCompLeqExpr(position,left,right);
    }

    public BinaryCompLtExpr buildBinaryCompLtExpr(int position, Expr left, Expr right){
        return new BinaryCompLtExpr(position,left,right);
    }

    public BinaryCompNeExpr buildBinaryCompNeExpr(int position, Expr left, Expr right){
        return new BinaryCompNeExpr(position, left,right);
    }

    public BinaryLogicAndExpr buildBinaryLogicAndExpr(int position, Expr left, Expr right){
        return new BinaryLogicAndExpr(position, left, right);
    }

    public BinaryLogicOrExpr buildBinaryLogicOrExpr(int position, Expr left, Expr right){
        return new BinaryLogicOrExpr(position, left, right);
    }

    public BlockStmt buildBlockStmt(int position, StmtList stmtList){
        return new BlockStmt(position,stmtList);
    }

    public BreakStmt buildBreakStmt(int position){
        return new BreakStmt(position);
    }

    public CastExpr buildCastExpr(int position, String type, Expr expr){
        return new CastExpr(position,type,expr);
    }

    public Class_ buildClass(int position, String filename, String name, String parent, MemberList memberList){
        return new Class_(position,filename,name,parent,memberList);
    }

    public ClassList buildClassList(int position){
        return new ClassList(position);
    }

    public ConstBooleanExpr buildConstBooleanExpr(int position,String spelling){
        return new ConstBooleanExpr(position,spelling);
    }

    public ConstIntExpr buildConstIntExpr(int position, String spelling){
        return new ConstIntExpr(position,spelling);
    }

    public ConstStringExpr buildConstStringExpr(int position, String spelling){
        return new ConstStringExpr(position, spelling);
    }

    public DeclStmt buildDeclStmt(int position, String name, Expr init){
        return new DeclStmt(position,name, init);
    }

    public DispatchExpr buildDispatchExpr(int position, Expr refExpr, String methodName, ExprList actualList){
        return new DispatchExpr(position, refExpr, methodName, actualList);
    }

    public ExprList buildExprList(int position){
        return new ExprList(position);
    }

    public ExprStmt buildExprStmt(int position, Expr expr){
        return new ExprStmt(position, expr);
    }

    public Field buildField(int position, String type, String name, Expr init){
        return new Field(position, type, name, init);
    }

    public Formal buildFormal(int position, String type, String id){
        return new Formal(position,type,id);
    }

    public FormalList buildFormalList(int position){
        return new FormalList(position);
    }

    public ForStmt buildForStmt(int position, Expr initExpr, Expr predExpr, Expr updateExpr, Stmt bodyStmt){
        return new ForStmt(position, initExpr, predExpr, updateExpr, bodyStmt);
    }

    public IfStmt buildIfStmt(int position, Expr predExpr, Stmt thenStmt, Stmt elseStmt){
        return new IfStmt(position, predExpr, thenStmt, elseStmt);
    }

    public InstanceofExpr buildInstanceOfExpr(int position, Expr expr, String type){
        return new InstanceofExpr(position, expr, type);
    }

    public MemberList buildMemberList(int position){
        return new MemberList(position);
    }

    public Method buildMethod(int position, String returnType, String name, FormalList formalList, StmtList stmtList){
        return new Method(position,returnType,name,formalList,stmtList);
    }

    public NewArrayExpr buildNewArrayExpr(int position, String type, Expr size){
        return new NewArrayExpr(position, type, size);
    }

    public NewExpr buildNewExpr(int position, String type){
        return new NewExpr(position, type);
    }

    public Program buildProgram(int position, ClassList classList){
        return new Program(position, classList);
    }

    public ReturnStmt buildReturnStmt(int position, Expr expr){
        return new ReturnStmt(position,expr);
    }

    public StmtList buildStmtList(int position){
        return new StmtList(position);
    }

    public UnaryDecrExpr buildUnaryDecrExpr(int position, Expr primary, boolean isPostfix){
        return new UnaryDecrExpr(position,primary,isPostfix);
    }

    public UnaryIncrExpr buildUnaryIncrExpr(int position, Expr primary, boolean isPostfix){
        return new UnaryIncrExpr(position,primary,isPostfix);
    }

    public UnaryNegExpr buildUnaryNegExpr(int position,Expr primary){
        return new UnaryNegExpr(position,primary);
    }

    public UnaryNotExpr buildUnaryNotExpr(int position,Expr primary){
        return new UnaryNotExpr(position,primary);
    }

    public VarExpr buildVarExpr(int position, Expr ref, String name){
        return new VarExpr(position, ref, name);
    }

    public WhileStmt buildWhileStmt(int position, Expr predExpr, Stmt bodyStmt){
        return new WhileStmt(position, predExpr, bodyStmt);
    }
}

