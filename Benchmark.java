import java.util.Random;

public class Benchmark {

    static final int[] SIZES = {
            10_000,
            100_000,
            250_000,
            500_000,
            750_000,
            1_000_000
    };

    // Número de operaciones por prueba para métodos O(log n)
    static final int TEST_OPS_LOG = 100_000;

    // Número de operaciones por prueba para métodos O(n)
    static final int TEST_OPS_LINEAR = 500;

    // Repeticiones completas
    static final int REPETITIONS = 5;

    static final Random rand = new Random(42);

    public static void main(String[] args) {

        System.out.println("=====================================================");
        System.out.println("      BENCHMARK MEJORADO - AVL vs MIN HEAP vs HASHMAP");
        System.out.println("=====================================================");

        benchmarkAVL();
        benchmarkMinHeap();
        benchmarkHashMap();
    }

    // =========================================================
    // AVL TREE
    // =========================================================
    static void benchmarkAVL() {

        System.out.println("\n--- AVLTree (Tiempo promedio por operación en ms) ---");

        System.out.printf(
                "%-12s %-20s %-20s %-20s%n",
                "N",
                "Insert (ms)",
                "Search (ms)",
                "Delete (ms)"
        );

        for (int N : SIZES) {

            double totalInsert = 0;
            double totalSearch = 0;
            double totalDelete = 0;

            for (int rep = 0; rep < REPETITIONS; rep++) {

                AVLTree<Estudiante> avl = new AVLTree<>();

                // =================================================
                // LLENAR AVL SIN MEDIR
                // =================================================

                // Se llena con N elementos antes de medir 
                for (int i = 0; i < N; i++) {

                    avl.insert(
                            new Estudiante(
                                    "est" + i,
                                    i,
                                    rand.nextDouble() * 100
                            )
                    );
                }

                // =================================================
                // WARMUP JVM
                // =================================================
                // Compila el código antes de medir para que los tiempos no estén sesgados
                for (int i = 0; i < 20_000; i++) {

                    avl.searchById(rand.nextInt(N));
                }

                // =================================================
                // PRECREAR INSERTS
                // =================================================
                // Objetos se precrean fuera del bloque de medición para no contaminar el resultado
                Estudiante[] insertStudents =
                        new Estudiante[TEST_OPS_LOG];

                for (int i = 0; i < TEST_OPS_LOG; i++) {

                    insertStudents[i] = new Estudiante(
                            "extra" + i,
                            N + i,
                            50.0
                    );
                }

                // =================================================
                // MEDICIÓN INSERT
                // =================================================
                long start = System.nanoTime();

                for (int i = 0; i < TEST_OPS_LOG; i++) {

                    avl.insert(insertStudents[i]);
                }

                long end = System.nanoTime();

                totalInsert +=
                        (end - start) / (double) TEST_OPS_LOG;

                // =================================================
                // MEDICIÓN SEARCH
                // =================================================
                start = System.nanoTime();

                for (int i = 0; i < TEST_OPS_LOG; i++) {

                    avl.searchById(rand.nextInt(N));
                }

                end = System.nanoTime();

                totalSearch +=
                        (end - start) / (double) TEST_OPS_LOG;

                // =================================================
                // PRECREAR DELETES
                // =================================================
                Estudiante[] deleteStudents =
                        new Estudiante[TEST_OPS_LOG];

                for (int i = 0; i < TEST_OPS_LOG; i++) {

                    deleteStudents[i] = new Estudiante(
                            "",
                            i,
                            0
                    );
                }

                // =================================================
                // MEDICIÓN DELETE
                // =================================================
                start = System.nanoTime();

                for (int i = 0; i < TEST_OPS_LOG; i++) {

                    avl.delete(deleteStudents[i]);
                }

                end = System.nanoTime();

                totalDelete +=
                        (end - start) / (double) TEST_OPS_LOG;
            }

            double avgInsert =
                    (totalInsert / REPETITIONS) / 1_000_000.0;

            double avgSearch =
                    (totalSearch / REPETITIONS) / 1_000_000.0;

            double avgDelete =
                    (totalDelete / REPETITIONS) / 1_000_000.0;

            System.out.printf(
                    "%-12d %-20.6f %-20.6f %-20.6f%n",
                    N,
                    avgInsert,
                    avgSearch,
                    avgDelete
            );
        }
    }

    // =========================================================
    // MIN HEAP
    // =========================================================
    static void benchmarkMinHeap() {

        System.out.println("\n--- MinHeap (Tiempo promedio por operación en ms) ---");

        System.out.printf(
                "%-12s %-20s %-20s %-20s%n",
                "N",
                "Insert (ms)",
                "ExtractMin (ms)",
                "RemoveO(n) (ms)"
        );

        for (int N : SIZES) {

            double totalInsert = 0;
            double totalExtract = 0;
            double totalRemove = 0;

            for (int rep = 0; rep < REPETITIONS; rep++) {

                MinHeap<Estudiante> heap = new MinHeap<>();

                // =================================================
                // LLENAR HEAP SIN MEDIR
                // =================================================

                // Se llena con N elementos antes de medir 
                for (int i = 0; i < N; i++) {

                    heap.Insert(
                            new Estudiante(
                                    "est" + i,
                                    i,
                                    rand.nextDouble() * 100
                            )
                    );
                }

                // =================================================
                // WARMUP JVM
                // =================================================
                for (int i = 0; i < 20_000; i++) {

                    heap.ExtractMin();
                }

                // =================================================
                // PRECREAR INSERTS
                // =================================================
                Estudiante[] insertStudents =
                        new Estudiante[TEST_OPS_LOG];

                for (int i = 0; i < TEST_OPS_LOG; i++) {

                    insertStudents[i] = new Estudiante(
                            "extra" + i,
                            N + i,
                            50.0
                    );
                }

                // =================================================
                // MEDICIÓN INSERT
                // =================================================
                long start = System.nanoTime();

                for (int i = 0; i < TEST_OPS_LOG; i++) {

                    heap.Insert(insertStudents[i]);
                }

                long end = System.nanoTime();

                totalInsert +=
                        (end - start) / (double) TEST_OPS_LOG;

                // =================================================
                // MEDICIÓN EXTRACT MIN
                // =================================================
                start = System.nanoTime();

                for (int i = 0; i < TEST_OPS_LOG; i++) {

                    heap.ExtractMin();
                }

                end = System.nanoTime();

                totalExtract +=
                        (end - start) / (double) TEST_OPS_LOG;

                // =================================================
                // PRECREAR REMOVES
                // =================================================
                Estudiante[] removeStudents =
                        new Estudiante[TEST_OPS_LINEAR];

                for (int i = 0; i < TEST_OPS_LINEAR; i++) {

                    removeStudents[i] = new Estudiante(
                            "",
                            N - i - 1,
                            0
                    );
                }

                // =================================================
                // MEDICIÓN REMOVE O(n)
                // =================================================
                start = System.nanoTime();

                for (int i = 0; i < TEST_OPS_LINEAR; i++) {

                    heap.removeByEstudiante(removeStudents[i]);
                }

                end = System.nanoTime();

                totalRemove +=
                        (end - start) / (double) TEST_OPS_LINEAR;
            }

            double avgInsert =
                    (totalInsert / REPETITIONS) / 1_000_000.0;

            double avgExtract =
                    (totalExtract / REPETITIONS) / 1_000_000.0;

            double avgRemove =
                    (totalRemove / REPETITIONS) / 1_000_000.0;

            System.out.printf(
                    "%-12d %-20.6f %-20.6f %-20.6f%n",
                    N,
                    avgInsert,
                    avgExtract,
                    avgRemove
            );
        }
    }

    // =========================================================
    // HASHMAP
    // =========================================================
    static void benchmarkHashMap() {

        System.out.println("\n--- HashMap (Tiempo promedio por operación en ms) ---");

        System.out.printf(
                "%-12s %-20s %-20s %-20s%n",
                "N",
                "Put (ms)",
                "Get (ms)",
                "Remove (ms)"
        );

        for (int N : SIZES) {

            double totalPut = 0;
            double totalGet = 0;
            double totalRemove = 0;

            for (int rep = 0; rep < REPETITIONS; rep++) {

                HashMap map = new HashMap();

                // ── Llenar sin medir ────────────────────────────────────
                for (int i = 0; i < N; i++)
                    map.put(i, rand.nextInt(1000));

                // ── Pre-dimensionar: insertar y remover TEST_OPS_LOG   ──
                //    elementos antes de medir, para que los resize no
                //    contaminen la medición de PUT.
                for (int i = 0; i < TEST_OPS_LOG; i++)
                    map.put(N + i, 0);
                for (int i = 0; i < TEST_OPS_LOG; i++)
                    map.remove(N + i);

                // ── Pre-crear PUT keys ──────────────────────────────────
                long[] putKeys = new long[TEST_OPS_LOG];
                int[] putValues = new int[TEST_OPS_LOG];
                for (int i = 0; i < TEST_OPS_LOG; i++) {
                    putKeys[i] = N + i;
                    putValues[i] = rand.nextInt(1000);
                }

                // ── Warmup PUT (compilar put vía JIT) ───────────────────
                for (int i = 0; i < 20_000; i++) {
                    map.put(N + TEST_OPS_LOG + i, 0);
                    map.remove(N + TEST_OPS_LOG + i);
                }

                // ── Medición PUT ────────────────────────────────────────
                long start = System.nanoTime();
                for (int i = 0; i < TEST_OPS_LOG; i++)
                    map.put(putKeys[i], putValues[i]);
                long end = System.nanoTime();
                totalPut += (end - start) / (double) TEST_OPS_LOG;

                // ── Warmup GET sobre el mapa completo ───────────────────
                int totalKeys = N + TEST_OPS_LOG;
                for (int i = 0; i < 20_000; i++)
                    map.get(rand.nextInt(totalKeys));

                // ── Medición GET ────────────────────────────────────────
                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS_LOG; i++)
                    map.get(rand.nextInt(totalKeys));
                end = System.nanoTime();
                totalGet += (end - start) / (double) TEST_OPS_LOG;

                // ── Medición REMOVE (ya viene caliente de warmups
                //    anteriores, pero igual se compila en la 1ra rep) ──
                long[] removeKeys = new long[TEST_OPS_LOG];
                for (int i = 0; i < TEST_OPS_LOG; i++)
                    removeKeys[i] = i;

                start = System.nanoTime();
                for (int i = 0; i < TEST_OPS_LOG; i++)
                    map.remove(removeKeys[i]);
                end = System.nanoTime();
                totalRemove += (end - start) / (double) TEST_OPS_LOG;
            }

            double avgPut =
                    (totalPut / REPETITIONS) / 1_000_000.0;

            double avgGet =
                    (totalGet / REPETITIONS) / 1_000_000.0;

            double avgRemove =
                    (totalRemove / REPETITIONS) / 1_000_000.0;

            System.out.printf(
                    "%-12d %-20.6f %-20.6f %-20.6f%n",
                    N,
                    avgPut,
                    avgGet,
                    avgRemove
            );
        }
    }
}
