package com.example.sara_ap.services;

import com.example.sara_ap.controller.StudentController.FilaDesgloseEstudiante;
import javafx.collections.ObservableList;

public class PredictiveService {

    public static class PredictionResult {
        public double acumuladoActual;
        public double requeridoExamenB2;
        public String mensaje;
        public String colorHex;
        public boolean enRiesgo;

        public PredictionResult(double acumuladoActual, double requeridoExamenB2, String mensaje, String colorHex, boolean enRiesgo) {
            this.acumuladoActual = acumuladoActual;
            this.requeridoExamenB2 = requeridoExamenB2;
            this.mensaje = mensaje;
            this.colorHex = colorHex;
            this.enRiesgo = enRiesgo;
        }
    }

    /**
     * Procesa analíticamente las notas reales del estudiante extraídas de la tabla elástica.
     */
    public PredictionResult calcularPrediccion(ObservableList<FilaDesgloseEstudiante> desgloses, String materia) {
        double notaB1 = 0.0;
        double notaB2 = 0.0;

        // 🔍 Buscamos la fila del TOTAL ACUMULADO en la lista elástica para extraer las notas verdaderas
        for (FilaDesgloseEstudiante f : desgloses) {
            if (f.getComponente().startsWith("TOTAL")) {
                try {
                    notaB1 = Double.parseDouble(f.getNotaB1().replace(",", "."));
                    notaB2 = Double.parseDouble(f.getNotaB2().replace(",", "."));
                } catch (NumberFormatException ignored) {}
                break;
            }
        }

        // REGLA 1: Evaluación del Primer Bimestre (Si B1 < 16.00 y B2 está en cero, salta alerta de tutoría)
        if (notaB1 < 14.00 && notaB2 == 0.0) {
            double faltaParaDieciseis = 16.00 - notaB1;
            return new PredictionResult(
                    notaB1,
                    faltaParaDieciseis,
                    "⚠️ ALERTA PRIMER BIMESTRE: Tu nota en B1 es de " + String.format("%.2f", notaB1) + " / 20.00 (Menor al umbral de 14.00). Panel de agendamiento de tutorías habilitado para rescate académico.",
                    "#b91c1c",
                    true
            );
        }

        // REGLA 2: Evaluación del Segundo Bimestre / Fin de Ciclo (Acumulado Total sobre 40 puntos)
        double acumuladoTotal40 = notaB1 + notaB2;

        if (acumuladoTotal40 < 28.00) {
            return new PredictionResult(
                    acumuladoTotal40,
                    0.0,
                    "❌ CURSO REPROBADO: Tu acumulado semestral definitivo es de " + String.format("%.2f", acumuladoTotal40) + " / 40.00 pts. No alcanzas el mínimo de 28.00.",
                    "#7f1d1d",
                    false
            );
        }

        // Estado óptimo por defecto para las materias aprobadas
        return new PredictionResult(
                acumuladoTotal40,
                0.0,
                "🎉 ¡CURSO APROBADO! Tu acumulado semestral es de " + String.format("%.2f", acumuladoTotal40) + " / 40.00 pts. Has superado con éxito el mínimo institucional de 28.00.",
                "#15803d",
                false
        );
    }
}