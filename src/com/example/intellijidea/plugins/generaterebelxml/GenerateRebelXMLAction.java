package com.example.intellijidea.plugins.generaterebelxml;

import com.example.intellijidea.plugins.generaterebelxml.options.Settings;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Action in the modules context menu to create a rebel.xml file based on a velocity template.
 * <p>
 * The template variables are
 * <ul>
 * <li>$TOMCAT_PATH - can be set in the options panel</li>
 * <li>$PATHS - full path names of the target/classes; separated by |</li>
 * </ul>
 * <pre>
 * $TOMCAT_PATH
 * #foreach( $PATH in $PATHS.split("\|") )
 * $PATH
 * #end
 * </pre>
 * @author markiewb
 */
public class GenerateRebelXMLAction extends AnAction {

    public static final String TEMPLATE_NAME = "rebel.xml";
    public static final String TARGET_FILENAME = "rebel.xml";
    public static final String TARGET_CLASSES = "/target/classes";

    @Override
    public void actionPerformed(AnActionEvent e) {
        List<Module> modules = getModules(e.getDataContext(), e.getProject());
        final List<Module> mavenModules = getMavenModules(modules);

        if (modules.size() != mavenModules.size()) {
            Messages.showDialog("No Maven projects selected", "???", new String[]{"OK"}, -1, null);
            return;
        }

        final Project project = e.getProject();
        Settings settings = ServiceManager.getService(project, Settings.class);
        final PsiDirectory directory = getTomcatDirectory(project, settings);
        if (directory == null) {
            Messages.showDialog(String.format("Tomcat directory %s not found.", settings.getTomcatPath()), "???", new String[]{"OK"}, -1, null);
            return;
        }

        final PsiFile rebelFile = directory.findFile(TARGET_FILENAME);
        if (rebelFile != null) {
            //display overwrite warning
            final int i = Messages.showDialog(rebelFile.getVirtualFile().getPath() + " already exists", "Warning:", new String[]{"Overwrite", "Cancel"}, 0, null);
            final boolean overwrite = i == 0;
            if (!overwrite) {
                return;
            }
            ApplicationManager.getApplication().runWriteAction(() -> rebelFile.delete());
        }

        try {
            final FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance(project);

            Properties properties = new Properties(fileTemplateManager.getDefaultProperties());
            properties.setProperty("TOMCAT_PATH", settings.getTomcatPath());
            properties.setProperty("PATHS", StringUtil.join(getOutputPaths(mavenModules), "|"));
            final FileTemplate template = fileTemplateManager.getJ2eeTemplate(TEMPLATE_NAME);
            final PsiElement fromTemplate = FileTemplateUtil.createFromTemplate(template, TARGET_FILENAME, properties, directory);

            openFileInEditor(project, fromTemplate);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private PsiDirectory getTomcatDirectory(Project project, Settings settings) {
        try {
            String tomcatPathFromSettings = settings.getTomcatPath();
            final File tomcatPath = new File(tomcatPathFromSettings);
            tomcatPath.mkdirs();

            return PsiManager.getInstance(project).findDirectory(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(tomcatPath));
        } catch (Exception e) {
            return null;
        }
    }

    @NotNull
    private List<String> getOutputPaths(List<Module> mavenModules) {
        List<String> paths = new ArrayList<>();
        for (Module mavenModule : mavenModules) {
            final String path = mavenModule.getModuleFile().getParent().getPath() + TARGET_CLASSES;
            paths.add(path);
        }
        return paths;
    }

    @NotNull
    private List<Module> getMavenModules(Collection<Module> modules) {
        final List<Module> mavenModules = new ArrayList<>();
        for (Module module : modules) {

            if (module.getModuleFile() != null) {
                final VirtualFile parent = module.getModuleFile().getParent();
                final boolean isMavenModule = parent.findChild("pom.xml") != null;
                if (isMavenModule) {
                    mavenModules.add(module);
                }
            }
        }
        return mavenModules;
    }

    private void openFileInEditor(Project project, PsiElement fromTemplate) {
        final PsiFile createdFile = fromTemplate.getContainingFile();

        if (createdFile != null) {
            final VirtualFile virtualFile = createdFile.getVirtualFile();

            if (virtualFile != null) {
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        //https://github.com/JetBrains/intellij-community/search?utf8=%E2%9C%93&q=MODULE_CONTEXT_ARRAY&type=Code

        if (e.getProject() == null) {
            e.getPresentation().setEnabled(false);
            return;
        }

        List<Module> modules = getModules(e.getDataContext(), e.getProject());
        final boolean isAtLeastOneModule = !modules.isEmpty();

        e.getPresentation().setEnabled(isAtLeastOneModule);
//        e.getPresentation().setEnabledAndVisible(isAtLeastOneModule && e.getProject() != null);
    }

    private List<Module> getModules(DataContext dataContext, Project project) {
        List<Module> modules = new ArrayList<>();
        if (project != null && dataContext!=null) {

            VirtualFile[] files = DataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
            if (files!=null) {
                final List<VirtualFile> filesList = Arrays.asList(files);

                modules = filesList.stream().map(file -> ModuleUtil.findModuleForFile(file, project)).filter(Objects::nonNull).collect(Collectors.toList());

            }
        }
        return modules;
    }
}
