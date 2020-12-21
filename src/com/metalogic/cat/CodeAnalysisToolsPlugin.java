/*
 * Copyright 2000-2005 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.metalogic.cat;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.util.ui.UIUtil;
import com.metalogic.graph.ClassStructureGraph;
import org.apache.log4j.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings({"HardCodedStringLiteral"})
public class CodeAnalysisToolsPlugin implements ProjectComponent {
//    static {
//        BasicConfigurator.configure();
//    }

    @SuppressWarnings({"HardCodedStringLiteral"})
    public static final String TOOL_WINDOW_ID = "Code Analysis";
    public static final Logger LOGGER = Logger.getLogger(CodeAnalysisToolsPlugin.class);


    private Project myProject;
    private JPanel myContentPanel;
    private PsiClass lastPsiClass;

    private boolean includeConstructors;


    @SuppressWarnings({"HardCodedStringLiteral", "StringConcatenation", "MagicCharacter", "ObjectToString"})
    public CodeAnalysisToolsPlugin(final Project project) {
        LOGGER.debug("NEW PLUGIN" + ' ' + this + ' ' + project);
        myProject = project;
    }

    public void projectOpened() {
        initToolWindow();
    }

    public void projectClosed() {
        unregisterToolWindow();
    }

    public final void initComponent() {
    }

    public final void disposeComponent() {
    }

    public String getComponentName() {
        return "Cat.Plugin";
    }



    private void initToolWindow() {
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
        toolWindowManager.registerToolWindow(
                TOOL_WINDOW_ID, toolWindowContent(), ToolWindowAnchor.RIGHT);

//        FileEditorManager fileEditorManager = FileEditorManager.getInstance (myProject);
//        fileEditorManager.addFileEditorManagerListener (this);
    }


    private JPanel toolWindowContent() {
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(myContentPanel()), BorderLayout.CENTER);
        panel.add(controlPanel(panel), BorderLayout.NORTH);

        return panel;
    }

    private JPanel controlPanel(final JPanel pluginPanel) {
        JCheckBox constructors = new JCheckBox("constructors");

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(repaintButton(pluginPanel, constructors), BorderLayout.CENTER);
        panel.add(constructors, BorderLayout.WEST);
        return panel;
    }


    private JButton repaintButton(final JPanel panel, JCheckBox constructors) {
        final JButton button = new JButton("Repaint!");
        //noinspection HardcodedFileSeparator
        button.setIcon(new ImageIcon(ClassStructureGraph.class.getResource("/actions/refresh.png")));
        button.addActionListener(
                new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        includeConstructors = constructors.isSelected();
                        updateUI();
                        panel.invalidate();   // TODO: make it work...
                        panel.validate();   // TODO: make it work...
                    }
                });
        return button;
    }


    private JPanel myContentPanel() {
        final JPanel myContentPanel = new JPanel();
        myContentPanel.setLayout(new BorderLayout());
        myContentPanel.setBackground(UIUtil.getTreeTextBackground());
        this.myContentPanel = myContentPanel;

        return myContentPanel;
    }


    public void updateUI() {
        final FileEditorManager fileEditorManager = FileEditorManager.getInstance(myProject);
        final Editor selectedTextEditor = fileEditorManager.getSelectedTextEditor();
        if (selectedTextEditor == null) return; // TODO: we can track notifications from EditorManager

        updateUI(selectedTextEditor.getDocument());
    }


    private void updateUI(final Document document) {
        final PsiDocumentManager psiDocMgr = PsiDocumentManager.getInstance(myProject);
        final PsiFile psiFile = psiDocMgr.getCachedPsiFile(document);
        if (psiFile == null) return;

        if (!(psiFile instanceof PsiJavaFile)) return;
        updateUI((PsiJavaFile) psiFile);
    }


    private void updateUI(final PsiJavaFile psiJavaFile) {
        final PsiClass[] classes = psiJavaFile.getClasses();
        if (classes.length == 0) return;
        updateUI(classes[0]);
    }


    private void updateUI(final PsiClass psiClass) {
        if (psiClass.equals(lastPsiClass)) return;  // TODO: we can track notifications from EditorManager
        lastPsiClass = psiClass;

        final ClassStructureGraph graph = new ClassStructureGraph(psiClass, includeConstructors);

        myContentPanel.removeAll();
        myContentPanel.add(graph.ui(), BorderLayout.CENTER);
    }


    private void unregisterToolWindow() {
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
        toolWindowManager.unregisterToolWindow(TOOL_WINDOW_ID);
    }
}
