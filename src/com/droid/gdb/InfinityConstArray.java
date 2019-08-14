package com.droid.gdb;

public class InfinityConstArray extends InfinityFile {

    public InfinityConstArray(String infinityFileDir, String infinityFileID) {
        super(infinityFileDir, infinityFileID);
    }

    public InfinityConstArrayCell get(long index, InfinityConstArrayCell dest) {
        byte[] readiedData = read(index * dest.getSize(), dest.getSize());
        if (readiedData == null)
            throw new NullPointerException();
        dest.parse(readiedData);
        return dest;
    }


    public void set(long index, InfinityConstArrayCell obj) {
        write(index * obj.getSize(), obj.build());
    }


    public long add(InfinityConstArrayCell obj) {
        long lastMaxPosition = super.add(obj.build());
        return  lastMaxPosition / obj.getSize();
    }
}
