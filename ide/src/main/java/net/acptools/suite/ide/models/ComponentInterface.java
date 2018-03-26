package net.acptools.suite.ide.models;

import net.acptools.suite.generator.models.modules.Module;

import java.util.Map;

public interface ComponentInterface {

    public Map<String, String> getProperties();

    public Map<String, String> getEvents();

    public String getName();

    public void setName(String name);

    public String getType();

    public void setType(String type);

    public String getDescription();

    public void setDescription(String description);

    Module getModuleInstance();
}
