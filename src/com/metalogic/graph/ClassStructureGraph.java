package com.metalogic.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JComponent;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.search.searches.ReferencesSearch;


public class ClassStructureGraph extends NodeList {

    public static final Logger LOGGER = Logger.getInstance(ClassStructureGraph.class);

    private final PsiClass psiClass;

    private final List<Node<?>> rootNodes = new ArrayList<>();
    private final Map<PsiNamedElement, Node<?>> element2node = new HashMap<>();

    private final List<SmallGraph> smallGraphs = new ArrayList<>();

    private List<Node<?>> visitedNodes;
    private List<Node<?>> visitedNodes2;

    private Box panel;
    private final boolean includeConstructors;


    public ClassStructureGraph(PsiClass psiClass, boolean includeConstructors) {
        this.psiClass = psiClass;
        this.includeConstructors = includeConstructors;

        final LocalSearchScope localSearchScope = new LocalSearchScope(psiClass);

        for (final PsiMethod method : psiClass.getMethods()) {
            if (includeMethod(method)) {
                LOGGER.warn("addMethod " + method);
                addMethod(method);
            }
        }
        for (final PsiField field : psiClass.getFields()) addField(field);

        for (final PsiMethod method : psiClass.getMethods()) {
            if (includeMethod(method)) {
                LOGGER.warn("processReferencesTo " + method);
                processReferencesTo(method, localSearchScope);
            }
        }
        for (final PsiField field : psiClass.getFields()) processReferencesTo(field, localSearchScope);
    }


    void addMethod(final PsiMethod method) {
        final MethodNode node = new MethodNode(method);
        node.setHasOverridingMethods(OverridingMethodsSearch.search(method).findAll().size() > 0);
        node.setHasSuperMethods(method.findSuperMethods().length > 0);

        referencedNodes.add(node);
        element2node.put(method, node);
    }


    void addField(final PsiField field) {
        final FieldNode node = new FieldNode(field);
        referencedNodes.add(node);
        element2node.put(field, node);
    }


    private void processReferencesTo(final PsiNamedElement element, final LocalSearchScope localSearchScope) {
        final Collection<PsiReference> references = ReferencesSearch.search(element, localSearchScope, false).findAll();
        for (final PsiReference reference : references) {
            if (!(reference instanceof PsiReferenceExpression)) continue;

            final PsiMethod callingMethod = findEnclosingMethod((PsiReferenceExpression) reference, psiClass);
            if (callingMethod == null) continue;

            if (includeMethod(callingMethod)) {
                LOGGER.warn(">> processReferencesTo " + callingMethod);
                processReferencesTo(callingMethod, localSearchScope);
                LOGGER.warn(">> processReferencesTo " + callingMethod);
                addLink(callingMethod, element);
            }
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
        final Node<?> sourceNode = element2node.get(from);
        final Node<?> targetNode = element2node.get(namedElement);
//        System.out.println("sourceNode = " + sourceNode);
//        System.out.println("targetNode = " + targetNode);
        LOGGER.warn(sourceNode.toString() + ":" + sourceNode.getClass() + "->" + targetNode.toString() + ":" + targetNode.getClass());

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
            final ArrayList<Node<?>> path = new ArrayList<>();
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
        visitedNodes = new ArrayList<>();
        for (Node<?> node : rootNodes) {
            node.scanForRecursions(new LinkedList<>());
        }
    }


    private void scanForRootNodes() {
        LOGGER.debug("==== COLLECTING ROOT NODES");
        for (Node<?> node : referencedNodes) {
            if (node.isRoot()) {
                LOGGER.debug("ADDED ROOT NODE " + node);
                rootNodes.add(node);
            }
        }
    }

    private void scanForGraphs() {
        LOGGER.debug("==== SCANNING GRAPHS");
        if (visitedNodes != null) visitedNodes.clear(); // TODO: rethink
        visitedNodes = new ArrayList<>();

        // assigned owners
        if (visitedNodes2 != null) visitedNodes2.clear(); // TODO: rethink
        visitedNodes2 = new ArrayList<>();

        for (Node<?> rootNode : rootNodes) {
            final SmallGraph owner = findOwnerGraph(rootNode);
            LOGGER.debug("Setting owner " + owner + " to root node " + rootNode);
            owner.addRootNode(rootNode);
            rootNode.assignOwner(owner, visitedNodes2, 0);
        }
    }


    /**
     * ... every node belongs to some SmallGraph
     */
    private SmallGraph findOwnerGraph(Node<?> rootNode) {
        LOGGER.info("LOOKING FOR OWNER GRAPH OF ROOT NODE " + rootNode);
        final SmallGraph graph = rootNode.findOwnerGraph(visitedNodes);
        if (graph != null) return graph;

        // no owner so far
        final SmallGraph newSmallGraph = new SmallGraph();
        smallGraphs.add(newSmallGraph);
        return newSmallGraph;
    }


    private boolean includeMethod(PsiMethod method) {
        return !method.isConstructor() || includeConstructors;
//        return true;
    }

}
