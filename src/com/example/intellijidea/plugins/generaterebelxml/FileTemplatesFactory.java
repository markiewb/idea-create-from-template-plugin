package com.example.intellijidea.plugins.generaterebelxml;

import com.intellij.icons.AllIcons;
import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.openapi.fileTypes.StdFileTypes;

public class FileTemplatesFactory implements FileTemplateGroupDescriptorFactory {

  public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
    FileTemplateGroupDescriptor descriptor = new FileTemplateGroupDescriptor("My extensions",AllIcons.Nodes.Plugin);
    final FileTemplateDescriptor fileDesc = new FileTemplateDescriptor("rebel.xml", StdFileTypes.XML.getIcon());
    descriptor.addTemplate(fileDesc);
    return descriptor;
  }

}