package com.metabrain.gdb;

public class InfinityLongArray extends InfinityConstArray {
    public InfinityLongArray(String infinityFileID) {
        super(infinityFileID);
    }

    LongCell longCell = new LongCell();

    public long getLong(long index) {
        get(index, longCell);
        return longCell.value;
    }

    public void setLong(long index, long value) {
        longCell.setData(value);
        set(index, longCell);
    }

    public long addLong(long value) {
        longCell.value = value;
        long lastMaxPosition = super.add(longCell.build());
        return lastMaxPosition / longCell.getSize();
    }

}
