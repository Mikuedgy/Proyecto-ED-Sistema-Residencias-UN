public class MinHeap<T extends Comparable<T>> {

    private T[] heap;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public MinHeap() {
        this.capacity = 16;
        this.heap = (T[]) new Comparable[capacity];
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        capacity *= 2;
        T[] nuevo = (T[]) new Comparable[capacity];
        for (int i = 0; i < size; i++) nuevo[i] = heap[i];
        heap = nuevo;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public T GetMin() {
        if (size == 0) return null;
        return heap[0];
    }

    public void Insert(T element) {
        if (size == capacity) resize();
        heap[size] = element;
        siftUp(size);
        size++;
    }

    public T ExtractMin() {
        if (size == 0) return null;
        T min = heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) siftDown(0);
        return min;
    }

    public void remove(int i) {
        if (i < 0 || i >= size) return;
        size--;
        heap[i] = heap[size];
        heap[size] = null;
        if (i < size) {
            siftUp(i);
            siftDown(i);
        }
    }

     // O(n) — búsqueda lineal necesaria porque el heap está ordenado por PBM, no por ID
    public void removeByEstudiante(Estudiante target) {
        for (int i = 0; i < size; i++) {
            Estudiante e = (Estudiante) heap[i];
            if (e.getId() == target.getId()) {
                remove(i);
                return;
            }
        }
    }

    public void ChangePriority(int i, T newValue) {
        if (i < 0 || i >= size) return;
        double oldPbm = ((Estudiante) heap[i]).getPbm();
        heap[i] = newValue;
        double newPbm = ((Estudiante) newValue).getPbm();
        if (newPbm < oldPbm) siftUp(i);
        else siftDown(i);
    }

    public MinHeap<T> clonar() {
        MinHeap<T> copia = new MinHeap<>();
        for (int i = 0; i < size; i++) {
            copia.Insert(heap[i]);
        }
        return copia;
    }

    private void swap(int a, int b) {
        T temp = heap[a];
        heap[a] = heap[b];
        heap[b] = temp;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            Estudiante actual = (Estudiante) heap[i];
            Estudiante padre  = (Estudiante) heap[parent];
            if (actual.getPbm() < padre.getPbm()) {
                swap(i, parent);
                i = parent;
            } else break;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int minIdx = i;
            int left   = 2 * i + 1;
            int right  = 2 * i + 2;

            if (left < size && ((Estudiante) heap[left]).getPbm() < ((Estudiante) heap[minIdx]).getPbm())
                minIdx = left;
            if (right < size && ((Estudiante) heap[right]).getPbm() < ((Estudiante) heap[minIdx]).getPbm())
                minIdx = right;

            if (minIdx != i) {
                swap(i, minIdx);
                i = minIdx;
            } else break;
        }
    }
}
