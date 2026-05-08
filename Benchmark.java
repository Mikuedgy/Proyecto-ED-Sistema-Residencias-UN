import java.util.Random;

public class Benchmark {

    static final int[] SIZES = {10_000, 100_000, 250_000, 500_000, 750_000, 1_000_000};
    static final int REPETITIONS = 5;
    static final int TEST_OPS = 100;
    static final Random rand = new Random(42);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   BENCHMARK - AVLTree y MinHeap");
        System.out.println("========================================");

        // --- NOVEDAD: CALENTAMIENTO DE LA JVM ---
        // Corremos una prueba pequeña sin imprimir para que Java optimice el código
        System.out.print("Calentando motores (JVM Warm-up)... ");
        warmup();
        System.out.println("¡Listo!\n");

        benchmarkAVL();
        benchmarkMinHeap();
    }

    private static void warmup() {
        AVLTree<Estudiante> tree = new AVLTree<>();
        for (int i = 0; i < 5000; i++) tree.insert(new Estudiante("w", (long)i, 0));
        for (int i = 0; i < 1000; i++) tree.searchById((long)i);
    }

    static void benchmarkAVL() {
        System.out.println("--- AVLTree (Costo Unitario Estable) ---");
        System.out.printf("%-12s %-15s %-15s %-15s%n", "N", "Insert (ms)", "Search (ms)", "Delete (ms)");

        for (int N : SIZES) {
            double totalInsertTime = 0;
            double totalSearchTime = 0;
            double totalDeleteTime = 0;

            for (int rep = 0; rep < REPETITIONS; rep++) {
                AVLTree<Estudiante> avl = new AVLTree<>();
                
                // --- NOVEDAD: CONTROL DE IDs ---
                // Pre-generamos IDs para asegurar que siempre buscamos algo que EXISTE
                long[] existingIds = new long[N];
                for (int i = 0; i < N; i++) {
                    existingIds[i] = i;
                    avl.insert(new Estudiante("est" + i, (long)i, rand.nextDouble() * 100));
                }

                // MEDICIÓN INSERT (Costo de añadir al árbol ya lleno)
                long start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    avl.insert(new Estudiante("extra", (long)(N + i), 50.0));
                }
                totalInsertTime += (System.nanoTime() - start) / (double) TEST_OPS;

                // MEDICIÓN SEARCH (Ahora garantizamos que el ID existe en el set de N)
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    avl.searchById(existingIds[rand.nextInt(N)]);
                }
                totalSearchTime += (System.nanoTime() - start) / (double) TEST_OPS;

                // MEDICIÓN DELETE (Eliminamos elementos reales del set)
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    avl.delete(new Estudiante("", existingIds[i], 0));
                }
                totalDeleteTime += (System.nanoTime() - start) / (double) TEST_OPS;
            }

            // Impresión con alta precisión (6 decimales)
            System.out.printf("%-12d %-15.6f %-15.6f %-15.6f%n", N, 
                (totalInsertTime/REPETITIONS)/1e6, 
                (totalSearchTime/REPETITIONS)/1e6, 
                (totalDeleteTime/REPETITIONS)/1e6);
        }
    }

    // ─────────────────────────────────────────────
    //  MIN HEAP
    // ─────────────────────────────────────────────
    static void benchmarkMinHeap() {
        System.out.println("\n--- MinHeap (Tiempos por operación individual en ms) ---");
        System.out.printf("%-12s %-15s %-15s %-15s%n",
            "N", "Insert (ms)", "ExtractMin (ms)", "RemoveO(n) (ms)");

        for (int N : SIZES) {
            double totalInsertTime = 0;
            double totalExtractTime = 0;
            double totalRemoveTime = 0;

            for (int rep = 0; rep < REPETITIONS; rep++) {
                MinHeap<Estudiante> heap = new MinHeap<>();

                // 1. PREPARACIÓN: Llenamos hasta N
                for (int i = 0; i < N; i++) {
                    heap.Insert(new Estudiante("est" + i, (long)i, rand.nextDouble() * 100));
                }

                // 2. MEDICIÓN INSERT
                long start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    heap.Insert(new Estudiante("extra", (long)(N + i), 50.0));
                }
                totalInsertTime += (System.nanoTime() - start) / (double) TEST_OPS;

                // 3. MEDICIÓN EXTRACTMIN
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    Estudiante e = heap.ExtractMin();
                    if (e != null) heap.Insert(e); // Reinsertar para no vaciar
                }
                totalExtractTime += (System.nanoTime() - start) / (double) TEST_OPS;

                // 4. MEDICIÓN REMOVE O(n): Búsqueda lineal real sobre N
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    // Buscamos IDs que sabemos que están al final para ver el peor caso O(n)
                    heap.removeByEstudiante(new Estudiante("", (long)(N - i - 1), 0));
                }
                totalRemoveTime += (System.nanoTime() - start) / (double) TEST_OPS;
            }

            double avgInsert = (totalInsertTime / REPETITIONS) / 1_000_000.0;
            double avgExtract = (totalExtractTime / REPETITIONS) / 1_000_000.0;
            double avgRemove = (totalRemoveTime / REPETITIONS) / 1_000_000.0;

            System.out.printf("%-12d %-15.6f %-15.6f %-15.6f%n",
                N, avgInsert, avgExtract, avgRemove);
        }
    }
}
