package com.metalogic.graph;

import com.intellij.psi.PsiMethod;
import com.metalogic.graph.ui.MethodButton;

import javax.swing.*;

import java.util.List;

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

    protected JComponent ui(final SmallGraph graphParent, List<Node<?>> path) {
        return makeHorizontalBox(new MethodButton(psiElement, hasOverridingMethods, hasSuperMethods), graphParent, path);
    }
}
