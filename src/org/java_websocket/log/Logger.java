package org.java_websocket.log;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;

import java.io.IOException;

public class Logger {

    public void trace(String s, Object... args) {
        System.out.println(s);
    }

    public void error(String s) {
        error(s, "");
    }

    public boolean isTraceEnabled() {
        return true;
    }

    public void error(String s, Object... args) {
        System.out.println(s);
    }
}
