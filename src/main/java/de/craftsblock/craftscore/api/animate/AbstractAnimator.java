package de.craftsblock.craftscore.api.animate;

import java.rmi.UnexpectedException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractAnimator<E> {

    public ConcurrentHashMap<Integer, E> items = new ConcurrentHashMap<>();
    public int index$next, index$current = 0, index$total = 0;

    public AbstractAnimator(Collection<E> items) {
        int temp = 0;
        for (E item : items) {
            this.items.put(temp, item);
            temp++;
        }
        index$next = temp;
        index$total = (temp - 1);
    }

    public abstract void put(int index, E item);
    public abstract void add(E item);
    public abstract void remove(int index) throws UnexpectedException;
    public abstract int getCurrent();
    public abstract void cycle();
    public abstract E get(int index);
    public abstract E cycleGet();

}
