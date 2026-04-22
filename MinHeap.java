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

    public void ChangePriority(int i, T newValue) {
    if (i < 0 || i >= minHeap.size()) return;

    int oldPbm = ((Estudiante) minHeap.get(i)).getPbm();
    minHeap.set(i, newValue);
    int newPbm = ((Estudiante) newValue).getPbm();

    if (newPbm < oldPbm) {
        siftUp(i);
    } else {
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
        
        Estudiante actual = (Estudiante) minHeap.get(i);
        Estudiante padre = (Estudiante) minHeap.get(parentIndex);

        if (actual.getPbm() < padre.getPbm()) {
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

        if (leftChild < size) {
            Estudiante hijoIzq = (Estudiante) minHeap.get(leftChild);
            Estudiante menorActual = (Estudiante) minHeap.get(minIndex);
            if (hijoIzq.getPbm() < menorActual.getPbm()) {
                minIndex = leftChild;
            }
        }

        if (rightChild < size) {
            Estudiante hijoDer = (Estudiante) minHeap.get(rightChild);
            Estudiante menorActual = (Estudiante) minHeap.get(minIndex);
            if (hijoDer.getPbm() < menorActual.getPbm()) {
                minIndex = rightChild;
            }
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