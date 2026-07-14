package com.example.sara_ap.domain;

/**
 * Clase abstracta que define la estructura base de una asignatura en SARA.
 * Sirve como contrato para implementar Polimorfismo en el cálculo de notas.
 */
public abstract class Course {
    private String code;       // Código único de la materia (ej. POO-01)
    private String name;       // Nombre de la asignatura
    private Professor professor; // Objeto Profesor completo asociado por ID

    public Course(String code, String name, Professor professor) {
        this.code = code;
        this.name = name;
        this.professor = professor;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }

    /**
     * Método polimórfico abstracto.
     * Cada tipo de curso (Teórico o Laboratorio) implementará su propia ponderación matemática.
     */
    public abstract double calculateFinalGrade(double component1, double component2);

    /**
     * Representación en formato de texto de la asignatura.
     */
    @Override
    public String toString() {
        return "Curso: " + name + " [" + code + "] -> Dictado por: " + (professor != null ? professor.toString() : "Sin asignar");
    }
}