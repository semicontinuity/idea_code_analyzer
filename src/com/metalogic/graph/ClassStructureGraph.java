package com.metalogic.graph;

import com.intellij.psi.*;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
//import com.intellij.util.containers.HashMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.util.*;

public class ClassStructureGraph extends NodeList {

    public static final Logger LOGGER = Logger.getLogger(ClassStructureGraph.class);
    static {
        LOGGER.setLevel(Level.DEBUG);
    }

    private List<Node> rootNodes = new ArrayList<Node>();
    protected List<SmallGraph> smallGraphs = new ArrayList<SmallGraph>();
    private Map<PsiNamedElement, Node> element2node = new HashMap<PsiNamedElement, Node>();

    private List<Node> visitedNodes;
    private List<Node> visitedNodes2;

    private Box panel;


    public void init(PsiClass psiClass) {
        final LocalSearchScope localSearchScope = new LocalSearchScope(psiClass);

        for (final PsiMethod method : psiClass.getMethods()) addMethod(method);
        for (final PsiField field : psiClass.getFields()) addField(field);

        for (final PsiMethod method : psiClass.getMethods()) processReferencesTo(psiClass, method, localSearchScope);
        for (final PsiField field : psiClass.getFields()) processReferencesTo(psiClass, field, localSearchScope);
    }


    void addMethod(final PsiMethod method) {
        final MethodNode methodNode = new MethodNode(method);
        methodNode.setHasOverridingMethods(OverridingMethodsSearch.search(method).findAll().size() > 0);
        methodNode.setHasSuperMethods(method.findSuperMethods().length > 0);

        referencedNodes.add(methodNode);
        element2node.put(method, methodNode);
    }


    void addField(final PsiField field) {
        final FieldNode methodNode = new FieldNode(field);
        referencedNodes.add(methodNode);
        element2node.put(field, methodNode);
    }


    private void processReferencesTo(final PsiClass psiClass, final PsiNamedElement field, final LocalSearchScope localSearchScope) {
        final Collection<PsiReference> references = ReferencesSearch.search(field, localSearchScope, false).findAll();
        for (final PsiReference reference : references) {
            if (!(reference instanceof PsiReferenceExpression)) continue;

            final PsiMethod callingMethod = findEnclosingMethod((PsiReferenceExpression) reference, psiClass);
            if (callingMethod == null) continue;

            addLink(callingMethod, field);
        }
    }


    private static PsiMethod findEnclosingMethod(final PsiElement expression, final PsiClass psiClass) {
        PsiElement parent = expression;
        PsiMethod method = null;
        do {
            parent = parent.getParent();
            if (parent instanceof PsiMethod)
                method = (PsiMethod) parent;    // outermost method?
            else if (parent instanceof PsiFile) break;
            else if ((parent instanceof PsiClass) && (!parent.equals(psiClass))) return null;
        }
        while (true);
        return method;
    }


    private void addLink(final PsiNamedElement from, final PsiNamedElement namedElement) {
        final Node sourceNode = element2node.get(from);
        final Node targetNode = element2node.get(namedElement);
//        System.out.println("sourceNode = " + sourceNode);
//        System.out.println("targetNode = " + targetNode);
        sourceNode.addReferencedNode(targetNode);
        targetNode.incReferenceCount();
    }


    public JComponent ui() {
        if (panel != null) return panel;
        layout();

        LOGGER.debug("===============================================");
        LOGGER.debug("CREATING NEW ClassStructuregraph UI");
        panel = Box.createVerticalBox();
        LOGGER.debug("Adding " + smallGraphs.size() + " sub-graphs");


        for (SmallGraph smallGraph : smallGraphs) {
            final LinkedList<Node> path = new LinkedList<Node>();
            panel.add(smallGraph.ui(path));
        }
//        panel.add(Box.createVerticalGlue());
        LOGGER.debug("===============================================");
        return panel;
    }


    private void layout() {
        LOGGER.debug("===============================================");
        LOGGER.debug("LAYING OUT GRAPH");
        scanForRootNodes();
        scanForRecursions();
        scanForGraphs();
        LOGGER.debug("===============================================");
    }

    private void scanForRecursions() {
        if (visitedNodes != null) visitedNodes.clear(); // TODO: rethink
        visitedNodes = new ArrayList<Node>();
        for (Node node : rootNodes) {
            node.scanForRecursions(new LinkedList<Node>());
        }
    }


    private void scanForRootNodes() {
        LOGGER.debug("==== COLLECTING ROOT NODES");
        for (Node node : referencedNodes) {
            if (node.isRoot()) {
                LOGGER.debug("ADDED ROOT NODE " + node);
                rootNodes.add(node);
            }
        }
    }

    private void scanForGraphs() {
        LOGGER.debug("==== SCANNING GRAPHS");
        if (visitedNodes != null) visitedNodes.clear(); // TODO: rethink
        visitedNodes = new ArrayList<Node>();

        // assigned owners
        if (visitedNodes2 != null) visitedNodes2.clear(); // TODO: rethink
        visitedNodes2 = new ArrayList<Node>();

        for (Node rootNode : rootNodes) {
            final SmallGraph owner = findOwnerGraph(rootNode);
            LOGGER.debug("Setting owner " + owner + " to root node " + rootNode);
            owner.addRootNode(rootNode);
            rootNode.assignOwner(visitedNodes2, owner, 0);
        }
    }


    /**
     * ... every node belongs to some SmallGraph
     *
     * @param rootNode
     * @return
     */
    private SmallGraph findOwnerGraph(Node rootNode) {
        LOGGER.debug("LOOKING FOR OWNER GRAPH OF ROOT NODE " + rootNode);
        final SmallGraph graph = rootNode.findOwnerGraph(visitedNodes);
        if (graph != null) return graph;

        // no owner so far
        final SmallGraph newSmallGraph = new SmallGraph();
        smallGraphs.add(newSmallGraph);
        return newSmallGraph;
    }
}
