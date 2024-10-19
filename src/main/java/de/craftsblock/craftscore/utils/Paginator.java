package de.craftsblock.craftscore.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * The Paginator class provides pagination functionality to split a list of objects into smaller pages.
 * It allows navigating through the pages and retrieving specific pages of the object list.
 *
 * @param <T> The type of objects to be paginated.
 * @author Philipp Maywald
 * @author CraftsBlock
 * @version 1.0
 * @since 3.5.4-SNAPSHOT
 * @deprecated This class and its methods are deprecated for removal and should not be used.
 * There is no specific alternative implementation provided in this version.
 * Developers are encouraged to avoid using these deprecated elements and switch to recommended replacements.
 */
@Deprecated(forRemoval = true)
@SuppressWarnings("unchecked")
public class Paginator<T> {

    private T[] objects;
    private final Double pagSize;
    private Integer currentPage;
    private Integer amountOfPages;

    /**
     * Constructs a Paginator object with an array of objects and a maximum number of objects per page.
     *
     * @param objects The array of objects to be paginated.
     * @param max     The maximum number of objects per page.
     * @deprecated This constructor is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated constructor and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public Paginator(T[] objects, Integer max) {
        this.objects = objects;
        pagSize = Double.valueOf(max);
    }

    /**
     * Constructs a Paginator object with a list of objects and a maximum number of objects per page.
     *
     * @param objects The list of objects to be paginated.
     * @param max     The maximum number of objects per page.
     * @deprecated This constructor is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated constructor and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public Paginator(List<T> objects, Integer max) {
        this(objects.toArray((T[]) new Object[0]), max);
    }

    /**
     * Sets the elements of the Paginator to the specified list of objects.
     *
     * @param objects The list of objects to be paginated.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public void setElements(List<T> objects) {
        this.objects = objects.toArray((T[]) new Object[0]);
    }

    /**
     * Checks if there is a next page available.
     *
     * @return True if there is a next page, false otherwise.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public boolean hasNext() {
        return currentPage < amountOfPages;
    }

    /**
     * Checks if there is a previous page available.
     *
     * @return True if there is a previous page, false otherwise.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public boolean hasPrev() {
        return currentPage > 1;
    }

    /**
     * Retrieves the page number of the next page.
     *
     * @return The page number of the next page.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public int getNext() {
        return currentPage + 1;
    }

    /**
     * Retrieves the page number of the previous page.
     *
     * @return The page number of the previous page.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public int getPrev() {
        return currentPage - 1;
    }

    /**
     * Retrieves the page number of the first page.
     *
     * @return The page number of the first page.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public int getFirst() {
        return 1;
    }

    /**
     * Retrieves the page number of the last page.
     *
     * @return The page number of the last page.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public int getLast() {
        return amountOfPages;
    }

    /**
     * Retrieves the page number of the current page.
     *
     * @return The page number of the current page.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
    public int getCurrent() {
        return currentPage;
    }

    /**
     * Retrieves a specific page of the objects based on the given page number.
     *
     * @param pageNum The page number of the requested page.
     * @return A List containing the objects in the specified page.
     * @deprecated This method is deprecated for removal and should not be used.
     * There is no specific alternative implementation provided in this version.
     * Developers are encouraged to avoid using this deprecated method and switch to recommended replacements.
     */
    @Deprecated(forRemoval = true)
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