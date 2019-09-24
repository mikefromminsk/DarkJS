package org.simpledb;

import org.pdk.store.data.DataOutputStream;

import java.io.File;
import java.util.*;
import java.io.IOException;
import java.util.Random;

public class DiskManager {

    public IniFile properties;
    public Map<String, InfinityFileData> infinityFileCache = new HashMap<>();
    public ActionThread actionThread;
    public Thread actionThreadInstance;

    public File dbDir;
    public Integer partSize;
    public Integer cacheSize;
    public Integer device_id;

    public final static String SECTION = "_manager_";
    public final static String PART_SIZE_KEY = "part_size";
    public final static Integer PART_SIZE_DEFAULT = 128 * 1024 * 1024;
    public final static String CACHE_SIZE_KEY = "cache_size";
    public final static Integer CACHE_SIZE_DEFAULT = 128 * 1024 * 1024;
    public final static String DEVICE_ID_KEY = "device_id";

    public DiskManager(String dbDirPath) throws IOException {
        dbDir = new File(dbDirPath);
        // TODO double save settings
        // TODO problem when DiskManager getFunctions without saving data rights

        if (!dbDir.isDirectory())
            if (!dbDir.mkdirs())
                throw new IOException();

        properties = new IniFile(new File(dbDir, "settings.properties"));

        loadProperties();
        saveProperties();

        createFtpTempDir();

        actionThread = new ActionThread(cacheSize);
        actionThreadInstance = new Thread(actionThread);
        actionThreadInstance.start();
    }

    private static Map<String, DiskManager> diskManagers = new HashMap<>();
    public static DiskManager getInstance(String storeDir) {
        DiskManager manager = diskManagers.get(storeDir);
        if (manager == null)
            try {
                diskManagers.put(storeDir, manager = new DiskManager(storeDir));
            } catch (IOException e) {
                e.printStackTrace();
             }
        return manager;
    }

    public static void removeInstance(DiskManager diskManager){
        diskManager.saveProperties();
        diskManager.actionThreadInstance.interrupt();
        diskManagers.values().remove(diskManager);
    }

    private void createFtpTempDir() {
        if (!DataOutputStream.ftpTempDir.exists())
            DataOutputStream.ftpTempDir.mkdirs();
        else {
            File[] files = DataOutputStream.ftpTempDir.listFiles();
            if (files != null)
                for (File file : files)
                    file.delete();
        }
    }

    private void loadProperties() {
        this.partSize = properties.getInt(SECTION, PART_SIZE_KEY, PART_SIZE_DEFAULT);
        this.cacheSize = properties.getInt(SECTION, CACHE_SIZE_KEY, CACHE_SIZE_DEFAULT);
        this.device_id = properties.getInt(SECTION, DEVICE_ID_KEY, Math.abs(new Random().nextInt()));
    }

    private void saveProperties() {
        properties.put(SECTION, PART_SIZE_KEY, "" + this.partSize);
        properties.put(SECTION, CACHE_SIZE_KEY, "" + this.cacheSize);
        properties.put(SECTION, DEVICE_ID_KEY, "" + this.device_id);
    }

    public File getFileById(long fileId) {
        return new File(dbDir, fileId + ".data");
    }
}
