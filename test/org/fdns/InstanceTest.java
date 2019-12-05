package org.fdns;

import org.junit.jupiter.api.Test;

class InstanceTest {

    void log(String str) {
        System.out.println(str);
    }

    @Test
    void send() {
        Network network = new Network();
        network.host("1", data -> "1: " + data);

        final String[] twoReassignToken = {""};
        network.host("2", data -> "2: " + data).proxy("1")
                .registration("two",  (reassignToken) -> {
                    twoReassignToken[0] = reassignToken;
                    log("two registered");
                }, (message) -> log("two not registered"));

        network.host("3", data -> "3: " + data).proxy("1")
                .registration("three",  (reassignToken) -> log("3: domain \"three\" registered"), (message) -> log("3:" + message));

        network.get("3").post("two", "test request data", data -> log(data), (message) -> log("2:" + message));

        network.host("4", data -> "4: " + data).proxy("1")
                .reassign("two", twoReassignToken[0], reassignToken -> {

                }, (message) -> {

                });

        network.get("3").post("two", "reassign post data",
                data -> {

                }, message -> {

                });

    }
}