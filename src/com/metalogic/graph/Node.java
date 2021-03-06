package com.metalogic.graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JComponent;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiNamedElement;
import com.metalogic.graph.ui.ElementButton;


public abstract class Node<E extends PsiNamedElement> extends NodeList {
    public static final com.intellij.openapi.diagnostic.Logger LOGGER = Logger.getInstance(Node.class);

    E psiElement;
    SmallGraph owner;
    int referenceCount;
    int depth;
    int recursiveIncomingArcsCount;

    boolean hasOverridingMethods;
    boolean hasSuperMethods;

    private final Set<Node<?>> incomingReferences = new HashSet<>();


    protected ElementButton<?> button;


    protected Node(final E psiElement) {
        this.psiElement = psiElement;
    }

    protected void addReferencedNode(final Node referencedNode) {
        if (referencedNodes.contains(referencedNode)) return;

        LOGGER.debug("Adding referenced node " + referencedNode + " to " + this);
        referencedNodes.add(referencedNode);
    }

    protected void incReferenceCount(Node<?> sourceNode) {
        if (incomingReferences.add(sourceNode)) {
            LOGGER.warn("REF to " + this + " FROM " + sourceNode);
            ++referenceCount;
        }
    }


    protected abstract JComponent ui(final SmallGraph graphParent, List<Node<?>> path, Consumer<Node<?>> nodeConsumer);


    protected JComponent makeHorizontalBox(ElementButton nodeButton, SmallGraph graphParent, List<Node<?>> path,
                                           Consumer<Node<?>> actionConsumer) {
        LOGGER.warn("Making horizontal box for " + this);
        final Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(nodeButton);

        if (referencedNodes.size() > 0) {
            final Box verticalBox = makeVerticalBox(graphParent, path, actionConsumer);
            horizontalBox.add(verticalBox);
        }

        horizontalBox.add(Box.createHorizontalGlue());
        LOGGER.warn("Done making horizontal box for " + this);
        return horizontalBox;
    }

    /**
     * @param path is used to stop recursions
     */
    private Box makeVerticalBox(SmallGraph graphParent, List<Node<?>> path, Consumer<Node<?>> actionConsumer) {
        LOGGER.warn("Making vertical box for nodes referenced by " + this + ": " + referencedNodes);
        final Box verticalBox = Box.createVerticalBox();
        for (Node node : referencedNodes) { // we don't go deeper...
            if (/*node.getReferenceCount() > 1*/ node.getReferenceCount() - node.getRecursiveIncomingArcsCount() > 1) {
                LOGGER.warn("Found >2ref node = " + node);
                graphParent.addAuxilliaryNode(node);
                continue;
            }

            path.add(this);
            if (!path.contains(node)) {
                verticalBox.add(node.ui(graphParent, path, actionConsumer));
            } else {
                LOGGER.warn("Node " + node + " is recursive and was already rendered");
            }
            path.remove(path.size() - 1);
        }
        LOGGER.warn("Done making vertical box for nodes referenced by " + this + ": " + referencedNodes);
        return verticalBox;
    }


    protected void assignOwnerAndComputeDepths(SmallGraph owner, Set<Node<?>> visitedNodes2, int depth) {
        if (depth > getDepth()) {
            this.depth = depth;
            LOGGER.debug(psiElement.getName() + " depth = " + depth);
            LOGGER.warn("Set depth of " + this + " to " + this.depth);
        }

        if (visitedNodes2.contains(this)) {
            LOGGER.debug("Node "+ this + " is already visited");
            return;
        }
        visitedNodes2.add(this);

        LOGGER.warn("Setting owner of " + this + " to " + owner);
        this.owner = owner;

        if (isTerminal()) {
            LOGGER.debug("Terminal node, returning");
            return;
        }

        for (Node<?> referenced : getReferencedNodes()) {
            referenced.assignOwnerAndComputeDepths(owner, visitedNodes2, depth + 1);
        }
    }


    protected SmallGraph findOwnerGraph(final List<Node<?>> visitedNodes) {
        if (visitedNodes.contains(this)) {
            LOGGER.debug("We already visited " + this);
            return owner;
        }

        visitedNodes.add(this);
        LOGGER.debug("visited " + this);
        if (isTerminal()) return owner;

        LOGGER.debug("I am not terminal node, scanning references");
        for (Node<?> referenced : getReferencedNodes()) {
            SmallGraph owner = referenced.findOwnerGraph(visitedNodes);
            if (owner != null) {
                LOGGER.debug("Found owner!!!");
                return owner;
            }
        }
        return null;
    }

    protected void scanForRecursions(final LinkedList<Node<?>> path) {
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

    private void addRecursivePath(final LinkedList<Node<?>> path) {
        final int start = path.indexOf(this);
        LOGGER.debug("RECURSIVE PATH PART:");
        for (int i = start; i < path.size(); i++) {
            LOGGER.debug(path.get(i) + " ");
        }
    }


    protected int getDepth() {
        return depth;
    }

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

    public void select(int kind) {
        button.select(kind);
    }

    public void deselect() {
        LOGGER.warn("DESELECT NODE " + this);
        button.deselect();
    }
}

