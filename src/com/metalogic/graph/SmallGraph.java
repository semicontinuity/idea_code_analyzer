package com.metalogic.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.search.LocalSearchScope;

public class SmallGraph /*extends NodeList */{
    public static final Logger LOGGER = Logger.getInstance(SmallGraph.class);

    public SmallGraph () {
        classStructureGraph = null;
    }

    public SmallGraph (
            Collection<? extends Node<?>> rootNodes,
            ClassStructureGraph classStructureGraph)
    {
        this.rootNodes = new HashSet<>(rootNodes);
        this.classStructureGraph = classStructureGraph;
    }


    private Set<Node<?>> rootNodes = new HashSet<>();
    private final ClassStructureGraph classStructureGraph;
    private Set<Node<?>> auxilliaryNodes = new HashSet<>();

    protected void addRootNode(Node<?> node) {
        rootNodes.add(node);
    }

    protected void addAuxilliaryNode(Node node) {
        if (auxilliaryNodes.add(node)) {
            LOGGER.warn("Added auxilliaryNode " + node + " to " + this);
        }
    }


    protected JComponent ui(List<Node<?>> path, ArrayList<Node<?>> visitedNodes) {
        final Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        final Box verticalBox = Box.createVerticalBox();
        LOGGER.warn("Adding layer 0 for root nodes");
        for (Node<?> rootNode : rootNodes) {
            if (!path.contains(rootNode)) {
                LOGGER.warn("Adding ui for root node " + rootNode);
                verticalBox.add(rootNode.ui(this, path, node -> {
                    LOGGER.warn("*** ACTION " + node);
                    classStructureGraph.deselectNodes();

                    node.select(0);
                    classStructureGraph.selectedNodes.add(node);

                    classStructureGraph.processReferencesTo(node.psiElement, new LocalSearchScope(classStructureGraph.psiClass), psiMethod -> {
                        LOGGER.warn("*** METHOD: " + psiMethod);
                        Node<?> refNode = classStructureGraph.element2node.get(psiMethod);
                        if (refNode != null) {
                            refNode.select(-1);
                            classStructureGraph.selectedNodes.add(refNode);
                        }
                    });

                    classStructureGraph.processReferencesFrom(node.psiElement, psiField -> {
                        LOGGER.warn("*** FIELD: " + psiField);
                        Node<?> refNode = classStructureGraph.element2node.get(psiField);
                        if (refNode != null) {
                            refNode.select(1);
                            classStructureGraph.selectedNodes.add(refNode);
                        }
                    });
                }));
            }
        }
        horizontalBox.add(verticalBox);
        LOGGER.warn("Added layer 0 for root nodes");

        if (auxilliaryNodes.size() > 0/* && (auxilliaryNodes.size() != rootNodes.size())*/) {
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
            LOGGER.warn("Adding layer for remaining nodes");
            final SmallGraph newGraph = new SmallGraph(auxilliaryNodes, classStructureGraph);
//            classStructureGraph.smallGraphs.add(newGraph);
            final List<Node<?>> newpath = new ArrayList<>();
            horizontalBox.add(newGraph.ui(newpath, visitedNodes));
            LOGGER.warn("Added layer for remaining nodes");
        }

        return horizontalBox;
    }
}
