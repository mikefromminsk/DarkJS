package com.droid.gdb;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InfinityStringArray extends InfinityFile {

    // TODO addObject secure

    protected InfinityConstArray meta;
    private Map<Long, InfinityLongArray> garbageCollector = new HashMap<>();


    public InfinityStringArray(String infinityFileID) {
        super(infinityFileID);
        meta = new InfinityConstArray(infinityFileID + ".meta");
        // TODO change all file types to with string length equal 4
        Map<String, String> garbage = DiskManager.getInstance().properties.getSection(infinityFileID + ".garbage");
        if (garbage != null)
            for (String key : garbage.keySet()) {
                Long sectorSize = Long.valueOf(key);
                InfinityLongArray garbageBySize = new InfinityLongArray(garbage.get(key));
                garbageCollector.put(sectorSize, garbageBySize);
            }
    }

    public MetaCell initMeta() {
        return new MetaCell();
    }

    protected MetaCell getMeta(long index) {
        return (MetaCell) meta.get(index, initMeta());
    }

    public InfinityStringArrayCellParser getObject(long index, InfinityStringArrayCellParser dest) {
        MetaCell metaCell = getMeta(index);
        byte[] readiedData = read(metaCell.start, metaCell.length);
        decodeData(readiedData, metaCell.accessKey);
        dest.parse(readiedData);
        return dest;
    }

    private StringCell stringCell = new StringCell();

    public String getString(long index) {
        getObject(index, stringCell);
        return stringCell.str;
    }

    public byte[] getBytes(long index) {
        return getString(index).getBytes();
    }

    private static Random randomOfAccessKeys = new Random(System.currentTimeMillis());
    private static Random random = new Random();

    public static long encodeData(byte[] data) {
        long accessKey = randomOfAccessKeys.nextLong();
        /*random.setSeed(accessKey);
        byte[] gamma = new byte[data.length];
        random.nextBytes(gamma);
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) ((data[i] + gamma[i]) % 256);*/
        return accessKey;
    }

    public static void decodeData(byte[] data, long accessKey) {
        /*random.setSeed(accessKey);
        byte[] gamma = new byte[data.length];
        random.nextBytes(gamma);
        for (int i = 0; i < data.length; i++)
            data[i] = (byte) ((256 + (data[i] - gamma[i])) % 256);*/
    }

    public void setBytes(long index, byte[] data) {
        MetaCell metaCell = getMeta(index);
        int lastSectorLength = getSectorLength((int) metaCell.length);
        int newSectorLength = getSectorLength(data.length);
        if (newSectorLength > lastSectorLength) {
            MetaCell garbage = getFreeSector(newSectorLength);
            removeSector(index, lastSectorLength);
            byte[] sectorWithData = new byte[newSectorLength];
            System.arraycopy(data, 0, sectorWithData, 0, data.length);
            long newAccessKey = encodeData(sectorWithData);
            if (garbage == null) {
                metaCell.start = super.add(sectorWithData);
                metaCell.length = data.length;
                metaCell.accessKey = newAccessKey;
                meta.set(index, metaCell);
            } else {
                write(garbage.start, sectorWithData);
                garbage.length = data.length;
                garbage.accessKey = newAccessKey;
                meta.set(index, garbage);
            }
        } else {
            byte[] sectorWithData = new byte[lastSectorLength];
            System.arraycopy(data, 0, sectorWithData, 0, data.length);
            metaCell.accessKey = encodeData(sectorWithData);
            metaCell.length = data.length;
            meta.set(index, metaCell);
            write(metaCell.start, sectorWithData);
        }
    }

    public void setString(long index, String data) {
        stringCell.str = data;
        setObject(index, stringCell);
    }

    public void setObject(long index, InfinityStringArrayCellBuilder cell) {
        setBytes(index, cell.build());
    }

    public void removeSector(long index, long sectorSize) {
        InfinityLongArray garbageBySize = garbageCollector.get(sectorSize);
        if (true) return;
        if (garbageBySize == null) {
            String garbageName = infinityFileID + ".garbage";
            String garbageNameWithSize = garbageName + sectorSize;
            garbageBySize = new InfinityLongArray(garbageNameWithSize);
            garbageBySize.addLong(1);
            garbageBySize.addLong(index);
            garbageCollector.put(sectorSize, garbageBySize);
            DiskManager.getInstance().properties.put(garbageName, "" + sectorSize, garbageNameWithSize);
        } else {
            long lastContentIndex = garbageBySize.getLong(0);
            if (lastContentIndex < garbageBySize.fileData.sumFilesSize / Long.BYTES) {
                garbageBySize.setLong(lastContentIndex + 1, index);
            } else {
                garbageBySize.addLong(index);
            }
            garbageBySize.setLong(0, lastContentIndex + 1);
        }
    }

    public MetaCell getFreeSector(long sectorSize) {
        // TODO enable garbage collector
        if (true) return null;
        InfinityLongArray garbageBySize = garbageCollector.get(sectorSize);
        if (garbageBySize != null) {
            long lastGarbageIndex = garbageBySize.getLong(0);
            if (lastGarbageIndex > 1) {
                garbageBySize.setLong(0, lastGarbageIndex - 1);
                return getMeta(garbageBySize.getLong(lastGarbageIndex));
            }
        }
        return null;
    }

    public long addBytes(byte[] data) {
        MetaCell metaCell = initMeta();
        if (data != null && data.length != 0) {
            byte[] sector = dataToSector(data);
            long newAccessKey = encodeData(sector);
            metaCell.start = super.add(sector);
            metaCell.length = data.length;
            metaCell.accessKey = newAccessKey;
        }
        return meta.add(metaCell);
    }

    public long addString(String data) {
        return addBytes(data.getBytes());
    }

    public long addObject(InfinityStringArrayCellBuilder cell) {
        return addBytes(cell.build());
    }

    private int getSectorLength(int dataLength) {
        int sectorSize = 1;
        while (sectorSize < dataLength)
            sectorSize *= 2;
        return sectorSize;
    }

    byte[] dataToSector(byte[] data) {
        byte[] result = new byte[getSectorLength(data.length)];
        System.arraycopy(data, 0, result, 0, data.length);
        return result;
    }
}
