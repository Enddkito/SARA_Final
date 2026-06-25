package com.example.sara_ap.domain;

public class TheoryCourse extends Course {

    public TheoryCourse(String code, String name, Professor professor) {
        super(code, name, professor); // Pasamos también los 3 datos aquí
    }

    /**
     * Implementación polimórfica para cursos teóricos (50% / 50%)
     */
    @Override
    public double calculateFinalGrade(double component1, double component2) {
        return (component1 * 0.50) + (component2 * 0.50);
    }
}