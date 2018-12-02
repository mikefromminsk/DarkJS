package com.metabrain.gui.server.model;

import com.metabrain.djs.refactored.node.Node;

import java.util.Map;


public class GetNodeBody {
    public Long threadId;
    public String nodeLink;
    public Boolean run;
    public Map<String, String> replacements;
    public Map<String, Map<String, Object>> nodes;
}
