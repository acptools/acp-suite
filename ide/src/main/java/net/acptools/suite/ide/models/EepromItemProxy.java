package net.acptools.suite.ide.models;

import net.acptools.suite.generator.models.project.EepromItem;

public class EepromItemProxy {
    private EepromItem parentEepromItem;

    public EepromItemProxy(EepromItem parentEepromItem) {
        this.parentEepromItem = parentEepromItem;
    }

    public String getName() {
        return parentEepromItem.getName();
    }

    public void setName(String name) {
        parentEepromItem.setName(name);
    }

    public String getType() {
        return parentEepromItem.getType();
    }

    public void setType(String type) {
        parentEepromItem.setType(type);
    }

    public String getValue() {
        return parentEepromItem.getValue();
    }

    public void setValue(String value) {
        parentEepromItem.setValue(value);
    }

    public boolean isCached() {
        return parentEepromItem.isCached();
    }

    public void setCached(boolean cached) {
        parentEepromItem.setCached(cached);
    }

    public String getDescription() {
        return parentEepromItem.getDescription();
    }

    public void setDescription(String description) {
        parentEepromItem.setDescription(description);
    }

    public boolean isArray() {
        return parentEepromItem.isArray();
    }

    public boolean isVariable() {
        return parentEepromItem.isVariable();
    }

    public void setLengthOfArray(int length) {
        parentEepromItem.setLengthOfArray(length);
    }

    public int getLengthOfArray() {
        return parentEepromItem.getLengthOfArray();
    }

}
