package com.example.sara_ap.domain;

public class TheoryCourse extends Course {

    public TheoryCourse(String code, String name, Professor professor) {
        super(code, name, professor);
    }

    @Override
    public double calculateFinalGrade(double component1, double component2) {
        // Al usar la suma de los 5 aportes guardados en la persistencia,
        // component1 ya contiene (pv + pp) y component2 contiene (ep + t + d).
        double total = component1 + component2;

        // Control de límites elásticos sobre los 100 puntos totales
        if (total > 100.0) return 100.0;
        if (total < 0.0) return 0.0;

        return total;
    }
}