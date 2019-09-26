package org.pdk.files.convertors.js;

import org.pdk.store.model.nodes.NodeBuilder;
import org.pdk.store.NodeType;
import org.pdk.files.Files;
import org.pdk.store.model.nodes.NativeNode;
import org.pdk.store.model.nodes.Node;
import org.pdk.modules.root.MathModule;
import jdk.nashorn.internal.ir.*;
import jdk.nashorn.internal.parser.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsBuilder {

    private NodeBuilder builder = new NodeBuilder();
    private ArrayList<Node> localStack = new ArrayList<>();

    Node jsLine(Node module, jdk.nashorn.internal.ir.Node statement) {
        try {
            if (module != null)
                localStack.add(module);

            if (statement instanceof VarNode) {
                VarNode varNode = (VarNode) statement;
                if (varNode.getInit() instanceof FunctionNode) {
                    return jsLine(module, varNode.getInit());
                } else {
                    Node node = jsLine(module, varNode.getName());
                    if (varNode.getInit() != null) {
                        Node setLink = jsLine(node, varNode.getInit());
                        node = builder.create().setSource(node).setSet(setLink).commit();
                    }
                    return node;
                }
            }

            if (statement instanceof ForNode) {
                ForNode forStatement = (ForNode) statement;

                Node forNode = builder.create().commit();
                Node initBlockNode = jsLine(forNode, forStatement.getInit());
                builder.set(forNode).addNext(initBlockNode).commit();

                Node blockNode = builder.create().commit();
                // TODO forNode.addLocal(blockNode) for name searching
                Node forBodyNode = jsLine(blockNode, forStatement.getBody());
                Node forTestNode = jsLine(forNode, forStatement.getTest());
                Node forStartNode = builder.create().setWhile(forBodyNode).setIf(forTestNode).commit();

                Node forModifyNode = jsLine(forNode, forStatement.getModify());
                builder.set(forBodyNode).addNext(forModifyNode).commit();

                builder.set(forNode).addNext(forStartNode).commit();
                return forNode;
            }

            if (statement instanceof FunctionNode) {
                FunctionNode function = (FunctionNode) statement;
                Node func = Files.getNode(module, function.getName(),
                        function.getName().startsWith("thread") ? NodeType.THREAD : NodeType.NODE);
                for (IdentNode param : function.getParameters()) {
                    Node titleData = builder.create(NodeType.STRING).setData(param.getName()).commit();
                    Node paramNode = builder.create().setTitle(titleData).commit();
                    builder.set(func).addParam(paramNode);
                }
                /*jsLine(functionNode, function.getBody());*/
                return func;
            }

            if (statement instanceof Block) {
                Block block = (Block) statement;
                Map<Node, Block> subBlocks = new LinkedHashMap<>();

                builder.set(module).removeAllNext().commit();

                for (jdk.nashorn.internal.ir.Node line : block.getStatements()) {
                    Node lineNode = jsLine(module, line);
                    if (line instanceof VarNode && ((VarNode) line).getInit() instanceof FunctionNode) // not a function. its a problem with nashorn parser.
                        subBlocks.put(lineNode, ((FunctionNode) ((VarNode) line).getInit()).getBody());
                    else
                        builder.set(module).addNext(lineNode);
                }
                for (Node lineNode : subBlocks.keySet()) {
                    Block subBlock = subBlocks.get(lineNode);
                    jsLine(lineNode, subBlock);
                }
                return builder.set(module).commit();
            }

            // i++ in for
            if (statement instanceof JoinPredecessorExpression) {
                JoinPredecessorExpression joinPredecessorExpression = (JoinPredecessorExpression) statement;
                return jsLine(module, joinPredecessorExpression.getExpression());
            }

            if (statement instanceof UnaryNode) {
                UnaryNode unaryNode = (UnaryNode) statement;
                // TODO addObject ++a --a
                TokenType tokenType = unaryNode.tokenType();
                if (tokenType == TokenType.INCPOSTFIX || tokenType == TokenType.DECPOSTFIX) {
                    Node variable = jsLine(module, unaryNode.getExpression());
                    NativeNode nativeNode = (NativeNode) Files.getNodeFromRootIfExist(MathModule.MATH_UTIL_NAME + "/" + MathModule.convertTokenTypeToFuncName(tokenType));
                    Node func = builder.create(NodeType.NATIVE_FUNCTION)
                            .setFunctionIndex(nativeNode.getFunctionIndex())
                            .addParam(variable)
                            .commit();
                    return builder.create()
                            .setSource(variable)
                            .setValue(variable) // important
                            .setSet(func)
                            .commit();
                } else if (tokenType.toString().equals("-")) {
                    NativeNode nativeNode = (NativeNode) Files.getNodeFromRootIfExist(MathModule.MATH_UTIL_NAME + "/" + MathModule.UNARY_MINUS);
                    Node expression = jsLine(module, unaryNode.getExpression());
                    return builder.create(NodeType.NATIVE_FUNCTION)
                            .setFunctionIndex(nativeNode.getFunctionIndex())
                            .addParam(expression)
                            .commit();
                } else {
                    return jsLine(module, unaryNode.getExpression());
                }
            }

            if (statement instanceof ExpressionStatement) {
                ExpressionStatement expressionStatement = (ExpressionStatement) statement;
                return jsLine(module, expressionStatement.getExpression());
            }

            if (statement instanceof BinaryNode) {
                BinaryNode binaryNode = (BinaryNode) statement;
                Node left = jsLine(module, binaryNode.lhs());
                Node right = jsLine(module, binaryNode.rhs());
                if (binaryNode.isAssignment()) {
                    if (binaryNode.tokenType() == TokenType.ASSIGN_ADD ||
                            binaryNode.tokenType() == TokenType.ASSIGN_SUB ||
                            binaryNode.tokenType() == TokenType.ASSIGN_MUL ||
                            binaryNode.tokenType() == TokenType.ASSIGN_DIV) {
                        NativeNode nativeFunc = (NativeNode) Files.getNodeFromRootIfExist(MathModule.MATH_UTIL_NAME + "/" + MathModule.convertTokenTypeToFuncName(binaryNode.tokenType()));
                        right = builder.create(NodeType.NATIVE_FUNCTION)
                                .setFunctionIndex(nativeFunc.getFunctionIndex())
                                .addParam(left)
                                .addParam(right)
                                .commit();
                    }
                    return builder.create()
                            .setSource(left)
                            .setSet(right)
                            .commit();
                } else {
                    NativeNode nativeFunc = (NativeNode) Files.getNodeFromRootIfExist( MathModule.MATH_UTIL_NAME + "/" + MathModule.convertTokenTypeToFuncName(binaryNode.tokenType()));
                    return builder.create(NodeType.NATIVE_FUNCTION)
                            .setFunctionIndex(nativeFunc.getFunctionIndex())
                            .addParam(left)
                            .addParam(right)
                            .commit();
                }
            }


            /*if (statement instanceof IndexNode) {
                IndexNode index = (IndexNode) statement;
                Node base = jsLine(module, index.getBase());
                Node objectNode = builder.create().setSource(base).commit();

                if (index.getIndex() instanceof LiteralNode || index.getIndex() instanceof IdentNode) {
                    Node indexNode = jsLine(module, index.getIndex());
                    builder.setString(objectNode).addProperty(indexNode).commit();
                }
                return objectNode;
            }*/

            if (statement instanceof AccessNode) {
                AccessNode index = (AccessNode) statement;
                Node base;

                if (index.getBase() instanceof IdentNode) {
                    if ("this".equals(((IdentNode) index.getBase()).getName()))
                        base = builder.create().commit();
                    else {
                        base = jsLine(module, index.getBase());
                        base = builder.create().setSource(base).commit();
                    }
                } else
                    base = jsLine(module, index.getBase());
                Node propertyNode = builder.create(NodeType.STRING).setData(index.getProperty()).commit();
                builder.set(base).addProperty(propertyNode).commit();
                return base;
            }

            if (statement instanceof IfNode) {
                IfNode ifStatement = (IfNode) statement;
                Node ifQuestionNode = jsLine(module, ifStatement.getTest());
                Node ifTrueNode = jsLine(module, ifStatement.getPass());
                Node ifElseNode;
                if (ifStatement.getFail() != null) {
                    ifElseNode = jsLine(module, ifStatement.getFail());
                    return builder.create()
                            .setIf(ifQuestionNode)
                            .setTrue(ifTrueNode)
                            .setElse(ifElseNode)
                            .commit();
                }
            }

            if (statement instanceof ReturnNode) {
                ReturnNode returnNode = (ReturnNode) statement;
                Node setNode = jsLine(module, returnNode.getExpression());
                return builder.create()
                        .setSource(module)
                        .setSet(setNode)
                        .setExit(module)
                        .commit();
            }

            if (statement instanceof IdentNode) {
                IdentNode identNode = (IdentNode) statement;
                String name = identNode.getName();

                Node ident = null;
                for (int i = localStack.size() - 1; i >= 0; i--) {
                    Node node = localStack.get(i);
                    Node findNode = builder.set(node).findLocal(name);
                    if (findNode == null)
                        findNode = builder.set(node).findParam(name);
                    if (findNode != null) {
                        ident = findNode;
                        break;
                    }
                }

                if (ident == null) {
                    ident = builder.create().commit();
                    Node titleData = builder.create(NodeType.STRING).setData(identNode.getName()).commit();
                    builder.set(ident).setTitle(titleData).commit();
                    builder.set(module).addLocal(ident).commit();
                }
                return ident;
            }

            if (statement instanceof ObjectNode) {
                ObjectNode objectNode = (ObjectNode) statement;
                Node obj = builder.create(NodeType.OBJECT).commit();
                Map<Node, Block> subBlocks = new LinkedHashMap<>();
                for (PropertyNode property : objectNode.getElements()) {
                    Node propNode = jsLine(obj, property);
                    if (property.getValue() instanceof FunctionNode)
                        subBlocks.put(propNode, ((FunctionNode) property.getValue()).getBody());
                }
                for (Node propNode : subBlocks.keySet()) {
                    Block subBlock = subBlocks.get(propNode);
                    jsLine(propNode, subBlock);
                }
                return obj;
            }

            if (statement instanceof PropertyNode) {
                PropertyNode propertyNode = (PropertyNode) statement;
                String key = "";
                if (propertyNode.getKey() instanceof LiteralNode) {
                    LiteralNode literalNode = (LiteralNode) propertyNode.getKey();
                    key = literalNode.getString();
                }
                if (propertyNode.getKey() instanceof IdentNode) {
                    IdentNode identNode = (IdentNode) propertyNode.getKey();
                    key = identNode.getName();
                }
                if (propertyNode.getValue() instanceof FunctionNode) {
                    return jsLine(module, propertyNode.getValue());
                } else {
                    Node value = jsLine(module, propertyNode.getValue());
                    Node titleData = builder.create(NodeType.STRING).setData(key).commit();
                    builder.set(value).setTitle(titleData).commit();
                    return builder.set(module).addLocal(value).commit();
                }
            }

            if (statement instanceof CallNode) { // TODO NewExpression
                CallNode call = (CallNode) statement;
                Node callNode = builder.create().commit();
                if (call.getArgs().size() > 0) {
                    for (jdk.nashorn.internal.ir.Node arg : call.getArgs()) {
                        Node argNode = jsLine(module, arg);
                        builder.set(callNode).addParam(argNode);
                    }
                } else {
                    builder.set(callNode).addParam(0L);
                }
                Node sourceFunc = jsLine(module, call.getFunction());
                return builder.set(callNode)
                        .setSource(sourceFunc)
                        .commit();
            }


            if (statement instanceof LiteralNode) {
                LiteralNode literalNode = (LiteralNode) statement;

                if (literalNode instanceof LiteralNode.ArrayLiteralNode) {
                    LiteralNode.ArrayLiteralNode arrayLiteralNode = (LiteralNode.ArrayLiteralNode) literalNode;
                    Node arr = builder.create(NodeType.ARRAY).commit();
                    for (jdk.nashorn.internal.ir.Node item : arrayLiteralNode.getElementExpressions()) {
                        Node itemNode = jsLine(module, item);
                        builder.set(arr).addCell(itemNode);
                    }
                    return arr;
                }
                if (literalNode.isNull()) {
                    return builder.create().commit();
                } else {
                    NodeType nodeType = NodeType.BOOLEAN;
                    if (literalNode.isNumeric())
                        nodeType = NodeType.NUMBER;
                    else if (literalNode.isString())
                        nodeType = NodeType.STRING;
                    Node value = builder.create(nodeType)
                            .setData(literalNode.getString())
                            .commit();
                    return builder.create()
                            .setValue(value)
                            .commit();
                }
            }
            return null;
        } finally {
            localStack.remove(module);
        }

    }

    // this function solve 05testFunctionVariables.js problem with var getFunctions
    private boolean isOperation(BinaryNode binaryNode) {
        try {
            return binaryNode.isLocal();
        } catch (NullPointerException e) {
            return true;
        }
    }

    public Node build(Node module, jdk.nashorn.internal.ir.Node rootParserNode) {
        addParentsToLocalStack(module);
        if (module == null)
            module = builder.create().commit();
        Block program = ((FunctionNode) rootParserNode).getBody();
        return jsLine(module, program);
    }

    private void addParentsToLocalStack(Node module) {
        if (module != null) {
            Node parent = builder.set(module).getLocalParentNode();
            while (parent != null) {
                localStack.add(parent);
                parent = builder.set(parent).getLocalParentNode();
            }
            Collections.reverse(localStack);
        }
    }

}
