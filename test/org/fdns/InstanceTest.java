package org.fdns;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InstanceTest {

    @Test
    void send() {
        Network network = new Network();
        network.host("1", null);
        network.host("2", data -> "result 2").proxy("1").registration("two");
        network.host("3", data -> "result 3").proxy("1").registration("three");
        network.get("2").post("three", "data", data -> {
            assertEquals(data, "result 3");
            System.out.println("all is ok");
        }, message -> fail(message));
    }
}