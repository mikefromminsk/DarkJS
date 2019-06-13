package com.droid.net.ftp;

import com.droid.djs.fs.Branch;
import com.droid.djs.fs.Files;
import com.droid.djs.fs.Master;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BranchTest {

    @Test
    void test() {
        Branch branch = new Branch();
        String testPath = "tests/BranchTest";
        String branchData = "" + new Random().nextInt();
        String masterStartData = "sdf";
        Files.putFile(Master.getInstance(), testPath, masterStartData);
        String str = Files.getFileString(Master.getInstance(), testPath);
        assertEquals(masterStartData, str);
        Files.putFile(branch.getRoot(), testPath, branchData);
        branch.mergeWithMaster();
        String masterData = Files.getFileString(Master.getInstance(), testPath);
        assertEquals(branchData, masterData);
    }
}