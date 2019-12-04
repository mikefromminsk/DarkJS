package org.fdns;

import org.junit.jupiter.api.Test;

class InstanceTest {

    @Test
    void send() {
        Network network = new Network();
        network.host("1");
        network.host("2").addProxy("1").registration("first");
        network.host("3").addProxy("1").registration("second");
        network.host("2").post("second", "data", null, null);
    }
}