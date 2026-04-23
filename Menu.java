import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        AVLTree<Estudiante> avl = new AVLTree<>();
        MinHeap<Estudiante> minHeap = new MinHeap<>();
        int cupos = 0;
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        System.out.println("=== SISTEMA DE GESTIÓN DE RESIDENCIAS UNAL ===");

        while (!salir) {
            System.out.println("\n--- Menú de Opciones ---");
            System.out.println("1. Registrar estudiante en el sistema");
            System.out.println("2. Definir/Actualizar cupos de residencia");
            System.out.println("3. Mostrar listado de estudiantes por PBM");
            System.out.println("4. Asignar cupos según disponibilidad de residencia");
            System.out.println("5. Consultar estado de asignación de un estudiante");
            System.out.println("6. Eliminar estudiante");
            System.out.println("7. Actualizar PBM de estudiante");
            System.out.println("8. Mostrar estudiantes según estado de asignación");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = Integer.parseInt(sc.nextLine().trim());

            switch (opcion) {
                case 1 -> {
                    System.out.print("ID: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine().trim();
                    System.out.print("PBM: ");
                    double pbm = Double.parseDouble(sc.nextLine().trim());

                    Estudiante e = new Estudiante(nombre, id, pbm);
                    avl.insert(e);
                    minHeap.Insert(e);
                    System.out.println("Estudiante registrado correctamente.");
                }

                case 2 -> {
                    System.out.print("Ingrese cantidad de cupos: ");
                    int cuposn = Integer.parseInt(sc.nextLine().trim());
                    if (cuposn < 0) {
                        System.out.println("La cantidad de cupos no puede ser negativa.");
                    } else {
                        cupos = cuposn;
                        System.out.println("Cupos actualizados: " + cupos);
                    }
                }

                case 3 -> {
                    System.out.println("=== LISTADO DE ESTUDIANTES POR PBM ===");
                    MinHeap<Estudiante> copia = minHeap.clonar();
                    if (copia.isEmpty()) {
                        System.out.println("No hay estudiantes registrados.");
                    } else {
                        while (!copia.isEmpty()) {
                            System.out.println(copia.ExtractMin());
                        }
                    }
                }

                case 4 -> {
                    System.out.println("Ejecutando: Asignación prioritaria...");
                    if (cupos == 0) {
                        System.out.println("No hay cupos disponibles.");
                    } else {
                        while (cupos > 0) {
                            Estudiante e = minHeap.ExtractMin();
                            if (e == null) break;
                            e.setTieneResidencia(true);
                            cupos--;
                            System.out.println("Cupo asignado a: " + e.getNombre());
                        }
                    }
                }

                case 5 -> {
                    System.out.print("Ingrese ID: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    Estudiante e = avl.searchById(id);
                    if (e != null) {
                        System.out.println(e);
                    } else {
                        System.out.println("Estudiante no encontrado.");
                    }
                }

                case 6 -> {
                    System.out.print("Ingrese ID: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    Estudiante e = avl.searchById(id);
                    if (e != null) {
                        avl.delete(e);
                        System.out.println("Estudiante eliminado correctamente.");
                    } else {
                        System.out.println("No existe un estudiante con ese ID.");
                    }
                }

                case 7 -> {
                    System.out.print("Ingrese ID: ");
                    long id = Long.parseLong(sc.nextLine().trim());
                    Estudiante e = avl.searchById(id);
                    if (e != null) {
                        System.out.print("Nuevo PBM: ");
                        double nuevoPbm = Double.parseDouble(sc.nextLine().trim());
                        e.setPbm(nuevoPbm);
                        System.out.println("PBM actualizado.");
                    } else {
                        System.out.println("Estudiante no encontrado.");
                    }
                }

                case 8 -> {
                    System.out.println("1. Mostrar asignados");
                    System.out.println("2. Mostrar no asignados");
                    int op = Integer.parseInt(sc.nextLine().trim());
                    if (op == 1) {
                        avl.printByResidency(true);
                    } else if (op == 2) {
                        avl.printByResidency(false);
                    } else {
                        System.out.println("Opción no válida.");
                    }
                }

                case 0 -> {
                    System.out.println("Saliendo del sistema...");
                    salir = true;
                }

                default -> System.out.println("Opción no válida.");
            }
        }
        sc.close();
    }
}
/*import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        // Se elimina la declaración del HashMap
        AVLTree<Estudiante> avl = new AVLTree<>();
        MinHeap<Estudiante> minHeap = new MinHeap<>();
        int cupos = 0;
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        System.out.println("=== SISTEMA DE GESTIÓN DE RESIDENCIAS UNAL ===");

        while (!salir) {
            System.out.println("\n--- Menú de Opciones ---");
            System.out.println("1. Registrar estudiante en el sistema"); 
            System.out.println("2. Definir/Actualizar cupos de residencia");
            System.out.println("3. Mostrar listado de estudiantes (Árbol)");
            System.out.println("4. Asignar cupos según disponibilidad de residencia");
            System.out.println("5. Consultar estado de asignación de un estudiante");
            System.out.println("6. Eliminar estudiante");
            System.out.println("7. Actualizar estudiante");
            System.out.println("8. Mostrar estudiantes según estado de asignación");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = Integer.parseInt(sc.nextLine().trim());

            switch (opcion) {
                case 1 -> {
                    System.out.println("Ejecutando: Registro de datos personales...");
                    System.out.print("ID: ");
                    long id = sc.nextLong();
                    sc.nextLine(); // Limpiar buffer
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();
                    System.out.print("PBM: ");
                    double pbm = sc.nextInt();
            
                    Estudiante e = new Estudiante(nombre, id, pbm);
            
                    // Se inserta en AVL (ordenado por ID) y MinHeap (ordenado por PBM)
                    avl.insert(e);
                    minHeap.Insert(e);
                }
            
                case 2 -> {
                    System.out.println("Ejecutando: Actualización de cupos disponibles...");
                    System.out.print("Ingrese cantidad de cupos: ");
                    int cuposn = sc.nextInt();
                    if (cuposn < 0) {
                        System.out.println("La cantidad de cupos no puede ser negativa.");
                    } else {
                        cupos = cuposn;
                    }
                }
            
                case 3 -> {
                    System.out.println("=== LISTADO DE ESTUDIANTES POR PUNTAJE (PBM) ===");
                    // Creamos una copia para no vaciar el heap principal
                    MinHeap<Estudiante> copiaHeap = minHeap.clonar(); 
    
                    if (copiaHeap.isEmpty()) {
                        System.out.println("No hay estudiantes registrados.");
                    } else {
                        while (!copiaHeap.isEmpty()) {
                            Estudiante e = copiaHeap.ExtractMin();
                            System.out.println(e);
                        }
                    }
                }
                    
            
                case 4 -> {
                    System.out.println("Ejecutando: Asignación prioritaria...");
                    while (cupos > 0) {
                        Estudiante e = minHeap.ExtractMin();
                        if (e == null) break;
                        e.setTieneResidencia(true);
                        cupos--;
                        System.out.println("Cupo asignado a: " + e.getNombre());
                    }
                }
            
                case 5 -> {
                    System.out.println("Ejecutando: Consulta por ID...");
                    System.out.print("Ingrese ID: ");
                    long id = sc.nextLong();
            
                    // Reemplazo de hashMap.get(id) por búsqueda en AVL
                    Estudiante e = avl.searchById(id);
            
                    if (e != null) {
                        System.out.println(e.toString());
                    } else {
                        System.out.println("Estudiante no encontrado.");
                    }
                }
            
                case 6 -> {
                    System.out.println("Ejecutando: Eliminación de estudiante...");
                    System.out.print("Ingrese ID: ");
                    long id = sc.nextLong();
            
                    Estudiante e = avl.searchById(id);
            
                    if (e != null) {
                        avl.delete(e);
                        // Nota: La eliminación en MinHeap requiere el índice o un método remove(T)
                        System.out.println("Estudiante eliminado correctamente.");
                    } else {
                        System.out.println("No existe un estudiante con ese ID.");
                    }
                }
            
                case 7 -> {
                    System.out.println("Ejecutando: Actualización de PBM...");
                    System.out.print("Ingrese ID: ");
                    long id = sc.nextLong();
            
                    Estudiante e = avl.searchById(id);
            
                    if (e != null) {
                        System.out.print("Nuevo PBM: ");
                        double nuevoPbm = sc.nextInt();
                        sc.nextLine();
                        
                        // Para actualizar prioridad en el Heap
                        e.setPbm(nuevoPbm);
                        // Se sugiere re-insertar o usar ChangePriority si se conoce el índice
                        System.out.println("PBM actualizado.");
                    } else {
                        System.out.println("Estudiante no encontrado.");
                    }
                }

                case 8 -> {
                    System.out.println("1. Mostrar asignados");
                    System.out.println("2. Mostrar no asignados");
                    int op = sc.nextInt();
                    if (op == 1) {
                        avl.printByResidency(true);
                    } else if (op == 2) {
                        avl.printByResidency(false);
                    }
                }
            
                case 0 -> {
                    System.out.println("Saliendo del sistema...");
                    salir = true;
                }
            
                default -> System.out.println("Opción no válida.");
            }
        }
        sc.close();
    }
}
*/
