package com.example.sara_ap.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa a la entidad Estudiante dentro del sistema SARA.
 * Hereda los atributos de identidad de la clase abstracta User.
 */
public class Student extends User {
    // Relación de composición/agregación: lista de inscripciones a materias
    private List<Enrollment> enrollments;

    // Constructor que invoca al constructor de la clase padre mediante 'super'
    public Student(String id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email, password);
        this.enrollments = new ArrayList<>();
    }

    // Métodos para gestionar las inscripciones académicas
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void addEnrollment(Enrollment enrollment) { this.enrollments.add(enrollment); }

    /**
     * Sobreescritura polimórfica del método abstracto de la clase padre.
     */
    @Override
    public String getRole() {
        return "Student";
    }
}