package com.example.sara_ap.controller;

import com.example.sara_ap.domain.Professor;
import com.example.sara_ap.HelloApplication;
import com.example.sara_ap.domain.Student;
import com.example.sara_ap.domain.Enrollment;
import com.example.sara_ap.domain.Course;
import com.example.sara_ap.infrastructure.CSVDataPersistence;
import com.example.sara_ap.services.StatisticalAnalyzer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeacherController {

    @FXML private Label lblBienvenida;
    @FXML private Label lblAlertaDesviacion;
    @FXML private ComboBox<String> cmbCursos;

    @FXML private TableView<StudentRow> tblNotas;
    @FXML private TableColumn<StudentRow, String> colId;
    @FXML private TableColumn<StudentRow, String> colNombre;
    @FXML private TableColumn<StudentRow, String> colNota;
    @FXML private TableColumn<StudentRow, String> colEstado;

    private Professor professorLogueado;
    private CSVDataPersistence persistencia = new CSVDataPersistence();
    private List<Course> cursosDisponibles = new ArrayList<>();
    private List<Student> todosLosEstudiantes = new ArrayList<>();

    // Se ejecuta automáticamente al entrar a la pantalla
    public void initData(Professor professor) {
        this.professorLogueado = professor;
        lblBienvenida.setText("👨‍🏫 Panel Docente - " + professor.getFirstName() + " " + professor.getLastName());

        // 1. Cargar datos desde tus CSV reales
        todosLosEstudiantes = persistencia.loadFullSystemData(cursosDisponibles);

        // 2. Filtrar y mostrar en el ComboBox únicamente las materias que dicta ESTE profesor
        cmbCursos.getItems().clear();
        for (Course c : cursosDisponibles) {
// 🔑 CORREGIDO: Usamos c.getProfessor() en lugar de c.getTenuredProfessor()
            if (c.getProfessor() != null && c.getProfessor().getId().trim().equals(professor.getId().trim())) {                cmbCursos.getItems().add(c.getCode() + " - " + c.getName());
            }
        }

        // Configurar cómo se van a mapear las columnas de la tabla
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colNota.setCellValueFactory(cellData -> cellData.getValue().notaProperty());
        colEstado.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
    }

    // Se activa cada vez que el docente cambia de materia en el ComboBox
    @FXML
    protected void onCursoSeleccionado() {
        String seleccion = cmbCursos.getValue();
        if (seleccion == null) return;

        String codigoCurso = seleccion.split(" - ")[0].trim();
        ObservableList<StudentRow> filasTabla = FXCollections.observableArrayList();
        List<Double> notasDelCurso = new ArrayList<>();

        // 1. Recorrer estudiantes buscando quiénes están inscritos en esta materia
        for (Student s : todosLosEstudiantes) {
            if (s.getEnrollments() != null) {
                for (Enrollment e : s.getEnrollments()) {
                    if (e.getCourse().getCode().trim().equalsIgnoreCase(codigoCurso)) {

                        // Calculamos la nota polimórfica llamando a la lógica heredada del curso
                        double notaFinal = e.getCourse().calculateFinalGrade(e.getComponent1(), e.getComponent2());
                        notasDelCurso.add(notaFinal);

                        // Evaluamos el estado de riesgo predictivo
                        String estado = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(notaFinal);

                        // Agregamos la fila a la lista visual
                        filasTabla.add(new StudentRow(s.getId(), s.getFirstName() + " " + s.getLastName(), String.format("%.2f", notaFinal), estado));
                    }
                }
            }
        }

        tblNotas.setItems(filasTabla);

        // 2. 📊 ¡ANÁLISIS DE DESVIACIÓN ESTÁNDAR EN CALIENTE!
        double desviacion = StatisticalAnalyzer.calculateStandardDeviation(notasDelCurso);

        // Si la dispersión es muy alta (ej. mayor a 1.5), encendemos la alarma en la interfaz
        if (desviacion > 1.5) {
            lblAlertaDesviacion.setText("⚠️ ALERTA DE DESVIACIÓN: Las notas de este curso están muy dispersas (Desviación: " + String.format("%.2f", desviacion) + "). Revisar casos individuales.");
        } else {
            lblAlertaDesviacion.setText("✅ Rendimiento del curso estable (Desviación: " + String.format("%.2f", desviacion) + ").");
        }
    }

    @FXML
    protected void onLogoutClick() throws IOException {
        Stage stage = (Stage) lblBienvenida.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 400);
        stage.setScene(scene);
    }

    // 💡 Clase interna necesaria para representar de forma limpia las filas de las tablas en JavaFX
    public static class StudentRow {
        private final SimpleStringProperty id;
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty nota;
        private final SimpleStringProperty estado;

        public StudentRow(String id, String nombre, String nota, String estado) {
            this.id = new SimpleStringProperty(id);
            this.nombre = new SimpleStringProperty(nombre);
            this.nota = new SimpleStringProperty(nota);
            this.estado = new SimpleStringProperty(estado);
        }

        public SimpleStringProperty idProperty() { return id; }
        public SimpleStringProperty nombreProperty() { return nombre; }
        public SimpleStringProperty notaProperty() { return nota; }
        public SimpleStringProperty estadoProperty() { return estado; }
    }
}