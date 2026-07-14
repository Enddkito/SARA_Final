package com.example.sara_ap.controller;

import com.example.sara_ap.services.PredictiveService;
import com.example.sara_ap.services.TutoriaDocumentService;
import com.example.sara_ap.services.SimulationService;
import com.example.sara_ap.HelloApplication;
import com.example.sara_ap.domain.Student;
import com.example.sara_ap.domain.Course;
import com.example.sara_ap.domain.Enrollment;
import com.example.sara_ap.infrastructure.CSVDataPersistence;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentController {

    @FXML private Label lblBienvenidaEstudiante;
    @FXML private TableView<FilaNotaEstudiante> tblNotasEstudiante;
    @FXML private TableColumn<FilaNotaEstudiante, String> colMateria;
    @FXML private TableColumn<FilaNotaEstudiante, String> colNota;
    @FXML private TableColumn<FilaNotaEstudiante, String> colEstado;

    @FXML private TabPane tabPaneEstudiante;
    @FXML private ComboBox<String> comboMaterias;
    @FXML private TableView<FilaDesgloseEstudiante> tablaDesglose;
    @FXML private TableColumn<FilaDesgloseEstudiante, String> colComponente;
    @FXML private TableColumn<FilaDesgloseEstudiante, String> colNotaB1;
    @FXML private TableColumn<FilaDesgloseEstudiante, String> colNotaB2;

    @FXML private BarChart<String, Number> graficoAportes;
    @FXML private LineChart<String, Number> graficoEvolucion;

    @FXML private Label lblAcumuladoPredictivo;
    @FXML private Label lblNotaNecesaria;
    @FXML private Label lblMensajePredictivo;

    @FXML private VBox panelAgendamiento;
    @FXML private DatePicker dpFechaTutoria;
    @FXML private ComboBox<String> comboHorasTutoria;
    @FXML private Button btnAgendarTutoria;

    // 🧪 INYECCIONES EXCLUSIVAS DE LA PESTAÑA: SIMULADOR DE NOTAS INDEPENDIENTE
    @FXML private ComboBox<String> comboSimuladorMaterias;
    @FXML private TableView<FilaDesgloseEstudiante> tblSimuladorDesglose;
    @FXML private TableColumn<FilaDesgloseEstudiante, String> colSimComponente;
    @FXML private TableColumn<FilaDesgloseEstudiante, String> colSimNotaB1;
    @FXML private TableColumn<FilaDesgloseEstudiante, String> colSimNotaB2;
    @FXML private Label lblSimAcumulado;
    @FXML private Label lblSimMensaje;

    private final ObservableList<FilaNotaEstudiante> listaNotasResumen = FXCollections.observableArrayList();
    private final ObservableList<FilaDesgloseEstudiante> listaNotasDesglose = FXCollections.observableArrayList();
    private final ObservableList<FilaDesgloseEstudiante> listaSimuladorDesglose = FXCollections.observableArrayList();

    private final PredictiveService predictiveService = new PredictiveService();
    private final TutoriaDocumentService tutoriaService = new TutoriaDocumentService();
    private final SimulationService simulationService = new SimulationService();

    private final CSVDataPersistence persistencia = new CSVDataPersistence();
    private List<Course> cursosSistema = new ArrayList<>();
    private List<Student> estudiantesSistema = new ArrayList<>();

    private Student estudianteLogueado;

    @FXML
    public void initialize() {
        colMateria.setCellValueFactory(cd -> cd.getValue().materiaProperty());
        colNota.setCellValueFactory(cd -> cd.getValue().notaProperty());
        colEstado.setCellValueFactory(cd -> cd.getValue().estadoProperty());
        tblNotasEstudiante.setItems(listaNotasResumen);

        colComponente.setCellValueFactory(cd -> cd.getValue().componenteProperty());
        colNotaB1.setCellValueFactory(cd -> cd.getValue().notaB1Property());
        colNotaB2.setCellValueFactory(cd -> cd.getValue().notaB2Property());
        tablaDesglose.setItems(listaNotasDesglose);

        if (comboHorasTutoria != null) {
            comboHorasTutoria.setItems(FXCollections.observableArrayList(
                    "09:00 - 10:00 (Horas de Consulta)",
                    "11:00 - 12:00 (Bloque Tutorías B2)",
                    "14:00 - 15:00 (Asesoría Académica)"
            ));
        }

        if (dpFechaTutoria != null) {
            dpFechaTutoria.setValue(LocalDate.now().plusDays(1));
        }

        // Escuchador del ComboBox principal
        comboMaterias.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                procesarCambioAsignatura(nuevo);
            }
        });

        if (tabPaneEstudiante != null) {
            tabPaneEstudiante.getSelectionModel().selectedIndexProperty().addListener((obs, viejoIdx, nuevoIdx) -> {
                String materiaSeleccionada = comboMaterias.getSelectionModel().getSelectedItem();
                if (materiaSeleccionada != null) {
                    actualizarGraficos(materiaSeleccionada);
                }
                refrescarDatosDesdeCSV();
            });
        }

        // Configurar los enlaces de propiedades para la tabla del simulador
        if (tblSimuladorDesglose != null) {
            colSimComponente.setCellValueFactory(cd -> cd.getValue().componenteProperty());
            colSimNotaB1.setCellValueFactory(cd -> cd.getValue().notaB1Property());
            colSimNotaB2.setCellValueFactory(cd -> cd.getValue().notaB2Property());
            tblSimuladorDesglose.setItems(listaSimuladorDesglose);
            tblSimuladorDesglose.setEditable(true);
        }

        // Desbloquear celdas numéricas de la pestaña simuladora y mapearlas al servicio independiente
        if (colSimNotaB1 != null) {
            colSimNotaB1.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
            colSimNotaB1.setOnEditCommit(e -> {
                if (!e.getRowValue().getComponente().startsWith("TOTAL")) {
                    e.getRowValue().setNotaB1(e.getNewValue());
                    simulationService.ejecutarRecalculoSimulado(listaSimuladorDesglose, tblSimuladorDesglose, comboSimuladorMaterias.getValue(), lblSimAcumulado, lblSimMensaje);
                }
            });
        }

        if (colSimNotaB2 != null) {
            colSimNotaB2.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
            colSimNotaB2.setOnEditCommit(e -> {
                if (!e.getRowValue().getComponente().startsWith("TOTAL")) {
                    e.getRowValue().setNotaB2(e.getNewValue());
                    simulationService.ejecutarRecalculoSimulado(listaSimuladorDesglose, tblSimuladorDesglose, comboSimuladorMaterias.getValue(), lblSimAcumulado, lblSimMensaje);
                }
            });
        }

        // Escuchador dinámico para el ComboBox del simulador
        if (comboSimuladorMaterias != null) {
            comboSimuladorMaterias.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
                if (nuevo != null) {
                    cargarMateriaEnSimulador(nuevo);
                }
            });
        }
    }

    public void initData(Student student) {
        if (student == null) return;
        this.estudianteLogueado = student;

        if (lblBienvenidaEstudiante != null) {
            lblBienvenidaEstudiante.setText("👋 ¡Bienvenido a SARA, " + student.getFirstName().trim() + " " + student.getLastName().trim() + "!");
        }

        // 🔄 1. Sincronizar y leer el CSV del disco duro de inmediato
        refrescarDatosDesdeCSV();

        // 🔑 2. POBLAR EL COMBOBOX ESCANEANDO LA DATA FRESCA DEL SISTEMA
        comboMaterias.getItems().clear();
        String cedulaBuscar = estudianteLogueado.getId().trim();

        for (Student s : estudiantesSistema) {
            if (s.getId().trim().equals(cedulaBuscar)) {
                if (s.getEnrollments() != null) {
                    for (Enrollment e : s.getEnrollments()) {
                        if (e.getCourse() != null) {
                            String nombreCurso = e.getCourse().getName().trim();
                            if (!comboMaterias.getItems().contains(nombreCurso)) {
                                comboMaterias.getItems().add(nombreCurso);
                            }
                        }
                    }
                }
                break;
            }
        }

        // 🔄 3. Seleccionar la primera materia de forma reactiva si el combo se llenó con éxito
        if (!comboMaterias.getItems().isEmpty()) {
            comboMaterias.getSelectionModel().selectFirst();
            procesarCambioAsignatura(comboMaterias.getSelectionModel().getSelectedItem());
        } else {
            System.out.println("⚠️ [SARA Alerta] No se encontraron materias matriculadas en el CSV para la cédula: " + cedulaBuscar);
        }

        // 🔬 4. Inicialización acoplada y limpia de la pestaña simuladora independiente
        if (comboSimuladorMaterias != null) {
            comboSimuladorMaterias.getItems().clear();
            comboSimuladorMaterias.getItems().addAll(comboMaterias.getItems());
            comboSimuladorMaterias.getSelectionModel().selectFirst();
            cargarMateriaEnSimulador(comboSimuladorMaterias.getSelectionModel().getSelectedItem());
        }
    }

    private void refrescarDatosDesdeCSV() {
        if (estudianteLogueado == null) return;
        cursosSistema.clear();
        estudiantesSistema = persistencia.loadFullSystemData(cursosSistema);

        listaNotasResumen.clear();
        String cedulaBuscar = estudianteLogueado.getId().trim();

        for (Student s : estudiantesSistema) {
            if (s.getId().trim().equals(cedulaBuscar)) {
                if (s.getEnrollments() != null) {
                    for (Enrollment e : s.getEnrollments()) {
                        double t1 = 0; for (double v : e.getNotasBimestre1()) t1 += v;
                        double t2 = 0; for (double v : e.getNotasBimestre2()) t2 += v;
                        double prom40 = t1 + t2;

                        String est;
                        if (prom40 >= 28.0) {
                            est = "Aprobado";
                        } else if (t1 < 16.0 && t2 == 0.0) {
                            est = "Alerta: Tutoría";
                        } else {
                            est = "Supletorio";
                        }

                        String nombreUI = e.getCourse() != null ? e.getCourse().getName() : "Materia";
                        listaNotasResumen.add(new FilaNotaEstudiante(nombreUI, prom40, est));
                    }
                }
                break;
            }
        }
    }

    private void procesarCambioAsignatura(String materia) {
        if (estudianteLogueado == null || materia == null) return;
        cursosSistema.clear();
        estudiantesSistema = persistencia.loadFullSystemData(cursosSistema);

        listaNotasDesglose.clear();

        double[] b1 = new double[]{0,0,0,0,0};
        double[] b2 = new double[]{0,0,0,0,0};
        String cedulaBuscar = estudianteLogueado.getId().trim();

        for (Student s : estudiantesSistema) {
            if (s.getId().trim().equals(cedulaBuscar) && s.getEnrollments() != null) {
                for (Enrollment e : s.getEnrollments()) {
                    if (e.getCourse() != null && e.getCourse().getName().trim().equalsIgnoreCase(materia.trim())) {
                        b1 = e.getNotasBimestre1();
                        b2 = e.getNotasBimestre2();
                        break;
                    }
                }
            }
        }

        listaNotasDesglose.add(new FilaDesgloseEstudiante("P. Virtual (10%)", b1[0], b2[0]));
        listaNotasDesglose.add(new FilaDesgloseEstudiante("P. Presencial (20%)", b1[1], b2[1]));
        listaNotasDesglose.add(new FilaDesgloseEstudiante("Examen (30%)", b1[2], b2[2]));
        listaNotasDesglose.add(new FilaDesgloseEstudiante("Talleres (20%)", b1[3], b2[3]));
        listaNotasDesglose.add(new FilaDesgloseEstudiante("Deberes (20%)", b1[4], b2[4]));

        double totalB1 = (b1[0]+b1[1]+b1[2]+b1[3]+b1[4]);
        double totalB2 = (b2[0]+b2[1]+b2[2]+b2[3]+b2[4]);
        listaNotasDesglose.add(new FilaDesgloseEstudiante("TOTAL ACUMULADO (Sobre 20 pts c/u)", totalB1, totalB2));

        actualizarGraficos(materia);

        PredictiveService.PredictionResult res = predictiveService.calcularPrediccion(listaNotasDesglose, materia);
        lblAcumuladoPredictivo.setText(String.format("%.2f / 40.00", res.acumuladoActual));

        if (res.enRiesgo) {
            lblNotaNecesaria.setText("N/A (Supletorio)");
            if (panelAgendamiento != null) {
                panelAgendamiento.setVisible(true);
                panelAgendamiento.setManaged(true);
            }
        } else {
            lblNotaNecesaria.setText(res.requeridoExamenB2 == 0 ? "0.00 / 10.00" : String.format("%.2f / 10.00", res.requeridoExamenB2));
            if (panelAgendamiento != null) {
                panelAgendamiento.setVisible(false);
                panelAgendamiento.setManaged(false);
            }
        }

        lblMensajePredictivo.setText(res.mensaje);
        lblMensajePredictivo.setStyle("-fx-text-fill: " + res.colorHex + "; -fx-font-weight: bold;");
    }

    private void actualizarGraficos(String materia) {
        if (graficoAportes == null || graficoEvolucion == null || materia == null) return;

        graficoAportes.getData().clear();
        graficoEvolucion.getData().clear();

        XYChart.Series<String, Number> serB1 = new XYChart.Series<>(); serB1.setName("Primer Bimestre (B1)");
        XYChart.Series<String, Number> serB2 = new XYChart.Series<>(); serB2.setName("Segundo Bimestre (B2)");

        for (FilaDesgloseEstudiante f : listaNotasDesglose) {
            if (!f.getComponente().startsWith("TOTAL")) {
                try {
                    serB1.getData().add(new XYChart.Data<>(f.getComponente(), Double.parseDouble(f.getNotaB1().replace(",", "."))));
                    serB2.getData().add(new XYChart.Data<>(f.getComponente(), Double.parseDouble(f.getNotaB2().replace(",", "."))));
                } catch (NumberFormatException ignored) {}
            }
        }
        graficoAportes.getData().addAll(serB1, serB2);

        XYChart.Series<String, Number> tendencia = new XYChart.Series<>();
        tendencia.setName(materia);

        double totalB1 = 0;
        double totalB2 = 0;
        for(FilaDesgloseEstudiante f : listaNotasDesglose) {
            if(f.getComponente().startsWith("TOTAL")) {
                totalB1 = Double.parseDouble(f.getNotaB1().replace(",", "."));
                totalB2 = Double.parseDouble(f.getNotaB2().replace(",", "."));
                break;
            }
        }

        tendencia.getData().add(new XYChart.Data<>("Cierre B1 (Sobre 20)", totalB1));
        tendencia.getData().add(new XYChart.Data<>("Cierre B2 (Sobre 40)", totalB1 + totalB2));
        graficoEvolucion.getData().add(tendencia);
    }

    private void cargarMateriaEnSimulador(String materia) {
        if (estudianteLogueado == null || materia == null) return;
        listaSimuladorDesglose.clear();

        double[] b1 = new double[]{0,0,0,0,0};
        double[] b2 = new double[]{0,0,0,0,0};
        String cedulaBuscar = estudianteLogueado.getId().trim();

        // 🔍 BÚSQUEDA AGREGADA Y GENÉRICA: Buscamos en el sistema por el nombre exacto de la materia del ComboBox
        for (Student s : estudiantesSistema) {
            if (s.getId().trim().equals(cedulaBuscar) && s.getEnrollments() != null) {
                for (Enrollment e : s.getEnrollments()) {
                    // Si el curso existe y su nombre coincide con el seleccionado, extraemos sus arreglos de notas
                    if (e.getCourse() != null && e.getCourse().getName().trim().equalsIgnoreCase(materia.trim())) {
                        b1 = e.getNotasBimestre1();
                        b2 = e.getNotasBimestre2();
                        break;
                    }
                }
            }
        }

        // 📊 Llenamos la tabla del simulador usando las estructuras dinámicas clonadas de la memoria
        listaSimuladorDesglose.add(new FilaDesgloseEstudiante("P. Virtual (10%)", b1[0], b2[0]));
        listaSimuladorDesglose.add(new FilaDesgloseEstudiante("P. Presencial (20%)", b1[1], b2[1]));
        listaSimuladorDesglose.add(new FilaDesgloseEstudiante("Examen (30%)", b1[2], b2[2]));
        listaSimuladorDesglose.add(new FilaDesgloseEstudiante("Talleres (20%)", b1[3], b2[3]));
        listaSimuladorDesglose.add(new FilaDesgloseEstudiante("Deberes (20%)", b1[4], b2[4]));

        double totalB1 = (b1[0] + b1[1] + b1[2] + b1[3] + b1[4]);
        double totalB2 = (b2[0] + b2[1] + b2[2] + b2[3] + b2[4]);
        listaSimuladorDesglose.add(new FilaDesgloseEstudiante("TOTAL ACUMULADO (Sobre 20 pts c/u)", totalB1, totalB2));

        // Ejecutamos el recálculo inicial en el servicio elástico
        simulationService.ejecutarRecalculoSimulado(listaSimuladorDesglose, tblSimuladorDesglose, materia, lblSimAcumulado, lblSimMensaje);
    }
    @FXML
    protected void onResetSimuladorClick(ActionEvent event) {
        String materiaSeleccionada = comboSimuladorMaterias.getSelectionModel().getSelectedItem();
        if (materiaSeleccionada != null) {
            refrescarDatosDesdeCSV();
            cargarMateriaEnSimulador(materiaSeleccionada);
        }
    }

    @FXML
    void onAgendarTutoriaClick(ActionEvent event) {
        if (estudianteLogueado == null) return;

        String nombreCompletoDinamico = estudianteLogueado.getFirstName().trim() + " " + estudianteLogueado.getLastName().trim();
        String asignaturaSeleccionada = comboMaterias.getSelectionModel().getSelectedItem();
        LocalDate fecha = dpFechaTutoria.getValue();
        String horario = comboHorasTutoria.getValue();

        if (asignaturaSeleccionada != null) {
            tutoriaService.procesarAgendamientoFormal(nombreCompletoDinamico, asignaturaSeleccionada, fecha, horario);
        }
    }

    @FXML
    void onLogoutClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 450, 400);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SARA - Sistema de Análisis de Rendimiento Académico");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cerrar sesión.");
        }
    }

    // =========================================================================
    // 💡 MOLDES ESTRUCTURALES INTERNOS COMPACTOS (UNIFICADOS)
    // =========================================================================
    public static class FilaNotaEstudiante {
        private final StringProperty materia = new SimpleStringProperty();
        private final StringProperty nota = new SimpleStringProperty();
        private final StringProperty estado = new SimpleStringProperty();
        public FilaNotaEstudiante(String materia, double nota, String estado) {
            setMateria(materia); setNota(String.format("%.2f", nota)); setEstado(estado);
        }
        public StringProperty materiaProperty() { return materia; }
        public String getMateria() { return materiaProperty().get(); }
        public void setMateria(String m) { materiaProperty().set(m); }
        public StringProperty notaProperty() { return nota; }
        public String getNota() { return notaProperty().get(); }
        public void setNota(String n) { notaProperty().set(n); }
        public StringProperty estadoProperty() { return estado; }
        public String getEstado() { return estadoProperty().get(); }
        public void setEstado(String e) { estadoProperty().set(e); }
    }

    public static class FilaDesgloseEstudiante {
        private final StringProperty componente = new SimpleStringProperty();
        private final StringProperty notaB1 = new SimpleStringProperty();
        private final StringProperty notaB2 = new SimpleStringProperty();
        public FilaDesgloseEstudiante(String componente, double notaB1, double notaB2) {
            setComponente(componente); setNotaB1(String.format("%.2f", notaB1)); setNotaB2(String.format("%.2f", notaB2));
        }
        public StringProperty componenteProperty() { return componente; }
        public String getComponente() { return componenteProperty().get(); }
        public void setComponente(String c) { componenteProperty().set(c); }
        public StringProperty notaB1Property() { return notaB1; }
        public String getNotaB1() { return notaB1Property().get(); }
        public void setNotaB1(String n) { notaB1Property().set(n); }
        public StringProperty notaB2Property() { return notaB2; }
        public String getNotaB2() { return notaB2Property().get(); }
        public void setNotaB2(String n) { notaB2Property().set(n); }
    }
}