package com.metalogic.graph;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;

import com.intellij.psi.PsiField;
import com.metalogic.graph.ui.FieldButton;

public class FieldNode extends Node<PsiField> {
    public FieldNode(final PsiField field) {
        super(field);
    }

    protected JComponent ui(final SmallGraph graphParent, final List<Node<?>> path, Consumer<Node<?>> actionConsumer) {
        button = new FieldButton(psiElement, this, actionConsumer);
        return makeHorizontalBox(button, graphParent, path, actionConsumer);
    }
}
