import java.util.Random;

public class Benchmark {

    static final int[] SIZES = {10_000, 100_000, 250_000, 500_000, 750_000, 1_000_000};
    static final int REPETITIONS = 5;
    static final int TEST_OPS = 100; // Número de operaciones para promediar el costo en tamaño N
    static final Random rand = new Random(42);

    public static void main(String[] args) {
        System.out.println("=====================================================");
        System.out.println("   BENCHMARK CORREGIDO - Costo unitario en tamaño N");
        System.out.println("=====================================================");

        benchmarkAVL();
        benchmarkMinHeap();
    }

    // ─────────────────────────────────────────────
    //  AVL TREE
    // ─────────────────────────────────────────────
    static void benchmarkAVL() {
        System.out.println("\n--- AVLTree (Tiempos por operación individual en ms) ---");
        System.out.printf("%-12s %-20s %-20s %-20s%n",
                "N", "Insert (ms)", "Search (ms)", "Delete (ms)");

        for (int N : SIZES) {
            double totalInsertTime = 0;
            double totalSearchTime = 0;
            double totalDeleteTime = 0;

            for (int rep = 0; rep < REPETITIONS; rep++) {
                AVLTree<Estudiante> avl = new AVLTree<>();

                // 1. PREPARACIÓN: Llenar el árbol hasta N sin medir tiempo
                for (int i = 0; i < N; i++) {
                    avl.insert(new Estudiante("est" + i, i, rand.nextDouble() * 100));
                }

                // 2. MEDICIÓN INSERT: Medimos solo TEST_OPS inserciones adicionales sobre el tamaño N
                long start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    avl.insert(new Estudiante("extra", N + i, 50.0));
                }
                totalInsertTime += (System.nanoTime() - start) / (double) TEST_OPS;

                // 3. MEDICIÓN SEARCH: Medimos TEST_OPS búsquedas aleatorias
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    avl.searchById(rand.nextInt(N));
                }
                totalSearchTime += (System.nanoTime() - start) / (double) TEST_OPS;

                // 4. MEDICIÓN DELETE: Medimos TEST_OPS eliminaciones
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    avl.delete(new Estudiante("", i, 0)); // Asumiendo que el ID basta para el delete
                }
                totalDeleteTime += (System.nanoTime() - start) / (double) TEST_OPS;
            }

            // Promedio de las repeticiones convertido a milisegundos
            double avgInsert = (totalInsertTime / REPETITIONS) / 1_000_000.0;
            double avgSearch = (totalSearchTime / REPETITIONS) / 1_000_000.0;
            double avgDelete = (totalDeleteTime / REPETITIONS) / 1_000_000.0;

            System.out.printf("%-12d %-20.6f %-20.6f %-20.6f%n",
                    N, avgInsert, avgSearch, avgDelete);
        }
    }

    // ─────────────────────────────────────────────
    //  MIN HEAP
    // ─────────────────────────────────────────────
    static void benchmarkMinHeap() {
        System.out.println("\n--- MinHeap (Tiempos por operación individual en ms) ---");
        System.out.printf("%-12s %-20s %-20s %-20s%n",
                "N", "Insert (ms)", "ExtractMin (ms)", "RemoveO(n) (ms)");

        for (int N : SIZES) {
            double totalInsertTime = 0;
            double totalExtractTime = 0;
            double totalRemoveTime = 0;

            for (int rep = 0; rep < REPETITIONS; rep++) {
                MinHeap<Estudiante> heap = new MinHeap<>();

                // 1. PREPARACIÓN: Llenar el Heap hasta N
                for (int i = 0; i < N; i++) {
                    heap.Insert(new Estudiante("est" + i, i, rand.nextDouble() * 100));
                }

                // 2. MEDICIÓN INSERT
                long start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    heap.Insert(new Estudiante("extra", N + i, 50.0));
                }
                totalInsertTime += (System.nanoTime() - start) / (double) TEST_OPS;

                // 3. MEDICIÓN EXTRACT MIN
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    heap.ExtractMin();
                }
                totalExtractTime += (System.nanoTime() - start) / (double) TEST_OPS;

                // 4. MEDICIÓN REMOVE (O(n)): Esta es la que más le preocupa al profe
                // Buscamos un elemento que probablemente esté al final para ver el peor caso de O(n)
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS; i++) {
                    // Buscamos un ID que sepamos que existe en la estructura llena
                    heap.removeByEstudiante(new Estudiante("", N - i - 1, 0));
                }
                totalRemoveTime += (System.nanoTime() - start) / (double) TEST_OPS;
            }

            double avgInsert = (totalInsertTime / REPETITIONS) / 1_000_000.0;
            double avgExtract = (totalExtractTime / REPETITIONS) / 1_000_000.0;
            double avgRemove = (totalRemoveTime / REPETITIONS) / 1_000_000.0;

            System.out.printf("%-12d %-20.6f %-20.6f %-20.6f%n",
                    N, avgInsert, avgExtract, avgRemove);
        }
    }
}

