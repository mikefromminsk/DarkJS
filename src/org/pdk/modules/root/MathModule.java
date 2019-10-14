package org.pdk.modules.root;

import org.pdk.modules.Module;
import org.pdk.store.model.data.BooleanData;
import org.pdk.store.model.data.Data;
import org.pdk.store.model.data.NumberData;
import org.pdk.store.model.data.StringData;
import org.simpledb.Bytes;

public class MathModule extends Module {

    public final static String MATH_UTIL_NAME = "Math";
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


    @Override
    public String path() {
        return MATH_UTIL_NAME;
    }

    @Override
    public void methods() {

        func(INC, (builder, ths) -> {
            NumberData number = ((NumberData) builder.set(ths).getValue());
            number.number += 1;
            return number;
        });

        func(DEC, (builder, ths) -> {
            NumberData number = ((NumberData) builder.set(ths).getValue());
            number.number -= 1;
            return number;
        });

        func(UNARY_MINUS, (builder, ths) -> {
            NumberData number = ((NumberData) builder.set(ths).getValue());
            number.number *= -1;
            return number;
        });

        func(EQ,/*        */(builder, ths) -> {
            Data par1 = builder.getParamData(0);
            Data par2 = builder.getParamData(1);
            if (par1 instanceof NumberData && par2 instanceof NumberData)
                return new BooleanData(((NumberData) par1).number == ((NumberData) par2).number);
            else if (par1 instanceof StringData && par2 instanceof StringData)
                return new BooleanData(Bytes.compare(((StringData) par1).bytes, ((StringData) par2).bytes));
            throw new ClassCastException();
        }, "par1", "par2");
        func(ADD,/*       */(builder, ths) -> {
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
        }, "par1", "par2");
        func(SUB,/*       */(builder, ths) -> new NumberData(builder.getNumberParam(0).number - builder.getNumberParam(1).number), "par1", "par2");
        func(MUL,/*       */(builder, ths) -> new NumberData(builder.getNumberParam(0).number * builder.getNumberParam(1).number), "par1", "par2");
        func(DIV,/*       */(builder, ths) -> new NumberData(builder.getNumberParam(0).number / builder.getNumberParam(1).number), "par1", "par2");
        func(MORE,/*      */(builder, ths) -> new BooleanData(builder.getNumberParam(0).number > builder.getNumberParam(1).number), "par1", "par2");
        func(MORE_OR_EQUAL, (builder, ths) -> new BooleanData(builder.getNumberParam(0).number >= builder.getNumberParam(1).number), "par1", "par2");
        func(LESS,/*      */(builder, ths) -> new BooleanData(builder.getNumberParam(0).number < builder.getNumberParam(1).number), "par1", "par2");
        func(LESS_OR_EQUAL, (builder, ths) -> new BooleanData(builder.getNumberParam(0).number <= builder.getNumberParam(1).number), "par1", "par2");

    }
}
