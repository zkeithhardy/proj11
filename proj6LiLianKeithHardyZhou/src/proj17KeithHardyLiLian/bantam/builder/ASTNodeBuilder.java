package proj17KeithHardyLiLian.bantam.builder;

import proj17KeithHardyLiLian.bantam.ast.*;

/**
 * this is the builder class used in parser in order to implement
 * the builder pattern
 */
public class ASTNodeBuilder {

    /**
     * builder for the Array assign expression
     * @param position position for the node
     * @param refName reference string
     * @param name name of the array assign expr
     * @param index index in we are interested
     * @param expr the expression of assignment
     * @return
     */
    public ArrayAssignExpr buildArrayAssignExpr(int position, String refName, String name, Expr index, Expr expr){
        return new ArrayAssignExpr(position, refName, name, index, expr);
    }

    /**
     * builder for the Array expression
     * @param position position of the expr
     * @param expr the expression of the array expr
     * @param name name of the array
     * @param idx index expression
     * @return ArrayExpr
     */
    public ArrayExpr buildArrayExpr(int position, Expr expr, String name, Expr idx){
        return new ArrayExpr(position,expr,name,idx);
    }

    /**
     * the assign expression builder
     * @param position position of the expr
     * @param refName reference name
     * @param name name of the assigned object
     * @param expr the expression that is to be assigned
     * @return AssignExpr
     */
    public AssignExpr buildAssignExpr(int position, String refName, String name, Expr expr){
        return new AssignExpr(position, refName, name, expr);
    }

    /**
     * builder for binary arith divide
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryArithDivideExpr
     */
    public BinaryArithDivideExpr buildBinaryArithDivideExpr(int position, Expr left, Expr right){
        return new BinaryArithDivideExpr(position,left,right);
    }

    /**
     * builder for binary divide
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryArithMinusExpr
     */
    public BinaryArithMinusExpr buildBinaryArithMinusExpr(int position, Expr left, Expr right){
        return new BinaryArithMinusExpr(position,left,right);
    }

    /**
     * builder for binary mod
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryArithModulusExpr
     */
    public BinaryArithModulusExpr buildBinaryArithModulusExpr(int position, Expr left, Expr right){
        return new BinaryArithModulusExpr(position,left,right);
    }

    /**
     * builder for binary plus
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryArithPlusExpr
     */
    public BinaryArithPlusExpr buildBinaryArithPlusExpr(int position, Expr left, Expr right){
        return new BinaryArithPlusExpr(position,left,right);
    }

    /**
     * builder for binary times
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryArithTimesExpr
     */
    public BinaryArithTimesExpr buildBinaryArithTimesExpr(int position, Expr left, Expr right){
        return new BinaryArithTimesExpr(position,left,right);
    }

    /**
     * builder for binary comparison equality
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryCompEqExpr
     */
    public BinaryCompEqExpr buildBinaryCompEqExpr(int position, Expr left, Expr right){
        return new BinaryCompEqExpr(position,left,right);
    }

    /**
     * builder for binary greater equal
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryCompGeqExpr
     */
    public BinaryCompGeqExpr buildBinaryCompGeqExpr(int position, Expr left, Expr right){
        return new BinaryCompGeqExpr(position,left,right);
    }

    /**
     * builder for binary greater
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryCompGtExpr
     */
    public BinaryCompGtExpr buildBinaryCompGtExpr(int position, Expr left, Expr right){
        return new BinaryCompGtExpr(position,left,right);
    }

    /**
     * builder for binary less than and equal
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryCompLeqExpr
     */
    public BinaryCompLeqExpr buildBinaryCompLeqExpr(int position, Expr left, Expr right){
        return new BinaryCompLeqExpr(position,left,right);
    }

    /**
     * builder for binary less than
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryCompLtExpr
     */
    public BinaryCompLtExpr buildBinaryCompLtExpr(int position, Expr left, Expr right){
        return new BinaryCompLtExpr(position,left,right);
    }

    /**
     * builder for binary not equal
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryCompNeExpr
     */
    public BinaryCompNeExpr buildBinaryCompNeExpr(int position, Expr left, Expr right){
        return new BinaryCompNeExpr(position, left,right);
    }

    /**
     * builder for binary logic and
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryLogicAndExpr
     */
    public BinaryLogicAndExpr buildBinaryLogicAndExpr(int position, Expr left, Expr right){
        return new BinaryLogicAndExpr(position, left, right);
    }

    /**
     * builder for binary logic or
     * @param position position of the expr
     * @param left left expr
     * @param right right expr
     * @return BinaryLogicOrExpr
     */
    public BinaryLogicOrExpr buildBinaryLogicOrExpr(int position, Expr left, Expr right){
        return new BinaryLogicOrExpr(position, left, right);
    }

    /**
     * builder for block stmt
     * @param position position of the stmt
     * @param stmtList list of stmts
     * @return BlockStmt
     */
    public BlockStmt buildBlockStmt(int position, StmtList stmtList){
        return new BlockStmt(position,stmtList);
    }

    /**
     * builder for break stmt
     * @param position position of the stmt
     * @return BreakStmt
     */
    public BreakStmt buildBreakStmt(int position){
        return new BreakStmt(position);
    }

    /**
     * builder for cast expression
     * @param position position of the expr
     * @param type type of the object it want to cast into
     * @param expr expression to be casted
     * @return CastExpr
     */
    public CastExpr buildCastExpr(int position, String type, Expr expr){
        return new CastExpr(position,type,expr);
    }

    /**
     * builder for class object
     * @param position position of the node
     * @param filename filename which the class is in
     * @param name name for the class
     * @param parent parent class name
     * @param memberList list of all members
     * @return Class_
     */
    public Class_ buildClass(int position, String filename, String name, String parent, MemberList memberList){
        return new Class_(position,filename,name,parent,memberList);
    }

    /**
     * builder for class list
     * @param position position of the node
     * @return ClassList
     */
    public ClassList buildClassList(int position){
        return new ClassList(position);
    }

    /**
     * builder for constant boolean expr
     * @param position position of the node
     * @param spelling spelling for the boolean value
     * @return ConstBooleanExpr
     */
    public ConstBooleanExpr buildConstBooleanExpr(int position,String spelling){
        return new ConstBooleanExpr(position,spelling);
    }

    /**
     * builder for constant int expression
     * @param position position of the node
     * @param spelling spelling for the integer value
     * @return ConstIntExpr
     */
    public ConstIntExpr buildConstIntExpr(int position, String spelling){
        return new ConstIntExpr(position,spelling);
    }

    /**
     * builder for the constant string expression
     * @param position position of the node
     * @param spelling spelling for the string value
     * @return ConstStringExpr
     */
    public ConstStringExpr buildConstStringExpr(int position, String spelling){
        return new ConstStringExpr(position, spelling);
    }

    /**
     * builder for declaration stmt
     * @param position position of the node
     * @param name name of the declared object
     * @param init initialization expression
     * @return DeclStmt
     */
    public DeclStmt buildDeclStmt(int position, String name, Expr init){
        return new DeclStmt(position,name, init);
    }

    /**
     * builder for dispatch expression
     * @param position position of the node
     * @param refExpr reference expression
     * @param methodName name of the dispatch stmt
     * @param actualList list of parameters/ value this expression takes in
     * @return DispatchExpr
     */
    public DispatchExpr buildDispatchExpr(int position, Expr refExpr, String methodName, ExprList actualList){
        return new DispatchExpr(position, refExpr, methodName, actualList);
    }

    /**
     * builder for expression list
     * @param position position of the node
     * @return ExprList
     */
    public ExprList buildExprList(int position){
        return new ExprList(position);
    }

    /**
     * builder for expression stmt
     * @param position position of the node
     * @param expr the actual expression body
     * @return ExprStmt
     */
    public ExprStmt buildExprStmt(int position, Expr expr){
        return new ExprStmt(position, expr);
    }

    /**
     * builder for a filed
     * @param position position of the node
     * @param type type of the field
     * @param name name of the field
     * @param init initialization expression
     * @return Field
     */
    public Field buildField(int position, String type, String name, Expr init){
        return new Field(position, type, name, init);
    }

    /**
     * builder for the formal
     * @param position position of the node
     * @param type type of the formal
     * @param id id for the formal
     * @return Formal
     */
    public Formal buildFormal(int position, String type, String id){
        return new Formal(position,type,id);
    }

    /**
     * builder for the list of formal
     * @param position position of the node
     * @return FormalList
     */
    public FormalList buildFormalList(int position){
        return new FormalList(position);
    }

    /**
     * builder for the for statement
     * @param position position of the node
     * @param initExpr initialization in for stmt
     * @param predExpr predication condition expression
     * @param updateExpr update expression in the for stmt
     * @param bodyStmt body of the for statement
     * @return ForStmt
     */
    public ForStmt buildForStmt(int position, Expr initExpr, Expr predExpr, Expr updateExpr, Stmt bodyStmt){
        return new ForStmt(position, initExpr, predExpr, updateExpr, bodyStmt);
    }

    /**
     * builder for the if statement
     * @param position position of the node
     * @param predExpr predicate expression
     * @param thenStmt if the predicate is satisfied, the stmt for what does next
     * @param elseStmt if the predicate is not satisfied, the stmt for what does next
     * @return IfStmt
     */
    public IfStmt buildIfStmt(int position, Expr predExpr, Stmt thenStmt, Stmt elseStmt){
        return new IfStmt(position, predExpr, thenStmt, elseStmt);
    }

    /**
     * builder for the instance of expression
     * @param position position of the node
     * @param expr expression to be checked
     * @param type type it want to be check as
     * @return InstanceofExpr
     */
    public InstanceofExpr buildInstanceOfExpr(int position, Expr expr, String type){
        return new InstanceofExpr(position, expr, type);
    }

    /**
     * builder for member list object
     * @param position position of the node
     * @return MemberList
     */
    public MemberList buildMemberList(int position){
        return new MemberList(position);
    }

    /**
     * builder for method node
     * @param position position of the node
     * @param returnType the return type in string
     * @param name the name of the method
     * @param formalList the formals it take in
     * @param stmtList the list of stmts in the method
     * @return Method
     */
    public Method buildMethod(int position, String returnType, String name, FormalList formalList, StmtList stmtList){
        return new Method(position,returnType,name,formalList,stmtList);
    }

    /**
     * the new array expression node builder
     * @param position position of the node
     * @param type type of the array element
     * @param size size of the array
     * @return NewArrayExpr
     */
    public NewArrayExpr buildNewArrayExpr(int position, String type, Expr size){
        return new NewArrayExpr(position, type, size);
    }

    /**
     * the new expression builder
     * @param position position of the node
     * @param type type of the new object
     * @return NewExpr
     */
    public NewExpr buildNewExpr(int position, String type){
        return new NewExpr(position, type);
    }

    /**
     * the builder for the program node
     * @param position position of the node
     * @param classList the list of classes in the program
     * @return Program
     */
    public Program buildProgram(int position, ClassList classList){
        return new Program(position, classList);
    }

    /**
     * the return statement builder
     * @param position position of the node
     * @param expr expression of the returned object
     * @return ReturnStmt
     */
    public ReturnStmt buildReturnStmt(int position, Expr expr){
        return new ReturnStmt(position,expr);
    }

    /**
     * builder for list of stmts
     * @param position position of the node
     * @return StmtList
     */
    public StmtList buildStmtList(int position){
        return new StmtList(position);
    }

    /**
     * builder for the unary decrement expression
     * @param position position of the node
     * @param primary primary expression to be decremented
     * @param isPostfix the boolean indicate whether the decrement is postfix or not
     * @return UnaryDecrExpr
     */
    public UnaryDecrExpr buildUnaryDecrExpr(int position, Expr primary, boolean isPostfix){
        return new UnaryDecrExpr(position,primary,isPostfix);
    }

    /**
     * the builder for increment expression
     * @param position position of the node
     * @param primary primary expression to be incremented
     * @param isPostfix the boolean indicate whether the decrement is postfix or not
     * @return UnaryIncrExpr
     */
    public UnaryIncrExpr buildUnaryIncrExpr(int position, Expr primary, boolean isPostfix){
        return new UnaryIncrExpr(position,primary,isPostfix);
    }

    /**
     * the builder for negation expression
     * @param position position of the node
     * @param primary primary expression to be negated
     * @return UnaryNegExpr
     */
    public UnaryNegExpr buildUnaryNegExpr(int position,Expr primary){
        return new UnaryNegExpr(position,primary);
    }

    /**
     * the builder for not expression
     * @param position position of the node
     * @param primary primary expression to be unary not
     * @return UnaryNotExpr
     */
    public UnaryNotExpr buildUnaryNotExpr(int position,Expr primary){
        return new UnaryNotExpr(position,primary);
    }

    /**
     * the builder for var expression
     * @param position position of the node
     * @param ref reference expression
     * @param name name of the var
     * @return VarExpr
     */
    public VarExpr buildVarExpr(int position, Expr ref, String name){
        return new VarExpr(position, ref, name);
    }

    /**
     * builder for the while stmt
     * @param position position of the node
     * @param predExpr predicate expression
     * @param bodyStmt body of the statement 
     * @return WhileStmt
     */
    public WhileStmt buildWhileStmt(int position, Expr predExpr, Stmt bodyStmt){
        return new WhileStmt(position, predExpr, bodyStmt);
    }
}

