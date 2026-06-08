public class HashMap {

    private Node[] buckets;
    private int size;
    private static final double LOAD_FACTOR = 0.75;

    public HashMap() {
        this.buckets = new Node[16];
        this.size = 0;
    }

    ////////////////////////////////////////// ----> CLASE INTERNA NODO
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
    //FUNCION HASH
    private int hash(long key) {
        return (int) (key % buckets.length);
    }

    //INSERTAR O ACTUALIZAR
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

    //OBTENER VALOR POR CLAVE
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

    //ELIMINAR POR CLAVE
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

    //REDIMENSIONAR
    @SuppressWarnings("unchecked")
    private void resize() {
        Node[] oldBuckets = buckets;
        buckets = new Node[oldBuckets.length * 2];
        size = 0;
        for (Node head : oldBuckets) {
            Node actual = head;
            while (actual != null) {
                put(actual.key, actual.value);
                actual = actual.next;
            }
        }
    }
}
