package com.example.intellijidea.plugins.generaterebelxml.options;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html
 * Project level by default. Will be saved in .idea/misc.xml
 */
@State(name = "GenerateRebelXMLSettings")
public class Settings implements PersistentStateComponent<Settings> {

    private String tomcatPath;

    public String getTomcatPath() {
        return tomcatPath;
    }

    public void setTomcatPath(String tomcatPath) {
        this.tomcatPath = tomcatPath;
    }

    @Nullable
    @Override
    public Settings getState() {
        return this;
    }

    public void loadState(Settings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
