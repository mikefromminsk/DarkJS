package org.pdk.gdb;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InfinityArrayTest {

    /*  @Test
      void testEncodingDecoding() {
          String startData = "create data";
          byte[] data = startData.getBytes();
          long accessKey = InfinityArray.encodeData(data);
          assertNotEquals(startData, new String(data));
          InfinityArray.decodeData(data, accessKey);
          assertEquals(startData, new String(data));
      }*/
    @RepeatedTest(1000)
    @Test
    void add() {
        InfinityStringArray testArray = new InfinityStringArray("out/SimpleGraphDB", "testArray");
        // TODO issue with multithreading access to equal infinity array
        long index = testArray.addString("tests");
        testArray.setString(index, "bests");
        String results = testArray.getString(index);
        assertEquals("bests", results);
    }

    /*@Test
    void testAddToGarbage() throws Exception {
        InfinityStringArray arr = new InfinityArray("testArrayGarbage");
        long index = arr.addObject("test");
        arr.setString(index, "testss");
        assertEquals("testss", arr.getString(index));

        InfinityConstArray garbage4 = arr.garbageCollector.getObject(4L);

        long garbage4Size = garbage4.getLong(0);
        long lastValue = garbage4.getLong(garbage4Size);
        assertEquals(index, lastValue);

        long ssIndex = arr.addObject("ss");
        arr.setString(ssIndex, "ssdd");
        InfinityConstArray garbage2 = arr.garbageCollector.getObject(2L);
        long garbage2Size = garbage2.getLong(0);
        lastValue = garbage2.getLong(garbage2Size);
        assertEquals(ssIndex, lastValue);

        long garbage4NewSize = garbage4.getLong(0);
        assertEquals(garbage4Size - 1, garbage4NewSize);
    }*/
}