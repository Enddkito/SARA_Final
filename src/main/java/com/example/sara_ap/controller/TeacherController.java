package com.example.sara_ap.controller;

import com.example.sara_ap.domain.Professor;
import com.example.sara_ap.HelloApplication;
import com.example.sara_ap.domain.Student;
import com.example.sara_ap.domain.Enrollment;
import com.example.sara_ap.domain.Course;
import com.example.sara_ap.infrastructure.CSVDataPersistence;
import com.example.sara_ap.services.StatisticalAnalyzer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    // 📋 TABLA PRIMER BIMESTRE
    @FXML private TableView<StudentRow> tblNotasB1;
    @FXML private TableColumn<StudentRow, String> colIdB1;
    @FXML private TableColumn<StudentRow, String> colNombreB1;
    @FXML private TableColumn<StudentRow, String> colPVirtualB1;
    @FXML private TableColumn<StudentRow, String> colPPresencialB1;
    @FXML private TableColumn<StudentRow, String> colExamenB1;
    @FXML private TableColumn<StudentRow, String> colTalleresB1;
    @FXML private TableColumn<StudentRow, String> colDeberesB1;
    @FXML private TableColumn<StudentRow, String> colNotaB1;
    @FXML private TableColumn<StudentRow, String> colEstadoB1;

    // 📋 TABLA SEGUNDO BIMESTRE
    @FXML private TableView<StudentRow> tblNotasB2;
    @FXML private TableColumn<StudentRow, String> colIdB2;
    @FXML private TableColumn<StudentRow, String> colNombreB2;
    @FXML private TableColumn<StudentRow, String> colPVirtualB2;
    @FXML private TableColumn<StudentRow, String> colPPresencialB2;
    @FXML private TableColumn<StudentRow, String> colExamenB2;
    @FXML private TableColumn<StudentRow, String> colTalleresB2;
    @FXML private TableColumn<StudentRow, String> colDeberesB2;
    @FXML private TableColumn<StudentRow, String> colNotaB2;
    @FXML private TableColumn<StudentRow, String> colEstadoB2;

    private Professor professorLogueado;
    private CSVDataPersistence persistencia = new CSVDataPersistence();
    private List<Course> cursosDisponibles = new ArrayList<>();
    private List<Student> todosLosEstudiantes = new ArrayList<>();

    public void initData(Professor professor) {
        if (professor == null) return;
        this.professorLogueado = professor;
        lblBienvenida.setText("👨‍🏫 Panel Docente - " + professor.getFirstName().trim() + " " + professor.getLastName().trim());

        // 1. Cargar datos frescos
        cursosDisponibles.clear();
        todosLosEstudiantes = persistencia.loadFullSystemData(cursosDisponibles);

        // 2. Poblar ComboBox
        cmbCursos.getItems().clear();
        String idProfesorBuscar = professor.getId().trim();

        for (Course c : cursosDisponibles) {
            if (c.getProfessor() != null) {
                if (c.getProfessor().getId().trim().equalsIgnoreCase(idProfesorBuscar)) {
                    cmbCursos.getItems().add(c.getCode().trim() + " - " + c.getName().trim());
                }
            }
        }

        // 🔗 ENLACES ESTÁNDAR DE PROPIEDADES (BIMESTRE 1)
        if (colIdB1 != null) colIdB1.setCellValueFactory(cd -> cd.getValue().idProperty());
        if (colNombreB1 != null) colNombreB1.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        if (colPVirtualB1 != null) colPVirtualB1.setCellValueFactory(cd -> cd.getValue().pVirtualProperty());
        if (colPPresencialB1 != null) colPPresencialB1.setCellValueFactory(cd -> cd.getValue().pPresencialProperty());
        if (colExamenB1 != null) colExamenB1.setCellValueFactory(cd -> cd.getValue().examenProperty());
        if (colTalleresB1 != null) colTalleresB1.setCellValueFactory(cd -> cd.getValue().talleresProperty());
        if (colDeberesB1 != null) colDeberesB1.setCellValueFactory(cd -> cd.getValue().deberesProperty());
        if (colNotaB1 != null) colNotaB1.setCellValueFactory(cd -> cd.getValue().notaProperty());
        if (colEstadoB1 != null) colEstadoB1.setCellValueFactory(cd -> cd.getValue().estadoProperty());

        // 🔗 ENLACES ESTÁNDAR DE PROPIEDADES (BIMESTRE 2)
        if (colIdB2 != null) colIdB2.setCellValueFactory(cd -> cd.getValue().idProperty());
        if (colNombreB2 != null) colNombreB2.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        if (colPVirtualB2 != null) colPVirtualB2.setCellValueFactory(cd -> cd.getValue().pVirtualProperty());
        if (colPPresencialB2 != null) colPPresencialB2.setCellValueFactory(cd -> cd.getValue().pPresencialProperty());
        if (colExamenB2 != null) colExamenB2.setCellValueFactory(cd -> cd.getValue().examenProperty());
        if (colTalleresB2 != null) colTalleresB2.setCellValueFactory(cd -> cd.getValue().talleresProperty());
        if (colDeberesB2 != null) colDeberesB2.setCellValueFactory(cd -> cd.getValue().deberesProperty());
        if (colNotaB2 != null) colNotaB2.setCellValueFactory(cd -> cd.getValue().notaProperty());
        if (colEstadoB2 != null) colEstadoB2.setCellValueFactory(cd -> cd.getValue().estadoProperty());
    }

    @FXML
    protected void onCursoSeleccionado() {
        String seleccion = cmbCursos.getValue();
        if (seleccion == null) return;

        String codigoCurso = seleccion.split(" - ")[0].trim();
        ObservableList<StudentRow> filasB1 = FXCollections.observableArrayList();
        ObservableList<StudentRow> filasB2 = FXCollections.observableArrayList();
        List<Double> notasTotalesDelCurso = new ArrayList<>();

        for (Student s : todosLosEstudiantes) {
            if (s.getEnrollments() != null) {
                for (Enrollment e : s.getEnrollments()) {
                    if (e.getCourse().getCode().trim().equalsIgnoreCase(codigoCurso)) {

                        // Procesar Bimestre 1
                        double[] b1 = e.getNotasBimestre1();
                        double totalB1 = e.getComponent1();
                        String estadoB1 = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(totalB1);

                        filasB1.add(new StudentRow(
                                s.getId(), s.getFirstName() + " " + s.getLastName(),
                                String.format("%.2f", b1[0]), String.format("%.2f", b1[1]),
                                String.format("%.2f", b1[2]), String.format("%.2f", b1[3]),
                                String.format("%.2f", b1[4]), String.format("%.2f", totalB1), estadoB1
                        ));

                        // Procesar Bimestre 2
                        double[] b2 = e.getNotasBimestre2();
                        double totalB2 = e.getComponent2();
                        String estadoB2 = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(totalB2);

                        filasB2.add(new StudentRow(
                                s.getId(), s.getFirstName() + " " + s.getLastName(),
                                String.format("%.2f", b2[0]), String.format("%.2f", b2[1]),
                                String.format("%.2f", b2[2]), String.format("%.2f", b2[3]),
                                String.format("%.2f", b2[4]), String.format("%.2f", totalB2), estadoB2
                        ));

                        double notaFinalSemestre = e.getCourse().calculateFinalGrade(e.getComponent1(), e.getComponent2());
                        notasTotalesDelCurso.add(notaFinalSemestre);
                    }
                }
            }
        }

        // Asignación explícita y refresco forzado de contenedores JavaFX
        if (tblNotasB1 != null) {
            tblNotasB1.setItems(filasB1);
            tblNotasB1.refresh();
        }
        if (tblNotasB2 != null) {
            tblNotasB2.setItems(filasB2);
            tblNotasB2.refresh();
        }

        double desviacion = StatisticalAnalyzer.calculateStandardDeviation(notasTotalesDelCurso);
        if (desviacion > 1.5) {
            lblAlertaDesviacion.setText("⚠️ ALERTA DE DESVIACIÓN SEMESTRAL: Las notas globales de este curso están muy dispersas (Desviación: " + String.format("%.2f", desviacion) + "). Revisar casos individuales.");
        } else {
            lblAlertaDesviacion.setText("✅ Rendimiento del curso estable (Desviación Semestral: " + String.format("%.2f", desviacion) + ").");
        }
    }

    @FXML
    protected void onLogoutClick() throws IOException {
        Stage stage = (Stage) lblBienvenida.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    // 🔑 CLASE MODELO INTERNA TOTALMENTE CAPACITADA PARA REFRESCAR JAVAFX
    public static class StudentRow {
        private final StringProperty id = new SimpleStringProperty();
        private final StringProperty nombre = new SimpleStringProperty();
        private final StringProperty pVirtual = new SimpleStringProperty();
        private final StringProperty pPresencial = new SimpleStringProperty();
        private final StringProperty examen = new SimpleStringProperty();
        private final StringProperty talleres = new SimpleStringProperty();
        private final StringProperty deberes = new SimpleStringProperty();
        private final StringProperty nota = new SimpleStringProperty();
        private final StringProperty estado = new SimpleStringProperty();

        public StudentRow(String id, String nombre, String pVirtual, String pPresencial, String examen, String talleres, String deberes, String nota, String estado) {
            setId(id);
            setNombre(nombre);
            setpVirtual(pVirtual);
            setpPresencial(pPresencial);
            setExamen(examen);
            setTalleres(talleres);
            setDeberes(deberes);
            setNota(nota);
            setEstado(estado);
        }

        public StringProperty idProperty() { return id; }
        public final String getId() { return idProperty().get(); }
        public final void setId(String id) { idProperty().set(id); }

        public StringProperty nombreProperty() { return nombre; }
        public final String getNombre() { return nombreProperty().get(); }
        public final void setNombre(String nombre) { nombreProperty().set(nombre); }

        public StringProperty pVirtualProperty() { return pVirtual; }
        public final String getpVirtual() { return pVirtualProperty().get(); }
        public final void setpVirtual(String pVirtual) { pVirtualProperty().set(pVirtual); }

        public StringProperty pPresencialProperty() { return pPresencial; }
        public final String getpPresencial() { return pPresencialProperty().get(); }
        public final void setpPresencial(String pPresencial) { pPresencialProperty().set(pPresencial); }

        public StringProperty examenProperty() { return examen; }
        public final String getExamen() { return examenProperty().get(); }
        public final void setExamen(String examen) { examenProperty().set(examen); }

        public StringProperty talleresProperty() { return talleres; }
        public final String getTalleres() { return talleresProperty().get(); }
        public final void setTalleres(String talleres) { talleresProperty().set(talleres); }

        public StringProperty deberesProperty() { return deberes; }
        public final String getDeberes() { return deberesProperty().get(); }
        public final void setDeberes(String deberes) { deberesProperty().set(deberes); }

        public StringProperty notaProperty() { return nota; }
        public final String getNota() { return notaProperty().get(); }
        public final void setNota(String nota) { notaProperty().set(nota); }

        public StringProperty estadoProperty() { return estado; }
        public final String getEstado() { return estadoProperty().get(); }
        public final void setEstado(String estado) { estadoProperty().set(estado); }
    }
}