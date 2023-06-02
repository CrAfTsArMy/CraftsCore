package de.craftsblock.craftscore.cache;

import java.util.HashMap;
import java.util.Map;

public class Cache<T> {

    private final int capacity;
    private int size;
    private final Map<String, Node> hashmap;
    private final DoublyLinkedList internalQueue;

    public Cache(final int capacity) {
        this.capacity = capacity;
        this.hashmap = new HashMap<>();
        this.internalQueue = new DoublyLinkedList();
        this.size = 0;
    }

    public T get(final String key) {
        Node node = hashmap.get(key);
        if (node == null) return null;
        internalQueue.moveNodeToFront(node);
        return hashmap.get(key).value;
    }

    public boolean contains(final String key) {
        return hashmap.containsKey(key) && get(key) != null;
    }

    public void put(final String key, final T value) {
        Node currentNode = hashmap.get(key);
        if (currentNode != null) {
            currentNode.value = value;
            internalQueue.moveNodeToFront(currentNode);
            return;
        }

        if (size == capacity) {
            String rearNodeKey = internalQueue.getRearKey();
            internalQueue.removeNodeFromRear();
            hashmap.remove(rearNodeKey);
            size--;
        }

        Node node = new Node(key, value);
        internalQueue.addNodeToFront(node);
        hashmap.put(key, node);
        size++;
    }

    public void remove(final String key) {
        Node node = hashmap.get(key);
        if (node == null) return;
        internalQueue.removeNode(node);
        hashmap.remove(key);
        size--;
    }

    public void clear() {
        hashmap.clear();
        internalQueue.clear();
        size = 0;
    }

    private class Node {
        String key;
        T value;
        Node next, prev;

        public Node(final String key, final T value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    private class DoublyLinkedList {
        private Node front, rear;

        public DoublyLinkedList() {
            front = rear = null;
        }

        private void addNodeToFront(final Node node) {
            if (rear == null) {
                front = rear = node;
                return;
            }
            node.next = front;
            front.prev = node;
            front = node;
        }

        public void moveNodeToFront(final Node node) {
            if (front == node) return;
            if (node == rear) {
                rear = rear.prev;
                rear.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }

            node.prev = null;
            node.next = front;
            front.prev = node;
            front = node;
        }

        private void removeNodeFromRear() {
            if (rear == null) return;
            if (front == rear) front = rear = null;
            else {
                rear = rear.prev;
                rear.next = null;
            }
        }

        private void removeNode(final Node node) {
            if (node == front) front = node.next;
            if (node == rear) rear = node.prev;
            if (node.prev != null) node.prev.next = node.next;
            if (node.next != null) node.next.prev = node.prev;
        }

        private void clear() {
            front = null;
            rear = null;
        }

        private String getRearKey() {
            return rear.key;
        }
    }

}

