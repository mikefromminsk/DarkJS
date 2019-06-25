package com.droid.djs;

import com.droid.gdb.InfinityFile;
import com.droid.gdb.map.InfinityHashMap;

import java.util.ArrayList;

public class DataStorage {

    private static final String dataStorageID = "data";
    private static final String hashStorageID = "hash";

    private static InfinityFile dataStorage;
    private static InfinityHashMap dataHashTree;

    private static DataStorage instance;

    public static DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();

            dataStorage = new InfinityFile(dataStorageID);
            dataHashTree = new InfinityHashMap(hashStorageID);

        }
        return instance;
    }

}
