/*
 * File: StringConstantsVisitor.java
 * Names: Zeb Keith-Hardy, Danqing Zhao, Tia Zhang
 * Class: CS 461
 * Project 11
 * Date: February 13, 2019
 */

package proj19KeithHardyLiLian.bantam.semant;

import proj19KeithHardyLiLian.bantam.ast.*;
import proj19KeithHardyLiLian.bantam.visitor.Visitor;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;


/**
* Visitor class that visits the nodes of an AST and logs all the string constants in a map
*/
public class StringConstantsVisitor extends Visitor {
    private int numStringConsts; //How many string constants have been counted so far, used for constants' map values
    private Map<String, String> stringConstMap; //Map of all string constants to a string constant identifier

    /**
     * Method that maps all the string constants in a program
     * The keys in the maps are the constants and the values are an identifier in the form StringConst_[unique number]
     *
     * @param ast is the program whose string constants are to be returned. It must be a AST node of type Program
     * @return the map of string constants
     */
    public Map<String, String> getStringConstants(Program ast) {
        numStringConsts = 0;
        stringConstMap = new HashMap<>();
        // put the filename in
        Class_ oneClassInFile = (Class_)ast.getClassList().get(0);
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] filename = oneClassInFile.getFilename().split(pattern);
        stringConstMap.put(filename[filename.length-1], "StringConst_0");

        numStringConsts++;
        ast.accept(this);
        return stringConstMap;
    }


    /**
     * Method for visiting string constant nodes
     *
     * @param node is the ConstStringExpr node to be visited
     */
    @Override
    public Object visit(ConstStringExpr node) {
        stringConstMap.put(node.getConstant(), "StringConst_" + numStringConsts);
        //It's a terminal node, so there shouldn't be a need to call super.visit()
        numStringConsts += 1;
        return null;
    }


    /**
     * Method for the "visit" to FormalList nodes, which causes it to retreat from the node and skip its children
     *
     * @param node is the FormalList node to be visited (and ignored)
     */
    @Override
    public Object visit(FormalList node) {
        return null;
    }
}





