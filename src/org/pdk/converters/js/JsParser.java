package org.pdk.converters.js;

import jdk.nashorn.internal.ir.Node;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.ParserException;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;
import org.pdk.converters.ConverterParser;
import org.pdk.storage.model.data.FileData;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class JsParser implements ConverterParser {

    @Override
    public Node parse(FileData data) {
        Options options = new Options("nashorn");
        options.set("anon.functions", true);
        options.set("build.only", true);
        options.set("scripting", true);
        ErrorManager errors = new ErrorManager();
        Context context = new Context(options, errors, Thread.currentThread().getContextClassLoader());
        try {
            Reader reader = new FileReader(data.getFile());
            Source source = Source.sourceFor("test", reader);
            jdk.nashorn.internal.parser.Parser parser = new jdk.nashorn.internal.parser.Parser(context.getEnv(), source, errors);
            jdk.nashorn.internal.ir.Node rootParserNode = parser.parse();
            if (rootParserNode == null)
                throw new ParserException("");
            return rootParserNode;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
