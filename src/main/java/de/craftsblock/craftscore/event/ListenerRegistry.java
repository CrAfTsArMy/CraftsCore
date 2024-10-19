package de.craftsblock.craftscore.event;

import de.craftsblock.craftscore.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ListenerRegistry {

    private final ConcurrentHashMap<Class<? extends Event>, EnumMap<EventPriority, List<Listener>>> data = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Short, ConcurrentLinkedQueue<Event>> channelQueues = new ConcurrentHashMap<>();

    public void register(ListenerAdapter adapter) {
        for (Method method : Utils.getMethodsByAnnotation(adapter.getClass(), EventHandler.class))
            try {
                if (method.getParameterCount() <= 0)
                    throw new IllegalStateException("The methode " + method.getName() + " is provided with " + EventHandler.class.getName() + " but does not include " + Event.class.getName() + " as argument!");

                Class<?> parameter = method.getParameters()[0].getType();
                if (!Event.class.isAssignableFrom(parameter))
                    throw new IllegalStateException("The methode " + method.getName() + " is provided with " + EventHandler.class.getName() + " but does not include " + Event.class.getName() + " as argument!");

                Class<? extends Event> event = parameter.asSubclass(Event.class);
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                data.computeIfAbsent(event, p -> new EnumMap<>(EventPriority.class))
                        .computeIfAbsent(eventHandler.priority(), e -> new ArrayList<>())
                        .add(new Listener(method, adapter, eventHandler.priority()));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void unregister(ListenerAdapter adapter) {
        for (Method method : Utils.getMethodsByAnnotation(adapter.getClass(), EventHandler.class))
            try {
                if (method.getParameterCount() <= 0)
                    throw new IllegalStateException("The methode " + method.getName() + " is provided with " + EventHandler.class.getName() + " but does not include " + Event.class.getName() + " as argument!");

                Class<?> parameter = method.getParameters()[0].getType();
                if (!Event.class.isAssignableFrom(parameter))
                    throw new IllegalStateException("The methode " + method.getName() + " is provided with " + EventHandler.class.getName() + " but does not include " + Event.class.getName() + " as argument!");

                Class<? extends Event> event = parameter.asSubclass(Event.class);
                EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                if (!data.containsKey(event)) continue;

                EnumMap<EventPriority, List<Listener>> listeners = data.get(event);
                if (!listeners.containsKey(eventHandler.priority())) continue;
                listeners.get(eventHandler.priority()).removeIf(listener -> listener.method().equals(method));

                // Remove the event priority if there are no left listeners
                if (listeners.get(eventHandler.priority()).isEmpty())
                    listeners.remove(eventHandler.priority());
                else
                    // Continue as there are more listeners remaining
                    continue;

                // Remove the event type if no listeners are left
                if (listeners.isEmpty()) data.remove(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void call(Event event) throws InvocationTargetException, IllegalAccessException {
        if (!this.data.containsKey(event.getClass()))
            return;

        EnumMap<EventPriority, List<Listener>> data = this.data.get(event.getClass());
        if (data.isEmpty()) return;


        for (EventPriority priority : EventPriority.values()) {
            if (!data.containsKey(priority)) continue;

            List<Listener> listeners = data.get(priority);
            if (listeners.isEmpty()) continue;

            for (Listener tile : listeners)
                tile.method.invoke(tile.self, event);
        }
    }

    public void queueCall(Event event) {
        queueCall((short) 0, event);
    }

    public void queueCall(Short channel, Event event) {
        channelQueues.computeIfAbsent(channel, i -> new ConcurrentLinkedQueue<>()).add(event);
    }

    public void callQueued() throws InvocationTargetException, IllegalAccessException {
        callQueued((short) 0);
    }

    public void callAllQueued() throws InvocationTargetException, IllegalAccessException {
        for (Short channel : channelQueues.keySet())
            callQueued(channel);
    }

    public void callQueued(Short channel) throws InvocationTargetException, IllegalAccessException {
        if (!channelQueues.containsKey(channel)) return;

        ConcurrentLinkedQueue<Event> queue = channelQueues.get(channel);
        for (Event event : queue) {
            call(event);
            queue.remove(event);
        }

        if (queue.isEmpty()) channelQueues.remove(channel);
    }

    private record Listener(Method method, Object self, EventPriority priority) {
    }

}
