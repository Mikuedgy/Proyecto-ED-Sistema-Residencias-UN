# Sistema de Gestión de Residencias Universitarias - UNAL

## Descripción

La Universidad Nacional ofrece el servicio de residencias universitarias destinadas al apoyo y bienestar de los estudiantes más vulnerables. Dado que los cupos son limitados y la demanda es alta, se desarrolló un sistema de gestión eficiente y justo basado en el puntaje PBM (indicador socioeconómico). Entre menor sea el puntaje PBM de un estudiante, mayor es su prioridad para obtener un cupo.

El sistema permite registrar, consultar, modificar y eliminar estudiantes, así como asignar cupos automáticamente según criterios de prioridad socioeconómica.

## Integrantes

- Santiago Villalobos Castelblanco
- Silvana Ramírez Ardila
- Darwin Josué Ojeda Torres
- Sofía Franco Segura
- Julián Gutiérrez

## Lenguajes y herramientas

- Java 21
- Control de versiones: Git
- Repositorio: GitHub

## Estructuras de datos implementadas

El sistema utiliza dos estructuras de datos implementadas manualmente, sin librerías externas:

- **AVLTree** — árbol AVL genérico ordenado por ID del estudiante. Permite registrar, buscar, eliminar y actualizar estudiantes en O(log n).
- **MinHeap** — montículo mínimo ordenado por puntaje PBM. Permite asignar cupos siempre al estudiante con mayor prioridad (menor PBM) en O(log n).

Cada estudiante se almacena simultáneamente en ambas estructuras. El AVLTree garantiza búsquedas eficientes por ID, mientras que el MinHeap garantiza asignaciones eficientes por PBM.

## Funcionalidades del sistema

| Opción | Descripción |
|--------|-------------|
| 1 | Registrar estudiante |
| 2 | Definir cupos disponibles |
| 3 | Asignar cupos por prioridad PBM |
| 4 | Consultar estudiante por ID |
| 5 | Eliminar estudiante |
| 6 | Actualizar PBM de estudiante |
| 7 | Listar estudiantes no asignados por PBM |
| 8 | Listar estudiantes asignados por PBM |
| 0 | Salir |

## Estructura del proyecto

```
Proyecto-ED-Sistema-Residencias-UN/
│
├── README.md
└── src/
    ├── Estudiante.java      # Clase modelo del estudiante
    ├── AVLTree.java         # Árbol AVL implementado manualmente
    ├── MinHeap.java         # MinHeap implementado manualmente
    ├── Menu.java            # Menú principal del sistema
    └── Benchmark.java       # Medición de tiempos de ejecución
```

## Guía de instalación

1. Clonar el repositorio:
```bash
git clone https://github.com/Mikuedgy/Proyecto-ED-Sistema-Residencias-UN.git
```

2. Ejecutar el sistema:
```bash
java Menu
```

## Notas

- Al iniciar el sistema se cargan automáticamente 50 estudiantes de prueba con datos aleatorios (IDs 1000–1049).
- Se requiere Java 21 o superior para compilar y ejecutar el proyecto.
