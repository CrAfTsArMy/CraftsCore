package de.craftsblock.craftscore.utils;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class Paginator<T> {

    private T[] objects;
    private final Double pagSize;
    private Integer currentPage;
    private Integer amountOfPages;

    public Paginator(T[] objects, Integer max) {
        this.objects = objects;
        pagSize = new Double(max);
    }

    public Paginator(List<T> objects, Integer max) {
        this(objects.toArray((T[]) new Object[0]), max);
    }
    public void setElements(List<T> objects) {
        this.objects = objects.toArray((T[]) new Object[0]);
    }
    public boolean hasNext() {
        return currentPage < amountOfPages;
    }
    public boolean hasPrev() {
        return currentPage > 1;
    }
    public int getNext() {
        return currentPage + 1;
    }
    public int getPrev() {
        return currentPage - 1;
    }
    public int getFirst() {
        return 1;
    }
    public int getLast() {
        return amountOfPages;
    }
    public int getCurrent() {
        return currentPage;
    }

    public List<T> getPage(Integer pageNum) {
        List<T> page = new ArrayList<>();
        double total = objects.length / pagSize;
        amountOfPages = (int) Math.ceil(total);
        currentPage = pageNum;

        if (objects.length == 0)
            return page;

        double startC = pagSize * (pageNum - 1);
        double finalC = startC + pagSize;

        for (; startC < finalC; startC++)
            if (startC < objects.length)
                page.add(objects[(int) startC]);
        return page;
    }

}