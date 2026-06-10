public class HashMap {

    private Node[] buckets;
    private int size;
    private static final double LOAD_FACTOR = 0.75;

    public HashMap() {
        this.buckets = new Node[16];
        this.size = 0;
    }

    private static class Node {
        long key;
        int value;
        Node next;

        Node(long key, int value) {
            this.key = key;
            this.value = value;
        }
    }


    ////////////////////////////////////////// ----> METODOS PROPIOS DE HASHMAP
    
    private int hash(long key) {
        return (int) (key & (buckets.length - 1));
    }

    public void put(long key, int value) {
        int idx = hash(key);
        Node actual = buckets[idx];
        while (actual != null) {
            if (actual.key == key) {
                actual.value = value;
                return;
            }
            actual = actual.next;
        }
        Node nuevo = new Node(key, value);
        nuevo.next = buckets[idx];
        buckets[idx] = nuevo;
        size++;
        if ((double) size > buckets.length * LOAD_FACTOR) {
            resize();
        }
    }

    public Integer get(long key) {
        int idx = hash(key);
        Node actual = buckets[idx];
        while (actual != null) {
            if (actual.key == key) {
                return actual.value;
            }
            actual = actual.next;
        }
        return null;
    }

    public void remove(long key) {
        int idx = hash(key);
        Node actual = buckets[idx];
        Node prev = null;
        while (actual != null) {
            if (actual.key == key) {
                if (prev == null) {
                    buckets[idx] = actual.next;
                } else {
                    prev.next = actual.next;
                }
                size--;
                return;
            }
            prev = actual;
            actual = actual.next;
        }
    }

    private void resize() {
        Node[] oldBuckets = buckets;
        buckets = new Node[oldBuckets.length * 2];
        for (Node head : oldBuckets) {
            Node actual = head;
            while (actual != null) {
                Node next = actual.next;
                int idx = hash(actual.key);
                actual.next = buckets[idx];
                buckets[idx] = actual;
                actual = next;
            }
        }
    }
}
