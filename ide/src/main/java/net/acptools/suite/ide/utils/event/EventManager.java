package net.acptools.suite.ide.utils.event;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventManager {

    private final Map<EventType, ObserverManager> m_observerManagers = new ConcurrentHashMap<>();

    public void registerObserver(EventType eventType, Observer observer) {
        synchronized (m_observerManagers) {
            if (!m_observerManagers.containsKey(eventType)) {
                m_observerManagers.put(eventType, new ObserverManager());
            }

            ObserverManager observerManager = m_observerManagers.get(eventType);
            observerManager.add(observer);
        }
    }

    public void unregisterObserver(EventType eventType, Observer observer) {
        synchronized (m_observerManagers) {
            if (!m_observerManagers.containsKey(eventType)) {
                return;
            }

            ObserverManager observerManager = m_observerManagers.get(eventType);
            observerManager.remove(observer);
        }
    }

    private ExecutorService executor = null;

    public ExecutorService executor() {
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        }
        return executor;
    }

    public void callEvent(EventType eventType) {
        executor().submit(() -> callEvent(eventType, null));
    }

    public void callEvent(EventType eventType, Object o) {
        if (!m_observerManagers.containsKey(eventType)) {
            return;
        }

        executor().submit(() -> {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    ObserverManager observerManager = m_observerManagers.get(eventType);
                    observerManager.callEvent(eventType, o);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
}