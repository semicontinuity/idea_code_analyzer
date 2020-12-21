package com.metalogic.graph;

import com.intellij.psi.PsiField;
import com.metalogic.graph.ui.ElementButton;
import com.metalogic.graph.ui.FieldButton;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class FieldNode extends Node<PsiField> {
    public FieldNode(final PsiField field) {
        super(field);
    }

    protected JComponent ui(final SmallGraph graphParent, final List<Node<?>> path) {
        final ElementButton myButton = new FieldButton(psiElement);
//        System.out.println("UI for psiElement = " + myButton);
        return box(graphParent, path, myButton);
    }
}
