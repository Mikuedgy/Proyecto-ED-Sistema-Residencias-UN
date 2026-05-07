import java.util.Random;
 
public class Benchmark {
 
    static final int[] SIZES = {10_000, 100_000, 1_000_000};
    static final int REPETITIONS = 5;
    static final Random rand = new Random(42);
 
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   BENCHMARK - AVLTree y MinHeap");
        System.out.println("========================================");
 
        benchmarkAVL();
        benchmarkMinHeap();
    }
 
    // ─────────────────────────────────────────────
    //  AVL TREE
    // ─────────────────────────────────────────────
    static void benchmarkAVL() {
        System.out.println("\n--- AVLTree ---");
        System.out.printf("%-12s %-20s %-20s %-20s%n",
            "N", "insert (ms)", "searchById (ms)", "delete (ms)");
 
        for (int N : SIZES) {
            long totalInsert = 0;
            long totalSearch = 0;
            long totalDelete = 0;
 
            for (int rep = 0; rep < REPETITIONS; rep++) {
                AVLTree<Estudiante> avl = new AVLTree<>();
 
                // IDs únicos secuenciales — garantiza que siempre existen al buscar/eliminar
                long[] ids = new long[N];
                for (int i = 0; i < N; i++) ids[i] = i;
 
                // ── INSERT ──────────────────────────────────────────────────
                long start = System.nanoTime();
                for (int i = 0; i < N; i++) {
                    avl.insert(new Estudiante("est" + i, ids[i], rand.nextDouble() * 100));
                }
                totalInsert += System.nanoTime() - start;
 
                // ── SEARCH — ids[rand.nextInt(N)] siempre existe, nunca null ─
                start = System.nanoTime();
                for (int i = 0; i < N; i++) {
                    avl.searchById(ids[rand.nextInt(N)]);
                }
                totalSearch += System.nanoTime() - start;
 
                // medir DELETE — eliminar y reinsertar para mantener tamaño N
                start = System.nanoTime();
                for (int i = 0; i < N; i++) {
                    long id = ids[rand.nextInt(N)];
                    Estudiante e = avl.searchById(id);
                    if (e != null) {
                        avl.delete(e);
                        avl.insert(e); // reinsertar para mantener N constante
                    }
                }
                totalDelete += System.nanoTime() - start;
            }
 
            double avgInsert = (totalInsert / REPETITIONS) / 1_000_000.0;
            double avgSearch = (totalSearch / REPETITIONS) / 1_000_000.0;
            double avgDelete = (totalDelete / REPETITIONS) / 1_000_000.0;
 
            System.out.printf("%-12d %-20.2f %-20.2f %-20.2f%n",
                N, avgInsert, avgSearch, avgDelete);
        }
    }
 
    // ─────────────────────────────────────────────
    //  MIN HEAP
    // ─────────────────────────────────────────────
    static void benchmarkMinHeap() {
        System.out.println("\n--- MinHeap ---");
        System.out.printf("%-12s %-20s %-20s %-20s%n",
            "N", "Insert (ms)", "ExtractMin (ms)", "removeByEst (ms)");
 
        for (int N : SIZES) {
            long totalInsert  = 0;
            long totalExtract = 0;
            long totalRemove  = 0;
 
            for (int rep = 0; rep < REPETITIONS; rep++) {
 
                // ── INSERT ──────────────────────────────────────────────────
                MinHeap<Estudiante> heap = new MinHeap<>();
                long start = System.nanoTime();
                for (int i = 0; i < N; i++) {
                    heap.Insert(new Estudiante("est" + i, i, rand.nextDouble() * 100));
                }
                totalInsert += System.nanoTime() - start;
 
                // ── EXTRACTMIN — extrae y reinserta para mantener tamaño N ──
                start = System.nanoTime();
                for (int i = 0; i < N; i++) {
                    Estudiante e = heap.ExtractMin();
                    if (e != null) heap.Insert(e);
                }
                totalExtract += System.nanoTime() - start;
 
                // ── REMOVEBYESTUDIANTE — O(n), heap de tamaño fijo ─────────
                // removeByEstudiante hace búsqueda lineal O(n) sobre el heap.
                // N operaciones sobre un heap de tamaño N sería O(n²), lo que
                // haría el benchmark impracticable a 1M. En su lugar se usa un
                // heap fijo de REMOVE_HEAP_SIZE elementos y se hacen
                // REMOVE_OPS eliminaciones, midiendo el costo por operación y
                // escalando al tamaño N para la tabla. Esto muestra el
                // contraste O(n) vs O(log n) sin bloquear el programa.
                final int REMOVE_HEAP_SIZE = Math.min(N, 50_000);
                final int REMOVE_OPS      = Math.min(N, 1_000);
                MinHeap<Estudiante> heapRemove = new MinHeap<>();
                for (int i = 0; i < REMOVE_HEAP_SIZE; i++) {
                    heapRemove.Insert(new Estudiante("est" + i, i, rand.nextDouble() * 100));
                }
                start = System.nanoTime();
                for (int i = 0; i < REMOVE_OPS; i++) {
                    heapRemove.removeByEstudiante(new Estudiante("", i, 0));
                }
                // costo promedio por op × N ops = tiempo total estimado para N
                long rawRemove = System.nanoTime() - start;
                totalRemove += (long)(rawRemove * ((double) N / REMOVE_OPS));
            }
 
            double avgInsert  = (totalInsert  / REPETITIONS) / 1_000_000.0;
            double avgExtract = (totalExtract / REPETITIONS) / 1_000_000.0;
            double avgRemove  = (totalRemove  / REPETITIONS) / 1_000_000.0;
 
            System.out.printf("%-12d %-20.2f %-20.2f %-20.2f%n",
                N, avgInsert, avgExtract, avgRemove);
        }
    }
}
