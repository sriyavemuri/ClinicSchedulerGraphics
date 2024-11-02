package com.example.project3.util;

/**
 * A generic List class that implements a dynamically growing array without importing Java collections.
 * This class provides methods for adding, removing, and accessing elements, and includes a custom iteration mechanism.
 *
 * @param <E> the type of elements in this list
 * Author: Sriya Vemuri, Zeel Patel
 */
public class List<E> {
    private E[] objects;
    private int size;
    private static final int INITIAL_CAPACITY = 4;
    private static final int GROW_BY = 4;
    private static final int NOT_FOUND = -1;

    /**
     * Constructs an empty List with an initial capacity of 4.
     */
    @SuppressWarnings("unchecked")
    public List() {
        objects = (E[]) new Object[INITIAL_CAPACITY]; // Initial capacity of 4
        size = 0;
    }

    /**
     * Finds the index of the specified element in the list.
     *
     * @param e the element to find
     * @return the index of the element if found, otherwise -1
     */
    private int find(E e) {
        for (int i = 0; i < size; i++) {
            if (objects[i].equals(e)) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    /**
     * Grows the internal array by a fixed amount (GROW_BY) to accommodate more elements.
     */
    @SuppressWarnings("unchecked")
    private void grow() {
        E[] newObjects = (E[]) new Object[objects.length + GROW_BY];
        for (int i = 0; i < size; i++) {
            newObjects[i] = objects[i];
        }
        objects = newObjects;
    }

    /**
     * Checks if the list contains the specified element.
     *
     * @param e the element to check for
     * @return true if the element is found, otherwise false
     */
    public boolean contains(E e) {
        return find(e) != NOT_FOUND;
    }

    /**
     * Adds a new element to the list if it does not already exist.
     * Increases the list capacity if the current capacity is full.
     *
     * @param e the element to add
     */
    public void add(E e) {
        if (contains(e)) {
            return; // Avoid adding duplicates
        }
        if (size == objects.length) {
            grow(); // Increase capacity if full
        }
        // Add the new element at the end
        objects[size] = e;
        size++;
    }

    /**
     * Removes the specified element from the list if it exists.
     * Shifts subsequent elements to fill the gap created by the removal.
     *
     * @param e the element to remove
     */
    public void remove(E e) {
        int index = find(e);
        if (index == NOT_FOUND) {
            return; // Object not found, nothing to remove
        }
        for (int i = index; i < size - 1; i++) {
            objects[i] = objects[i + 1];
        }
        objects[size - 1] = null; // Nullify the last element
        size--;
    }

    /**
     * Checks if the list is empty.
     *
     * @return true if the list has no elements, otherwise false
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the list.
     *
     * @return the current size of the list
     */
    public int size() {
        return size;
    }

    /**
     * Retrieves the element at the specified index.
     *
     * @param index the index of the element to retrieve
     * @return the element at the specified index
     * @throws IllegalArgumentException if the index is out of bounds
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        return objects[index];
    }

    /**
     * Replaces the element at the specified index with the specified element.
     *
     * @param index the index of the element to replace
     * @param e the new element
     * @throws IllegalArgumentException if the index is out of bounds
     */
    public void set(int index, E e) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        objects[index] = e;
    }

    /**
     * Returns the index of the specified element in the list.
     *
     * @param e the element to find
     * @return the index of the element if found, otherwise -1
     */
    public int indexOf(E e) {
        return find(e);
    }

    // Custom iteration mechanism instead of Java Iterator
    private int currentIndex = 0;

    /**
     * Checks if there are more elements to iterate over.
     *
     * @return true if there are more elements, otherwise false
     */
    public boolean hasNext() {
        return currentIndex < size;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element
     * @throws IllegalArgumentException if there are no more elements
     */
    public E next() {
        if (!hasNext()) {
            throw new IllegalArgumentException("No more elements.");
        }
        return objects[currentIndex++];
    }

    /**
     * Resets the custom iterator index to the beginning of the list.
     */
    public void resetIterator() {
        currentIndex = 0; // Reset the iterator index
    }
}