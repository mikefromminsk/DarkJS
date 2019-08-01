package com.droid.djs.fs;

import com.droid.djs.serialization.js.JsBuilder;
import com.droid.djs.serialization.js.JsParser;
import com.droid.djs.serialization.json.JsonBuilder;
import com.droid.djs.serialization.json.JsonParser;
import com.droid.djs.nodes.NodeBuilder;
import com.droid.djs.nodes.Data;
import com.droid.djs.nodes.Node;
import com.google.gson.JsonElement;

import java.io.*;
import java.util.Random;

public class DataOutputStream extends OutputStream {


    public final static File ftpTempDir = new File("out/FtpTemp");
    public final static Random random = new Random();
    private Node node;
    private File tempFile = new File(ftpTempDir, "" + random.nextInt());
    private FileOutputStream out;
    private Branch branch;

    public DataOutputStream(Branch branch, Node node) {
        this.branch = branch;
        this.node = node;
        if (!ftpTempDir.exists())
            ftpTempDir.mkdirs();
        try {
            out = new FileOutputStream(tempFile);
        } catch (Exception e1) {
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        branch.updateTimer();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }


    @Override
    public void close() {
        try {
            out.close();
            // TODO error with uploading a empty file
            Node res = Files.putFile(node, new FileInputStream(tempFile));
            NodeBuilder builder = new NodeBuilder().set(res);
            Data dataNode = (Data) builder.getValueNode();
            Data parserNode = builder.getParserNode();
            if (parserNode != null && dataNode != null) {
                String parser = parserNode.data.readString();
                String data = dataNode.data.readString();
                if ("json".equals(parser)) {
                    JsonElement jsonElement = JsonParser.parse(data);
                    JsonBuilder.build(node, jsonElement);
                }
                if ("node.js".equals(parser)) {
                    jdk.nashorn.internal.ir.Node nashornNode = JsParser.parse(data);
                    new JsBuilder().build(node, nashornNode);
                }
            }

        /*if (parser == null) {
            // nothing
        } else if (parser.equals("thread.js")) {
            NodeBuilder value = new NodeBuilder().set(builder.getValueNode());
            String sourceCode = value.getData().readString();
            builder.setValue(null).commit();
            new JsBuilder().build(res, sourceCode);
        } else if (parser.equals("html")) {
            Node parent = builder.set(node).getLocalParentNode();
            parent.build(node.build());
            builder.set(parent).commit();
            // TODO delete local from parent
        }*/
            tempFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
