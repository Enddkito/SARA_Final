package com.example.sara_ap.services;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TutoriaDocumentService {

    // Ruta atómica al recurso compartido del sistema
    private static final String FILE_PATH = "src/main/resources/com/example/sara_ap/tutoring_appointments.csv";

    public void procesarAgendamientoFormal(String estudiante, String asignatura, LocalDate fecha, String horario) {
        if (fecha == null || horario == null || horario.isEmpty()) {
            Alert alertError = new Alert(Alert.AlertType.WARNING);
            alertError.setTitle("SARA - Validación");
            alertError.setHeaderText("Campos Incompletos");
            alertError.setContentText("Por favor, selecciona una fecha y un horario válido para agendar la tutoría.");
            alertError.showAndWait();
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateada = fecha.format(formatter);

        // 💾 PERSISTENCIA REAL: Escribe una nueva línea al final del archivo CSV sin borrar lo anterior (append = true)
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {

            // Guarda el registro con formato estándar delimitado por comas
            pw.println(String.format("%s,%s,%s,%s,Pendiente", estudiante, asignatura, fechaFormateada, horario));
            System.out.println("💾 [SARA Sync] Tutoría guardada con éxito en el canal local.");

        } catch (IOException e) {
            System.err.println("Error crítico al guardar la tutoría en el repositorio CSV: " + e.getMessage());
        }

        // Pop-up con diseño institucional elástico para el Estudiante
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SARA - Control de Acompañamiento");
        alert.setHeaderText("✨ ¡Tutoría de Rescate Académico Agendada! ✨");

        String cuerpoMensaje = String.format(
                "Se ha generado con éxito el Formulario Digital de Compromiso Académico.\n\n" +
                        "📌 DETALLES DEL AGENDAMIENTO REAL:\n" +
                        "• Estudiante: %s\n" +
                        "• Asignatura: %s\n" +
                        "• Fecha Asignada: %s\n" +
                        "• Horario: %s\n" +
                        "• Modalidad: Presencial / Cubículo del Docente\n\n" +
                        "📧 INTERCONEXIÓN LOCAL: El registro se guardó en el historial compartido. " +
                        "El profesor podrá visualizar este requerimiento en tiempo real al ingresar a su panel de control.",
                estudiante, asignatura, fechaFormateada, horario
        );

        alert.setContentText(cuerpoMensaje);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");

        alert.showAndWait();
    }
    public void registrarTutoriaObligatoria(String estudianteId, String asignatura, LocalDate fecha, String horario) throws java.io.IOException {
        if (fecha == null || horario == null || horario.isEmpty()) {
            throw new IllegalArgumentException("Campos de fecha u horario incompletos.");
        }

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateada = fecha.format(formatter);

        // Reutilizamos la lógica del archivo dinámico o el PATH_PREFIX que tengas configurado
        String filePath = "src/main/resources/com/example/sara_ap/tutoring_appointments.csv";

        // Guardamos con el estado "Obligatoria"
        try (java.io.FileWriter fw = new java.io.FileWriter(filePath, true);
             java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {

            pw.println(String.format("%s,%s,%s,%s,Obligatoria", estudianteId, asignatura, fechaFormateada, horario));
            System.out.println("💾 [SARA Sync] Tutoría obligatoria persistida en el repositorio.");
        }
    }
}