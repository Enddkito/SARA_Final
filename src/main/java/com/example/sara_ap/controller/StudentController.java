package com.example.sara_ap.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent; // 👈 Asegúrate de tener este import
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.example.sara_ap.HelloApplication;
import java.io.IOException;

public class StudentController {

    @FXML private Label lblBienvenidaEstudiante;
    @FXML private TableView<FilaNotaEstudiante> tblNotasEstudiante;
    @FXML private TableColumn<FilaNotaEstudiante, String> colMateria;
    @FXML private TableColumn<FilaNotaEstudiante, Double> colNota;
    @FXML private TableColumn<FilaNotaEstudiante, String> colEstado;

    private ObservableList<FilaNotaEstudiante> listaNotas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMateria.setCellValueFactory(new PropertyValueFactory<>("materia"));
        colNota.setCellValueFactory(new PropertyValueFactory<>("nota"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tblNotasEstudiante.setItems(listaNotas);
        lblBienvenidaEstudiante.setText("¡Bienvenido a SARA, Estudiante!");
        cargarNotasEjemplo();
    }

    private void cargarNotasEjemplo() {
        listaNotas.add(new FilaNotaEstudiante("Estructuras de Datos", 8.5, "Aprobado"));
        listaNotas.add(new FilaNotaEstudiante("Cálculo en Una Variable", 5.8, "Supletorio"));
        listaNotas.add(new FilaNotaEstudiante("Física Clásica", 9.2, "Aprobado"));
    }

    // 🔴 FUNCIONALIDAD CORREGIDA: Este método ahora sí saca al alumno y lo regresa al Login principal
    @FXML
    void onLogoutClick(ActionEvent event) {
        try {
            System.out.println("Cerrando sesión del estudiante...");

            // 1. Cargar el menú de bienvenida general
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 450, 400);

            // 2. Obtener la ventana actual a través del botón que disparó el evento
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // 3. Reemplazar la escena y centrar la ventana
            stage.setScene(scene);
            stage.setTitle("SARA - Sistema de Análisis de Rendimiento Académico");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Error al regresar a la pantalla principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class FilaNotaEstudiante {
        private final String materia;
        private final double nota;
        private final String estado;

        public FilaNotaEstudiante(String materia, double nota, String estado) {
            this.materia = materia;
            this.nota = nota;
            this.estado = estado;
        }

        public String getMateria() { return materia; }
        public double getNota() { return nota; }
        public String getEstado() { return estado; }
    }
}