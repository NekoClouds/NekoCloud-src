package me.nekocloud.core.api.event;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventBus {

    Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority;
    Map<Class<?>, EventHandlerMethod[]> byEventBaked;
    Lock lock;
    Logger logger;

    public EventBus() {
        this(null);
    }

    public EventBus(Logger logger) {
        this.byListenerAndPriority = new HashMap<>();
        this.byEventBaked = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
        this.logger = ((logger == null) ? Logger.getLogger("global") : logger);
    }

    public void post(Object event) {
        EventHandlerMethod[] handlers = this.byEventBaked.get(event.getClass());
        if (handlers != null) {
            for (EventHandlerMethod method : handlers) {
                try {
                    method.invoke(event);
                }
                catch (IllegalAccessException ex) {
                    throw new Error("Method became inaccessible: " + event, ex);
                }
                catch (IllegalArgumentException ex2) {
                    throw new Error("Method rejected target/argument: " + event, ex2);
                }
                catch (InvocationTargetException ex3) {
                    this.logger.log(Level.WARNING, MessageFormat.format("Error dispatching event {0} to listener {1}", event, method.getListener()), ex3.getCause());
                }
            }
        }
    }

    private Map<Class<?>, Map<Byte, Set<Method>>> findHandlers(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = new HashMap<Class<?>, Map<Byte, Set<Method>>>();
        for (Method m : listener.getClass().getDeclaredMethods()) {
            EventHandler annotation = m.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) {
                    this.logger.log(Level.INFO, "Method {0} in class {1} annotated with {2} does not have single argument", new Object[] { m, listener.getClass(), annotation });
                }
                else {
                    Map<Byte, Set<Method>> prioritiesMap = handler.computeIfAbsent(params[0], k -> new HashMap<>());
                    Set<Method> priority = prioritiesMap.computeIfAbsent(annotation.priority(), k -> new HashSet<>());
                    priority.add(m);
                }
            }
        }
        return handler;
    }

    public void register(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = this.findHandlers(listener);
        this.lock.lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> e : handler.entrySet()) {
                Map<Byte, Map<Object, Method[]>> prioritiesMap = this.byListenerAndPriority.computeIfAbsent(e.getKey(), k -> new HashMap<>());
                for (Map.Entry<Byte, Set<Method>> entry : e.getValue().entrySet()) {
                    Map<Object, Method[]> currentPriorityMap = prioritiesMap.computeIfAbsent(entry.getKey(), k -> new HashMap<>());
                    Method[] baked = new Method[entry.getValue().size()];
                    currentPriorityMap.put(listener, entry.getValue().toArray(baked));
                }
                this.bakeHandlers(e.getKey());
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void unregister(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = this.findHandlers(listener);
        this.lock.lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> e : handler.entrySet()) {
                Map<Byte, Map<Object, Method[]>> prioritiesMap = this.byListenerAndPriority.get(e.getKey());
                if (prioritiesMap != null) {
                    for (Byte priority : e.getValue().keySet()) {
                        Map<Object, Method[]> currentPriority = prioritiesMap.get(priority);
                        if (currentPriority != null) {
                            currentPriority.remove(listener);
                            if (!currentPriority.isEmpty()) {
                                continue;
                            }
                            prioritiesMap.remove(priority);
                        }
                    }
                    if (prioritiesMap.isEmpty()) {
                        this.byListenerAndPriority.remove(e.getKey());
                    }
                }
                this.bakeHandlers(e.getKey());
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private void bakeHandlers(Class<?> eventClass) {
        Map<Byte, Map<Object, Method[]>> handlersByPriority = this.byListenerAndPriority.get(eventClass);
        if (handlersByPriority != null) {
            List<EventHandlerMethod> handlersList = new ArrayList<>(handlersByPriority.size() * 2);
            byte value = -128;
            byte b;
            do {
                Map<Object, Method[]> handlersByListener = handlersByPriority.get(value);
                if (handlersByListener != null) {
                    for (Map.Entry<Object, Method[]> listenerHandlers : handlersByListener.entrySet()) {
                        for (Method method : listenerHandlers.getValue()) {
                            EventHandlerMethod ehm = new EventHandlerMethod(listenerHandlers.getKey(), method);
                            handlersList.add(ehm);
                        }
                    }
                }
                b = value;
                ++value;
            } while (b < 127);
            this.byEventBaked.put(eventClass, handlersList.toArray(new EventHandlerMethod[handlersList.size()]));
        }
        else {
            this.byEventBaked.remove(eventClass);
        }
    }
}

