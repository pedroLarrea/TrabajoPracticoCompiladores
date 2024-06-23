package Estructuras;

import java.util.LinkedList;

public class SimpleMap {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private LinkedList<Entry>[] buckets;
    private int size;

    @SuppressWarnings("unchecked")
    public SimpleMap() {
        buckets = new LinkedList[INITIAL_CAPACITY];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList<>();
        }
        size = 0;
    }

    private static class Entry {
        String key;
        String value;

        Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private int getBucketIndex(String key) {
        return Math.abs(key.hashCode() % buckets.length);
    }

    public void put(String key, String value) {
        int index = getBucketIndex(key);
        LinkedList<Entry> bucket = buckets[index];
        for (Entry entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        bucket.add(new Entry(key, value));
        size++;
        if (size >= buckets.length * LOAD_FACTOR) {
            resize();
        }
    }

    public String get(String key) {
        int index = getBucketIndex(key);
        LinkedList<Entry> bucket = buckets[index];
        for (Entry entry : bucket) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    public void remove(String key) {
        int index = getBucketIndex(key);
        LinkedList<Entry> bucket = buckets[index];
        for (Entry entry : bucket) {
            if (entry.key.equals(key)) {
                bucket.remove(entry);
                size--;
                return;
            }
        }
    }

    public boolean containsKey(String key) {
        int index = getBucketIndex(key);
        LinkedList<Entry> bucket = buckets[index];
        for (Entry entry : bucket) {
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        LinkedList<Entry>[] oldBuckets = buckets;
        buckets = new LinkedList[oldBuckets.length * 2];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList<>();
        }

        size = 0;
        for (LinkedList<Entry> bucket : oldBuckets) {
            for (Entry entry : bucket) {
                put(entry.key, entry.value);
            }
        }
    }
}
