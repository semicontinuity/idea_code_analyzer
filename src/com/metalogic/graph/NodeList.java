package com.metalogic.graph;

import java.util.ArrayList;
import java.util.List;

public class NodeList {
    protected List<Node<?>> referencedNodes = new ArrayList<>();


    public List<Node<?>> getReferencedNodes() {
        return referencedNodes;
    }
}
