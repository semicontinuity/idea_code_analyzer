package com.metalogic.graph.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiNamedElement;
import com.metalogic.graph.Node;

public abstract class ElementButton<E extends PsiNamedElement> extends JButton implements ActionListener {
    protected E psiElement;
    protected Node<?> node;
    protected Consumer<Node<?>> actionConsumer;
    protected Color regularBackground;

    protected ElementButton(final E element, Node<?> node, Consumer<Node<?>> actionConsumer) {
        psiElement = element;
        this.node = node;
        this.actionConsumer = actionConsumer;
        addActionListener(this);
        setBorder(BorderFactory.createEmptyBorder());
        regularBackground = getBackground();
    }

    protected int italic(boolean b) {
        return b ? Font.ITALIC : Font.PLAIN;
    }

    protected int bold(boolean aPublic) {
        return aPublic ? Font.BOLD : Font.PLAIN;
    }


    public void actionPerformed(ActionEvent e) {
        if (psiElement == null) return;
//            final int endOffset = method.getBody().getLBrace().getTextRange().getEndOffset();
//        if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
            actionConsumer.accept(node);
//        } else {
            TextRange textRange = psiElement.getTextRange();
            final int endOffset = textRange.getStartOffset();
            final Project project = psiElement.getProject();
            final VirtualFile virtualFile = psiElement.getContainingFile().getVirtualFile();
            FileEditorManager.getInstance(project).openTextEditor(
                    new OpenFileDescriptor(project, virtualFile, endOffset), true);
//        }
    }

    public void select(int kind) {
        setBackground(background(kind));
    }

    private Color background(int kind) {
        if (kind == 0) {
            return Color.GREEN;
        } else if (kind > 0) {
            return Color.BLUE;
        } else {
            return Color.ORANGE;
        }
    }

    public void deselect() {
        setBackground(regularBackground);
    }


    protected abstract ImageIcon icon();


    protected static ImageIcon icon(final String iconResource) {
        final URL resource = ElementButton.class.getResource(iconResource);
        if (resource == null) return null;
        return new ImageIcon(resource);
    }

    protected static BufferedImage image(final String iconResource) {
        final URL resource = ElementButton.class.getResource(iconResource);
        if (resource == null) return null;
        try {
            return ImageIO.read(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
