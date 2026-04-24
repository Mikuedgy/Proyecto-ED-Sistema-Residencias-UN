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
                long[] ids = new long[N];
 
                // generar IDs únicos secuenciales para garantizar que existen
                for (int i = 0; i < N; i++) ids[i] = i;
 
                // medir INSERT
                long start = System.nanoTime();
                for (int i = 0; i < N; i++) {
                    avl.insert(new Estudiante("est" + i, ids[i], rand.nextDouble() * 100));
                }
                totalInsert += System.nanoTime() - start;
 
                // medir SEARCH — buscar IDs que sí existen
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
 
            // promediar sobre las repeticiones y convertir a ms
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
        System.out.printf("%-12s %-20s %-20s%n",
            "N", "Insert (ms)", "ExtractMin (ms)");
 
        for (int N : SIZES) {
            long totalInsert = 0;
            long totalExtract = 0;
 
            for (int rep = 0; rep < REPETITIONS; rep++) {
                MinHeap<Estudiante> heap = new MinHeap<>();
 
                // medir INSERT
                long start = System.nanoTime();
                for (int i = 0; i < N; i++) {
                    heap.Insert(new Estudiante("est" + i, i, rand.nextDouble() * 100));
                }
                totalInsert += System.nanoTime() - start;
 
                // medir EXTRACTMIN — extraer y reinsertar para mantener tamaño N
                start = System.nanoTime();
                for (int i = 0; i < N; i++) {
                    Estudiante e = heap.ExtractMin();
                    if (e != null) {
                        heap.Insert(e); // reinsertar para mantener N constante
                    }
                }
                totalExtract += System.nanoTime() - start;
            }
 
            double avgInsert  = (totalInsert  / REPETITIONS) / 1_000_000.0;
            double avgExtract = (totalExtract / REPETITIONS) / 1_000_000.0;
 
            System.out.printf("%-12d %-20.2f %-20.2f%n",
                N, avgInsert, avgExtract);
        }
    }
}