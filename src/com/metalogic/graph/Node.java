package com.metalogic.graph;

import com.intellij.psi.PsiNamedElement;
import com.metalogic.graph.ui.ElementButton;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;


public abstract class Node<E extends PsiNamedElement> extends NodeList {
    public static final Logger LOGGER = Logger.getLogger(Node.class);

    E psiElement;
    SmallGraph owner;
    int referenceCount;
    int depth;
    int recursiveIncomingArcsCount;

    boolean hasOverridingMethods;
    boolean hasSuperMethods;


    protected Node(final E psiElement) {
        this.psiElement = psiElement;
    }

    protected void addReferencedNode(final Node referencedNode) {
        if (referencedNodes.contains(referencedNode)) return;

        LOGGER.debug("Adding referenced node " + referencedNode + " to " + this);
        referencedNodes.add(referencedNode);
    }

    protected void incReferenceCount() {
        ++referenceCount;
    }


    protected abstract JComponent ui(final SmallGraph graphParent, LinkedList<Node> path);


    JComponent box(SmallGraph graphParent, LinkedList<Node> path, ElementButton myButton) {
        final Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(myButton);

        if (referencedNodes.size() > 0) {
            final Box verticalBox = Box.createVerticalBox();
            for (Node node : referencedNodes) { // we don't go deeper...
                if (/*node.getReferenceCount() > 1*/ node.getReferenceCount()
                        - node.getRecursiveIncomingArcsCount() > 1) {
                    LOGGER.debug("Found >2ref node = " + node);
                    graphParent.addAuxilliaryNode(node);
                    continue;
                }

                path.add(this);
                if (!path.contains(node)) {
                    verticalBox.add(node.ui(graphParent, path));
                }
                path.removeLast();
            }
            horizontalBox.add(verticalBox);
        }

        horizontalBox.add(Box.createHorizontalGlue());
        return horizontalBox;
    }


    protected void assignOwner(
            List<Node> visitedNodes2, SmallGraph owner, int depth) {
        LOGGER.debug("Going to assign owner " + owner + " (recursively) to " + this);
        if (visitedNodes2.contains(this)) {
            LOGGER.debug("Owner already assigned");
            return;
        }
        visitedNodes2.add(this);


        LOGGER.debug("setting owner of " + this + " to " + owner);
        this.owner = owner;

        if (depth > getDepth()) {
            this.depth = depth;
            LOGGER.debug(psiElement.getName() + " depth = " + depth);
        }
        if (isTerminal()) {
            LOGGER.debug("Terminal node, returning");
            return;
        }

        for (Node referenced : getReferencedNodes()) {
            referenced.assignOwner(visitedNodes2, owner, depth + 1);
        }
    }

    protected SmallGraph findOwnerGraph(final List<Node> visitedNodes) {
        if (visitedNodes.contains(this)) {
            LOGGER.debug("We already visited " + this);
            return owner;
        }

        visitedNodes.add(this);
        LOGGER.debug("visited " + this);
        if (isTerminal()) return owner;

        LOGGER.debug("I am not terminal node, scanning references");
        for (Node referenced : getReferencedNodes()) {
            SmallGraph owner = referenced.findOwnerGraph(visitedNodes);
            if (owner != null) {
                LOGGER.debug("Found owner!!!");
                return owner;
            }
        }
        return null;
    }

    protected void scanForRecursions(final LinkedList<Node> path) {
        LOGGER.debug("Visiting " + this);
        if (path.contains(this)) {
            LOGGER.debug("Recursion detected!");
            recursiveIncomingArcsCount = 1;
            addRecursivePath(path); // just logs
            return;
        }

        path.add(this);
        for (Node referenced : getReferencedNodes()) {
            referenced.scanForRecursions(path);
        }
        path.removeLast();
    }

    private void addRecursivePath(final LinkedList<Node> path) {
        final int start = path.indexOf(this);
        LOGGER.debug("RECURSIVE PATH PART:");
        for (int i = start; i < path.size(); i++) {
            LOGGER.debug(path.get(i) + " ");
        }
    }


    protected int getDepth() {
        return depth;
    }

    /**
     * @return
     */
    private boolean isTerminal() {
        return referencedNodes.size() == 0;
    }

    private int getReferenceCount() {
        return referenceCount;
    }

    protected boolean isRoot() {
        return referenceCount == 0;
    }

    public String toString() {
        return psiElement.getName();
    }

    private int getRecursiveIncomingArcsCount() {
        return recursiveIncomingArcsCount;
    }
}

