package com.metalogic.graph;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SmallGraph /*extends NodeList */{

    public SmallGraph () { }

    public SmallGraph (List<Node> nodes) { this.rootNodes = nodes; }


    private List<Node> visitedNodes;    // TODO: replace by Set! (don't forget equals ()and hashCode())
    private List<Node> rootNodes = new ArrayList<Node>();   // TODO: replace by Set! (don't forget equals ()and hashCode())
    private List<Node> auxilliaryNodes = new ArrayList<Node>(); // TODO: replace by Set! (don't forget equals ()and hashCode())

    protected void addRootNode(Node node) {
        rootNodes.add(node);
    }

    protected void addAuxilliaryNode(Node node) {
        if (auxilliaryNodes.contains(node)) return;
        auxilliaryNodes.add(node);
    }


    protected JComponent ui(LinkedList<Node> path) {
        if (visitedNodes != null) visitedNodes.clear(); // TODO: rethink
        visitedNodes = new ArrayList<Node>();

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
            final LinkedList<Node> newpath = new LinkedList<Node>();
            horizontalBox.add(newGraph.ui(newpath));
        }

        return horizontalBox;
    }


    /**
     * Recursive
     */
    private void process(NodeList start, Box[] verticalBoxes, SmallGraph graphParent) {
        List<Node> nodeList = start.getReferencedNodes();

        for (Node node : nodeList) {
//            System.out.println("processing node " + node);
            if (visitedNodes.contains(node)) {
//                System.out.println("was processed!");
                return;
            }
            visitedNodes.add(node);

//            final int depth = node.getDepth();
//            Box box = verticalBoxes[depth];
//            if (box == null) {
//                System.out.println("Creating new smallGraphUI!");
//                box = Box.createVerticalBox();
//                verticalBoxes[depth] = box;
//                System.out.println("And assinging to depth " + depth);
//            }
//
//            box.add(new MyButton(node));
//
//            process(node, verticalBoxes, null);


            final int depth = node.getDepth();
            Box box = verticalBoxes[depth];
            if (box == null) {
//                System.out.println("Creating new smallGraphUI!");
                box = Box.createVerticalBox();
                verticalBoxes[depth] = box;
//                System.out.println("And assinging to depth " + depth);
            }

//            box.add(new MyButton(node));

            process(node, verticalBoxes, null);
        }
    }
}
