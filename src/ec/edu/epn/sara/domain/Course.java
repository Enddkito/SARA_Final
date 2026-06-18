package ec.edu.epn.sara.domain;

/**
 * Clase abstracta que define la estructura base de una asignatura en SARA.
 * Sirve como contrato para implementar Polimorfismo en el cálculo de notas.
 */
public abstract class Course {
    private String code;
    private String name;

    public Course(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    /**
     * Método polimórfico abstracto.
     * Cada tipo de curso (Teórico o Laboratorio) implementará su propia ponderación matemática.
     */
    public abstract double calculateFinalGrade(double component1, double component2);
}