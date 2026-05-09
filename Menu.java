import java.util.Random;
import java.util.Scanner;

public class Menu {

    static void cargarDatosPrueba(AVLTree<Estudiante> avl, MinHeap<Estudiante> minHeap) {
        Random rand = new Random(42);
        String[] nombres = {
            "Ana", "Luis", "María", "Carlos", "Sofia", "Diego", "Valentina", "Andrés",
            "Isabella", "Santiago", "Camila", "Mateo", "Daniela", "Sebastián", "Mariana",
            "Felipe", "Laura", "Nicolás", "Paula", "Alejandro"
        };
        String[] apellidos = {
            "Torres", "García", "López", "Ruiz", "Méndez", "Herrera", "Cruz", "Mora",
            "Díaz", "Reyes", "Vargas", "Castillo", "Romero", "Jiménez", "Morales",
            "Suárez", "Ramírez", "Flores", "Núñez", "Vega"
        };

        for (int i = 0; i < 50; i++) {
            long id = 1000 + i;
            String nombre = nombres[rand.nextInt(nombres.length)] + " " + apellidos[rand.nextInt(apellidos.length)];
            double pbm = Math.round((rand.nextDouble() * 20) * 10.0) / 10.0;
            Estudiante e = new Estudiante(nombre, id, pbm);
            avl.insert(e);
            minHeap.Insert(e);
        }
        System.out.println("  50 estudiantes de prueba cargados.\n");
    }

    public static void main(String[] args) {
        AVLTree<Estudiante> avl = new AVLTree<>();
        MinHeap<Estudiante> minHeap = new MinHeap<>();
        int cupos = 0;
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        cargarDatosPrueba(avl, minHeap);

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  ____  _____ ____ ___ ____  _____ _   _  ____ ___ _    ___   ║");
        System.out.println("║ |  _ \\| ____/ ___|_ _|  _ \\| ____| \\ | |/ ___|_ _/ \\  / __|  ║");
        System.out.println("║ | |_) |  _| \\___ \\| || | | |  _| |  \\| | |    | / _ \\ \\__ \\  ║");
        System.out.println("║ |  _ <| |___ ___) | || |_| | |___| |\\  | |___ |/ ___ \\ ___/  ║");
        System.out.println("║ |_| \\_\\_____|____/___|____/|_____|_| \\_|\\____/_/   \\_\\|___/  ║");
        System.out.println("║                                                              ║");
        System.out.println("║         Gestión de cupos por puntaje PBM - UNAL              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║           MENÚ DE OPCIONES                   ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1. Registrar estudiante                     ║");
            System.out.println("║  2. Definir cupos                            ║");
            System.out.println("║  3. Asignar cupos                            ║");
            System.out.println("║  4. Consultar estudiante                     ║");
            System.out.println("║  5. Eliminar estudiante                      ║");
            System.out.println("║  6. Actualizar PBM de estudiante             ║");
            System.out.println("║  7. Listar no asignados por PBM              ║");
            System.out.println("║  8. Listar asignados por PBM                 ║");
            System.out.println("║  0. Salir                                    ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  !! Entrada inválida. Ingrese un número.");
                continue;
            }

            switch (opcion) {
                case 1 -> {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║         REGISTRAR ESTUDIANTE                 ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    System.out.print("  ID: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    if (avl.searchById(id) != null) {
                        System.out.println("  !! Ya existe un estudiante con ese ID.");
                    } else {
                        System.out.print("  Nombre: ");
                        String nombre = sc.nextLine().trim();
                        System.out.print("  PBM: ");
                        double pbm = Double.parseDouble(sc.nextLine().trim());
                        if (pbm < 0 || pbm > 100) {
                            System.out.println("  !! El PBM debe estar entre 0 y 100.");
                        } else {
                        Estudiante e = new Estudiante(nombre, id, pbm);
                        avl.insert(e);
                        minHeap.Insert(e);
                        System.out.println("  !! Estudiante registrado correctamente.");
                        }
                    }
                }

                case 2 -> {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║         ACTUALIZAR CUPOS                     ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    System.out.print("  Ingrese cantidad de cupos: ");
                    int cuposn = Integer.parseInt(sc.nextLine().trim());
                    if (cuposn < 0) {
                        System.out.println("  !! La cantidad de cupos no puede ser negativa.");
                    } else {
                        cupos = cuposn;
                        System.out.println("  !! Cupos actualizados: " + cupos);
                    }
                }

                case 3 -> {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║         ASIGNACIÓN PRIORITARIA               ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    if (cupos == 0) {
                            System.out.println("  !! No hay cupos disponibles.");
                        } else {
                            int asignados = 0;
                            int totalEnHeap = minHeap.getSize(); // ← tamaño real del heap
                            Estudiante[] temp = new Estudiante[totalEnHeap]; // ← cambio aquí
                            int tempSize = 0;

                            while (cupos > 0) {
                                Estudiante e = minHeap.ExtractMin();
                                if (e == null) break;
                                temp[tempSize++] = e;
                                if (!e.getHasResidency()) {
                                    e.setTieneResidencia(true);
                                    cupos--;
                                    asignados++;
                                    System.out.println("  !! Cupo asignado a: " + e.getNombre() + " (PBM: " + e.getPbm() + ")");
                                }
                            }
                            for (int i = 0; i < tempSize; i++) minHeap.Insert(temp[i]);
                            if (asignados == 0) System.out.println("  !! No hay más estudiantes sin residencia.");
                        }
                }

                case 4 -> {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║         CONSULTAR ESTUDIANTE                 ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    System.out.print("  Ingrese ID: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    Estudiante e = avl.searchById(id);
                    if (e != null) {
                        System.out.println("  " + e);
                    } else {
                        System.out.println("  !! Estudiante no encontrado.");
                    }
                }

                case 5 -> {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║         ELIMINAR ESTUDIANTE                  ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    System.out.print("  Ingrese ID: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    Estudiante e = avl.searchById(id);
                    if (e != null) {
                        avl.delete(e);
                        minHeap.removeByEstudiante(e);
                        System.out.println("  !! Estudiante eliminado correctamente.");
                    } else {
                        System.out.println("  !! No existe un estudiante con ese ID.");
                    }
                }

                case 6 -> {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║         ACTUALIZAR PBM                       ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    System.out.print("  Ingrese ID: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    Estudiante e = avl.searchById(id);
                    if (e != null) {
                        System.out.print("  Nuevo PBM: ");
                        double nuevoPbm = Double.parseDouble(sc.nextLine().trim());
                        if (nuevoPbm < 0 || nuevoPbm > 100) {
                            System.out.println("  !! El PBM debe estar entre 0 y 100.");
                        } else {
                        minHeap.removeByEstudiante(e);
                        e.setPbm(nuevoPbm);
                        minHeap.Insert(e);
                        System.out.println("  !! PBM actualizado a: " + nuevoPbm);
                        }
                    } else {
                        System.out.println("  !! Estudiante no encontrado.");
                    }
                }

                case 7 -> {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║     ESTUDIANTES NO ASIGNADOS POR PBM         ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    MinHeap<Estudiante> copia = minHeap.clonar();
                    boolean hayNoAsignados = false;
                    while (!copia.isEmpty()) {
                        Estudiante e = copia.ExtractMin();
                        if (!e.getHasResidency()) {
                            System.out.println("  " + e);
                            hayNoAsignados = true;
                        }
                    }
                    if (!hayNoAsignados) {
                        System.out.println("  Todos los estudiantes tienen residencia asignada.");
                    }
                }

                case 8 -> {
                    System.out.println("\n╔══════════════════════════════════════════════╗");
                    System.out.println("║       ESTUDIANTES ASIGNADOS POR PBM          ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    MinHeap<Estudiante> copia = minHeap.clonar();
                    boolean hayAsignados = false;
                    while (!copia.isEmpty()) {
                        Estudiante e = copia.ExtractMin();
                        if (e.getHasResidency()) {
                            System.out.println("  " + e);
                            hayAsignados = true;
                        }
                    }
                    if (!hayAsignados) {
                        System.out.println("  No hay estudiantes con residencia asignada.");
                    }
                }

                case 0 -> {
                    System.out.println("\n  Saliendo del sistema...");
                    System.out.println("╔══════════════════════════════════════════════╗");
                    System.out.println("║            ¡Hasta luego!                     ║");
                    System.out.println("╚══════════════════════════════════════════════╝");
                    salir = true;
                }

                default -> System.out.println("  !! Opción no válida.");
            }
        }
        sc.close();
    }
}
