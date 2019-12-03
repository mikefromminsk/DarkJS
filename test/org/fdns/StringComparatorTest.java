package org.fdns;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringComparatorTest {

    @Test
    void compare() {
        assertEquals(3, StringComparator.compare("привет", "тивирпg"));
    }
}