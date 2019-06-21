package org.java_websocket.log;

import org.java_websocket.AbstractWebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.server.WebSocketServer;

public class LoggerFactory {

    public static Logger getLogger(Class c) {
        return new Logger();
    }
}
