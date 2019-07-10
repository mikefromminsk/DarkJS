package com.droid.djs.nodes;

import com.droid.djs.NodeStorage;
import com.droid.djs.serialization.node.NodeBuilder;
import com.droid.djs.nodes.consts.NodeType;
import com.droid.gdb.DiskManager;

import java.util.ArrayList;
import java.util.Date;

class NodeStorageTest implements Runnable {

    private static Integer count = 1000;
    public static ArrayList<String> out = new ArrayList<>();
    private static Thread thread;

    static void add(String str) {
        if (out != null)
            out.add(str);
    }

    static void add(String key, String value) {
        add(key + ": " + value);
    }

    static void add(String key, Integer value) {
        add(key, "" + value);
    }

    static void add(String key, Long value) {
        add(key, "" + value);
    }

    private static Date timeObj = new Date();

    static long time() {
        return timeObj.getTime();
    }

    public static int getRandom(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

    public static ArrayList<String> getLog() {
        if (thread != null && thread.isAlive()) {
            // progress request
            return out;
        } else {
            if (out == null) { // getFunctions request
                out = new ArrayList<>();
                return null;
            } else if (out.size() > 0) {  // last request
                ArrayList<String> result = out;
                out = null;
                return result;
            } else {
                // first request
                add("partSize", DiskManager.getInstance().partSize);
                add("cacheSize", DiskManager.getInstance().cacheSize);
                add("Start", "put " + count + " records without duplicates");
                thread = new Thread(new NodeStorageTest());
                thread.start();
                return out;
            }
        }
    }

    @Override
    public void run() {
        NodeBuilder builder = new NodeBuilder();

        long start = time();
        for (int i = 0; i < count; i++)
            builder.create(NodeType.STRING).setData("" + i).commit();
        add("Finish", time() - start);

        add("Start", "commit");
        start = time();
        NodeStorage.getInstance().transactionCommit();
        add("Finish(ms)", time() - start);

        add("Start", "put " + count + " records with duplicates");
        for (int i = 0; i < count; i++)
            builder.create(NodeType.STRING).setData("" + i).commit();
        add("Finish", time() - start);

        add("Start", "random put " + count + " records with duplicates");
        for (int i = 0; i < count; i++)
            builder.create(NodeType.STRING).setData("" + getRandom(0, count)).commit();
        add("Finish", time() - start);
        NodeStorage.getInstance().transactionCommit();
    }
}
