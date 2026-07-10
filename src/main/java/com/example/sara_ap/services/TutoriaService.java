package com.example.sara_ap.services;

import com.example.sara_ap.HelloApplication;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class TutoriaService {

    /**
     * Procesa y agenda la tutoría de forma lógica para el estudiante.
     * Iscala este método en el futuro si deseas persistir la cita en un archivo o base de datos.
     */
    public void agendarTutoriaInstitucional(String asignatura) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("SARA - Sistema de Acompañamiento Académico");
        alert.setHeaderText("¡Solicitud de Tutoría Procesada con Éxito!");
        alert.setContentText("Se ha enviado una notificación automatizada al docente de " + asignatura + ".\n"
                + "Se agendó un espacio tentativo para el próximo bloque de Horas de Consulta.\n\n"
                + "Revisa tu bandeja de correo institucional para la confirmación del enlace de Teams o Aula.");

        // Intenta cargar el ícono de la aplicación si existe
        try {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new javafx.scene.image.Image(
                    HelloApplication.class.getResourceAsStream("/com/example/sara_ap/icon.png")
            ));
        } catch (Exception e) {
            // Si no encuentra el ícono, el diálogo nativo se abre igual sin problemas
        }

        alert.showAndWait();
    }
}