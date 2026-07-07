package com.example.sara_ap.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import com.example.sara_ap.HelloApplication;
import java.io.IOException;

public class StudentController {

    @FXML private Label lblBienvenidaEstudiante;
    @FXML private TableView<FilaNotaEstudiante> tblNotasEstudiante;
    @FXML private TableColumn<FilaNotaEstudiante, String> colMateria;
    @FXML private TableColumn<FilaNotaEstudiante, String> colNota; // 🔑 Sincronizado a String para renderizado elástico formateado
    @FXML private TableColumn<FilaNotaEstudiante, String> colEstado;

    private ObservableList<FilaNotaEstudiante> listaNotas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Enlaces estándar elásticos de propiedades nativas
        if (colMateria != null) colMateria.setCellValueFactory(cd -> cd.getValue().materiaProperty());
        if (colNota != null) colNota.setCellValueFactory(cd -> cd.getValue().notaProperty());
        if (colEstado != null) colEstado.setCellValueFactory(cd -> cd.getValue().estadoProperty());

        if (tblNotasEstudiante != null) {
            tblNotasEstudiante.setItems(listaNotas);
        }

        lblBienvenidaEstudiante.setText("¡Bienvenido a SARA, Estudiante!");
        cargarNotasEjemplo();
    }

    private void cargarNotasEjemplo() {
        listaNotas.clear();
        listaNotas.add(new FilaNotaEstudiante("Estructuras de Datos", 8.5, "Aprobado"));
        listaNotas.add(new FilaNotaEstudiante("Cálculo en Una Variable", 5.8, "Supletorio"));
        listaNotas.add(new FilaNotaEstudiante("Física Clásica", 9.2, "Aprobado"));
    }

    @FXML
    void onLogoutClick(ActionEvent event) {
        try {
            System.out.println("Cerrando sesión del estudiante...");
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 450, 400);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SARA - Sistema de Análisis de Rendimiento Académico");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al regresar a la pantalla principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔑 CLASE INTERNA ADAPTADA A LAS ESPECIFICACIONES NATIVAS DE INYECCIÓN GRÁFICA
    public static class FilaNotaEstudiante {
        private final StringProperty materia = new SimpleStringProperty();
        private final StringProperty nota = new SimpleStringProperty();
        private final StringProperty estado = new SimpleStringProperty();

        public FilaNotaEstudiante(String materia, double nota, String estado) {
            setMateria(materia);
            setNota(String.format("%.2f", nota));
            setEstado(estado);
        }

        public StringProperty materiaProperty() { return materia; }
        public String getMateria() { return materiaProperty().get(); }
        public void setMateria(String materia) { materiaProperty().set(materia); }

        public StringProperty notaProperty() { return nota; }
        public String getNota() { return notaProperty().get(); }
        public void setNota(String nota) { notaProperty().set(nota); }

        public StringProperty estadoProperty() { return estado; }
        public String getEstado() { return estadoProperty().get(); }
        public void setEstado(String estado) { estadoProperty().set(estado); }
    }
}