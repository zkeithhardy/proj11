package proj18KeithHardyLiLian.bantam.semant;



import proj18KeithHardyLiLian.bantam.ast.*;
import proj18KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Map;
import java.util.HashMap;



/**
 * Visitor class that visits the nodes of an AST and logs all the classes in a map
 */
public class ClassNameVisitor extends Visitor {
    private int numClassConsts; //How many string constants have been counted so far, used for constants' map values
    private Map<String, String> classNameMap; //Map of all string constants to a string constant identifier

    /**
     * Method that maps all the string constants in a program
     * The keys in the maps are the constants and the values are an identifier in the form StringConst_[unique number]
     *
     * @param ast is the program whose string constants are to be returned. It must be a AST node of type Program
     * @return the map of string constants
     */
    public Map<String, String> getClassNames(Program ast) {
        classNameMap = new HashMap<>();
        classNameMap.put("Object", "Class_" + 0);
        classNameMap.put("TextIO", "Class_" + 1);
        classNameMap.put("String", "Class_" + 2);
        classNameMap.put("Sys", "Class_" + 3);
        classNameMap.put("Integer","Class_" + 4);
        classNameMap.put("Boolean","Class_" + 5);
        classNameMap.put("Object[]","Class_" + 6);
        classNameMap.put("String[]","Class_" + 7);
        classNameMap.put("Integer[]","Class_" + 8);
        classNameMap.put("Boolean[]","Class_" + 9);
        classNameMap.put("int[]","Class_" + 10);
        classNameMap.put("boolean[]","Class_" + 11);
        numClassConsts = 12;

        ast.accept(this);
        return classNameMap;
    }


    /**
     * Method for visiting string constant nodes
     *
     * @param node is the ConstStringExpr node to be visited
     */
    @Override
    public Object visit(Class_ node) {
        classNameMap.put(node.getName(), "Class_" + numClassConsts);
        //It's a terminal node, so there shouldn't be a need to call super.visit()
        numClassConsts += 1;
        return null;
    }
}






