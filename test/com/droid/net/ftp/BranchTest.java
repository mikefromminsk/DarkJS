package com.droid.net.ftp;

import com.droid.djs.fs.Branch;
import com.droid.djs.fs.Files;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {
 /*   @Test
    void test() {
        Branch branch = new Branch();
        String testPath = "tests/BranchTest";
        String branchData = "" + new Random().nextInt();
        String masterStartData = "sdf";
        Files.putFile(Instance.get().getMaster(), testPath, masterStartData);
        String str = Files.getFileString(Instance.get().getMaster(), testPath);
        assertEquals(masterStartData, str);
        Files.putFile(branch.getRoot(), testPath, branchData);
        branch.mergeWithMaster();
        String masterData = Files.getFileString(Instance.get().getMaster(), testPath);
        assertEquals(branchData, masterData);
    }*/
}