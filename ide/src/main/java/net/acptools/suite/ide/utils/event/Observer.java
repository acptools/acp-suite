package net.acptools.suite.ide.utils.event;

public interface Observer {
    void onEvent(EventType eventType, Object o);
}