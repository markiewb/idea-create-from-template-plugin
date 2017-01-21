package com.example.intellijidea.plugins.generaterebelxml.options;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;

import javax.swing.*;

public class Configuration implements Configurable {

    private Settings settings;
    private OptionsPanel optionsPanel;

    public Configuration(Project project) {
        this.settings = ServiceManager.getService(project, Settings.class);
    }

    @Override
    public JComponent createComponent() {
        if (optionsPanel == null) {
            optionsPanel = new OptionsPanel();
        }
        this.reset();
        return optionsPanel.getRootPanel();
    }

    @Override
    public boolean isModified() {
        return !Comparing.equal(optionsPanel.getTomcatPathTextField().getText(), this.settings.getTomcatPath());
    }

    @Override
    public void apply() throws ConfigurationException {
        this.settings.setTomcatPath(optionsPanel.getTomcatPathTextField().getText());
    }

    @Override
    public void reset() {
        optionsPanel.getTomcatPathTextField().setText(this.settings.getTomcatPath());
    }

    @Override
    public void disposeUIResources() {
        optionsPanel = null;
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Generate rebel.xml";
    }
}
