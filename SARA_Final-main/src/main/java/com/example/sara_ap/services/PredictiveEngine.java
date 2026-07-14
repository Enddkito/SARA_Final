package com.example.sara_ap.services;

public class PredictiveEngine {

    /**
     * Evalúa la nota final y retorna una categoría excluyente de riesgo académico.
     * Criterios: Aprobado (>= 7.5), Zona Media (5.5 - 7.4) y Riesgo (< 5.5)
     */
    public static String evaluateRisk(double finalGrade) {
        if (finalGrade >= 7.5) {
            return "Aprobado";
        } else if (finalGrade >= 5.5) {
            return "Zona Media";
        } else {
            return "Riesgo Académico";
        }
    }
}