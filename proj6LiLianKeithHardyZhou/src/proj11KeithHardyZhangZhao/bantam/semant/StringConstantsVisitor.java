/*
 * File: StringConstantsVisitor.java
 * Names: Zeb Keith-Hardy, Danqing Zhao, Tia Zhang
 * Class: CS 461
 * Project 11
 * Date: February 13, 2019
 */




package proj11KeithHardyZhangZhao.bantam.semant;

import proj11KeithHardyZhangZhao.bantam.ast.ConstStringExpr;
import proj11KeithHardyZhangZhao.bantam.visitor.Visitor;
import proj11KeithHardyZhangZhao.bantam.ast.Program;
import java.util.Map;
import java.util.HashMap;

public class StringConstantsVisitor extends Visitor {
    private int numStringConsts; //How many string constants have been counted so far, used for constants' map
    private Map<String, String> stringConstMap; //Map of all string constants to a string constant identifier

    /*
    *
    */
    public StringConstantsVisitor(){
        numStringConsts = 0;
        stringConstMap = new HashMap<>();
    }


    /*
    *
    */
    public Map<String, String> getStringConstants(Program ast){
        ast.accept(this);
        return stringConstMap;
    }


    /*
    *
    */
    public Object visit(ConstStringExpr node) {
        //Even if the same constant appeared multiple times, it doesn't matter if the value is updated
        //It's still a unique number
        //TODO double check that Dale doesn't care
        stringConstMap.put(node.getConstant(), "StringConst_" + numStringConsts);
        numStringConsts += 1;
        return null;
    }
}





