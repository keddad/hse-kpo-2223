package utils;

public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K l, V r) {
        key = l;
        value = r;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
