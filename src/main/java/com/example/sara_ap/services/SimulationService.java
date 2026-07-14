package com.example.sara_ap.services;

import com.example.sara_ap.controller.StudentController.FilaDesgloseEstudiante;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class SimulationService {

    private final PredictiveService predictiveService = new PredictiveService();

    /**
     * Recalcula en caliente los totales parciales basándose únicamente en los datos de la RAM
     * modificados por el alumno, actualizando la fila TOTAL y las etiquetas predictivas de la pestaña.
     */
    public void ejecutarRecalculoSimulado(
            ObservableList<FilaDesgloseEstudiante> listaSimulada,
            TableView<FilaDesgloseEstudiante> tabla,
            String materiaActiva,
            Label lblAcumulado,
            Label lblMensaje
    ) {
        if (listaSimulada == null || listaSimulada.isEmpty()) return;

        double nuevoTotalB1 = 0.0;
        double nuevoTotalB2 = 0.0;
        FilaDesgloseEstudiante filaTotal = null;

        // 1. Recorrer los aportes editados por el estudiante ignorando la fila final
        for (FilaDesgloseEstudiante f : listaSimulada) {
            if (!f.getComponente().startsWith("TOTAL")) {
                try {
                    nuevoTotalB1 += Double.parseDouble(f.getNotaB1().replace(",", "."));
                    nuevoTotalB2 += Double.parseDouble(f.getNotaB2().replace(",", "."));
                } catch (NumberFormatException ignored) {}
            } else {
                filaTotal = f;
            }
        }

        // 2. Modificar visualmente la celda acumulada de la tabla simulada
        if (filaTotal != null) {
            filaTotal.setNotaB1(String.format("%.2f", nuevoTotalB1));
            filaTotal.setNotaB2(String.format("%.2f", nuevoTotalB2));
            tabla.refresh();
        }

        // 3. Procesar el nuevo escenario simulado a través del motor predictivo global
        PredictiveService.PredictionResult res = predictiveService.calcularPrediccion(listaSimulada, materiaActiva);

        // 4. Actualizar las etiquetas exclusivas del panel de simulación en el frontend
        if (lblAcumulado != null) {
            lblAcumulado.setText(String.format("%.2f / 40.00 (Simulado)", res.acumuladoActual));
        }

        if (lblMensaje != null) {
            lblMensaje.setText("[Escenario Hipotético] " + res.mensaje);
            lblMensaje.setStyle("-fx-text-fill: " + res.colorHex + "; -fx-font-weight: bold;");
        }
    }
}