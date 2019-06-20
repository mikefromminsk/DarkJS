package com.droid.djs.runner;

import com.droid.Main;
import com.droid.djs.builder.NodeBuilder;
import com.droid.djs.nodes.Node;
import com.droid.djs.serialization.node.Formatter;
import com.droid.djs.treads.Secure;
import com.droid.djs.treads.ThreadPool;
import org.apache.commons.io.FileUtils;
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
        String sourceCode = null;
        NodeBuilder builder = new NodeBuilder();

        try {
            File nodesTestsDir = new File("test_res/run/");
            File[] tests = nodesTestsDir.listFiles();
            if (tests != null) {
                List<File> list = Arrays.asList(tests);
                Collections.reverse(list);
                for (File script : list) {

                    sourceCode = FileUtils.readFileToString(script, StandardCharsets.UTF_8);
                    Node module = ThreadPool.getInstance().runScript("tests/" + script.getName(), sourceCode,
                            Secure.getAccessCode(Main.login, Main.password));

                    Node testVar = builder.set(module).findLocal("test");
                    if (testVar == null)
                        System.out.println(Formatter.toJson(module));
                    assertNotNull(testVar);
                    Node testValue = builder.set(testVar).getValueNode();
                    if (testValue == null)
                        System.out.println(Formatter.toJson(module));
                    assertNotNull(testValue);
                    Boolean testData = (Boolean) builder.set(testValue).getData().getObject();
                    if (testData == null || !testData)
                        System.out.println(Formatter.toJson(module));
                    assertTrue(testData);
                }
            } else {
                fail("tests not found");
            }
        } catch (IOException e) {
            fail(e);
        }
    }
}