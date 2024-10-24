package com.example.project3.util;

/**
 * A generic List class that implements a dynamically growing array without importing Java collections.
 * Author: Sriya Vemuri, Zeel Patel
 */
public class List<E> {
    private E[] objects;
    private int size;
    private static final int INITIAL_CAPACITY = 4;
    private static final int GROW_BY = 4;
    private static final int NOT_FOUND = -1;

    @SuppressWarnings("unchecked")
    public List() {
        objects = (E[]) new Object[INITIAL_CAPACITY]; // Initial capacity of 4
        size = 0;
    }

    private int find(E e) {
        for (int i = 0; i < size; i++) {
            if (objects[i].equals(e)) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    @SuppressWarnings("unchecked")
    private void grow() {
        E[] newObjects = (E[]) new Object[objects.length + GROW_BY];
        for (int i = 0; i < size; i++) {
            newObjects[i] = objects[i];
        }
        objects = newObjects;
    }

    public boolean contains(E e) {
        return find(e) != NOT_FOUND;
    }

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

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        return objects[index];
    }

    public void set(int index, E e) {
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Index out of bounds.");
        }
        objects[index] = e;
    }

    public int indexOf(E e) {
        return find(e);
    }

    // Custom iteration mechanism instead of Java Iterator
    private int currentIndex = 0;

    public boolean hasNext() {
        return currentIndex < size;
    }

    public E next() {
        if (!hasNext()) {
            throw new IllegalArgumentException("No more elements.");
        }
        return objects[currentIndex++];
    }

    public void resetIterator() {
        currentIndex = 0; // Reset the iterator index
    }
}