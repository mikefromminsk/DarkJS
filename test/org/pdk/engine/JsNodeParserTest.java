package org.pdk.engine;

import org.pdk.engine.convertors.js.JsParser;
import jdk.nashorn.internal.runtime.ParserException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class JsNodeParserTest {

    @Test
    void parse() throws IOException {
        File file = new File("test_res/build/JsParserScript.js");
        String scriptStr = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        try {
            JsParser.parse(scriptStr);
        } catch (ParserException e) {
            fail("parse exception");
        }
    }

    @Test
    void parseSimple() {
        try {
            JsParser.parse("var wef = 1;");
        } catch (ParserException e) {
            fail("parse exception");
        }
    }

    /*@Test
    void parseException() {
        Boolean parseError;
        try {
            JsParser.fromMap("1d1d");
            parseError = false;
        } catch (ParserException e) {
            parseError = true;
        }
        assertTrue(parseError);
    }*/

}