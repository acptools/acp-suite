package net.acptools.suite.ide.utils.event;

import java.util.*;

public class ObserverManager {
    private Set<Observer> m_weakReferencedObservers;

    public ObserverManager() {
        m_weakReferencedObservers = Collections.newSetFromMap(new HashMap<Observer, Boolean>()); // allows a set of weak references
    }

    public void add(Observer observer) {
        if (observer != null) {
            m_weakReferencedObservers.add(observer);
        }
    }

    public void remove(Observer observer) {
        m_weakReferencedObservers.remove(observer);
    }

    public void callEvent(EventType eventType, Object o) {
        m_weakReferencedObservers.stream()
                .filter(Objects::nonNull)
                .forEach(observer -> observer.onEvent(eventType, o));

        m_weakReferencedObservers.remove(null);
    }
}
