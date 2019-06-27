package com.droid.djs.runner.utils;

import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.Node;
import com.droid.djs.runner.Func;
import jdk.nashorn.internal.parser.TokenType;

import java.util.LinkedHashMap;
import java.util.Map;

public class MathUtils extends Utils {

    public final static String UNARY_MINUS = "unaryMinus";
    public final static String EQ = "equals"; // ==
    public final static String ADD = "add"; // +
    public final static String SUB = "sub"; // -
    public final static String DIV = "div"; // /
    public final static String MUL = "mul"; // *
    public final static String INC = "inc"; // +1
    public final static String DEC = "dec"; // -1
    public final static String MORE = "more"; // >
    public final static String MORE_OR_EQUAL = "moreOrEqual"; // >=
    public final static String LESS = "less"; // <
    public final static String LESS_OR_EQUAL = "lessOrEqual"; // <=

    public static Map<String, Func> mapFunctions = new LinkedHashMap<>();

    @Override
    public void func(String name, Func func, Node... args) {
        super.func(name, func, args);
        mapFunctions.put(name, func);
    }

    public static Integer funcNameToFuncIndex(String funcName) {
        Func func = mapFunctions.get(funcName);
        return functions.indexOf(func);
    }

    public static Integer tokenToFunctionIndex(TokenType tokenType) {
        String funcName = convertTokenTypeToFuncName(tokenType);
        return funcNameToFuncIndex(funcName);
    }

    public static String convertTokenTypeToFuncName(TokenType tokenType) {
        switch (tokenType) {
            case EQ:
                return EQ;
            case ADD:
                return ADD;
            case SUB:
                return SUB;
            case DIV:
                return DIV;
            case MUL:
                return MUL;
            case ASSIGN_ADD:
                return ADD;
            case ASSIGN_SUB:
                return SUB;
            case ASSIGN_DIV:
                return DIV;
            case ASSIGN_MUL:
                return MUL;
            case INCPOSTFIX:
                return INC;
            case DECPOSTFIX:
                return DEC;
            case GT:
                return MORE;
            case GE:
                return MORE_OR_EQUAL;
            case LT:
                return LESS;
            case LE:
                return LESS_OR_EQUAL;
        }
        return EQ;
    }

    @Override
    public String name() {
        return "Math";
    }

    @Override
    public void methods() {
        func(UNARY_MINUS, (builder, node, ths) -> {
            Object leftObject = leftObject(builder, node);
            if (leftObject instanceof Double) {
                Double newString = -(Double) leftObject;
                return builder.create(NodeType.NUMBER).setData(newString).commit();
            }
            return node;
        }, par("par1", NodeType.NUMBER));

        func(EQ, (builder, node, ths) -> {
                    Object leftObject = leftObject(builder, node);
                    Object rightObject = rightObject(builder, node);
                    return (leftObject != null && leftObject.equals(rightObject)) ? Utils.trueValue : Utils.falseValue;
                }, par("par1", NodeType.NUMBER),
                par("par2", NodeType.NUMBER));

        func(ADD, (builder, node, ths) -> {
                    Object leftObject = leftObject(builder, node);
                    Object rightObject = rightObject(builder, node);
                    if (leftObject instanceof String && rightObject instanceof String) {
                        String newString = leftObject + (String) rightObject;
                        return builder.create(NodeType.STRING).setData(newString).commit();
                    } else if (leftObject instanceof Double && rightObject instanceof Double) {
                        Double newString = (Double) leftObject + (Double) rightObject;
                        return builder.create(NodeType.NUMBER).setData(newString).commit();
                    }
                    return null;
                }, par("par1", NodeType.NUMBER),
                par("par2", NodeType.NUMBER));

        func(SUB, (builder, node, ths) -> {
                    Object leftObject = leftObject(builder, node);
                    Object rightObject = rightObject(builder, node);
                    if (leftObject instanceof Double && rightObject instanceof Double) {
                        Double newString = (Double) leftObject - (Double) rightObject;
                        return builder.create(NodeType.NUMBER).setData(newString).commit();
                    }
                    return null;
                }, par("par1", NodeType.NUMBER),
                par("par2", NodeType.NUMBER));

        func(MUL, (builder, node, ths) -> {
                    Object leftObject = leftObject(builder, node);
                    Object rightObject = rightObject(builder, node);
                    if (leftObject instanceof Double && rightObject instanceof Double) {
                        Double newString = (Double) leftObject * (Double) rightObject;
                        return builder.create(NodeType.NUMBER).setData(newString).commit();
                    }
                    return null;
                }, par("par1", NodeType.NUMBER),
                par("par2", NodeType.NUMBER));

        func(DIV, (builder, node, ths) -> {
                    Object leftObject = leftObject(builder, node);
                    Object rightObject = rightObject(builder, node);
                    if (leftObject instanceof Double && rightObject instanceof Double) {
                        Double newString = (Double) leftObject / (Double) rightObject;
                        return builder.create(NodeType.NUMBER).setData(newString).commit();
                    }
                    return null;
                }, par("par1", NodeType.NUMBER),
                par("par2", NodeType.NUMBER));

        func(INC, (builder, node, ths) -> {
            Object leftObject = leftObject(builder, node);
            if (leftObject instanceof Double) {
                Double newString = (Double) leftObject + 1;
                return builder.create(NodeType.NUMBER).setData(newString).commit();
            }
            return null;
        }, par("par1", NodeType.NUMBER));

        func(DEC, (builder, node, ths) -> {
            Object leftObject = leftObject(builder, node);
            if (leftObject instanceof Double) {
                Double newString = (Double) leftObject - 1;
                return builder.create(NodeType.NUMBER).setData(newString).commit();
            }
            return null;
        }, par("par1", NodeType.NUMBER));

        func(MORE, (builder, node, ths) -> {
            Object leftObject = leftObject(builder, node);
            Object rightObject = rightObject(builder, node);
            if (leftObject instanceof Double && rightObject instanceof Double)
                return ((Double) leftObject > (Double) rightObject) ? trueValue : falseValue;
            return null;
        }, par("par1", NodeType.NUMBER), par("par2", NodeType.NUMBER));

        func(MORE_OR_EQUAL, (builder, node, ths) -> {
            Object leftObject = leftObject(builder, node);
            Object rightObject = rightObject(builder, node);
            if (leftObject instanceof Double && rightObject instanceof Double)
                return ((Double) leftObject >= (Double) rightObject) ? trueValue : falseValue;
            return null;
        }, par("par1", NodeType.NUMBER), par("par2", NodeType.NUMBER));

        func(LESS, (builder, node, ths) -> {
            Object leftObject = leftObject(builder, node);
            Object rightObject = rightObject(builder, node);
            if (leftObject instanceof Double && rightObject instanceof Double)
                return ((Double) leftObject < (Double) rightObject) ? trueValue : falseValue;
            return null;
        }, par("par1", NodeType.NUMBER), par("par2", NodeType.NUMBER));


        func(LESS_OR_EQUAL, (builder, node, ths) -> {
            Object leftObject = leftObject(builder, node);
            Object rightObject = rightObject(builder, node);
            if (leftObject instanceof Double && rightObject instanceof Double)
                return ((Double) leftObject <= (Double) rightObject) ? trueValue : falseValue;
            return null;
        }, par("par1", NodeType.NUMBER), par("par2", NodeType.NUMBER));

    }
}
