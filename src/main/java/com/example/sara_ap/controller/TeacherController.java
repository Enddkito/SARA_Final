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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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

    // 🎓 TABLA RESUMEN SEMESTRAL DE PROMEDIOS
    @FXML private TableView<FinalRow> tblPromediosFinales;
    @FXML private TableColumn<FinalRow, String> colFinalId;
    @FXML private TableColumn<FinalRow, String> colFinalNombre;
    @FXML private TableColumn<FinalRow, String> colFinalB1;
    @FXML private TableColumn<FinalRow, String> colFinalB2;
    @FXML private TableColumn<FinalRow, String> colFinalNotaSemestre;
    @FXML private TableColumn<FinalRow, String> colFinalEstado;

    private Professor professorLogueado;
    private CSVDataPersistence persistencia = new CSVDataPersistence();
    private List<Course> cursosDisponibles = new ArrayList<>();
    private List<Student> todosLosEstudiantes = new ArrayList<>();
    private String rutaArchivoActivo = "src/main/resources/grades.csv";

    public void initData(Professor professor) {
        if (professor == null) return;
        this.professorLogueado = professor;
        lblBienvenida.setText("👨‍🏫 Panel Docente - " + professor.getFirstName().trim() + " " + professor.getLastName().trim());

        cursosDisponibles.clear();
        todosLosEstudiantes = persistencia.loadFullSystemData(cursosDisponibles);

        cmbCursos.getItems().clear();
        String idProfesorBuscar = professor.getId().trim();

        for (Course c : cursosDisponibles) {
            if (c.getProfessor() != null && c.getProfessor().getId().trim().equalsIgnoreCase(idProfesorBuscar)) {
                cmbCursos.getItems().add(c.getCode().trim() + " - " + c.getName().trim());
            }
        }

        // Enlaces de propiedades (Bimestre 1)
        if (colIdB1 != null) colIdB1.setCellValueFactory(cd -> cd.getValue().idProperty());
        if (colNombreB1 != null) colNombreB1.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        if (colPVirtualB1 != null) colPVirtualB1.setCellValueFactory(cd -> cd.getValue().pVirtualProperty());
        if (colPPresencialB1 != null) colPPresencialB1.setCellValueFactory(cd -> cd.getValue().pPresencialProperty());
        if (colExamenB1 != null) colExamenB1.setCellValueFactory(cd -> cd.getValue().examenProperty());
        if (colTalleresB1 != null) colTalleresB1.setCellValueFactory(cd -> cd.getValue().talleresProperty());
        if (colDeberesB1 != null) colDeberesB1.setCellValueFactory(cd -> cd.getValue().deberesProperty());
        if (colNotaB1 != null) colNotaB1.setCellValueFactory(cd -> cd.getValue().notaProperty());
        if (colEstadoB1 != null) colEstadoB1.setCellValueFactory(cd -> cd.getValue().estadoProperty());

        // Enlaces de propiedades (Bimestre 2)
        if (colIdB2 != null) colIdB2.setCellValueFactory(cd -> cd.getValue().idProperty());
        if (colNombreB2 != null) colNombreB2.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        if (colPVirtualB2 != null) colPVirtualB2.setCellValueFactory(cd -> cd.getValue().pVirtualProperty());
        if (colPPresencialB2 != null) colPPresencialB2.setCellValueFactory(cd -> cd.getValue().pPresencialProperty());
        if (colExamenB2 != null) colExamenB2.setCellValueFactory(cd -> cd.getValue().examenProperty());
        if (colTalleresB2 != null) colTalleresB2.setCellValueFactory(cd -> cd.getValue().talleresProperty());
        if (colDeberesB2 != null) colDeberesB2.setCellValueFactory(cd -> cd.getValue().deberesProperty());
        if (colNotaB2 != null) colNotaB2.setCellValueFactory(cd -> cd.getValue().notaProperty());
        if (colEstadoB2 != null) colEstadoB2.setCellValueFactory(cd -> cd.getValue().estadoProperty());

        // 🎨 CABECERAS EXPANDIDAS E INFORMATIVAS
        if (colPVirtualB1 != null) colPVirtualB1.setText("P. Virtual (10%)");
        if (colPPresencialB1 != null) colPPresencialB1.setText("P. Presencial (20%)");
        if (colExamenB1 != null) colExamenB1.setText("Examen (30%)");
        if (colTalleresB1 != null) colTalleresB1.setText("Talleres (20%)");
        if (colDeberesB1 != null) colDeberesB1.setText("Deberes (20%)");

        if (colPVirtualB2 != null) colPVirtualB2.setText("P. Virtual (10%)");
        if (colPPresencialB2 != null) colPPresencialB2.setText("P. Presencial (20%)");
        if (colExamenB2 != null) colExamenB2.setText("Examen (30%)");
        if (colTalleresB2 != null) colTalleresB2.setText("Talleres (20%)");
        if (colDeberesB2 != null) colDeberesB2.setText("Deberes (20%)");

        // Enlaces de propiedades (Pestaña Final Semestral)
        if (colFinalId != null) colFinalId.setCellValueFactory(cd -> cd.getValue().idProperty());
        if (colFinalNombre != null) colFinalNombre.setCellValueFactory(cd -> cd.getValue().nombreProperty());
        if (colFinalB1 != null) colFinalB1.setCellValueFactory(cd -> cd.getValue().b1Property());
        if (colFinalB2 != null) colFinalB2.setCellValueFactory(cd -> cd.getValue().b2Property());
        if (colFinalNotaSemestre != null) colFinalNotaSemestre.setCellValueFactory(cd -> cd.getValue().notaFinalProperty());
        if (colFinalEstado != null) colFinalEstado.setCellValueFactory(cd -> cd.getValue().estadoProperty());

        // Aplicamos el semáforo de aprobación real en la pestaña final
        configurarColoresAprobacionFinal();

        // 🛠️ CONFIGURACIÓN RESPONSIVE AUTOMÁTICA DE COLUMNAS
        autoajustarColumnas();

        // 🎨 CONFIGURACIÓN DE COLORES TIPO SEMÁFORO EN BASE A LA NOTA REAL
        configurarColoresEstadoSemaforo();

        configurarTablasEditables();
    }

    private void autoajustarColumnas() {
        if (tblNotasB1 != null) {
            tblNotasB1.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            if (colIdB1 != null) colIdB1.setPrefWidth(90);
            if (colNombreB1 != null) colNombreB1.setPrefWidth(150);
            if (colPVirtualB1 != null) colPVirtualB1.setPrefWidth(105);
            if (colPPresencialB1 != null) colPPresencialB1.setPrefWidth(115);
            if (colExamenB1 != null) colExamenB1.setPrefWidth(100);
            if (colTalleresB1 != null) colTalleresB1.setPrefWidth(100);
            if (colDeberesB1 != null) colDeberesB1.setPrefWidth(100);
            if (colNotaB1 != null) colNotaB1.setPrefWidth(85);
            if (colEstadoB1 != null) colEstadoB1.setPrefWidth(120);
        }

        if (tblNotasB2 != null) {
            tblNotasB2.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            if (colIdB2 != null) colIdB2.setPrefWidth(90);
            if (colNombreB2 != null) colNombreB2.setPrefWidth(150);
            if (colPVirtualB2 != null) colPVirtualB2.setPrefWidth(105);
            if (colPPresencialB2 != null) colPPresencialB2.setPrefWidth(115);
            if (colExamenB2 != null) colExamenB2.setPrefWidth(100);
            if (colTalleresB2 != null) colTalleresB2.setPrefWidth(100);
            if (colDeberesB2 != null) colDeberesB2.setPrefWidth(100);
            if (colNotaB2 != null) colNotaB2.setPrefWidth(85);
            if (colEstadoB2 != null) colEstadoB2.setPrefWidth(120);
        }

        if (tblPromediosFinales != null) {
            tblPromediosFinales.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            if (colFinalId != null) colFinalId.setPrefWidth(100);
            if (colFinalNombre != null) colFinalNombre.setPrefWidth(180);
            if (colFinalB1 != null) colFinalB1.setPrefWidth(130);
            if (colFinalB2 != null) colFinalB2.setPrefWidth(130);
            if (colFinalNotaSemestre != null) colFinalNotaSemestre.setPrefWidth(150);
            if (colFinalEstado != null) colFinalEstado.setPrefWidth(140);
        }
    }

    private void configurarColoresEstadoSemaforo() {
        javafx.util.Callback<TableColumn<StudentRow, String>, TableCell<StudentRow, String>> factory = column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    StudentRow filaActual = getTableRow().getItem();
                    if (filaActual != null) {
                        try {
                            double notaBimestre = Double.parseDouble(filaActual.getNota().replace(",", "."));

                            if (notaBimestre < 12.0) {
                                setText("Riesgo: Peligro");
                                setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            } else if (notaBimestre >= 12.0 && notaBimestre < 14.0) {
                                setText("Alerta de Riesgo");
                                setStyle("-fx-background-color: #fff2cc; -fx-text-fill: #b38600; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            } else {
                                setText("Estable");
                                setStyle("-fx-background-color: #d9ead3; -fx-text-fill: #274e13; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            }
                        } catch (Exception ignored) {
                            setText(item);
                            setStyle("");
                        }
                    }
                }
            }
        };

        if (colEstadoB1 != null) colEstadoB1.setCellFactory(factory);
        if (colEstadoB2 != null) colEstadoB2.setCellFactory(factory);
    }

    private void configurarTablasEditables() {
        if (tblNotasB1 != null) tblNotasB1.setEditable(true);
        if (tblNotasB2 != null) tblNotasB2.setEditable(true);

        if (colPVirtualB1 != null) {
            colPVirtualB1.setCellFactory(TextFieldTableCell.forTableColumn());
            colPVirtualB1.setOnEditCommit(e -> { e.getRowValue().setpVirtual(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 1); });
        }
        if (colPPresencialB1 != null) {
            colPPresencialB1.setCellFactory(TextFieldTableCell.forTableColumn());
            colPPresencialB1.setOnEditCommit(e -> { e.getRowValue().setpPresencial(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 1); });
        }
        if (colExamenB1 != null) {
            colExamenB1.setCellFactory(TextFieldTableCell.forTableColumn());
            colExamenB1.setOnEditCommit(e -> { e.getRowValue().setExamen(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 1); });
        }

        if (colPVirtualB2 != null) {
            colPVirtualB2.setCellFactory(TextFieldTableCell.forTableColumn());
            colPVirtualB2.setOnEditCommit(e -> { e.getRowValue().setpVirtual(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 2); });
        }
        if (colPPresencialB2 != null) {
            colPPresencialB2.setCellFactory(TextFieldTableCell.forTableColumn());
            colPPresencialB2.setOnEditCommit(e -> { e.getRowValue().setpPresencial(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 2); });
        }
        if (colExamenB2 != null) {
            colExamenB2.setCellFactory(TextFieldTableCell.forTableColumn());
            colExamenB2.setOnEditCommit(e -> { e.getRowValue().setExamen(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 2); });
        }
    }

    private void actualizarDatosEnMemoria(StudentRow row, int bimestre) {
        String seleccion = cmbCursos.getValue();
        if (seleccion == null) return;
        String codigoCurso = seleccion.split(" - ")[0].trim();

        for (Student s : todosLosEstudiantes) {
            if (s.getId().trim().equalsIgnoreCase(row.getId().trim()) && s.getEnrollments() != null) {
                for (Enrollment enc : s.getEnrollments()) {
                    if (enc.getCourse() != null && enc.getCourse().getCode().trim().equalsIgnoreCase(codigoCurso)) {
                        try {
                            if (bimestre == 1) {
                                double[] b1 = enc.getNotasBimestre1();
                                b1[0] = Double.parseDouble(row.getpVirtual().replace(",", "."));
                                b1[1] = Double.parseDouble(row.getpPresencial().replace(",", "."));
                                b1[2] = Double.parseDouble(row.getExamen().replace(",", "."));
                                enc.updateBimestralGrades(b1, enc.getNotasBimestre2());
                            } else {
                                double[] b2 = enc.getNotasBimestre2();
                                b2[0] = Double.parseDouble(row.getpVirtual().replace(",", "."));
                                b2[1] = Double.parseDouble(row.getpPresencial().replace(",", "."));
                                b2[2] = Double.parseDouble(row.getExamen().replace(",", "."));
                                enc.updateBimestralGrades(enc.getNotasBimestre1(), b2);
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        onCursoSeleccionado();
    }

    @FXML
    protected void onCursoSeleccionado() {
        String seleccion = cmbCursos.getValue();
        if (seleccion == null) return;

        String codigoCurso = seleccion.split(" - ")[0].trim();
        ObservableList<StudentRow> filasB1 = FXCollections.observableArrayList();
        ObservableList<StudentRow> filasB2 = FXCollections.observableArrayList();
        List<Double> notasTotalesDelCurso = new ArrayList<>();

        // 1. Llenar tablas de Bimestre 1 y Bimestre 2
        for (Student s : todosLosEstudiantes) {
            if (s.getEnrollments() != null) {
                for (Enrollment e : s.getEnrollments()) {
                    if (e.getCourse() != null && e.getCourse().getCode().trim().equalsIgnoreCase(codigoCurso)) {

                        double[] b1 = e.getNotasBimestre1();
                        double totalB1 = (b1[0] + b1[1] + b1[2] + b1[3] + b1[4]);
                        if (totalB1 > 20.0) totalB1 = 20.0;
                        String estadoB1 = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(totalB1);

                        filasB1.add(new StudentRow(
                                s.getId(), s.getFirstName() + " " + s.getLastName(),
                                String.format("%.2f", b1[0]), String.format("%.2f", b1[1]),
                                String.format("%.2f", b1[2]), String.format("%.2f", b1[3]),
                                String.format("%.2f", b1[4]), String.format("%.2f", totalB1), estadoB1
                        ));

                        double[] b2 = e.getNotasBimestre2();
                        double totalB2 = (b2[0] + b2[1] + b2[2] + b2[3] + b2[4]);
                        if (totalB2 > 20.0) totalB2 = 20.0;
                        String estadoB2 = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(totalB2);

                        filasB2.add(new StudentRow(
                                s.getId(), s.getFirstName() + " " + s.getLastName(),
                                String.format("%.2f", b2[0]), String.format("%.2f", b2[1]),
                                String.format("%.2f", b2[2]), String.format("%.2f", b2[3]),
                                String.format("%.2f", b2[4]), String.format("%.2f", totalB2), estadoB2
                        ));

                        double notaFinalSemestre = (totalB1 + totalB2) / 2.0;
                        notasTotalesDelCurso.add(notaFinalSemestre);
                    }
                }
            }
        }

        // 2. ✨ CORRECCIÓN EXTRAORDINARIA: Lógica aislada e independiente para la pestaña final semestral
        ObservableList<FinalRow> filasFinales = FXCollections.observableArrayList();
        for (Student s : todosLosEstudiantes) {
            if (s.getEnrollments() != null) {
                for (Enrollment e : s.getEnrollments()) {
                    if (e.getCourse() != null && e.getCourse().getCode().trim().equalsIgnoreCase(codigoCurso)) {
                        double totalB1 = (e.getNotasBimestre1()[0] + e.getNotasBimestre1()[1] + e.getNotasBimestre1()[2] + e.getNotasBimestre1()[3] + e.getNotasBimestre1()[4]);
                        double totalB2 = (e.getNotasBimestre2()[0] + e.getNotasBimestre2()[1] + e.getNotasBimestre2()[2] + e.getNotasBimestre2()[3] + e.getNotasBimestre2()[4]);

                        if (totalB1 > 20.0) totalB1 = 20.0;
                        if (totalB2 > 20.0) totalB2 = 20.0;

                        double notaFinalSemestre = (totalB1 + totalB2) / 2.0;
                        String estadoFinal = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(notaFinalSemestre);

                        filasFinales.add(new FinalRow(
                                s.getId(), s.getFirstName() + " " + s.getLastName(),
                                String.format("%.2f", totalB1), String.format("%.2f", totalB2),
                                String.format("%.2f", notaFinalSemestre), estadoFinal
                        ));
                    }
                }
            }
        }

        // 3. Refrescar contenedores gráficos
        if (tblPromediosFinales != null) {
            tblPromediosFinales.setItems(filasFinales);
            tblPromediosFinales.refresh();
        }
        if (tblNotasB1 != null) { tblNotasB1.setItems(filasB1); tblNotasB1.refresh(); }
        if (tblNotasB2 != null) { tblNotasB2.setItems(filasB2); tblNotasB2.refresh(); }

        if (!notasTotalesDelCurso.isEmpty()) {
            double desviacion = StatisticalAnalyzer.calculateStandardDeviation(notasTotalesDelCurso);
            if (desviacion > 1.5) {
                lblAlertaDesviacion.setText("⚠️ ALERTA DE DESVIACIÓN SEMESTRAL: Las notas globales de este curso están muy dispersas (Desviación: " + String.format("%.2f", desviacion) + "). Revisar casos individuales.");
            } else {
                lblAlertaDesviacion.setText("✅ Rendimiento del curso estable (Desviación Semestral: " + String.format("%.2f", desviacion) + ").");
            }
        } else {
            lblAlertaDesviacion.setText("ℹ️ No hay calificaciones registradas para mostrar en este curso.");
        }
    }

    @FXML
    private void handleLoadCSV(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("SARA - Importar Archivo de Notas Externo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));

        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            String rutaCarga = selectedFile.getAbsolutePath();
            System.out.println("📂 SARA importando origen externo: " + rutaCarga);
            try {
                persistencia.importGradesFromProfessorCSV(rutaCarga, todosLosEstudiantes, cursosDisponibles);
                onCursoSeleccionado();
                System.out.println("✅ Archivo externo sincronizado.");
            } catch (Exception e) {
                System.err.println("❌ Error en importación: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveChanges() {
        if (tblNotasB1 == null || tblNotasB1.getItems().isEmpty()) {
            System.out.println("⚠️ No hay datos en la tabla para guardar.");
            return;
        }
        try {
            persistencia.saveAllGrades(todosLosEstudiantes);

            List<String> lineasParaArchivo = new ArrayList<>();
            lineasParaArchivo.add("id_estudiante,codigo_materia,b1_virtual,b1_presencial,b1_examen,b1_talleres,b1_deberes,b2_virtual,b2_presencial,b2_examen,b2_talleres,b2_deberes");

            for (Student s : todosLosEstudiantes) {
                if (s.getEnrollments() != null) {
                    for (Enrollment e : s.getEnrollments()) {
                        double[] b1 = e.getNotasBimestre1();
                        double[] b2 = e.getNotasBimestre2();
                        lineasParaArchivo.add(String.format("%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f",
                                s.getId().trim(), e.getCourse().getCode().trim(),
                                b1[0], b1[1], b1[2], b1[3], b1[4],
                                b2[0], b2[1], b2[2], b2[3], b2[4]));
                    }
                }
            }
            CSVDataPersistence.saveToCSV(this.rutaArchivoActivo, lineasParaArchivo);
            System.out.println("💾 ¡Calificaciones sincronizadas directamente en " + this.rutaArchivoActivo + "!");
        } catch (Exception e) {
            System.err.println("❌ Error al guardar calificaciones: " + e.getMessage());
        }
    }
    @FXML
    private void onExportPDFClick(ActionEvent event) {
        if (tblPromediosFinales == null || tblPromediosFinales.getItems().isEmpty()) {
            System.out.println("⚠️ No hay datos consolidados en la tabla final para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("SARA - Exportar Reporte Semestral de Calificaciones");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento PDF (*.pdf)", "*.pdf"));
        String nombreCurso = cmbCursos.getValue() != null ? cmbCursos.getValue() : "Curso";
        String codigoCurso = nombreCurso.split(" - ")[0].trim();
        fileChooser.setInitialFileName("Reporte_Final_" + codigoCurso + ".pdf");

        File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (file != null) {
            // 🚀 Invocamos al servicio de manera óptima y elegante
            boolean exito = com.example.sara_ap.services.ReportService.exportToPDF(file, nombreCurso, tblPromediosFinales.getItems());

            if (exito) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("SARA - Exportación Completada");
                alert.setHeaderText(null);
                alert.setContentText("El PDF definitivo ha sido generado exitosamente de forma directa.");
                alert.showAndWait();
            } else {
                System.err.println("❌ Falló el guardado del stream PDF desde el controlador.");
            }
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

    private void configurarColoresAprobacionFinal() {
        if (colFinalEstado == null) return;
        colFinalEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    FinalRow filaActual = getTableRow().getItem();
                    if (filaActual != null) {
                        try {
                            double notaFinal = Double.parseDouble(filaActual.getNota().replace(",", "."));
                            if (notaFinal >= 14.0) {
                                setText("APROBADO");
                                setStyle("-fx-background-color: #d9ead3; -fx-text-fill: #274e13; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            } else if (notaFinal >= 12.0 && notaFinal < 14.0) {
                                setText("A SUPLETORIO");
                                setStyle("-fx-background-color: #fff2cc; -fx-text-fill: #b38600; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            } else {
                                setText("REPROBADO");
                                setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            }
                        } catch (Exception ignored) {
                            setText(item);
                            setStyle("");
                        }
                    }
                }
            }
        });
    }

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
            setId(id); setNombre(nombre); setpVirtual(pVirtual); setpPresencial(pPresencial);
            setExamen(examen); setTalleres(talleres); setDeberes(deberes); setNota(nota); setEstado(estado);
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

    public static class FinalRow {
        private final StringProperty id = new SimpleStringProperty();
        private final StringProperty nombre = new SimpleStringProperty();
        private final StringProperty b1 = new SimpleStringProperty();
        private final StringProperty b2 = new SimpleStringProperty();
        private final StringProperty notaFinal = new SimpleStringProperty();
        private final StringProperty estado = new SimpleStringProperty();

        public FinalRow(String id, String nombre, String b1, String b2, String notaFinal, String estado) {
            this.id.set(id); this.nombre.set(nombre); this.b1.set(b1); this.b2.set(b2); this.notaFinal.set(notaFinal); this.estado.set(estado);
        }
        public StringProperty idProperty() { return id; }
        public StringProperty nombreProperty() { return nombre; }
        public StringProperty b1Property() { return b1; }
        public StringProperty b2Property() { return b2; }
        public StringProperty notaFinalProperty() { return notaFinal; }
        public StringProperty estadoProperty() { return estado; }
        public String getNota() { return notaFinal.get(); }
    }
}