package org.pdk.files.converters.js;

import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.ParserException;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;
import org.pdk.files.converters.utils.ConverterParser;

public class JsParser implements ConverterParser {

    public JsParser() {

    }

    public jdk.nashorn.internal.ir.Node parse(String sourceString) throws ParserException {
        Options options = new Options("nashorn");
        options.set("anon.functions", true);
        options.set("build.only", true);
        options.set("scripting", true);
        ErrorManager errors = new ErrorManager();
        Context context = new Context(options, errors, Thread.currentThread().getContextClassLoader());
        Source source = Source.sourceFor("test", sourceString);
        jdk.nashorn.internal.parser.Parser parser = new jdk.nashorn.internal.parser.Parser(context.getEnv(), source, errors);
        jdk.nashorn.internal.ir.Node rootParserNode = parser.parse();
        if (rootParserNode == null)
            throw new ParserException("");
        return rootParserNode;
    }

    @Override
    public Object parse() {
        return null;
    }
}
