package com.droid.gdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InfinityStringArray extends InfinityFile {

    // TODO addObject secure

    public InfinityConstArray meta;
    private Map<Long, InfinityLongArray> garbageCollector = new HashMap<>();
    private String infinityFileDir;

    public InfinityStringArray(String infinityFileDir, String infinityFileName) {
        super(infinityFileDir, infinityFileName);
        this.infinityFileDir = infinityFileDir;
        meta = new InfinityConstArray(infinityFileDir, infinityFileName + ".meta");
        // TODO change all file types to with string length equal 4
        Map<String, String> garbage = diskManager.properties.getSection(infinityFileName + ".garbage");
        if (garbage != null)
            for (String key : garbage.keySet()) {
                Long sectorSize = Long.valueOf(key);
                InfinityLongArray garbageBySize = new InfinityLongArray(infinityFileDir, garbage.get(key));
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

    public void setBytes(long index, byte[] data) {
        MetaCell metaCell = getMeta(index);
        int lastSectorLength = getSectorLength((int) metaCell.length);
        int newSectorLength = getSectorLength(data.length);
        if (newSectorLength > lastSectorLength) {
            MetaCell garbage = getFreeSector(newSectorLength);
            removeSector(index, lastSectorLength);
            byte[] sectorWithData = new byte[newSectorLength];
            System.arraycopy(data, 0, sectorWithData, 0, data.length);
            if (garbage == null) {
                metaCell.start = super.add(sectorWithData);
                metaCell.length = data.length;
                meta.set(index, metaCell);
            } else {
                write(garbage.start, sectorWithData);
                garbage.length = data.length;
                meta.set(index, garbage);
            }
        } else {
            byte[] sectorWithData = new byte[lastSectorLength];
            System.arraycopy(data, 0, sectorWithData, 0, data.length);
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
            String garbageName = infinityFileName + ".garbage";
            String garbageNameWithSize = garbageName + sectorSize;
            garbageBySize = new InfinityLongArray(infinityFileDir, garbageNameWithSize);
            garbageBySize.addLong(1);
            garbageBySize.addLong(index);
            garbageCollector.put(sectorSize, garbageBySize);
            diskManager.properties.put(garbageName, "" + sectorSize, garbageNameWithSize);
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
            metaCell.start = super.add(sector);
            metaCell.length = data.length;
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

    protected byte[] dataToSector(byte[] data) {
        byte[] result = new byte[getSectorLength(data.length)];
        System.arraycopy(data, 0, result, 0, data.length);
        return result;
    }

    public void close() throws IOException {
        super.close();
        meta.close();
        for (Long id: garbageCollector.keySet())
            garbageCollector.get(id).close();
    }
}
