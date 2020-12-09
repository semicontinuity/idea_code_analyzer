package com.metalogic.graph.ui;

import com.intellij.psi.PsiField;

import javax.swing.*;

public class FieldButton extends ElementButton<PsiField> {
    static ImageIcon classIcon = ElementButton.icon("/nodes/field.png");

    public FieldButton(final PsiField psiField) {
        super(psiField);

        setText(psiField.getName());
        setIcon(classIcon);
    }

    protected ImageIcon icon() {
        return classIcon;
    }
}