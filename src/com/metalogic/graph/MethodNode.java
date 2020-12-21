package com.metalogic.graph;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;

import com.intellij.psi.PsiMethod;
import com.metalogic.graph.ui.MethodButton;

public class MethodNode extends Node<PsiMethod> {
    public MethodNode(final PsiMethod method) {
        super(method);
//        System.out.println("method = " + method);
    }

    public void setHasOverridingMethods(boolean hasOverridingMethods) {
        this.hasOverridingMethods = hasOverridingMethods;
    }

    public void setHasSuperMethods(boolean hasSuperMethods) {
        this.hasSuperMethods = hasSuperMethods;
    }

    protected JComponent ui(final SmallGraph graphParent, List<Node<?>> path, Consumer<Node<?>> actionConsumer) {
        button = new MethodButton(psiElement, hasOverridingMethods, hasSuperMethods, this, actionConsumer);
        return makeHorizontalBox(button, graphParent, path, actionConsumer);
    }
}
