package org.pdk.files.converters.js;

import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.parser.TokenType;
import org.pdk.files.converters.ConverterBuilder;
import org.pdk.store.NodeBuilder;
import org.pdk.store.model.node.Node;

import java.util.ArrayList;
import java.util.Collections;

import static org.pdk.modules.root.MathModule.*;

public class JsBuilder extends ConverterBuilder {

    private ArrayList<Node> localStack = new ArrayList<>();

    public JsBuilder(NodeBuilder builder) {
        super(builder);
    }

    Node jsLine(Node module, jdk.nashorn.internal.ir.Node statement) {
        try {
            if (module != null)
                localStack.add(module);


            return null;
        } finally {
            localStack.remove(module);
        }
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

    public Node build(Node module, jdk.nashorn.internal.ir.Node rootParserNode) {
        addParentsToLocalStack(module);
        if (module == null)
            module = builder.create().commit();
        Block program = ((FunctionNode) rootParserNode).getBody();
        return jsLine(module, program);
    }

    private void addParentsToLocalStack(Node module) {
        if (module != null) {
            Node parent = builder.set(module).getLocalParent();
            while (parent != null) {
                localStack.add(parent);
                parent = builder.set(parent).getLocalParent();
            }
            Collections.reverse(localStack);
        }
    }
}
