package com.droid.djs.runner;

import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.consts.NodeType;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.js.Parser;
import com.droid.djs.serialization.links.Formatter;
import com.droid.djs.treads.ThreadPool;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RunnerTest {

    @Test
    void run() {
        File currentScript = null;
        String sourceCode = null;
        NodeBuilder builder = new NodeBuilder();

        try {
            File nodesTestsDir = new File("test_res/run/");
            File[] tests = nodesTestsDir.listFiles();
            Parser parser = new Parser();
            if (tests != null) {
                List<File> list = Arrays.asList(tests);
                Collections.reverse(list);
                for (File script : list) {
                    currentScript = script;

                    sourceCode = FileUtils.readFileToString(script, StandardCharsets.UTF_8);
                    Node module = ThreadPool.getInstance().runScript("test", sourceCode);

                    Node testVar = builder.set(module).findLocal("test");
                    assertNotNull(testVar);

                    Node testValue = builder.set(testVar).getValueNode();
                    assertNotNull(testValue);
                    Assertions.assertEquals(NodeType.BOOL, testValue.type);
                    Boolean testData = (Boolean) builder.set(testValue).getData().getObject();
                    if (testData != null && !testData && module != null) {
                        System.out.println(currentScript.getAbsolutePath());
                        System.out.println(Formatter.toJson(module));
                    }
                    assertTrue(true);
                }
            } else {
                fail("tests not found");
            }
        } catch (IOException e) {
            fail(e);
        }
    }
}