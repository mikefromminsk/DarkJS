package com.metabrain.gui.server;

import com.google.gson.Gson;
import com.metabrain.gui.server.model.GetNodeBody;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class DjsThreadTest {

    private static Gson json = new Gson();

    @Test
    void setNode() throws IOException {
        File file = new File("test_res/node.json");
        String nodeBody = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        GetNodeBody getNodeBody = json.fromJson(nodeBody, GetNodeBody.class);
        DjsThread.updateNode(getNodeBody.body);
    }
}