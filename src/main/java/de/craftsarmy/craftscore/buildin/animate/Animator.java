package de.craftsarmy.craftscore.buildin.animate;

import de.craftsarmy.craftscore.api.animate.AbstractAnimator;

import java.rmi.UnexpectedException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public final class Animator<E> extends AbstractAnimator<E> {

    public Animator(Collection<E> items) {
        super(items);
    }

    @Override
    public void put(int index, E item) {
        items.put(index, item);
    }

    @Override
    public void add(E item) {
        items.put(index$next, item);
        index$next++;
    }

    @Override
    public void remove(int index) throws UnexpectedException {
        if ((items.size() - 1) == index$total && index >= 0 && index < items.size()) {
            ConcurrentHashMap<Integer, E> items$temp = new ConcurrentHashMap<>();
            items.remove(index);
            int index$temp = 0;
            for (Integer id : items.keySet()) {
                items$temp.put(index$temp, items.get(id));
                index$temp++;
            }
            items = items$temp;
            index$next = index$temp;
            index$total = (index$temp - 1);
            return;
        }
        if (items.size() == index$total)
            throw new UnexpectedException("Unexpected size of the Item list! [Expected: " + index$total + " | Current: " + items.size() + "]");
        throw new IndexOutOfBoundsException("The index " + index + " is out of bounds! [Max: " + (items.size() - 1) + "]");
    }

    @Override
    public int getCurrent() {
        return index$current;
    }

    @Override
    public void cycle() {
        if (index$current >= index$total) {
            index$current = 0;
        } else {
            index$current++;
        }
    }

    @Override
    public E get(int index) {
        return items.get(index);
    }

    @Override
    public E cycleGet() {
        E temp = get(index$current);
        cycle();
        return temp;
    }
}
