package com.metalogic.graph.ui;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.util.ui.UIUtil;
import com.metalogic.graph.MethodNode;
import com.metalogic.graph.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class MethodButton extends ElementButton<PsiMethod> {

    static ImageIcon classIcon = ElementButton.icon("/nodes/method.png");

    public MethodButton(
            final PsiMethod psiMethod,
            boolean hasOverridingMethods,
            boolean hasSuperMethods,
            MethodNode methodNode,
            Consumer<Node<?>> actionConsumer) {

        super(psiMethod, methodNode, actionConsumer);

        setText(psiMethod.getName());

        final PsiModifierList modifierList = psiMethod.getModifierList();
        boolean isPublic = modifierList.hasModifierProperty("public");
        boolean isPrivate = modifierList.hasModifierProperty("private");
        boolean isProtected = modifierList.hasModifierProperty("protected");
        boolean isStatic = modifierList.hasModifierProperty("static");
        boolean isAbstract = modifierList.hasModifierProperty("abstract");


        final Font font = getFont();
        final Font afont = font.deriveFont(bold(isPublic || isProtected) | italic(isStatic));
        setFont(afont);

        setForeground(isPublic || (!isPrivate && !isProtected) ? Color.BLACK : Color.GRAY);
        final BufferedImage image1 = image(arrows(hasOverridingMethods, hasSuperMethods));

        final BufferedImage image2 = image(isAbstract ? "/icons/abstractMethod.png" : "/icons/method.png");

        setIcon(new ImageIcon(overlay(image1, image2)));
    }

    private String arrows(boolean hasOverridingMethods, boolean hasSuperMethods) {
        if (hasOverridingMethods)
            return hasSuperMethods ? "/icons/updown.png" : "/icons/down.png";
        else
            return hasSuperMethods ? "/icons/up.png" : "/icons/blank.png";
    }

    private Image overlay(BufferedImage img1, BufferedImage img2) {
        img1.getGraphics().drawImage(img2, 0, 0, img2.getWidth(null), img2.getHeight(null), 0, 0, img2.getWidth(null), img2.getHeight(null), null);
        return img1;
    }

    private Image concat(BufferedImage image1, BufferedImage image2) {
        final int w = image1.getWidth() + image2.getWidth();
        final int h = Math.max(image1.getHeight(), image2.getHeight());
//        final Image image = new BufferedImage(w, h,  TYPE_INT_RGB);
        final Image image = UIUtil.createImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.drawImage(image1, 0, 0, null);
        g2.drawImage(image2, image1.getWidth(), 0, null);
        g2.dispose();
        return image;
    }

    private String decoration(boolean hasOverridingMethods, boolean hasSuperMethods) {
        if (!hasOverridingMethods && !hasSuperMethods)
            return "\u25A1";
        else if (hasOverridingMethods && hasSuperMethods)
            return "\u25CA";
        else if (hasSuperMethods)
            return "\u25B5";
        else
            return "\u25BD";
    }

    protected ImageIcon icon() {
        return classIcon;
    }
}
