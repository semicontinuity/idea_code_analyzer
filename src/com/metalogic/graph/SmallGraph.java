package com.metalogic.graph;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;

public class SmallGraph /*extends NodeList */{

    public SmallGraph () { }

    public SmallGraph (List<? extends Node<?>> rootNodes) { this.rootNodes = new ArrayList<>(rootNodes); }


    private List<Node<?>> rootNodes = new ArrayList<>();   // TODO: replace by Set! (don't forget equals ()and hashCode())
    private List<Node<?>> auxilliaryNodes = new ArrayList<>(); // TODO: replace by Set! (don't forget equals ()and hashCode())

    protected void addRootNode(Node<?> node) {
        rootNodes.add(node);
    }

    protected void addAuxilliaryNode(Node node) {
        if (auxilliaryNodes.contains(node)) return;
        auxilliaryNodes.add(node);
    }


    protected JComponent ui(List<Node<?>> path, ArrayList<Node<?>> visitedNodes) {
        final Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        final Box verticalBox = Box.createVerticalBox();
        for (Node rootNode : rootNodes) {
            if (!path.contains(rootNode)) {
                verticalBox.add(rootNode.ui(this, path));
            }
        }
        horizontalBox.add(verticalBox);

        if (auxilliaryNodes.size() > 0 && (auxilliaryNodes.size() != rootNodes.size())) {
//            Component horizontalStrut = Box.createHorizontalStrut(2);
//            horizontalStrut.setForeground(Color.YELLOW);
//            horizontalBox.add(horizontalStrut);
//
//            final Box verticalBox2 = Box.createVerticalBox();
//            for (Node node : auxilliaryNodes) {
//                verticalBox2.add(new Node.MyButton(node));
//            }
//            horizontalBox.add(verticalBox2);


//            ClassStructureGraph classStructureGraph = new ClassStructureGraph();
//            classStructureGraph.referencedNodes = auxilliaryNodes;
//            for (Node node : auxilliaryNodes) {
//                node.referenceCount = 0;
//                classStructureGraph.assignOwnerRecursively(node, null);
//            }
//            classStructureGraph.layout();
            final SmallGraph newGraph = new SmallGraph(auxilliaryNodes);
//            classStructureGraph.smallGraphs.add(newGraph);
            final List<Node<?>> newpath = new ArrayList<>();
            horizontalBox.add(newGraph.ui(newpath, visitedNodes));
        }

        return horizontalBox;
    }
}
