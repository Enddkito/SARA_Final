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

    // 📅 COMPONENTES INYECTADOS PARA LAS TUTORÍAS DE SU PESTAÑA DOCENTE
    @FXML private TableView<FilaTutoriaProfesor> tblTutoriasProfesor;
    @FXML private TableColumn<FilaTutoriaProfesor, String> colProfEstudiante;
    @FXML private TableColumn<FilaTutoriaProfesor, String> colProfMateria;
    @FXML private TableColumn<FilaTutoriaProfesor, String> colProfFecha;
    @FXML private TableColumn<FilaTutoriaProfesor, String> colProfHorario;
    @FXML private TableColumn<FilaTutoriaProfesor, String> colProfEstado;
    private final ObservableList<FilaTutoriaProfesor> listaTutoriasProfesor = FXCollections.observableArrayList();

    @FXML
    public void handleLimpiarTutoria(ActionEvent event) {
        // 1. Obtener la fila seleccionada por el profesor
        FilaTutoriaProfesor tutoriaSeleccionada = tblTutoriasProfesor.getSelectionModel().getSelectedItem();

        if (tutoriaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("SARA - Control");
            alert.setHeaderText("Ninguna selección");
            alert.setContentText("Por favor, selecciona una tutoría de la tabla para marcarla como cumplida.");
            alert.showAndWait();
            return;
        }

        // 2. Confirmación visual de cumplimiento
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("SARA - Concluir Acompañamiento");
        confirmacion.setHeaderText("¿Dar por cumplida la tutoría?");
        confirmacion.setContentText("Se eliminará la solicitud del panel y se actualizará el repositorio histórico.");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            // 3. PERSISTENCIA REAL: Leer todo el archivo, omitir la línea cumplida y volver a escribir
            List<String> lineasRestantes = new ArrayList<>();
            lineasRestantes.add("Estudiante,Asignatura,Fecha,Horario,Estado"); // Cabecera

            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(TUTORIAS_FILE_PATH))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty() || linea.startsWith("Estudiante,Asignatura")) continue;

                    String[] datos = linea.split(",");
                    // Si los datos coinciden exactamente con la fila seleccionada, la ignoramos (la limpiamos)
                    if (datos[0].trim().equalsIgnoreCase(tutoriaSeleccionada.estudianteProperty().get()) &&
                            datos[2].trim().equalsIgnoreCase(tutoriaSeleccionada.fechaProperty().get()) &&
                            datos[3].trim().equalsIgnoreCase(tutoriaSeleccionada.horarioProperty().get())) {
                        continue; // No la añadimos a la lista, simulando la eliminación
                    }
                    lineasRestantes.add(linea);
                }
            } catch (IOException e) {
                System.err.println("Error al leer el archivo para limpiar: " + e.getMessage());
            }

            // 4. Sobrescribir el archivo CSV con las líneas sobrantes usando el método estático que ya tienes
            try {
                CSVDataPersistence.saveToCSV(TUTORIAS_FILE_PATH, lineasRestantes);
                System.out.println("🎉 [SARA Sync] Repositorio CSV actualizado. Tutoría archivada.");
            } catch (IOException e) {
                System.err.println("Error al reescribir el repositorio CSV: " + e.getMessage());
            }

            // 5. Refrescar la interfaz elásticamente
            cargarTutoriasDesdeCSV();
        }
    }

    private Professor professorLogueado;
    private CSVDataPersistence persistencia = new CSVDataPersistence();
    private List<Course> cursosDisponibles = new ArrayList<>();
    private List<Student> todosLosEstudiantes = new ArrayList<>();
    private String rutaArchivoActivo = "src/main/resources/grades.csv";

    private static final String TUTORIAS_FILE_PATH = "src/main/resources/com/example/sara_ap/tutoring_appointments.csv";

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

        // Cabeceras informativas
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

        // Inicializar enlaces elásticos de la tabla de Tutorías Recibidas
        if (tblTutoriasProfesor != null) {
            colProfEstudiante.setCellValueFactory(cd -> cd.getValue().estudianteProperty());
            colProfMateria.setCellValueFactory(cd -> cd.getValue().materiaProperty());
            colProfFecha.setCellValueFactory(cd -> cd.getValue().fechaProperty());
            colProfHorario.setCellValueFactory(cd -> cd.getValue().horarioProperty());
            colProfEstado.setCellValueFactory(cd -> cd.getValue().estadoProperty());
            tblTutoriasProfesor.setItems(listaTutoriasProfesor);
        }

        // Carga automática desde el archivo común al ingresar
        cargarTutoriasDesdeCSV();

        configurarColoresAprobacionFinal();
        autoajustarColumnas();
        configurarColoresEstadoSemaforo();
        configurarTablasEditables();
    }

    private void autoajustarColumnas() {
        if (tblNotasB1 != null) tblNotasB1.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        if (tblNotasB2 != null) tblNotasB2.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        if (tblPromediosFinales != null) tblPromediosFinales.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // 🔑 Sincronización elástica de la nueva tabla de tutorías
        if (tblTutoriasProfesor != null) tblTutoriasProfesor.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    private void configurarColoresEstadoSemaforo() {
        javafx.util.Callback<TableColumn<StudentRow, String>, TableCell<StudentRow, String>> factory = column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
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
                            setText(item); setStyle("");
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

        // ==========================================
        // 📋 CONFIGURACIÓN DE EDICIÓN - BIMESTRE 1
        // ==========================================
        if (colPVirtualB1 != null) {
            colPVirtualB1.setCellFactory(TextFieldTableCell.forTableColumn());
            colPVirtualB1.setOnEditCommit(e -> { e.getRowValue().setpVirtual(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 1, 0); });
        }
        if (colPPresencialB1 != null) {
            colPPresencialB1.setCellFactory(TextFieldTableCell.forTableColumn());
            colPPresencialB1.setOnEditCommit(e -> { e.getRowValue().setpPresencial(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 1, 1); });
        }
        if (colExamenB1 != null) {
            colExamenB1.setCellFactory(TextFieldTableCell.forTableColumn());
            colExamenB1.setOnEditCommit(e -> { e.getRowValue().setExamen(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 1, 2); });
        }
        if (colTalleresB1 != null) {
            colTalleresB1.setCellFactory(TextFieldTableCell.forTableColumn());
            colTalleresB1.setOnEditCommit(e -> { e.getRowValue().setTalleres(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 1, 3); });
        }
        if (colDeberesB1 != null) {
            colDeberesB1.setCellFactory(TextFieldTableCell.forTableColumn());
            colDeberesB1.setOnEditCommit(e -> { e.getRowValue().setDeberes(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 1, 4); });
        }

        // ==========================================
        // 📋 CONFIGURACIÓN DE EDICIÓN - BIMESTRE 2
        // ==========================================
        if (colPVirtualB2 != null) {
            colPVirtualB2.setCellFactory(TextFieldTableCell.forTableColumn());
            colPVirtualB2.setOnEditCommit(e -> { e.getRowValue().setpVirtual(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 2, 0); });
        }
        if (colPPresencialB2 != null) {
            colPPresencialB2.setCellFactory(TextFieldTableCell.forTableColumn());
            colPPresencialB2.setOnEditCommit(e -> { e.getRowValue().setpPresencial(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 2, 1); });
        }
        if (colExamenB2 != null) {
            colExamenB2.setCellFactory(TextFieldTableCell.forTableColumn());
            colExamenB2.setOnEditCommit(e -> { e.getRowValue().setExamen(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 2, 2); });
        }
        if (colTalleresB2 != null) {
            colTalleresB2.setCellFactory(TextFieldTableCell.forTableColumn());
            colTalleresB2.setOnEditCommit(e -> { e.getRowValue().setTalleres(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 2, 3); });
        }
        if (colDeberesB2 != null) {
            colDeberesB2.setCellFactory(TextFieldTableCell.forTableColumn());
            colDeberesB2.setOnEditCommit(e -> { e.getRowValue().setDeberes(e.getNewValue()); actualizarDatosEnMemoria(e.getRowValue(), 2, 4); });
        }
    }
    private void actualizarDatosEnMemoria(StudentRow row, int bimestre, int indiceNota) {
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
                                // Identifica dinámicamente qué columna se editó y asigna el valor
                                if (indiceNota == 0) b1[0] = Double.parseDouble(row.getpVirtual().replace(",", "."));
                                if (indiceNota == 1) b1[1] = Double.parseDouble(row.getpPresencial().replace(",", "."));
                                if (indiceNota == 2) b1[2] = Double.parseDouble(row.getExamen().replace(",", "."));
                                if (indiceNota == 3) b1[3] = Double.parseDouble(row.getTalleres().replace(",", "."));
                                if (indiceNota == 4) b1[4] = Double.parseDouble(row.getDeberes().replace(",", "."));
                                enc.updateBimestralGrades(b1, enc.getNotasBimestre2());
                            } else {
                                double[] b2 = enc.getNotasBimestre2();
                                if (indiceNota == 0) b2[0] = Double.parseDouble(row.getpVirtual().replace(",", "."));
                                if (indiceNota == 1) b2[1] = Double.parseDouble(row.getpPresencial().replace(",", "."));
                                if (indiceNota == 2) b2[2] = Double.parseDouble(row.getExamen().replace(",", "."));
                                if (indiceNota == 3) b2[3] = Double.parseDouble(row.getTalleres().replace(",", "."));
                                if (indiceNota == 4) b2[4] = Double.parseDouble(row.getDeberes().replace(",", "."));
                                enc.updateBimestralGrades(enc.getNotasBimestre1(), b2);
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        onCursoSeleccionado(); // Fuerza el recalculo de totales y desviación estándar en vivo
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
                    if (e.getCourse() != null && e.getCourse().getCode().trim().equalsIgnoreCase(codigoCurso)) {
                        double[] b1 = e.getNotasBimestre1();
                        double totalB1 = (b1[0] + b1[1] + b1[2] + b1[3] + b1[4]);
                        String estadoB1 = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(totalB1);

                        filasB1.add(new StudentRow(s.getId(), s.getFirstName() + " " + s.getLastName(),
                                String.format("%.2f", b1[0]), String.format("%.2f", b1[1]), String.format("%.2f", b1[2]),
                                String.format("%.2f", b1[3]), String.format("%.2f", b1[4]), String.format("%.2f", totalB1), estadoB1));

                        double[] b2 = e.getNotasBimestre2();
                        double totalB2 = (b2[0] + b2[1] + b2[2] + b2[3] + b2[4]);
                        String estadoB2 = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(totalB2);

                        filasB2.add(new StudentRow(s.getId(), s.getFirstName() + " " + s.getLastName(),
                                String.format("%.2f", b2[0]), String.format("%.2f", b2[1]), String.format("%.2f", b2[2]),
                                String.format("%.2f", b2[3]), String.format("%.2f", b2[4]), String.format("%.2f", totalB2), estadoB2));

                        notasTotalesDelCurso.add((totalB1 + totalB2) / 2.0);
                    }
                }
            }
        }

        ObservableList<FinalRow> filasFinales = FXCollections.observableArrayList();
        for (Student s : todosLosEstudiantes) {
            if (s.getEnrollments() != null) {
                for (Enrollment e : s.getEnrollments()) {
                    if (e.getCourse() != null && e.getCourse().getCode().trim().equalsIgnoreCase(codigoCurso)) {
                        double totalB1 = (e.getNotasBimestre1()[0] + e.getNotasBimestre1()[1] + e.getNotasBimestre1()[2] + e.getNotasBimestre1()[3] + e.getNotasBimestre1()[4]);
                        double totalB2 = (e.getNotasBimestre2()[0] + e.getNotasBimestre2()[1] + e.getNotasBimestre2()[2] + e.getNotasBimestre2()[3] + e.getNotasBimestre2()[4]);
                        double notaFinalSemestre = (totalB1 + totalB2) / 2.0;
                        String estadoFinal = com.example.sara_ap.services.PredictiveEngine.evaluateRisk(notaFinalSemestre);

                        filasFinales.add(new FinalRow(s.getId(), s.getFirstName() + " " + s.getLastName(),
                                String.format("%.2f", totalB1), String.format("%.2f", totalB2), String.format("%.2f", notaFinalSemestre), estadoFinal));
                    }
                }
            }
        }

        if (tblPromediosFinales != null) tblPromediosFinales.setItems(filasFinales);
        if (tblNotasB1 != null) tblNotasB1.setItems(filasB1);
        if (tblNotasB2 != null) tblNotasB2.setItems(filasB2);

        if (!notasTotalesDelCurso.isEmpty()) {
            double desviacion = StatisticalAnalyzer.calculateStandardDeviation(notasTotalesDelCurso);
            lblAlertaDesviacion.setText(desviacion > 1.5 ? "⚠️ ALERTA DE DESVIACIÓN: Dispersión alta (" + String.format("%.2f", desviacion) + ")." : "✅ Rendimiento estable (" + String.format("%.2f", desviacion) + ").");
        }

        // 🔑 🚀 Sincronización elástica: Filtra y renderiza las tutorías de este curso específico en tiempo real
        cargarTutoriasDesdeCSV();
    }

    @FXML
    public void handleLoadCSV(ActionEvent event) {
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
                System.out.println("✅ Archivo externo sincronizado con éxito.");
            } catch (Exception e) {
                System.err.println("❌ Error en importación manual: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onExportPDFClick(ActionEvent event) {
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
            boolean exito = com.example.sara_ap.services.ReportService.exportToPDF(file, nombreCurso, tblPromediosFinales.getItems());

            if (exito) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("SARA - Exportación Completada");
                alert.setHeaderText(null);
                alert.setContentText("El PDF definitivo ha sido generado exitosamente de forma directa.");
                alert.showAndWait();
            } else {
                System.err.println("❌ Falló el guardado del reporte PDF.");
            }
        }
    }

    @FXML
    private void handleSaveChanges() {
        try {
            persistencia.saveAllGrades(todosLosEstudiantes);
            System.out.println("💾 ¡Calificaciones sincronizadas con éxito!");
        } catch (Exception e) {
            System.err.println("❌ Error al guardar calificaciones: " + e.getMessage());
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
                    setText(null); setStyle("");
                } else {
                    FinalRow filaActual = getTableRow().getItem();
                    if (filaActual != null) {
                        double notaFinal = Double.parseDouble(filaActual.getNota().replace(",", "."));
                        if (notaFinal >= 14.0) {
                            setText("APROBADO"); setStyle("-fx-background-color: #d9ead3; -fx-text-fill: #274e13; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        } else if (notaFinal >= 12.0 && notaFinal < 14.0) {
                            setText("A SUPLETORIO"); setStyle("-fx-background-color: #fff2cc; -fx-text-fill: #b38600; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        } else {
                            setText("REPROBADO"); setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        }
                    }
                }
            }
        });
    }

    // 📥 🔑 MÉTODO DE LECTURA FILTRADO INTELIGENTE POR ASIGNATURA SELECCIONADA
    private void cargarTutoriasDesdeCSV() {
        if (listaTutoriasProfesor == null) return;
        listaTutoriasProfesor.clear();

        String seleccionCurso = cmbCursos.getValue();
        if (seleccionCurso == null) {
            System.out.println("ℹ️ [SARA Sync] No hay curso seleccionado todavía para filtrar tutorías.");
            return;
        }

        String nombreMateriaActiva = seleccionCurso.contains(" - ") ? seleccionCurso.split(" - ")[1].trim() : seleccionCurso.trim();
        String linea;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(TUTORIAS_FILE_PATH))) {
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("Estudiante,Asignatura")) continue;

                String[] datos = linea.split(",");
                if (datos.length >= 5) {
                    String materiaAgendada = datos[1].trim();

                    if (materiaAgendada.equalsIgnoreCase(nombreMateriaActiva)) {
                        listaTutoriasProfesor.add(new FilaTutoriaProfesor(
                                datos[0].trim(), // Estudiante
                                datos[1].trim(), // Asignatura
                                datos[2].trim(), // Fecha
                                datos[3].trim(), // Horario
                                datos[4].trim()  // Estado
                        ));
                    }
                }
            }
            System.out.println("📥 [SARA Sync] Se acoplaron " + listaTutoriasProfesor.size() + " tutorías en vivo para la asignatura: " + nombreMateriaActiva);
        } catch (java.io.IOException e) {
            System.err.println("Aviso: El repositorio compartido de tutorías se está inicializando: " + e.getMessage());
        }
    }

    // =========================================================================
    // 🔑 MOLDES ESTRUCTURALES INTERNOS COMPACTOS (UNIFICADOS)
    // =========================================================================

    public static class FilaTutoriaProfesor {
        private final StringProperty estudiante = new SimpleStringProperty();
        private final StringProperty materia = new SimpleStringProperty();
        private final StringProperty fecha = new SimpleStringProperty();
        private final StringProperty horario = new SimpleStringProperty();
        private final StringProperty estado = new SimpleStringProperty();

        public FilaTutoriaProfesor(String estudiante, String materia, String fecha, String horario, String estado) {
            this.estudiante.set(estudiante);
            this.materia.set(materia);
            this.fecha.set(fecha);
            this.horario.set(horario);
            this.estado.set(estado);
        }
        public StringProperty estudianteProperty() { return estudiante; }
        public StringProperty materiaProperty() { return materia; }
        public StringProperty fechaProperty() { return fecha; }
        public StringProperty horarioProperty() { return horario; }
        public StringProperty estadoProperty() { return estado; }
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
        public StringProperty nombreProperty() { return nombre; }
        public StringProperty pVirtualProperty() { return pVirtual; }
        public StringProperty pPresencialProperty() { return pPresencial; }
        public StringProperty examenProperty() { return examen; }
        public StringProperty talleresProperty() { return talleres; }
        public StringProperty deberesProperty() { return deberes; }
        public StringProperty notaProperty() { return nota; }
        public StringProperty estadoProperty() { return estado; }

        public final String getId() { return idProperty().get(); }
        public final String getNombre() { return nombreProperty().get(); }
        public final String getpVirtual() { return pVirtualProperty().get(); }
        public final String getpPresencial() { return pPresencialProperty().get(); }
        public final String getExamen() { return examenProperty().get(); }
        public final String getTalleres() { return talleresProperty().get(); }
        public final String getDeberes() { return deberesProperty().get(); }
        public final String getNota() { return notaProperty().get(); }
        public final String getEstado() { return estadoProperty().get(); }

        public final void setId(String value) { idProperty().set(value); }
        public final void setNombre(String value) { nombreProperty().set(value); }
        public final void setpVirtual(String value) { pVirtual.set(value); }
        public final void setpPresencial(String value) { pPresencial.set(value); }
        public final void setExamen(String value) { examen.set(value); }
        public final void setTalleres(String value) { talleres.set(value); }
        public final void setDeberes(String value) { deberes.set(value); }
        public final void setNota(String value) { nota.set(value); }
        public final void setEstado(String value) { estado.set(value); }
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