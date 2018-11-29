package com.metabrain.gui.server.model;

import java.util.Map;


public class GetNodeBody {
    public Long nodeId;
    public Long threadId;
    public Map<String, Map<String, Object>> body;
}
