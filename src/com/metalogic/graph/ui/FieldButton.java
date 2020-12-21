package com.metalogic.graph.ui;

import java.util.function.Consumer;

import com.intellij.psi.PsiField;
import com.metalogic.graph.FieldNode;
import com.metalogic.graph.Node;

import javax.swing.*;

public class FieldButton extends ElementButton<PsiField> {
    static ImageIcon classIcon = ElementButton.icon("/nodes/field.png");

    public FieldButton(final PsiField psiField, FieldNode node, Consumer<Node<?>> actionConsumer) {
        super(psiField, node, actionConsumer);

        setText(psiField.getName());
        setIcon(classIcon);
    }

    protected ImageIcon icon() {
        return classIcon;
    }
}
