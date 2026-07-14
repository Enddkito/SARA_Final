package com.example.sara_ap.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatisticalAnalyzer {

    /**
     * Computa el promedio aritmético (media) del grupo.
     * [cite: 161]
     */
    public static double calculateMean(List<Double> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;

        double sum = 0.0;
        for (double grade : grades) {
            sum += grade;
        }
        return sum / grades.size();
    }

    /**
     * Identifica el punto medio de la distribución de notas (Mediana).
     * [cite: 162, 163]
     */
    public static double calculateMedian(List<Double> grades) {
        if (grades == null || grades.isEmpty()) return 0.0;

        // Clonamos la lista para no alterar el orden original de las notas del curso
        List<Double> sortedGrades = new ArrayList<>(grades);
        Collections.sort(sortedGrades);

        int size = sortedGrades.size();
        if (size % 2 != 0) {
            // Si es impar, es el elemento del centro
            return sortedGrades.get(size / 2);
        } else {
            // Si es par, es el promedio de los dos elementos centrales
            double mid1 = sortedGrades.get((size / 2) - 1);
            double mid2 = sortedGrades.get(size / 2);
            return (mid1 + mid2) / 2.0;
        }
    }

    /**
     * Evalúa la dispersión de las calificaciones respecto a la media (Desviación Estándar).
     * [cite: 164]
     */
    public static double calculateStandardDeviation(List<Double> grades) {
        if (grades == null || grades.size() <= 1) return 0.0;

        double mean = calculateMean(grades);
        double sumOfSquaredDifferences = 0.0;

        for (double grade : grades) {
            sumOfSquaredDifferences += Math.pow(grade - mean, 2);
        }

        // Usamos la desviación estándar poblacional o muestral (aquí muestral N-1)
        return Math.sqrt(sumOfSquaredDifferences / (grades.size() - 1));
    }
}