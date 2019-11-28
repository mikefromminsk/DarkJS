package org.pdk.funcitons.modules;

import org.pdk.funcitons.Function;
import org.pdk.funcitons.FunctionManager;
import org.pdk.storage.NodeBuilder;
import org.pdk.storage.model.DataOrNode;
import org.pdk.storage.model.data.BooleanData;
import org.pdk.storage.model.data.Data;
import org.pdk.storage.model.data.NumberData;
import org.pdk.storage.model.data.StringData;
import org.pdk.storage.model.node.Node;
import org.simpledb.Bytes;

public class Math {

    public final static String MATH_MODULE = "Math";
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

    public Math(FunctionManager functions) {
        functions.reg(new Function(MATH_MODULE, INC) {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                ((NumberData)builder.getValue()).number += 1;
                return builder.getNode();
            }
        });
        functions.reg(new Function(MATH_MODULE, DEC) {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                ((NumberData)builder.getValue()).number -= 1;
                return builder.getNode();
            }
        });
        functions.reg(new Function(MATH_MODULE, UNARY_MINUS) {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                NumberData numberData = builder.getNumberParam(0);
                numberData.number *= -1;
                return numberData;
            }
        });
        functions.reg(new Function(MATH_MODULE, EQ, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                Data par1 = builder.getParamData(0);
                Data par2 = builder.getParamData(1);
                if (par1 instanceof NumberData && par2 instanceof NumberData)
                    return new BooleanData(((NumberData) par1).number == ((NumberData) par2).number);
                else if (par1 instanceof StringData && par2 instanceof StringData)
                    return new BooleanData(Bytes.compare(((StringData) par1).bytes, ((StringData) par2).bytes));
                throw new ClassCastException();
            }
        });
        functions.reg(new Function(MATH_MODULE, ADD, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                Data par1 = builder.getParamData(0);
                Data par2 = builder.getParamData(1);
                if (par1 instanceof NumberData && par2 instanceof NumberData)
                    return new NumberData(((NumberData) par1).number + ((NumberData) par2).number);
                else if (par1 instanceof StringData && par2 instanceof NumberData)
                    return new StringData((new String(((StringData) par1).bytes) + ((NumberData) par2).number).getBytes());
                else if (par2 instanceof StringData && par1 instanceof NumberData)
                    return new StringData((((NumberData) par1).number  +  new String(((StringData) par2).bytes)).getBytes());
                else if (par1 instanceof StringData && par2 instanceof StringData)
                    return new StringData(Bytes.concat(((StringData) par1).bytes, ((StringData) par2).bytes));
                throw new ClassCastException();
            }
        });

        functions.reg(new Function(MATH_MODULE, SUB, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                return new NumberData(builder.getNumberParam(0).number - builder.getNumberParam(1).number);
            }
        });

        functions.reg(new Function(MATH_MODULE, MUL, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                return new NumberData(builder.getNumberParam(0).number * builder.getNumberParam(1).number);
            }
        });

        functions.reg(new Function(MATH_MODULE, DIV, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                return new NumberData(builder.getNumberParam(0).number / builder.getNumberParam(1).number);
            }
        });

        functions.reg(new Function(MATH_MODULE, MORE, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                return new BooleanData(builder.getNumberParam(0).number > builder.getNumberParam(1).number);
            }
        });

        functions.reg(new Function(MATH_MODULE, MORE_OR_EQUAL, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                return new BooleanData(builder.getNumberParam(0).number >= builder.getNumberParam(1).number);
            }
        });

        functions.reg(new Function(MATH_MODULE, LESS, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                return new BooleanData(builder.getNumberParam(0).number < builder.getNumberParam(1).number);
            }
        });

        functions.reg(new Function(MATH_MODULE, LESS_OR_EQUAL, "par1", "par2") {
            @Override
            public DataOrNode invoke(NodeBuilder builder, Node ths) {
                return new BooleanData(builder.getNumberParam(0).number <= builder.getNumberParam(1).number);
            }
        });
    }
}
