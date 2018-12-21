package com.metabrain.djs.node;

import com.metabrain.gdb.DiskManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NodeStorageTest implements Runnable {

    private static Random random = new Random();
    public static List<String> out;

    static void add(String str) {
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

    public static void startTest() {
        if (out == null) {
            out = new ArrayList<>();
            new Thread(new NodeStorageTest()).start();
        }
    }

    @Override
    public void run() {
        if (out != null) {
            String treeFileName = "" + random.nextInt();
            add("partSize", treeFileName);
            add("partSize", DiskManager.getInstance().partSize);
            add("cacheSize", DiskManager.getInstance().cacheSize);
            int count = 1000;
            NodeBuilder builder = new NodeBuilder();

            //diskTesting();

            add("Start", "put " + count + " records without duplicates");
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
        }
    }
}
