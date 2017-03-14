# Intellij IDEA "create from template" plugin

This plugin is the draft for an inhouse-plugin for our company to generate application specific rebel.xml files.

It adds an action in the Maven modules context menu (within the project view) to create a rebel.xml file based on a velocity template. 

It shows the usage some IDEA APIs:
* How to add an action to the context menu
* How to create a context sensitive action. The action is only activated for Maven-based module nodes. 
* How to add an project specific option panel including "for current project" indicator - see ```projectConfigurable``` in the ```plugin.xml```
* How to register and use a project based settings service. See ```projectService``` in the ```plugin.xml```
* How to register a file template. See ```fileTemplateGroup``` in ```plugin.xml``` and ```resources/fileTemplates.j2ee```

<img src="https://raw.githubusercontent.com/markiewb/idea-create-from-template-plugin/master/doc/example.png"/>
<img src="https://raw.githubusercontent.com/markiewb/idea-create-from-template-plugin/master/doc/AtMavenModule.png"/>
<img src="https://raw.githubusercontent.com/markiewb/idea-create-from-template-plugin/master/doc/AtChangeList.png"/>
<img src="https://raw.githubusercontent.com/markiewb/idea-create-from-template-plugin/master/doc/AtChangeListFile.png"/>

## Updates 
      1.3: Add action to context menu of files/changelists in "Local Changes" window
      1.2: Add action to context menu of Maven modules in "Maven Projects" window (+ Refactor to use VIRTUAL_FILE_ARRAY to get com.intellij.openapi.module.Module)
      1.1: Make settings per project type and enable the "for current project" indicator in the settings

## License
Apache 2.0
