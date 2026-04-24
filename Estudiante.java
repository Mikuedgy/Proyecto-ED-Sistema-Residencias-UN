public class Estudiante implements Comparable<Estudiante> {
    private String nombre;
    private long id;
    private double pbm;
    private boolean tieneResidencia;

    public Estudiante(String nombre, long id, double pbm) {
        this.nombre = nombre;
        this.id = id;
        this.pbm = pbm;
        this.tieneResidencia = false;
    }

    public String getNombre()          { return nombre; }
    public long getId()                { return id; }
    public double getPbm()                { return pbm; }
    public boolean isTieneResidencia() { return tieneResidencia; }
    public boolean getHasResidency()   { return tieneResidencia; } // alias para AVLTree

    public void setPbm(double pbm)        { this.pbm = pbm; }
    public void setTieneResidencia(boolean tieneResidencia) {
        this.tieneResidencia = tieneResidencia;
    }

    @Override
    public int compareTo(Estudiante otro) {
        return Long.compare(this.id, otro.id);
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Estudiante: " + nombre + " | PBM: " + pbm +
               " | Estado: " + (tieneResidencia ? "Asignado" : "No Asignado");
    }
}

