public class MinHeap <T extends Comparable<T>> {

    private ArrayList<T> minHeap;
    // aqui se puede hacer un cambio cuando tengamos HashMap para mejorar la complejidad de ChangePriority(id)

    public MinHeap() {
        this.minHeap = new ArrayList<>();
    }

    public T GetMin() {
        if (minHeap.isEmpty()) {
            return null;
        }
        return minHeap.get(0);
    }

    public void Insert(T element) {
        minHeap.add(element);
        siftUp(minHeap.size() - 1); 
    }

    public T ExtractMin() {
        if (minHeap.isEmpty()) {
            return null;
        }
        T min = minHeap.get(0);
        int lastIndex = minHeap.size() - 1;
        minHeap.set(0, minHeap.get(lastIndex));
        minHeap.remove(lastIndex);
        if (!minHeap.isEmpty()) {
            siftDown(0);
        }
        return min;
    }

    public void ChangePriority(int i, T NewValue) {
        if (i < 0 || i >= minHeap.size()) return;

        T oldValue = minHeap.get(i);
        minHeap.set(i, NewValue);

        if (NewValue.compareTo(oldValue) < 0) {
            siftUp(i);
        }
        else {
            siftDown(i);
        }
    }

    public void remove(int i) {
        if (i < 0 || i >= minHeap.size()) return;

        int lastIndex = minHeap.size() - 1;
        Swap(i, lastIndex);
        minHeap.remove(lastIndex);

        if (i < minHeap.size()) {
            siftUp(i);
            siftDown(i); //"Internamente solo actua el que es necesario"
        }
    }
    
    private void Swap(int iA, int iB) {
    T temp = minHeap.get(iA);
    minHeap.set(iA, minHeap.get(iB));
    minHeap.set(iB, temp);
    }

    private void siftUp(int i) {
    while (i > 0) {
        int parentIndex = (i - 1) / 2;
        if (minHeap.get(i).compareTo(minHeap.get(parentIndex)) < 0) {
            Swap(i, parentIndex);
            i = parentIndex; 
        } else {
            break;
        }
    }
    }

    private void siftDown(int i) {
    int minIndex = i;
    int size = minHeap.size();

    while (true) {
        int leftChild = 2 * i + 1;
        int rightChild = 2 * i + 2;

        if (leftChild < size && minHeap.get(leftChild).compareTo(minHeap.get(minIndex)) < 0) {
            minIndex = leftChild;
        }

        if (rightChild < size && minHeap.get(rightChild).compareTo(minHeap.get(minIndex)) < 0) {
            minIndex = rightChild;
        }

        if (minIndex != i) {
            Swap(i, minIndex);
            i = minIndex;
        } else {
            break;
        }
    }
    }
}